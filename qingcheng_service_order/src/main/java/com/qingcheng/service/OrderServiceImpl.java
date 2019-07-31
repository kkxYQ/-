package com.qingcheng.service;
import java.util.HashMap;
import	java.util.stream.Collectors;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.OrderItemMapper;
import com.qingcheng.dao.OrderLogMapper;
import com.qingcheng.dao.OrderMapper;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.order.GroupOrder;
import com.qingcheng.pojo.order.Order;
import com.qingcheng.pojo.order.OrderItem;
import com.qingcheng.pojo.order.OrderLog;
import com.qingcheng.goods.SkuService;
import com.qingcheng.order.CartService;
import com.qingcheng.order.OrderService;
import com.qingcheng.utils.IdWorker;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Service(interfaceClass = OrderService.class)
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderLogMapper orderLogMapper;


    /**
     * 返回全部记录
     * @return
     */
    public List<Order> findAll() {
        return orderMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Order> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<Order> orders = (Page<Order>) orderMapper.selectAll();
        return new PageResult<Order>(orders.getTotal(),orders.getResult());
    }

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return
     */
    public List<Order> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        List <Order> orderList = orderMapper.selectByExample (example);
        return orderList;
    }

    /**
     * 批量发货
     * @param orders
     */
    public void batchSend(List <Order> orders) {
        //判断订单号和物流公司是否为空
        for (Order order : orders) {
            if(order.getShippingCode ()==null||order.getShippingName ()==null){
                throw new RuntimeException ("请填写物流公司订单号");
            }
        }
        //循环订单
        for (Order order : orders) {
            order.setOrderStatus ("3");//订单状态 已发货
            order.setConsignStatus ("2");//发货状态 已发货
            order.setConsignTime (new Date ());//发货时间
            orderMapper.updateByPrimaryKeySelective (order);//更新状态
            //记录日志
            OrderLog orderLog = new OrderLog ();
            orderLog.setOrderId (order.getId ());//订单id
            orderLog.setOrderStatus (order.getOrderStatus ());//订单状态
            orderLog.setPayStatus (order.getPayStatus ());//付款状态
            orderLog.setConsignStatus (order.getConsignStatus ());//发货状态
            orderLogMapper.insert (orderLog);

        }

    }

    /**
     * 分页+条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Order> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<Order> orders = (Page<Order>) orderMapper.selectByExample(example);
        return new PageResult<Order>(orders.getTotal(),orders.getResult());
    }

    /**
     * 根据Id查询
     * @param id
     * @return
     */
    public Order findById(String id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    /**
     * 订单保存需要在订单页面显示订单号以及支付的金额
     * @param order
     */
    @Autowired
    private CartService cartService;
    @Autowired
    private IdWorker idWorker;
    @Reference
    private SkuService skuService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public Map<String ,Object> add(Order order) {
        long setid = idWorker.nextId ();//雪花算法生成id
        //1.获取选中购物车
        List <Map <String, Object>> orderItemList = cartService.findNewOrderItemList (order.getUsername ());//购物车(刷新单价)
        List <OrderItem> orderItems = orderItemList.stream ()
                .filter (cart -> (boolean) cart.get ("checked"))
                .map (cart -> (OrderItem) cart.get ("item"))
                .collect (Collectors.toList ());//选中购物车

        for (OrderItem orderItem : orderItems){
            orderItem.setOrderId (setid+"");
        }

        //2.扣减库存
        if(!skuService.deductionStock(orderItems)){
            throw new RuntimeException("库存不足666");
        };
        try {
            //3.保存订单主表
            order.setId (setid+"");//不使用数据库自增长的id
            //合计数 使用流
            IntStream numStream = orderItems.stream ().mapToInt (OrderItem::getNum);
            int totalNum = numStream.sum ();//总数量
            IntStream numMoney = orderItems.stream ().mapToInt (OrderItem::getMoney);
            int totalMoney = numMoney.sum ();//订单总金额
            int preMoney=cartService.preferential (order.getUsername ());//优惠金额
            order.setTotalNum (totalNum);
            order.setTotalNum (totalMoney);
            order.setPreMoney (preMoney);
            order.setPayMoney (totalMoney-preMoney);//实际支付金额
            order.setCreateTime (new Date ());//订单创建时间
            order.setEndTime (new Date ());
            order.setOrderStatus ("0");//订单状态
            order.setPayStatus ("0");//支付状态：未支付
            order.setConsignStatus ("0");//发货状态：未发货
            orderMapper.insert (order);

            //制造异常
            //int x=1/0;

            //4.保存订单明细表
            //打折比例方便以后退款使用   支付金额/总金额
            double proportion=(double)order.getPreMoney ()/totalMoney;
            for (OrderItem orderItem : orderItems){
                orderItem.setOrderId (order.getId ());//订单主表id
                orderItem.setId (idWorker.nextId ()+"");
                orderItem.setPayMoney ((int) (orderItem.getMoney ()*proportion));//支付金额
                orderItemMapper.insert (orderItem);
            }
        } catch (Exception e) {
            rabbitTemplate.convertAndSend ("","queue.skuback", JSON.toJSONString (orderItems));
            throw new RuntimeException ("订单生成失败");//抛出异常让其回滚
        }
        //5.清空选中购物车
        cartService.deleteCheckedCart (order.getUsername ());
        //6.返回
        Map map=new HashMap ();
        map.put ("ordersn",order.getId ());//产生的订单号
        map.put ("money",order.getPayMoney ());//支付的金额
        return map;
    }

    /**
     * 修改
     * @param order
     */
    public void update(Order order) {
        orderMapper.updateByPrimaryKeySelective(order);
    }

    /**
     *  删除
     * @param id
     */
    public void delete(String id) {
        orderMapper.deleteByPrimaryKey(id);
    }

    /**
     * 订单列表与详情
     * @param id
     * @return
     */
    public GroupOrder getOrders(String id) {
        GroupOrder groupOrder = new GroupOrder ();
        Order order = orderMapper.selectByPrimaryKey (id);
        Example example = new Example (OrderItem.class);
        Example.Criteria criteria = example.createCriteria ();
        criteria.andEqualTo ("orderId",id);
        List <OrderItem> orderItems = orderItemMapper.selectByExample (example);
        groupOrder.setOrder (order);
        groupOrder.setOrderItems (orderItems);
        return groupOrder;
    }

    /**
     * 订单合并
     * @param idone
     * @param idtwo
     * @return
     */
    @Transactional
    public void merge(String idone, String idtwo) {
        Order orderOne = orderMapper.selectByPrimaryKey (idone);//主订单
        Order orderTwo = orderMapper.selectByPrimaryKey (idtwo);//从订单

        Example example = new Example (OrderItem.class);
        Example.Criteria criteria = example.createCriteria ();
        criteria.andEqualTo ("orderId", idtwo);
        List <OrderItem> orderItemList = orderItemMapper.selectByExample (example);//从订单详细

        orderOne.setTotalNum (orderOne.getTotalNum ()+orderTwo.getTotalNum ());//数量合计
        orderOne.setTotalMoney (orderOne.getTotalMoney ()+orderTwo.getTotalMoney ());//金额合计
        orderOne.setPreMoney (orderOne.getPreMoney ()+orderTwo.getPreMoney ());//优惠金额合计
        orderOne.setPostFee (orderOne.getPostFee ()+orderTwo.getPostFee ());//邮费合计
        orderOne.setPayMoney (orderOne.getPayMoney ()+orderTwo.getPayMoney ());//实付金额

        orderMapper.updateByPrimaryKeySelective (orderOne);//更新数据

        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderId (idone);
            orderItemMapper.updateByPrimaryKeySelective (orderItem);
        }


        orderTwo.setIsDelete ("1");//更新从订单上的订单状态 已完成
        orderMapper.updateByPrimaryKeySelective (orderTwo);


    }


    @Override
    @Transactional
    public void updatePayStatus(String orderId, String transactionId) {
        Order order = orderMapper.selectByPrimaryKey (orderId);
        if(order!=null&&"0".equals (order.getPayStatus ())){//订单存在并且显示未支付 0
            order.setPayStatus ("1");//已支付
            order.setOrderStatus ("1");
            order.setUpdateTime (new Date ());
            order.setPayTime (new Date ());
            order.setTransactionId (transactionId);//微信交易流水号
            orderMapper.updateByPrimaryKeySelective (order);
            //记录订单变动日志

            OrderLog orderLog=new OrderLog();
            orderLog.setOperater("system");// 系统
            orderLog.setOperateTime(new Date());//当前日期
            orderLog.setOrderStatus("1");
            orderLog.setPayStatus("1");
            orderLog.setRemarks("支付流水号"+transactionId);
            orderLog.setOrderId(order.getId());
            orderLog.setId (idWorker.nextId ()+"");
            orderLogMapper.insert(orderLog);
        }

    }


    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 订单id
            if(searchMap.get("id")!=null && !"".equals(searchMap.get("id"))){
                criteria.andLike("id","%"+searchMap.get("id")+"%");
            }
            // 支付类型，1、在线支付、0 货到付款
            if(searchMap.get("payType")!=null && !"".equals(searchMap.get("payType"))){
                criteria.andLike("payType","%"+searchMap.get("payType")+"%");
            }
            // 物流名称
            if(searchMap.get("shippingName")!=null && !"".equals(searchMap.get("shippingName"))){
                criteria.andLike("shippingName","%"+searchMap.get("shippingName")+"%");
            }
            // 物流单号
            if(searchMap.get("shippingCode")!=null && !"".equals(searchMap.get("shippingCode"))){
                criteria.andLike("shippingCode","%"+searchMap.get("shippingCode")+"%");
            }
            // 用户名称
            if(searchMap.get("username")!=null && !"".equals(searchMap.get("username"))){
                criteria.andLike("username","%"+searchMap.get("username")+"%");
            }
            // 买家留言
            if(searchMap.get("buyerMessage")!=null && !"".equals(searchMap.get("buyerMessage"))){
                criteria.andLike("buyerMessage","%"+searchMap.get("buyerMessage")+"%");
            }
            // 是否评价
            if(searchMap.get("buyerRate")!=null && !"".equals(searchMap.get("buyerRate"))){
                criteria.andLike("buyerRate","%"+searchMap.get("buyerRate")+"%");
            }
            // 收货人
            if(searchMap.get("receiverContact")!=null && !"".equals(searchMap.get("receiverContact"))){
                criteria.andLike("receiverContact","%"+searchMap.get("receiverContact")+"%");
            }
            // 收货人手机
            if(searchMap.get("receiverMobile")!=null && !"".equals(searchMap.get("receiverMobile"))){
                criteria.andLike("receiverMobile","%"+searchMap.get("receiverMobile")+"%");
            }
            // 收货人地址
            if(searchMap.get("receiverAddress")!=null && !"".equals(searchMap.get("receiverAddress"))){
                criteria.andLike("receiverAddress","%"+searchMap.get("receiverAddress")+"%");
            }
            // 订单来源：1:web，2：app，3：微信公众号，4：微信小程序  5 H5手机页面
            if(searchMap.get("sourceType")!=null && !"".equals(searchMap.get("sourceType"))){
                criteria.andLike("sourceType","%"+searchMap.get("sourceType")+"%");
            }
            // 交易流水号
            if(searchMap.get("transactionId")!=null && !"".equals(searchMap.get("transactionId"))){
                criteria.andLike("transactionId","%"+searchMap.get("transactionId")+"%");
            }
            // 订单状态
            if(searchMap.get("orderStatus")!=null && !"".equals(searchMap.get("orderStatus"))){
                criteria.andLike("orderStatus","%"+searchMap.get("orderStatus")+"%");
            }
            // 支付状态
            if(searchMap.get("payStatus")!=null && !"".equals(searchMap.get("payStatus"))){
                criteria.andLike("payStatus","%"+searchMap.get("payStatus")+"%");
            }
            // 发货状态
            if(searchMap.get("consignStatus")!=null && !"".equals(searchMap.get("consignStatus"))){
                criteria.andLike("consignStatus","%"+searchMap.get("consignStatus")+"%");
            }
            // 是否删除
            if(searchMap.get("isDelete")!=null && !"".equals(searchMap.get("isDelete"))){
                criteria.andLike("isDelete","%"+searchMap.get("isDelete")+"%");
            }

            // 数量合计
            if(searchMap.get("totalNum")!=null ){
                criteria.andEqualTo("totalNum",searchMap.get("totalNum"));
            }
            // 金额合计
            if(searchMap.get("totalMoney")!=null ){
                criteria.andEqualTo("totalMoney",searchMap.get("totalMoney"));
            }
            // 优惠金额
            if(searchMap.get("preMoney")!=null ){
                criteria.andEqualTo("preMoney",searchMap.get("preMoney"));
            }
            // 邮费
            if(searchMap.get("postFee")!=null ){
                criteria.andEqualTo("postFee",searchMap.get("postFee"));
            }
            // 实付金额
            if(searchMap.get("payMoney")!=null ){
                criteria.andEqualTo("payMoney",searchMap.get("payMoney"));
            }
            //订单批量
            if(searchMap.get("ids")!=null){
                criteria.andIn("id",(Iterable)searchMap.get("ids"));
            }
        }
        return example;
    }

}
