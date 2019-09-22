package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.entity.SpecEntity;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.service.SpecificationService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 规格管理
 */
@RestController
@RequestMapping("/specification")
public class SpecController {

    @Reference
    private SpecificationService specService;

    /**
     * 规格高级分页查询
     * @param spec
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody Specification spec, Integer page, Integer rows) {
        PageResult result = specService.findPage(spec, page, rows);
        return result;
    }

    /**
     * 规格添加
     * @param specEntity
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody  SpecEntity specEntity) {
        try {
            specService.add(specEntity);
            return new Result(true, "保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败!");
        }
    }

    /**
     * 规格数据回显
     * @param id 规格id
     * @return
     */
    @RequestMapping("/findOne")
    public SpecEntity findOne(Long id) {
        SpecEntity one = specService.findOne(id);
        return one;
    }

    /**
     * 更新保存
     * @param specEntity
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody SpecEntity specEntity) {
        try {
            specService.update(specEntity);
            return new Result(true, "修改成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败!");
        }
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public  Result delete(Long[] ids) {
        try {
            specService.delete(ids);
            return new Result(true,"删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败!");
        }
    }

    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList() {
        return specService.selectOptionList();
    }
}
