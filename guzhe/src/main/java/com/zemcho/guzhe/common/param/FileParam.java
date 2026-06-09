package com.zemcho.guzhe.common.param;

import lombok.Data;

/**
 * @author Ryan
 * @title: FileParam
 * @projectName master
 * @description: ZEMCHO
 * @date 2020/10/19 0019 17:33
 */
@Data
public class FileParam {
    private String fileId;

    private int firstRow = 1;
    
    private int lastRow = 0;
}