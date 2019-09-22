package cn.itcast.core.service;

import cn.itcast.core.dao.template.TypeTemplateDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.pojo.template.TypeTemplateQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TemplateServiceImpl implements TemplateService {

    @Autowired
    private TypeTemplateDao templateDao;

    @Override
    public PageResult findPage(TypeTemplate template, Integer page, Integer rows) {
        PageHelper.startPage(page, rows);
        TypeTemplateQuery query = new TypeTemplateQuery();
        TypeTemplateQuery.Criteria criteria = query.createCriteria();
        if (template != null) {
            if (template.getName() != null && !"".equals(template.getName())) {
                criteria.andNameLike("%"+template.getName()+"%");
            }
        }
        Page<TypeTemplate> templateList = (Page<TypeTemplate>)templateDao.selectByExample(query);
        return new PageResult(templateList.getTotal(), templateList.getResult());
    }

    @Override
    public void add(TypeTemplate template) {
        templateDao.insertSelective(template);
    }

    @Override
    public TypeTemplate findOne(Long id) {
        return templateDao.selectByPrimaryKey(id);
    }

    @Override
    public void update(TypeTemplate template) {
        templateDao.updateByPrimaryKeySelective(template);
    }

    @Override
    public void delete(Long[] ids) {
        if (ids != null) {
            for (Long id : ids) {
                templateDao.deleteByPrimaryKey(id);
            }
        }
    }
}
