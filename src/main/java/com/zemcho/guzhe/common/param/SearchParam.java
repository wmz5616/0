package com.zemcho.guzhe.common.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zemcho.guzhe.controller.wechat.shop.param.ShopAuditHandleParam;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Ryan
 * @title: ClassificationSearchParam
 * @projectName master
 * @description: ZEMCHO
 * @date 2020/9/15 0015 16:17
 */
@Data
public class SearchParam extends PageParam {
    private Integer searchId;

    private List<Integer> searchIds;

    private List<Integer> filterIds;

    private String keyword;

    private Integer searchType;

    private List<Integer> searchTypeList;

    private Boolean searchStatus;

    private Integer searchIntStatus;

    private List<Integer> searchStatusList;

    private Integer searchField1;

    private Integer searchField2;

    private Integer searchField3;

    private Integer searchField4;

    private Integer searchField5;

    private String searchStrField1;

    private String searchStrField2;

    private String searchStrField3;

    private Integer startNum;

    private Integer endNum;

    private String orderField;

    private String orderType;

    private Boolean isReturnPermsData = false;

    private List<Integer> searchIntList;

    private List<String> searchStrList;

    private Integer bannerType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime2;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime2;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime limitTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate searchDate;
}
