package com.zemcho.guzhe.mapper.sys;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.entity.sys.Notice;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * @author Ryan
 */
public interface NoticeMapper {
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Integer insert(@Param("data") Notice data);

    Notice selectById(@Param("id") Integer id);

    Integer update(@Param("data") Notice data);

    Integer deletes(@Param("ids") Collection<Integer> ids);

    List<Notice> selectList(@Param("param") SearchParam param);

    @Select("SELECT id, status FROM sys_notice WHERE status = 1 AND publish_time <= #{currentTime} order by id desc")
    List<Notice> selectUnpublishedArticles(@Param("currentTime") LocalDateTime currentTime);

    /**
     * 批量更新置顶状态
     *
     * @param ids
     * @param isTop
     * @return
     */
    Integer updateTopStatusByIds(@Param("ids") Collection<Integer> ids, @Param("isTop") Integer isTop);

    List<Notice> selectTopNotice();
}
