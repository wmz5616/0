package com.zemcho.guzhe.service.sys;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.DeleteParam;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.sys.param.NoticeSaveParam;

/**
 * @author Ryan
 */
public interface NoticeService {
    /**
     * 新增文章
     *
     * @return
     */
    Result addNotices(NoticeSaveParam param);

    /**
     * 编辑文章
     *
     * @return
     */
    Result updateNotices(NoticeSaveParam param);

    /**
     * 删除文章
     *
     * @return
     */
    Result deleteNotices(DeleteParam param);

    /**
     * 获取文章详情
     *
     * @return
     */
    Result getNoticesInfo(SearchParam param);

    /**
     * 获取文章列表
     *
     * @return
     */
    Result getNoticesList(SearchParam param);

    /**
     * 设置通知公告置顶状态
     *
     * @param param
     * @return
     */
    Result setTopStatus(SearchParam param);

    Result getTopNotice();

}
