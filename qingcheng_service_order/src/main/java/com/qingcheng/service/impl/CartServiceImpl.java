package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.pojo.goods.Category;
import com.qingcheng.pojo.goods.Sku;
import com.qingcheng.pojo.order.OrderItem;
import com.qingcheng.service.goods.CategoryService;
import com.qingcheng.service.goods.SkuService;
import com.qingcheng.service.order.CartService;
import com.qingcheng.service.order.PreferentialService;
import com.qingcheng.utils.CacheKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Reference
    private SkuService skuService;
    @Reference
    private CategoryService categoryService;

    /**
     * 从redis中提取某用户的购物车
     * @param username
     * @return
     */
    @Override
    public List<Map<String, Object>> findCartList(String username) {
        System.out.println ("从redis中提取购物车"+username);
        List<Map<String, Object>> carlist = (List <Map <String, Object>>) redisTemplate.boundHashOps (CacheKey.CART_LIST).get (username);
        if (carlist==null){
            carlist=new ArrayList <> ();
        }
        return carlist;
    }

    /**
     * 添加商品到购物车
     * @param username
     * @param skuId
     * @param num
     */
    @Override
    public void addItem(String username, String skuId, Integer num) {
        //实现思路：?遍历购物车，如果购物车中存在该商品则累加数量，如果不存在则添加购物车项
        //获取购物车
        //     map checked:false/true, item:OrderItem orderItem
        List <Map <String, Object>> cartList = findCartList (username);

        boolean flag=false;//商品是否在购物车中存在

        for (Map <String, Object> map : cartList) {
            OrderItem orderItem = (OrderItem) map.get ("item");
            if(skuId.equals(orderItem.getSkuId ())){//购物车中存在该商品
                if(orderItem.getNum ()<=0){//如果数量小于等于0删除该商品 以免计算重量报错
                    cartList.remove (map);
                    break;
                }
                int weight=orderItem.getWeight ()/orderItem.getNum ();//单个商品的数量
                orderItem.setNum (orderItem.getNum ()+num);//数量累加
                orderItem.setMoney (orderItem.getNum ()*orderItem.getPrice ());//金额计算
                orderItem.setWeight (weight*orderItem.getNum ());//重量计算

                //商品数量的累加有可能小于0
                if(orderItem.getNum ()<=0){//如果数量小于等于0删除该商品
                    cartList.remove (map);
                }

                flag=true;
                break;
            }
        }


        if(flag==false){//购物车中没有该商品

            Sku sku = skuService.findById (skuId);
            if(sku==null){
                throw new RuntimeException ("商品不存在");
            }
            if (!"1".equals (sku.getStatus ())){
                throw new RuntimeException("商品状态不合法");
            }
            if (num<=0){//商品数量不能为0或者负数
                throw new RuntimeException("商品数量不合法");
            }
            OrderItem orderItem=new OrderItem ();
            orderItem.setSkuId (skuId);
            orderItem.setSpuId (sku.getSpuId ());
            orderItem.setNum (num);
            orderItem.setImage (sku.getImage ());
            orderItem.setPrice(sku.getPrice());
            orderItem.setName(sku.getName());
            orderItem.setMoney (orderItem.getPrice ()*num);//金额计算
            if(sku.getWeight ()==null){
                sku.setWeight (0);
            }
            orderItem.setWeight (sku.getWeight ()*num);//总量计算
            //商品分类 3-2-1 一级一级往上找  先在缓存中查询 查询不到在去数据库中查 并且存入redis中 下一次查询直接从redis中取
            orderItem.setCategoryId3 (sku.getCategoryId ());

            Category category2= (Category) redisTemplate.boundHashOps (CacheKey.CATEGORY).get (sku.getCategoryId ());
            if(category2==null){
                category2 = categoryService.findById (sku.getCategoryId ());//根据三级id查询二级
                redisTemplate.boundHashOps (CacheKey.CATEGORY).put (sku.getCategoryId (),category2);
            }
            orderItem.setCategoryId2 (category2.getParentId ());

            Category category1= (Category) redisTemplate.boundHashOps (CacheKey.CATEGORY).get (category2.getParentId ());
            if(category1==null){
                category1=categoryService.findById (category2.getParentId ());//根据二级id查询一级
                redisTemplate.boundHashOps (CacheKey.CATEGORY).put (category2.getParentId (),category1);
            }
            orderItem.setCategoryId1 (category1.getParentId ());

            Map map=new HashMap ();
            map.put ("item",orderItem);
            map.put ("checked",true);//默认选中

            cartList.add (map);
        }

        redisTemplate.boundHashOps (CacheKey.CART_LIST).put (username,cartList);//更新购物车
    }

    @Override
    public Boolean updateChecked(String username, String skuId, boolean checked) {
        //获取购物车
        List <Map <String, Object>> cartList = findCartList (username);
        //判断缓存中是否含有已购商品
        Boolean flag=false;
        for (Map <String, Object> map : cartList) {
            OrderItem orderItem= (OrderItem) map.get ("item");
            if(orderItem.getSkuId ().equals (skuId)){
                map.put ("checked",checked);
                flag=true;
                break;
            }
        }
        if (flag){
            redisTemplate.boundHashOps (CacheKey.CART_LIST).put (username,cartList);//存入缓冲中
        }
        return flag;
    }

    @Override
    public void deleteCheckedCart(String username) {
        //购物车使用stream流（filter）获取未选中商品
        List <Map <String, Object>> cartList = findCartList (username).stream ()
                .filter (cart -> (boolean) cart.get ("checked")==false)
                .collect(Collectors.toList());

        redisTemplate.boundHashOps (CacheKey.CART_LIST).put (username,cartList);//更新购物车
    }


    @Autowired
    private PreferentialService preferentialService;
    @Override
    public int preferential(String username) {
        //获取选中购物车 List<OrderItem>  List<Map>
        List <OrderItem> orderItemList = findCartList (username).stream ()
                .filter (cart->(boolean)cart.get("checked")==true)//筛选
                .map (cart->(OrderItem)cart.get ("item"))//提取map
                .collect (Collectors.toList ());//变成新的list
        //按分类聚合统计每个分类的金额 group By
        //分类 金额
        //1   120
        //2   500
        Map <Integer, IntSummaryStatistics> cartMap = orderItemList.stream ()
                //::方法引用  分组(根据哪一个字段进行聚合)   聚合统计的方法（指定求出来的聚合的字段）
           .collect (Collectors.groupingBy (OrderItem::getCategoryId3, Collectors.summarizingInt (OrderItem::getMoney)));

        int allpreMoney=0;//累计优惠金额
        //循环结果统计每个分类的优惠金额并累加
        for (Integer categoryId : cartMap.keySet ()) {
            //获取品类的消费金额
            int sum = (int) cartMap.get (categoryId).getSum ();//求和
            int preMoney=preferentialService.findPreMoneyByCategoryId (categoryId,sum);//获取优惠金额
            System.out.println("分类："+categoryId+"  消费金额："+ sum+  " 优惠金额：" + preMoney );
            allpreMoney+=preMoney;
        }

        return allpreMoney;
    }

    @Override
    public List <Map <String, Object>> findNewOrderItemList(String username) {
        //获取选中购物车
        List <Map <String, Object>> cartList = findCartList (username);
        //循环购物车列表重新读取每个商品的价格
        for (Map <String, Object> map : cartList) {
            OrderItem orderItem = (OrderItem) map.get ("item");
            Sku sku = skuService.findById (orderItem.getSkuId ());
            orderItem.setPrice (sku.getPrice ());//更新价格
            orderItem.setMoney (sku.getPrice ()*orderItem.getNum ());//更新金额
        }
        redisTemplate.boundHashOps (CacheKey.CART_LIST).put (username,cartList);

        return cartList;
    }
}
