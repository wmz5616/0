package com.zemcho.guzhe.service.cas.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.cas.param.CasUserParam;
import com.zemcho.guzhe.controller.cas.vo.CasUserVo;
import com.zemcho.guzhe.entity.cas.CasUser;
import com.zemcho.guzhe.mapper.cas.CasUserMapper;
import com.zemcho.guzhe.service.cas.UserService;
import com.zemcho.guzhe.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IUserService implements UserService {

    @Autowired
    private CasUserMapper casUserMapper;


    @Override
    public Result selectUserList(SearchParam param) {
        int pageNum = param.getPageNum();
        int pageSize = param.getPageSize();

        PageHelper.startPage(pageNum, pageSize);
        List<CasUserVo> list = casUserMapper.selectLists(param);
        PageInfo<CasUserVo> pageInfo = new PageInfo<>(list);

        return Result.success("获取成功", pageInfo);
    }


    @Override
    public List<CasUserVo> selectByIds(SearchParam param) {
        return casUserMapper.selectLists(param);
    }

    @Override
    public Result updateStatus(CasUserParam param) {
        if (param.getId() == null) {
            return Result.error("用户 ID 不能为空");
        }
        if (param.getLock() == null) {
            return Result.error("状态不能为空");
        }

        CasUser user = new CasUser();
        user.setId(param.getId());

        if (param.getLock() == 0) {
            // 1. 状态选“正常”：清空所有锁定相关信息
            user.setLock(0);
            user.setLockReason("");
            user.setLockExpiredAt(null);
        } else {
            // 2. 状态选“锁定”：校验锁定原因必填
            if (StringUtil.isBlank(param.getLockReason())) {
                return Result.error("锁定原因不能为空");
            }
            user.setLockReason(param.getLockReason());

            // 3. 根据“锁定时效”判断是有限还是永久
            if (param.getLockStatus() != null && param.getLockStatus() == 1) {
                // 情况 A：有限时间锁定 -> 对应数据库 lock=1
                if (param.getLockExpiredAt() == null) {
                    return Result.error("请选择解锁时间");
                }
                // 校验时间有效性
                if (param.getLockExpiredAt().isBefore(LocalDateTime.now())) {
                    return Result.error("解锁时间必须晚于当前时间");
                }
                user.setLock(1);
                user.setLockExpiredAt(param.getLockExpiredAt());
            } else {
                // 情况 B：永久锁定 -> 对应数据库 lock=2
                user.setLock(2);
                user.setLockExpiredAt(null);
            }
        }

        // 4. 处理备注（可选）
        user.setRemark(param.getRemark());

        // 5. 执行更新
        int rows = casUserMapper.update(user);
        return rows > 0 ? Result.success("操作成功") : Result.error("操作失败");

    }
}
