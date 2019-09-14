package cn.itcast.core.test;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.BrandQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

//配置spring测试环境
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring/applicationContext*.xml"})
public class TestBrandDao {

    @Autowired
    private BrandDao brandDao;

    @Test
    public void testfindBrandById() {
        Brand brand = brandDao.selectByPrimaryKey(1L);
        System.out.println("=======" + brand);
    }

    @Test
    public void testfindBrandAll() {
        List<Brand> brands = brandDao.selectByExample(null);
        System.out.println("=======" + brands);
    }

    @Test
    public void testfindBrandByWhere() {
        //创建查询对象
        BrandQuery brandQuery = new BrandQuery();
        //设置查询的字段名,如果不写默认是* 查询所有
        //brandQuery.setFields("id, name");
        //不设置默认是false不去重,
        brandQuery.setDistinct(true);
        //设置排序, 设置根据id降序排序
        brandQuery.setOrderByClause("id desc");

        //创建where查询条件对象
        BrandQuery.Criteria criteria = brandQuery.createCriteria();
        //查询id等于1的
        criteria.andIdEqualTo(1L);
        //根据name字段模糊查询
        criteria.andNameLike("%联%");
        //根据首字母字段模糊查询
        criteria.andFirstCharLike("%L%");

        List<Brand> brands = brandDao.selectByExample(brandQuery);
        System.out.println("=======" + brands);
    }
}
