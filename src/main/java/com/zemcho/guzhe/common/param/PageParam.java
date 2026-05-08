package com.zemcho.guzhe.common.param;

import lombok.Data;

import jakarta.validation.constraints.Min;

/**
 * @author Ryan
 * @title: PageParam
 * @projectName master
 * @description: ZEMCHO
 * @date 2020/9/15 0015 14:39
 */
@Data
public class PageParam {
    /** 最小页码 */
    public final static int MIN_PAGE_NUM = 1;

    /** 最小分页大小 */
    public final static int MIN_PAGE_SIZE = 5;

    @Min(value = 1)
    private int pageNum = 1;

    @Min(value = 5)
    private int pageSize = 10;
}
