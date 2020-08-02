package cn.itcast.core.service;

import cn.itcast.core.pojo.entity.BuyerCart;

import java.util.List;

public interface CartService {
    /**
     * 将商品加入到这个人的现有的购物车列表中
     * @param cartList  这个人现有的购物车列表
     * @param itemId    商品库存id
     * @param num       购买数量
     */
    public List<BuyerCart> addItemToCartList(List<BuyerCart> cartList, Long itemId, Integer num);

    public  void setCartListToRedis(String userName, List<BuyerCart> cartList);

    public List<BuyerCart> getCartListFromRedis(String userName);

    public List<BuyerCart> mergeCookieCartListToRedisCartList(List<BuyerCart> cookieCartList, List<BuyerCart> redisCartList);
}
