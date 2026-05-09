package com.zemcho.guzhe.service.sys.impl;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.common.param.SortSetParam;
import com.zemcho.guzhe.entity.sys.Article;
import com.zemcho.guzhe.mapper.sys.ArticleMapper;
import com.zemcho.guzhe.service.sys.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IArticleService implements ArticleService {
    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public Result add(Article data) {
        Integer maxSort = articleMapper.selectMaxSort();
        if (maxSort == null) {
            maxSort = 0;
        }
        maxSort += 1;
        data.setSort(maxSort);

        data.setStatus(0);
        data.setCreateTime(LocalDateTime.now());
        articleMapper.insert(data);

        return Result.success("操作成功");
    }

    @Override
    public Result update(Article data) {
        if (data.getId() == null) {
            return Result.error("参数错误");
        }
        articleMapper.update(data);
        return Result.success("操作成功");
    }

    @Override
    public Result select(SearchParam param) {
        List<Article> list = articleMapper.selectAll();
        return Result.success("获取成功", list);
    }

    @Override
    public Result delete(List<Integer> deleteIds) {
        articleMapper.delete(deleteIds);
        return Result.success("删除成功");
    }

    /**
     * 修改文章排序
     *
     * @param param
     * @return
     */
    @Override
    public Result setArticlesSorts(SortSetParam param) {
        List<Integer> ids = param.getIds();

        Integer sort = 1;
        for (Integer id : ids) {
            Article article = new Article();
            article.setId(id);
            article.setSort(sort);
            articleMapper.update(article);
            sort++;
        }

        return Result.success("操作成功");
    }

    /**
     * 获取文章详情
     */
    @Override
    public Result getArticleInfo(SearchParam param) {
        if (param.getSearchId() == null) {
            return Result.error("文章ID不能为空");
        }

        Article article = articleMapper.selectById(param.getSearchId());

        // 校验文章是否存在，以及是否被禁用 (status=1为禁用)
        if (article == null || article.getStatus() == 1) {
            return Result.error("该文章不存在或已被禁用");
        }

        return Result.success("获取成功", article);
    }
}
