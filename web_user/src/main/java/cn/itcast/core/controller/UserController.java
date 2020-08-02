package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.service.UserService;
import cn.itcast.core.util.PhoneFormatCheckUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    /**
     * 发送短信验证码
     * @param phone     手机号
     * @return
     */
    @RequestMapping("/sendCode")
    public Result sendCode(String phone) {
        try {
            if (phone == null || "".equals(phone)) {
                return new Result(false, "手机号不能为空!");
            }
            if (!PhoneFormatCheckUtils.isPhoneLegal(phone)) {
                return new Result(false, "手机号格式不正确");
            }
            userService.sendCode(phone);
            return  new Result(true, "发送成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "发送失败!");
        }
    }

    /**
     * 添加用户
     * @param user      用户对象
     * @param smscode   验证码
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody User user, String smscode) {
        try {
            Boolean isCheck = userService.checkSmsCode(user.getPhone(), smscode);
            if (!isCheck) {
                return new Result(false, "手机号或者验证码不正确!");
            }
            user.setStatus("Y");
            user.setCreated(new Date());
            user.setUpdated(new Date());
            user.setSourceType("1");
            userService.add(user);
            return new Result(true, "用户注册成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "用户注册失败!");
        }
    }
}
