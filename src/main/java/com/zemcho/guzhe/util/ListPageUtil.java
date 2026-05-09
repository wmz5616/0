package com.zemcho.guzhe.util;

import java.util.Collections;
import java.util.List;

/**
 * @author Ryan
 * @title: ListPageUtil
 * @projectName master
 * @description: ZEMCHO
 * @date 2020/9/22 0022 10:25
 */
public class ListPageUtil {
    public static List getPagedList(List data, int pageNum, int pageSize) {
        int fromIndex = (pageNum - 1) * pageSize;
        if (fromIndex >= data.size()) {
            return Collections.emptyList();
        }

        int toIndex = pageNum * pageSize;
        if (toIndex >= data.size()) {
            toIndex = data.size();
        }
        return data.subList(fromIndex, toIndex);
    }
    

}
