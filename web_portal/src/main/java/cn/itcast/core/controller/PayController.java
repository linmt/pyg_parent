package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.service.OrderService;
import cn.itcast.core.service.PayService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付业务
 */
@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private OrderService orderService;

    @Reference
    private PayService payService;

    /**
     * 获取当前登录用户名, 根据用户名获取redis中的支付日志对象, 根据支付日志对象中的支付单号和总金额
     * 调用微信统一下单接口, 生成支付链接返回
     * @return
     */
    @RequestMapping("/createNative")
    public Map createNative() {
        //1. 获取当前登录用户的用户名
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        //2. 根据用户名获取支付日志对象
        PayLog payLog = orderService.getPayLogByUserName(userName);
        if (payLog != null) {
            //3. 调用统一下单接口生成支付链接
            Map map = payService.createNative(payLog.getOutTradeNo(), "1");//payLog.getTotalFee()
            return map;
        }
        return new HashMap();
    }

    /**
     * 调用查询订单接口, 查询是否支付成功
     * @param out_trade_no
     * @return
     */
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        Result result = null;
        int flag = 1;
        while(true) {
            //1. 判断支付单号等于null
            if (out_trade_no == null) {
                result = new Result(false, "二维码超时");
                break;
            }
            //2. 调用查询接口查询支付是否成功
            Map map = payService.queryPayStatus(out_trade_no);
            if ("SUCCESS".equals(map.get("trade_state"))) {
                result = new Result(true, "支付成功!");
                //3. 如果支付成功, 支付日志表和订单表的支付状态改为已支付, redis的支付日志对象删除
                orderService.updatePayStatus(userName);
                break;
            }
            
            try {
                Thread.sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //如果5分钟没有支付则支付超时
            if (flag > 100) {
                result = new Result(false, "二维码超时");
                break;
            }
            flag++;
        }
        return result;
    }
}
