package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.BuyerCart;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.service.CartService;
import cn.itcast.core.util.Constants;
import cn.itcast.core.util.CookieUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 购物车业务
 */
@RestController
@RequestMapping("/cart")
public class BuyerCartController {

    @Reference
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    /**
     * 添加商品到购物车
     * @CrossOrigin注解相当于设置了响应头信息, 是w3c支持的一种跨域解决方案
     * origins属性设置的地址是, 返回响应给静态页面, 静态页面所在服务器地址, 也即是service_page项目地址
     * @param itemId    商品库存id
     * @param num       购买数量
     */
    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins="http://localhost:8086",allowCredentials="true")
    public Result addGoodsToCartList(Long itemId, Integer num) {
        try {
            //1. 获取当前登录用户名称
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            //2. 获取购物车列表
            List<BuyerCart> cartList = findCartList();
            //3. 将当前商品加入到购物车列表
            cartList = cartService.addItemToCartList(cartList, itemId, num);
            //4. 判断当前用户是否登录, 未登录用户名为"anonymousUser"
            if ("anonymousUser".equals(userName)) {
                //4.a.如果未登录, 则将购物车列表存入cookie中
                CookieUtil.setCookie(request, response,
                        Constants.CART_LIST_COOKIE,
                        JSON.toJSONString(cartList),
                        60 * 60 * 24 * 30, "utf-8");
            } else {
                //4.b.如果已登录, 则将购物车列表存入redis中
                cartService.setCartListToRedis(userName, cartList);
            }
            return new Result(true, "添加成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败!");
        }
    }

    /**
     * 获取购物车列表所有数据返回
     * @return
     */
    @RequestMapping("/findCartList")
    public List<BuyerCart> findCartList() {
        //1. 获取当前登录用户名称
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        //2. 从cookie中获取购物车列表json格式字符串
        String cookieCartListStr = CookieUtil.getCookieValue(request, Constants.CART_LIST_COOKIE, "utf-8");
        //3. 如果购物车列表json串为空则返回"[]"
        if (cookieCartListStr == null || "".equals(cookieCartListStr)) {
            cookieCartListStr = "[]";
        }
        //4. 将购物车列表json转换为对象
        List<BuyerCart> cookieCartList = JSON.parseArray(cookieCartListStr, BuyerCart.class);
        //5. 判断用户是否登录, 未登录用户为"anonymousUser"
        if ("anonymousUser".equals(userName)) {
            //5.a. 未登录, 返回cookie中的购物车列表对象
            return cookieCartList;
        } else {
            //5.b.1.已登录, 从redis中获取购物车列表对象
            List<BuyerCart> redisCartList = cartService.getCartListFromRedis(userName);
            //5.b.2.判断cookie中是否存在购物车列表
            if (cookieCartList.size() > 0) {
                //如果cookie中存在购物车列表则和redis中的购物车列表合并成一个对象
                redisCartList = cartService.mergeCookieCartListToRedisCartList(cookieCartList, redisCartList);
                //删除cookie中购物车列表
                CookieUtil.deleteCookie(request, response, Constants.CART_LIST_COOKIE);
                //将合并后的购物车列表存入redis中
                cartService.setCartListToRedis(userName, redisCartList);
            }
            //5.b.3.返回购物车列表对象
            return redisCartList;
        }
    }
}
