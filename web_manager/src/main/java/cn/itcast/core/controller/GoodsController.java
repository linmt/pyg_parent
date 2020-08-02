package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.GoodsEntity;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.service.GoodsService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品管理
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

//    @Reference
//    private SolrManagerService solrManagerService;
//
//    @Reference
//    private CmsService cmsService;

    @RequestMapping("/search")
    public PageResult search(@RequestBody Goods goods, Integer page , Integer rows) {
        //获取当前登录用户的用户名
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(userName);
        PageResult result = goodsService.findPage(goods, page, rows);
        return result;
    }

    @RequestMapping("/findOne")
    public GoodsEntity findOne(Long id) {
        return goodsService.findOne(id);
    }

    @RequestMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            if (ids != null) {
                for (Long id : ids) {
                    //1. 根据商品id到数据库中删除
                    goodsService.delete(id);
                    //2. 根据商品id到solr索引库中删除对应的数据
//                    solrManagerService.deleteItemFromSolr(id);
                }
            }
            return  new Result(true, "删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return  new Result(false, "删除失败!");
        }
    }

    /**
     * 修改商品状态
     * @param ids       需要修改的商品id数组
     * @param status    状态码, 由页面传入
     * @return
     */
    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids, String status) {
        try {
            if (ids != null) {
                for (Long id : ids) {
                    //1. 到数据库中根据商品id改变商品的上架状态
                    goodsService.updateStatus(id, status);
                    //2. 对于审核通过的商品
//                    if ("1".equals(status)) {
//                        //3. 将根据商品id获取库存数据, 放入solr索引库供搜索使用
//                        solrManagerService.saveItemToSolr(id);
//                        //4. 根据商品id获取商品的详细数据, 并且根据数据和模板生成商品详情页面
//                        Map<String, Object> goodsData = cmsService.findGoodsData(id);
//                        cmsService.createStaticPage(id, goodsData);
//                    }
                }
            }

            return new Result(true, "状态修改成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "状态修改失败!");
        }
    }

    /**
     * 测试生成静态页面
     * @param goodsId   商品id
     * @return
     */
//    @RequestMapping("/testPage")
//    public Boolean testCreatePage(Long goodsId) {
//        try {
//            Map<String, Object> goodsData = cmsService.findGoodsData(goodsId);
//            cmsService.createStaticPage(goodsId, goodsData);
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
}
