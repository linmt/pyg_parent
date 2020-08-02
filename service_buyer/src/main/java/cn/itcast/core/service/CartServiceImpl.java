package cn.itcast.core.service;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.entity.BuyerCart;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.util.Constants;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    
    @Autowired
    private ItemDao itemDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<BuyerCart> addItemToCartList(List<BuyerCart> cartList, Long itemId, Integer num) {
        //1. 根据商品SKU ID查询SKU商品信息
        Item item = itemDao.selectByPrimaryKey(itemId);
        //2. 判断商品是否存在不存在, 抛异常
        if (item == null) {
            throw new RuntimeException("此商品不存在!");
        }
        //3. 判断商品状态是否为1已审核, 状态不对抛异常
        if (!"1".equals(item.getStatus())) {
            throw new RuntimeException("此商品审核未通过, 不允许购买!");
        }
        //4.获取商家ID
        String sellerId = item.getSellerId();
        //5.根据商家ID查询购物车列表中是否存在该商家的购物车
        BuyerCart buyerCart = findBuyerCartBySellerId(cartList, sellerId);
        //6.判断如果购物车列表中不存在该商家的购物车
        if (buyerCart == null) {
            //6.a.1 新建购物车对象
            buyerCart = new BuyerCart();
            //设置新创建的购物车对象的卖家id
            buyerCart.setSellerId(sellerId);
            //设置新创建的购物车对象的卖家名称
            buyerCart.setSellerName(item.getSeller());
            //创建购物项集合
            List<OrderItem> orderItemList = new ArrayList<>();
            //创建购物项
            OrderItem orderItem = createOrderItem(item, num);
            //将购物项加入到购物项集合中
            orderItemList.add(orderItem);
            //将购物项集合加入到购物车中
            buyerCart.setOrderItemList(orderItemList);
            //6.a.2 将新建的购物车对象添加到购物车列表
            cartList.add(buyerCart);
        } else {
            //6.b.1如果购物车列表中存在该商家的购物车 (查询购物车明细列表中是否存在该商品)
            List<OrderItem> orderItemList = buyerCart.getOrderItemList();
            OrderItem orderItem = findOrderItemByItemId(orderItemList, itemId);
            //6.b.2判断购物车明细是否为空
            if (orderItem == null) {
                //6.b.3为空，新增购物车明细
                orderItem = createOrderItem(item, num);
                //将新增的购物项加入到购物项集合中
                orderItemList.add(orderItem);
            } else {
                //6.b.4不为空，在原购物车明细上添加数量，更改金额
                //设置购买数量 = 原有购买数量 + 现在购买数量
                orderItem.setNum(orderItem.getNum() + num);
                //设置总价格
                orderItem.setTotalFee(orderItem.getPrice().multiply(new BigDecimal(orderItem.getNum())));
                //6.b.5如果购物车明细中数量操作后小于等于0，则移除
                if (orderItem.getNum() <= 0) {
                    orderItemList.remove(orderItem);
                }
                //6.b.6如果购物车中购物车明细列表为空,则移除
                if (orderItemList.size() <= 0) {
                    cartList.remove(buyerCart);
                }
            }
        }
        //7. 返回购物车列表对象
        return cartList;
    }

    /**
     * 从当前购物项集合中查询是否存在这个商品, 存在则返回这个购物项对象, 不存在则返回null
     * @param orderItemList 购物项集合
     * @param itemId        商品库存id
     * @return
     */
    private OrderItem findOrderItemByItemId(List<OrderItem> orderItemList, Long itemId) {
        if (orderItemList != null) {
            for (OrderItem orderItem : orderItemList) {
                if (orderItem.getItemId().equals(itemId)) {
                    return orderItem;
                }
            }
        }
        return null;
    }

    /**
     * 创建购物项对象
     * @param item  库存对象
     * @param num   购买数量
     * @return
     */
    private  OrderItem createOrderItem(Item item, Integer num) {
        if (num <= 0) {
            throw new RuntimeException("购买数量非法!");
        }
        OrderItem orderItem = new OrderItem();
        //购买数量
        orderItem.setNum(num);
        //商品id
        orderItem.setGoodsId(item.getGoodsId());
        //库存id
        orderItem.setItemId(item.getId());
        //示例图片
        orderItem.setPicPath(item.getImage());
        //单价
        orderItem.setPrice(item.getPrice());
        //卖家id
        orderItem.setSellerId(item.getSellerId());
        //商品库存标题
        orderItem.setTitle(item.getTitle());
        //总价 = 单价 * 购买数量
        orderItem.setTotalFee(item.getPrice().multiply(new BigDecimal(num)));
        return orderItem;
    }

    /**
     * 查询此购物车集合中有没有这个卖家的购物车对象, 有则返回, 没有返回null
     * @param cartList  购物车集合
     * @param sellerId  卖家id
     * @return
     */
    private BuyerCart findBuyerCartBySellerId(List<BuyerCart> cartList, String sellerId) {
        if (cartList != null) {
            for (BuyerCart cart : cartList) {
                if (cart.getSellerId().equals(sellerId)) {
                    return cart;
                }
            }
        }
        return null;
    }

    @Override
    public void setCartListToRedis(String userName, List<BuyerCart> cartList) {
        redisTemplate.boundHashOps(Constants.CART_LIST_REDIS).put(userName, cartList);
//        redisTemplate.boundHashOps(Constants.CART_LIST_REDIS).delete(userName);
    }

    @Override
    public List<BuyerCart> getCartListFromRedis(String userName) {
        List<BuyerCart> cartList = (List<BuyerCart>)redisTemplate.boundHashOps(Constants.CART_LIST_REDIS).get(userName);
        if (cartList == null) {
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    @Override
    public List<BuyerCart> mergeCookieCartListToRedisCartList(List<BuyerCart> cookieCartList, List<BuyerCart> redisCartList) {
        if (cookieCartList != null) {
            //遍历cookie购物车集合
            for (BuyerCart cookieCart : cookieCartList) {
                //遍历cookie购物车中的购物项集合
                for (OrderItem cookieOrderItem : cookieCart.getOrderItemList()) {
                    //将cookie中的购物项, 加入到redis的购物车集合中
                    redisCartList = addItemToCartList(redisCartList, cookieOrderItem.getItemId(), cookieOrderItem.getNum());
                }
            }
        }
        return redisCartList;
    }
}
