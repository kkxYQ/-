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
     * ��redis����ȡĳ�û��Ĺ��ﳵ
     * @param username
     * @return
     */
    @Override
    public List<Map<String, Object>> findCartList(String username) {
        System.out.println ("��redis����ȡ���ﳵ"+username);
        List<Map<String, Object>> carlist = (List <Map <String, Object>>) redisTemplate.boundHashOps (CacheKey.CART_LIST).get (username);
        if (carlist==null){
            carlist=new ArrayList <> ();
        }
        return carlist;
    }

    /**
     * �����Ʒ�����ﳵ
     * @param username
     * @param skuId
     * @param num
     */
    @Override
    public void addItem(String username, String skuId, Integer num) {
        //ʵ��˼·��?�������ﳵ��������ﳵ�д��ڸ���Ʒ���ۼ��������������������ӹ��ﳵ��
        //��ȡ���ﳵ
        //     map checked:false/true, item:OrderItem orderItem
        List <Map <String, Object>> cartList = findCartList (username);

        boolean flag=false;//��Ʒ�Ƿ��ڹ��ﳵ�д���

        for (Map <String, Object> map : cartList) {
            OrderItem orderItem = (OrderItem) map.get ("item");
            if(skuId.equals(orderItem.getSkuId ())){//���ﳵ�д��ڸ���Ʒ
                if(orderItem.getNum ()<=0){//�������С�ڵ���0ɾ������Ʒ ���������������
                    cartList.remove (map);
                    break;
                }
                int weight=orderItem.getWeight ()/orderItem.getNum ();//������Ʒ������
                orderItem.setNum (orderItem.getNum ()+num);//�����ۼ�
                orderItem.setMoney (orderItem.getNum ()*orderItem.getPrice ());//������
                orderItem.setWeight (weight*orderItem.getNum ());//��������

                //��Ʒ�������ۼ��п���С��0
                if(orderItem.getNum ()<=0){//�������С�ڵ���0ɾ������Ʒ
                    cartList.remove (map);
                }

                flag=true;
                break;
            }
        }


        if(flag==false){//���ﳵ��û�и���Ʒ

            Sku sku = skuService.findById (skuId);
            if(sku==null){
                throw new RuntimeException ("��Ʒ������");
            }
            if (!"1".equals (sku.getStatus ())){
                throw new RuntimeException("��Ʒ״̬���Ϸ�");
            }
            if (num<=0){//��Ʒ��������Ϊ0���߸���
                throw new RuntimeException("��Ʒ�������Ϸ�");
            }
            OrderItem orderItem=new OrderItem ();
            orderItem.setSkuId (skuId);
            orderItem.setSpuId (sku.getSpuId ());
            orderItem.setNum (num);
            orderItem.setImage (sku.getImage ());
            orderItem.setPrice(sku.getPrice());
            orderItem.setName(sku.getName());
            orderItem.setMoney (orderItem.getPrice ()*num);//������
            if(sku.getWeight ()==null){
                sku.setWeight (0);
            }
            orderItem.setWeight (sku.getWeight ()*num);//��������
            //��Ʒ���� 3-2-1 һ��һ��������  ���ڻ����в�ѯ ��ѯ������ȥ���ݿ��в� ���Ҵ���redis�� ��һ�β�ѯֱ�Ӵ�redis��ȡ
            orderItem.setCategoryId3 (sku.getCategoryId ());

            Category category2= (Category) redisTemplate.boundHashOps (CacheKey.CATEGORY).get (sku.getCategoryId ());
            if(category2==null){
                category2 = categoryService.findById (sku.getCategoryId ());//��������id��ѯ����
                redisTemplate.boundHashOps (CacheKey.CATEGORY).put (sku.getCategoryId (),category2);
            }
            orderItem.setCategoryId2 (category2.getParentId ());

            Category category1= (Category) redisTemplate.boundHashOps (CacheKey.CATEGORY).get (category2.getParentId ());
            if(category1==null){
                category1=categoryService.findById (category2.getParentId ());//���ݶ���id��ѯһ��
                redisTemplate.boundHashOps (CacheKey.CATEGORY).put (category2.getParentId (),category1);
            }
            orderItem.setCategoryId1 (category1.getParentId ());

            Map map=new HashMap ();
            map.put ("item",orderItem);
            map.put ("checked",true);//Ĭ��ѡ��

            cartList.add (map);
        }

        redisTemplate.boundHashOps (CacheKey.CART_LIST).put (username,cartList);//���¹��ﳵ
    }

    @Override
    public Boolean updateChecked(String username, String skuId, boolean checked) {
        //��ȡ���ﳵ
        List <Map <String, Object>> cartList = findCartList (username);
        //�жϻ������Ƿ����ѹ���Ʒ
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
            redisTemplate.boundHashOps (CacheKey.CART_LIST).put (username,cartList);//���뻺����
        }
        return flag;
    }

    @Override
    public void deleteCheckedCart(String username) {
        //���ﳵʹ��stream����filter����ȡδѡ����Ʒ
        List <Map <String, Object>> cartList = findCartList (username).stream ()
                .filter (cart -> (boolean) cart.get ("checked")==false)
                .collect(Collectors.toList());

        redisTemplate.boundHashOps (CacheKey.CART_LIST).put (username,cartList);//���¹��ﳵ
    }


    @Autowired
    private PreferentialService preferentialService;
    @Override
    public int preferential(String username) {
        //��ȡѡ�й��ﳵ List<OrderItem>  List<Map>
        List <OrderItem> orderItemList = findCartList (username).stream ()
                .filter (cart->(boolean)cart.get("checked")==true)//ɸѡ
                .map (cart->(OrderItem)cart.get ("item"))//��ȡmap
                .collect (Collectors.toList ());//����µ�list
        //������ۺ�ͳ��ÿ������Ľ�� group By
        //���� ���
        //1   120
        //2   500
        Map <Integer, IntSummaryStatistics> cartMap = orderItemList.stream ()
                //::��������  ����(������һ���ֶν��оۺ�)   �ۺ�ͳ�Ƶķ�����ָ��������ľۺϵ��ֶΣ�
           .collect (Collectors.groupingBy (OrderItem::getCategoryId3, Collectors.summarizingInt (OrderItem::getMoney)));

        int allpreMoney=0;//�ۼ��Żݽ��
        //ѭ�����ͳ��ÿ��������Żݽ��ۼ�
        for (Integer categoryId : cartMap.keySet ()) {
            //��ȡƷ������ѽ��
            int sum = (int) cartMap.get (categoryId).getSum ();//���
            int preMoney=preferentialService.findPreMoneyByCategoryId (categoryId,sum);//��ȡ�Żݽ��
            System.out.println("���ࣺ"+categoryId+"  ���ѽ�"+ sum+  " �Żݽ�" + preMoney );
            allpreMoney+=preMoney;
        }

        return allpreMoney;
    }

    @Override
    public List <Map <String, Object>> findNewOrderItemList(String username) {
        //��ȡѡ�й��ﳵ
        List <Map <String, Object>> cartList = findCartList (username);
        //ѭ�����ﳵ�б����¶�ȡÿ����Ʒ�ļ۸�
        for (Map <String, Object> map : cartList) {
            OrderItem orderItem = (OrderItem) map.get ("item");
            Sku sku = skuService.findById (orderItem.getSkuId ());
            orderItem.setPrice (sku.getPrice ());//���¼۸�
            orderItem.setMoney (sku.getPrice ()*orderItem.getNum ());//���½��
        }
        redisTemplate.boundHashOps (CacheKey.CART_LIST).put (username,cartList);

        return cartList;
    }
}
