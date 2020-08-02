package cn.itcast.core.controller;

import cn.itcast.core.service.SearchService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/itemsearch")
public class SearchController {

    @Reference
    private SearchService searchService;

    /**
     * 返回的数据有查询到的集合, 当前页, 每页展示多少条数据, 总记录数, 总页数
     */
    @RequestMapping("/search")
    public Map<String, Object> search(@RequestBody  Map paramMap) {
        Map<String, Object> resultMap = searchService.search(paramMap);
        return resultMap;
    }
}
