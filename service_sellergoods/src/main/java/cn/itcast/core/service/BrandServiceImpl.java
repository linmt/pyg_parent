package cn.itcast.core.service;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.good.Brand;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandDao brandDao;

    public List<Brand> findAll() {
        List<Brand> brands = brandDao.selectByExample(null);
        return brands;
    }

    public PageResult findPage(Integer page, Integer rows) {
        //利用分页助手实现分页, 第一个参数:当前页, 第二个参数: 每页展示数据条数
        PageHelper.startPage(page, rows);
        Page<Brand> brandList = (Page<Brand>) brandDao.selectByExample(null);
        return new PageResult(brandList.getTotal(), brandList.getResult());
    }

    public void add(Brand brand) {
        //插入数据, 插入的时候不会判断传入对象中的属性是否为空, 所有字段都参与拼接sql语句执行效率低
        //brandDao.insert();
        //插入数据, 插入的时候会判断传入对象中的属性是否为空, 如果为空, 不参与拼接sql语句, sql语句会变短执行效率会提高.
        brandDao.insertSelective(brand);
    }

    public Brand findOne(Long id) {
        return brandDao.selectByPrimaryKey(id);
    }

    public void update(Brand brand) {
        //根据主键作为条件修改, 这个方法带selective说明传入的对象会进行判断, 如果传入的对象属性为null则不会拼接到sql语句中
        brandDao.updateByPrimaryKeySelective(brand);
        //根据组件作为条件修改, 这个方法不带selective, 所以传入对象中的属性如果为null也会参与拼接sql语句, 修改完如果有属性为null则数据库中的值也会被修改为null
        //brandDao.updateByPrimaryKey(brand);
        //根据非主键条件修改, 第一个参数传入需要修改的对象, 第二个参数传入修改的条件对象. 不带selective说明传入的对象属性不管是否为null都会参与修改
        //brandDao.updateByExample(, );
        //根据非主键条件修改, 第一个参数传入需要修改的对象, 第二个参数传入修改的条件对象. 带selective说明如果传入的修改对象中的属性为null不参与修改.
        //brandDao.updateByExampleSelective(, )
    }

    public void delete(Long[] ids) {
        if (ids != null) {
            for (Long id : ids) {
                brandDao.deleteByPrimaryKey(id);
            }
        }
    }
}
