package cn.itcast.core.service;

import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemQuery;
import com.alibaba.dubbo.config.annotation.Service;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.servlet.ServletContext;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CmsServiceImpl implements CmsService, ServletContextAware {

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private GoodsDescDao descDao;

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private ItemCatDao catDao;

    /*
    为何在common中有相应的jar包，这里却用不了？
    <dependency>
        <groupId>javax.servlet</groupId>
        <artifactId>servlet-api</artifactId>
        <scope>provided</scope>
    </dependency>

    因为任何一个tomcat中都有该jar包，C:\mysoft\programming\java\tomcat\apache-tomcat-7.0.94\lib

    加了provided后，这里就不会打到war包中，但是这个属性打断了传递依赖性，只能将其复制到service_page中，但是provided属性还要保留
    */
    /*
    这里没有配置springmvc，所以无法注入
     */
    private ServletContext servletContext;

    @Autowired
    private FreeMarkerConfigurer freemarkerConfig;

    @Override
    public void createStaticPage(Long goodsId, Map<String, Object> rootMap) throws Exception {
        //1. 获取模板的初始化对象
        Configuration configuration = freemarkerConfig.getConfiguration();
        //2. 获取模板对象
        Template template = configuration.getTemplate("item.ftl");

        //3. 创建输出流, 指定生成静态页面的位置和名称
        String path = goodsId + ".html";
        System.out.println("===path====" + path);
        String realPath = getRealPath(path);

        Writer out = new OutputStreamWriter(new FileOutputStream(new File(realPath)), "utf-8");
        //4. 生成
        template.process(rootMap, out);
        //5.关闭流
        out.close();
    }

    /**
     * 将相对路径转换成绝对路径
     * @param path  相对路径
     * @return
     */
    private String getRealPath(String path) {
        String realPath = servletContext.getRealPath(path);
        System.out.println("===realPath=====" + realPath);
        return realPath;
    }

    @Override
    public Map<String, Object> findGoodsData(Long goodsId) {
        Map<String, Object> resultMap = new HashMap<>();

        //1. 获取商品数据
        Goods goods = goodsDao.selectByPrimaryKey(goodsId);
        //2. 获取商品详情数据
        GoodsDesc goodsDesc = descDao.selectByPrimaryKey(goodsId);

        //3. 获取库存集合数据
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andGoodsIdEqualTo(goodsId);
        List<Item> itemList = itemDao.selectByExample(query);

        //4. 获取商品对应的分类数据
        if (goods != null) {
            ItemCat itemCat1 = catDao.selectByPrimaryKey(goods.getCategory1Id());
            ItemCat itemCat2 = catDao.selectByPrimaryKey(goods.getCategory2Id());
            ItemCat itemCat3 = catDao.selectByPrimaryKey(goods.getCategory3Id());
            resultMap.put("itemCat1", itemCat1.getName());
            resultMap.put("itemCat2", itemCat2.getName());
            resultMap.put("itemCat3", itemCat3.getName());
        }
        //5. 将商品所有数据封装成Map返回
        resultMap.put("goods", goods);
        resultMap.put("goodsDesc", goodsDesc);
        resultMap.put("itemList", itemList);
        return resultMap;
    }

    /**
     * 由于当前项目是service项目, 没有配置springMvc所以没有初始化servletContext对象,
     * 但是我们这个项目配置了spring, spring中有servletContextAware接口, 这个接口中用servletContext对象
     * 这个是spring初始化好的, 所以我们实现servletContextAware接口, 目的是使用里面的servletContext对象给
     * 我们当前类上的servletContext对象赋值
     * @param servletContext
     */
    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
