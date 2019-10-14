package cn.itcast.core.controller;

import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.service.TemplateService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 模板管理
 */
@RestController
@RequestMapping("/typeTemplate")
public class TemplateController {

    @Reference
    private TemplateService templateService;

    @RequestMapping("/findOne")
    public TypeTemplate findOne(Long id) {
        return templateService.findOne(id);
    }

    /**
     * 根据模板id, 查询规格集合和对应的规格选项集合数据
     * 数据例如: [{"id":27,"text":"网络","options":
     *                              [{"id":1, option_name:3G},{"id":2, option_name:4G}]},
     *          {"id":32,"text":"机身内存","options":
     *                              [{"id":3, option_name:128G},{"id":4, option_name:256G}]}
     *          ]
     * 这里的options不是随便取的，而是页面上<span ng-repeat="option in pojo.options">定义的
     *
     * 返回的数据结构如上，有两种处理方式：
     * 一、封装成实体类，如之前已经封装过的SpecEntity，但是里面的规格选项不叫options，而是specificationOptionList，只能再封装一个新的实体类，比较麻烦
     * 二、返回Map
     * @param id
     */
    @RequestMapping("/findBySpecList")
    public List<Map> findBySpecList(Long id) {
        List<Map> list = templateService.findBySpecList(id);
        return list;
    }
}
