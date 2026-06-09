package com.zemcho.ddql.mapper.personalCenter;

import com.zemcho.ddql.entity.personalCenter.MessageAnnouncement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MessageAnnouncementMapper {

    int insert(@Param("data") MessageAnnouncement data);

    int update(@Param("data") MessageAnnouncement data);

    int read(@Param("id") Integer id);

    int readAll(@Param("userId") Integer userId);

    @Select("SELECT * FROM message_announcement WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<MessageAnnouncement> select(@Param("userId") Integer userId);

}
