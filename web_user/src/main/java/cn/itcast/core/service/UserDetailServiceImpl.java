package cn.itcast.core.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义认证类:
 * 在之前这里负责用户名密码的校验工作, 并给给当前用户赋予对应的访问权限
 * 现在cas和springSecurity集成, 集成后, 用户名密码的校验工作交给cas完成, 所以能够进入到
 * 这里类的方法中的都是已经成功认证的用户, 这里只需要给登录过的用户赋予对应的访问权限就可以
 */
public class UserDetailServiceImpl implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //创建权限集合
        List<GrantedAuthority> authorityList = new ArrayList<>();
        //向权限集合中加入访问权限
        authorityList.add(new SimpleGrantedAuthority("ROLE_USER"));
        return new User(username, "", authorityList);
    }
}
