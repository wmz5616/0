package com.zemcho.guzhe.service.sys;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.common.param.SortSetParam;
import com.zemcho.guzhe.entity.sys.Article;

import java.util.List;

public interface ArticleService {

    Result add(Article data);

    Result update(Article data);

    Result select(SearchParam param);

    Result delete(List<Integer> deleteIds);

    /**
     * 修改文章排序
     *
     * @param param
     * @return
     */
    Result setArticlesSorts(SortSetParam param);
}
