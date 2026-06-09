package com.zemcho.guzhe.service.cas;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.dto.AuthAttrData;
import com.zemcho.guzhe.common.param.ChangeParam;
import com.zemcho.guzhe.common.param.DeleteParam;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.cas.param.AdminSaveParam;

public interface AdminService {
    /**
     * 新增管理员
     *
     * @param param
     * @param authAttrData
     * @return
     */
    Result addAdmin(AdminSaveParam param, AuthAttrData authAttrData);

    /**
     * 编辑管理员
     *
     * @param param
     * @param authAttrData
     * @param token
     * @return
     */
    Result updateAdmin(AdminSaveParam param, AuthAttrData authAttrData, String token);

    /**
     * 获取管理员列表
     *
     * @param param
     * @param authAttrData
     * @return
     */
    Result getAdminLists(SearchParam param, AuthAttrData authAttrData);

    /**
     * 编辑管理员状态
     *
     * @param param
     * @param authAttrData
     * @return
     */
    Result setStatus(ChangeParam param, AuthAttrData authAttrData);

    /**
     * 删除管理员
     *
     * @param param
     * @param authAttrData
     * @return
     */
    Result deleteAdmin(DeleteParam param, AuthAttrData authAttrData);



}
