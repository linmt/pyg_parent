package cn.itcast.core.pojo.entity;

import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojo.specification.SpecificationOption;

import java.io.Serializable;
import java.util.List;

/**
 * 自定义封装的规格和规格选项集合实体对象
 */
public class SpecEntity implements Serializable{

    //规格对象
    //必须和页面对应<td><input class="form-control" placeholder="规格名称" ng-model="entity.specification.specName">
    private Specification specification;
    //规格选项集合
    //<tr ng-repeat="pojo in entity.specificationOptionList">
    private List<SpecificationOption> specificationOptionList;

    public Specification getSpecification() {
        return specification;
    }

    public void setSpecification(Specification specification) {
        this.specification = specification;
    }

    public List<SpecificationOption> getSpecificationOptionList() {
        return specificationOptionList;
    }

    public void setSpecificationOptionList(List<SpecificationOption> specificationOptionList) {
        this.specificationOptionList = specificationOptionList;
    }
}
