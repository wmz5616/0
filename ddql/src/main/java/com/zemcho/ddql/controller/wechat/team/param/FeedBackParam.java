package com.zemcho.ddql.controller.wechat.team.param;

import lombok.Data;

/**
 * @author HXH
 */
@Data
public class FeedBackParam {
    /**
     * 团体ID
     */
    private Integer teamId;

    /**
     * 反馈内容（不超过50字）
     */
    private String content;

    /**
     * 是否匿名：0-否，1-是
     */
    private Integer isAnonymous;
}
