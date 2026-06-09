package com.zemcho.guzhe.controller.sys;

import com.zemcho.guzhe.aspect.log.Log;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.dto.AuthAttrData;
import com.zemcho.guzhe.common.param.DeleteParam;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.common.param.SortSetParam;
import com.zemcho.guzhe.entity.sys.Article;
import com.zemcho.guzhe.service.sys.ArticleService;
import com.zemcho.guzhe.util.Constant;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 新增文章
     * @param data
     * @param result
     * @param authAttrData
     * @return
     */
    @RequestMapping("/add")
    @Log(description = "新增文章", module = "文章管理")
    public Result add(@Valid @RequestBody Article data, BindingResult result,
                      @RequestAttribute(Constant.REQUEST_ATTR_DATA) AuthAttrData authAttrData) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return articleService.add(data);
    }

    /**
     * 修改文章
     * @param data
     * @param result
     * @param authAttrData
     * @return
     */
    @RequestMapping("/update")
    @Log(description = "修改文章", module = "文章管理")
    public Result update(@Valid @RequestBody Article data, BindingResult result,
                        @RequestAttribute(Constant.REQUEST_ATTR_DATA) AuthAttrData authAttrData) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return articleService.update(data);
    }

    /**
     * 查询文章
     * @param param
     * @param result
     * @param authAttrData
     * @return
     */
    @RequestMapping("/select")
    public Result select(@Valid @RequestBody SearchParam param, BindingResult result,
                         @RequestAttribute(Constant.REQUEST_ATTR_DATA) AuthAttrData authAttrData) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return articleService.select(param);
    }

    /**
     * 删除文章
     * @param param
     * @param result
     * @param authAttrData
     * @return
     */
    @RequestMapping("/delete")
    @Log(description = "删除文章", module = "文章管理")
    public Result delete(@Valid @RequestBody DeleteParam param, BindingResult result,
                         @RequestAttribute(Constant.REQUEST_ATTR_DATA) AuthAttrData authAttrData) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return articleService.delete(new ArrayList<>(param.getDeleteIds()));
    }

    /**
     * 更改文章展示顺序
     *
     * @param param
     * @param result
     * @return
     */
    @Log(description = "更改文章展示顺序", module = "文章管理")
    @RequestMapping("/sort/set")
    public Result setArticlesSorts(@Validated @RequestBody SortSetParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return articleService.setArticlesSorts(param);
    }
}
