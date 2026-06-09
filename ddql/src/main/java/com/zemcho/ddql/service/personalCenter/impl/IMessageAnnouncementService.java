package com.zemcho.ddql.service.personalCenter.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.config.jwt.JWTUtil;
import com.zemcho.ddql.entity.personalCenter.MessageAnnouncement;
import com.zemcho.ddql.mapper.personalCenter.MessageAnnouncementMapper;
import com.zemcho.ddql.service.personalCenter.MessageAnnouncementService;
import com.zemcho.ddql.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IMessageAnnouncementService implements MessageAnnouncementService {

    @Autowired
    private MessageAnnouncementMapper messageAnnouncementMapper;

    @Override
    public void insert(Integer userId, String title, String content) {
        MessageAnnouncement data = new MessageAnnouncement(null, userId, title, content, 0, LocalDateTime.now(), null);
        messageAnnouncementMapper.insert(data);
    }

    @Override
    public Result selectUserMessage(String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }
        List<MessageAnnouncement> list = messageAnnouncementMapper.select(userId);

        return Result.success("获取成功", list);
    }

    @Override
    public Result readMessage(Integer id) {
        messageAnnouncementMapper.read(id);
        return Result.success("操作成功");
    }

    @Override
    public Result readAllMessage(String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }
        messageAnnouncementMapper.readAll(userId);
        return Result.success("操作成功");
    }
}
