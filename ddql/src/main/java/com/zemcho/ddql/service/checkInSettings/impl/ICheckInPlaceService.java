package com.zemcho.ddql.service.checkInSettings.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.ChangeParam;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.config.jwt.JWTUtil;
import com.zemcho.ddql.controller.cas.vo.CasUserVo;
import com.zemcho.ddql.controller.checkInSettings.vo.CheckInPlaceVo;
import com.zemcho.ddql.controller.checkInSettings.param.CheckInPlaceParam;
import com.zemcho.ddql.entity.checkInSettings.CheckInPlace;
import com.zemcho.ddql.entity.checkInSettings.CheckInPlaceUser;
import com.zemcho.ddql.mapper.cas.CasUserMapper;
import com.zemcho.ddql.mapper.checkInSettings.CheckInPlaceMapper;
import com.zemcho.ddql.mapper.checkInSettings.CheckInPlaceUserMapper;
import com.zemcho.ddql.mapper.checkInSettings.CheckInTypeMapper;
import com.zemcho.ddql.service.checkInSettings.CheckInPlaceService;
import com.zemcho.ddql.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ICheckInPlaceService implements CheckInPlaceService {

    @Autowired
    private CheckInPlaceMapper checkInPlaceMapper;

    @Autowired
    private CheckInPlaceUserMapper checkInPlaceUserMapper;

    @Autowired
    private CheckInTypeMapper checkInTypeMapper;

    @Autowired
    private CasUserMapper casUserMapper;

    @Override
    @Transactional
    public Result add(CheckInPlaceParam param) {
        // 检查参数
        if (checkInPlaceMapper.ifExistsByName(param.getName(), 0)) {
            return Result.error("场所名称已存在");
        }
        if (param.getName() == null || param.getAddress() == null
                || param.getCheckInDistance() == null || param.getCheckInTypeId() == null
                || param.getContactPhone() == null) {
            return Result.error("参数错误");
        }
        if (param.getUserIds() == null) {
            return Result.error("参数错误");
        }
        if (param.getCheckInMethod() == null
                || (param.getCheckInMethod() != 0 && param.getCheckInMethod() != 1)) {
            return Result.error("参数错误");
        }
        if (!checkInTypeMapper.ifExistById(param.getCheckInTypeId())) {
            return Result.error("签到类型不存在");
        }
        CheckInPlace checkInPlace = new CheckInPlace();
        checkInPlace.setName(param.getName());
        checkInPlace.setAddress(param.getAddress());
        checkInPlace.setLocation(param.getLocation());
        checkInPlace.setCheckInMethod(param.getCheckInMethod());
        checkInPlace.setCheckInDistance(param.getCheckInDistance());
        checkInPlace.setCheckInTypeId(param.getCheckInTypeId());
        checkInPlace.setContactPhone(param.getContactPhone());
        // 默认启用
        checkInPlace.setStatus(0);
        checkInPlace.setSort(param.getSort());
        checkInPlace.setIntroduction(param.getIntroduction());
        checkInPlace.setCreateTime(LocalDateTime.now());
        if (param.getImages() != null && !param.getImages().isEmpty()) {
            // 转为以;分隔的字符串
            String images = String.join(";", param.getImages());
            checkInPlace.setImages(images);
        }
        checkInPlace.setRemark(param.getRemark());
        checkInPlaceMapper.insert(checkInPlace);

        // 插入关联关系
        Integer placeId = checkInPlace.getId();
        List<CheckInPlaceUser> list = new ArrayList<>();
        for (Integer userId : param.getUserIds()) {
            CheckInPlaceUser checkInPlaceUser = new CheckInPlaceUser();
            checkInPlaceUser.setUserId(userId);
            checkInPlaceUser.setPlaceId(placeId);
            list.add(checkInPlaceUser);
        }
        checkInPlaceUserMapper.insert(list);
        return Result.success("添加成功");
    }

    @Override
    @Transactional
    public Result update(CheckInPlaceParam param) {
        if (param.getId() == null) {
            return Result.error("id不能为空");
        }
        if (!checkInPlaceMapper.ifExistsById(param.getId())) {
            return Result.error("场所不存在");
        }
        if (checkInPlaceMapper.ifExistsByName(param.getName(), param.getId())) {
            return Result.error("场所名称已存在");
        }
        CheckInPlace checkInPlace = new CheckInPlace();
        checkInPlace.setId(param.getId());
        checkInPlace.setName(param.getName());
        checkInPlace.setAddress(param.getAddress());
        checkInPlace.setLocation(param.getLocation());
        checkInPlace.setCheckInMethod(param.getCheckInMethod());
        checkInPlace.setCheckInDistance(param.getCheckInDistance());
        checkInPlace.setCheckInTypeId(param.getCheckInTypeId());
        checkInPlace.setContactPhone(param.getContactPhone());
        checkInPlace.setStatus(param.getStatus());
        checkInPlace.setSort(param.getSort());
        checkInPlace.setIntroduction(param.getIntroduction());
        if (param.getImages() != null && !param.getImages().isEmpty()) {
            // 转为以;分隔的字符串
            String images = String.join(";", param.getImages());
            checkInPlace.setImages(images);
        }
        checkInPlace.setRemark(param.getRemark());
        checkInPlaceMapper.update(checkInPlace);
        if (param.getUserIds() != null) {
            // 先删除原有关联关系
            checkInPlaceUserMapper.deleteByPlaceId(param.getId());
            // 再插入新的管理关系
            Integer placeId = checkInPlace.getId();
            List<CheckInPlaceUser> list = new ArrayList<>();
            for (Integer userId : param.getUserIds()) {
                CheckInPlaceUser checkInPlaceUser = new CheckInPlaceUser();
                checkInPlaceUser.setUserId(userId);
                checkInPlaceUser.setPlaceId(placeId);
                list.add(checkInPlaceUser);
            }
            checkInPlaceUserMapper.insert(list);
        }
        return Result.success("修改成功");
    }

    @Override
    public Result select(SearchParam param) {
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        List<CheckInPlaceVo> list = checkInPlaceMapper.select(param);
        PageInfo<CheckInPlaceVo> pageInfo = new PageInfo<>(list);
        return Result.success("获取成功", pageInfo);
    }

    @Override
    public Result selectUserByPlaceId(Integer placeId) {
        List<CheckInPlaceUser> list = checkInPlaceUserMapper.select(placeId, null);
        List<Integer> userIds = list.stream().map(CheckInPlaceUser::getUserId).toList();
        List<CasUserVo> userList = casUserMapper.selectByIds(userIds);
        return Result.success("获取成功", userList);
    }

    @Override
    public Result delete(List<Integer> deleteIds) {
        checkInPlaceMapper.delete(deleteIds);
        return Result.success("删除成功");
    }

    /**
     * 编辑打卡地点状态
     *
     * @param param
     * @return
     */
    @Override
    public Result setStatus(ChangeParam param) {
        List<Integer> ids = new ArrayList<>(param.getChangeIds());

        checkInPlaceMapper.updateStatusByIds(ids, param.getStatus());

        return Result.success("操作成功");
    }

    @Override
    public Result selectUserPlace(String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return Result.error("用户不存在");
        }
        List<CheckInPlaceUser> checkInPlaceUsers = checkInPlaceUserMapper.select(null, userId);
        List<Integer> list = checkInPlaceUsers.stream().map(CheckInPlaceUser::getPlaceId).toList();
        List<CheckInPlaceVo> checkInPlaces = new ArrayList<>();
        if (!list.isEmpty()) {
            checkInPlaces = checkInPlaceMapper.selectByIds(list);
        }
        return Result.success("获取成功", checkInPlaces);
    }

    @Override
    @Transactional
    public Result update(CheckInPlaceParam param, String token) {
        if (param.getId() == null) {
            return Result.error("id不能为空");
        }
        if (!checkInPlaceMapper.ifExistsById(param.getId())) {
            return Result.error("场所不存在");
        }
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return Result.error("用户不存在");
        }
        if (checkInPlaceMapper.ifExistsByName(param.getName(), param.getId())) {
            return Result.error("场所名称已存在");
        }
        if (checkInPlaceUserMapper.select(param.getId(), userId) == null) {
            return Result.error("您没有权限修改此场所");
        }
        CheckInPlace checkInPlace = new CheckInPlace();
        checkInPlace.setId(param.getId());
        checkInPlace.setName(param.getName());
        if (param.getImages() != null && !param.getImages().isEmpty()) {
            // 转为以;分隔的字符串
            String images = String.join(";", param.getImages());
            checkInPlace.setImages(images);
        }
        checkInPlace.setRemark(param.getRemark());
        checkInPlace.setIntroduction(param.getIntroduction());
        checkInPlaceMapper.update(checkInPlace);
        return Result.success("修改成功");
    }
}
