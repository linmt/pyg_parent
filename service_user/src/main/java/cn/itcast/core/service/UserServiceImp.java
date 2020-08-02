package cn.itcast.core.service;

import cn.itcast.core.dao.user.UserDao;
import cn.itcast.core.pojo.user.User;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImp implements UserService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private ActiveMQQueue smsDestination;

    @Value("${template_code}")
    private String template_code;

    @Value("${sign_name}")
    private String sign_name;

    @Autowired
    private UserDao userDao;

    @Override
    public void sendCode(final String phone) {
        //1. 生成一个随机6为数字, 作为验证码
        StringBuffer sb = new StringBuffer();
        for (int i =1; i < 7; i++) {
            int s  = new Random().nextInt(10);
            sb.append(s);
        }
        //2. 手机号作为key, 验证码作为value保存到redis中, 生存时间为10分钟
        redisTemplate.boundValueOps(phone).set(sb.toString(), 60 * 10, TimeUnit.SECONDS);
        final String smsCode = sb.toString();

        //3. 将手机号, 短信内容, 模板编号, 签名封装成map消息发送给消息服务器
        jmsTemplate.send(smsDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
            MapMessage message = session.createMapMessage();
            message.setString("mobile", phone);//手机号
            message.setString("template_code", template_code);//模板编码
            message.setString("sign_name", sign_name);//签名
            Map map=new HashMap();
            map.put("code", smsCode);	//验证码
            message.setString("param", JSON.toJSONString(map));
            return (Message) message;
            }
        });
    }

    @Override
    public Boolean checkSmsCode(String phone, String smsCode) {
        if (phone == null || smsCode == null || "".equals(phone) || "".equals(smsCode)) {
            return false;
        }
        //1. 根据手机号到redis中获取我们自己存的验证码
        String redisSmsCode = (String)redisTemplate.boundValueOps(phone).get();

        //2. 判断页面传入的验证码和我们自己存的验证码是否一致
        if (smsCode.equals(redisSmsCode)) {
            return true;
        }
        return false;
    }

    @Override
    public void add(User user) {
        userDao.insertSelective(user);
    }

    public static void main(String[] args) {
        StringBuffer sb = new StringBuffer();
        for (int i =1; i < 7; i++) {
            int s  = new Random().nextInt(10);
            sb.append(s);
        }
        System.out.println("=====" + sb.toString());
    }
}
