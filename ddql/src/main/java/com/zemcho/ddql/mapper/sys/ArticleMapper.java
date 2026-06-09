package com.zemcho.ddql.mapper.sys;

import com.zemcho.ddql.entity.sys.Article;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ArticleMapper {
    /**
     * 查询最大排序
     *
     * @return
     */
    Integer selectMaxSort();

    int insert(@Param("data") Article data);

    int update(@Param("data") Article data);

    @Select("select * from sys_article ORDER BY sort asc, id desc")
    List<Article> selectAll();

    int delete(@Param("deleteIds") List<Integer> deleteIds);

    /**
     * 获取用户端--文章列表
     *
     * @return
     */
    List<Article> selectWechatList();
}
