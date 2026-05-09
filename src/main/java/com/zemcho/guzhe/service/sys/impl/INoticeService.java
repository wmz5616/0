package com.zemcho.guzhe.service.sys.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.DeleteParam;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.sys.param.NoticeSaveParam;
import com.zemcho.guzhe.entity.sys.Notice;
import com.zemcho.guzhe.mapper.sys.NoticeMapper;
import com.zemcho.guzhe.service.sys.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * @author Ryan
 */
@Service
@Transactional
public class INoticeService implements NoticeService {
    @Autowired
    private NoticeMapper noticesMapper;

    /**
     * 新增文章
     *
     * @return
     */
    @Override
    public Result addNotices(NoticeSaveParam param) {
        Boolean isPublish = param.getIsPublish();

        LocalDateTime publishTime = param.getPublishTime();
        Integer status = 0;

        LocalDateTime now = LocalDateTime.now();
        if (!isPublish && param.getPublishTime() == null) {
            status = 1;
        } else if (isPublish) {
            status = 2;
        }

        Notice article = new Notice();
        article.setTitle(param.getTitle());
        article.setContent(param.getContent());
        article.setType(param.getType() != null ? param.getType() : 1);
        article.setIsPublish(isPublish);
        article.setPublishTime(publishTime);
        article.setStatus(status);
        article.setCreateTime(now);

        noticesMapper.insert(article);

        return Result.success("操作成功");
    }

    /**
     * 编辑文章
     *
     * @return
     */
    @Override
    public Result updateNotices(NoticeSaveParam param) {
        Integer id = param.getId();
        if (id == null) {
            return Result.error("id不能为空");
        }
        Notice article = noticesMapper.selectById(id);
        if (article == null) {
            return Result.error("该文章不存在");
        }

        // 编辑
        Boolean isPublish = param.getIsPublish();
        LocalDateTime publishTime = param.getPublishTime();

        Integer status = 0;

        LocalDateTime now = LocalDateTime.now();
        if (!isPublish && param.getPublishTime() != null) {
            status = 1;
        } else if (isPublish) {
            status = 2;
        }
        article.setTitle(param.getTitle());
        article.setContent(param.getContent());
        if (param.getType() != null) {
            article.setType(param.getType());
        }
        article.setIsPublish(isPublish);
        article.setPublishTime(publishTime);
        article.setStatus(status);
        noticesMapper.update(article);

        return Result.success("操作成功");
    }

    /**
     * 删除文章
     *
     * @return
     */
    @Override
    public Result deleteNotices(DeleteParam param) {
        Set<Integer> ids = param.getDeleteIds();

        if (ids != null && ids.size() > 0) {
            noticesMapper.deletes(ids);
        }

        return Result.success("操作成功");
    }


    /**
     * 获取文章详情
     *
     * @return
     */
    @Override
    public Result getNoticesInfo(SearchParam param) {
        Integer articlesId = param.getSearchId();
        if (articlesId == null) {
            return Result.error("id不能为空");
        }
        Notice existArticles = noticesMapper.selectById(articlesId);

        return Result.success("获取成功", existArticles);
    }

    /**
     * 获取文章列表
     *
     * @return
     */
    @Override
    public Result getNoticesList(SearchParam param) {
        int pageNum = param.getPageNum();
        int pageSize = param.getPageSize();

        PageHelper.startPage(pageNum, pageSize);
        List<Notice> articlesList = noticesMapper.selectList(param);
        PageInfo<Notice> pageInfo = new PageInfo<>(articlesList);

        return Result.success("获取成功", pageInfo);
    }

    /**
     * 设置通知公告置顶状态
     *
     * @param param
     * @return
     */
    @Override
    public Result setTopStatus(SearchParam param) {
        List<Integer> ids = param.getSearchIds();
        if (ids == null || ids.isEmpty()) {
            return Result.error("参数异常");
        }

        Integer isTop = param.getSearchIntStatus();
        if (isTop == null) {
            return Result.error("参数异常!");
        }

        noticesMapper.updateTopStatusByIds(ids, isTop);

        return Result.success("操作成功");
    }

    // 定时任务方法
    @Scheduled(fixedRate = 300000) // 每5分钟执行一次
    public void updateArticleStatus() {
        // 获取所有状态为1且发布时间小于等于当前时间的文章
        List<Notice> articles = noticesMapper.selectUnpublishedArticles(LocalDateTime.now());

        for (Notice article : articles) {
            Notice newArticle = new Notice();
            newArticle.setId(article.getId());
            newArticle.setStatus(2); // 已发布
            noticesMapper.update(newArticle);
        }
    }

    //获取置顶公告
    @Override
    public Result getTopNotice() {
        List<Notice> notices = noticesMapper.selectTopNotice();
        return Result.success("获取成功", notices);
    }
}