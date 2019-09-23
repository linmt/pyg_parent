package cn.itcast.core.service;

import cn.itcast.core.pojo.seller.Seller;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义验证类, 实现springSecurity框架的UserDetailService接口
 *
 * 这里不能用注解@Service,@Service只是将该类声明为一个bean，不能在里面注入SellerService
 *
 * 解决办法：spring-security.xml中手动生命该类，并注入SellerService
 */
public class UserDetailServiceImpl implements UserDetailsService {

    /*
    这里没办法用dubbo的注解注入，因为spring-security.xml配置的包扫描，
    只能扫描没有子包的包下面的类，service和controller不能同时被扫描

    解决办法：spring-security.xml中手动注入
    <dubbo:reference id="sellerService" interface="cn.itcast.core.service.SellerService">
    */
    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<GrantedAuthority> authList = new ArrayList<GrantedAuthority>();
        authList.add(new SimpleGrantedAuthority("ROLE_SELLER"));

        //1. 判断用户名是否为空, 如果为空直接返回null
        if (username == null) {
            return null;
        }
        //2. 根据用户名到数据库查询对应的用户对象
        Seller seller = sellerService.findOne(username);
        //3. 如果用户对象查不到, 返回null
        if (seller != null) {
            //4. 如果用户对象查到了, 判断是否已经审核通过, 如果未通过返回null
            if ("1".equals(seller.getStatus())) {
                //5. 返回springSecurity的User对象, 将这个用户的用户名, 密码, 所应该具有的访问权限集合返回
                //User是框架的
                return new User(username, seller.getPassword(), authList);
            }
        }
        return null;
    }
}
