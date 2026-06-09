package com.zemcho.ddql.controller.wechat.personalCenter;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.checkInSettings.param.CheckInPlaceParam;
import com.zemcho.ddql.entity.personalCenter.MessageAnnouncement;
import com.zemcho.ddql.entity.team.TeamCheckInSettings;
import com.zemcho.ddql.service.checkInSettings.CheckInPlaceService;
import com.zemcho.ddql.service.equipment.EquipmentService;
import com.zemcho.ddql.service.personalCenter.MessageAnnouncementService;
import com.zemcho.ddql.service.team.TeamCheckInSettingsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wechat/personalCenter")
public class WechatPersonalCenterController {

    @Autowired
    private TeamCheckInSettingsService teamCheckInSettingsService;

    @Autowired
    private MessageAnnouncementService messageAnnouncementService;

    @Autowired
    private CheckInPlaceService checkInPlaceService;

    @Autowired
    private EquipmentService equipmentService;

    /**
     * 更新团队打卡设置
     *
     * @param data
     * @param result
     * @return
     */
    @RequestMapping("/teamCheckInSettings/update")
    public Result updateTeamCheckInSettings(@Valid @RequestBody TeamCheckInSettings data, BindingResult result,
                                            @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return teamCheckInSettingsService.update(data, token);
    }

    /**
     * 查询团队打卡设置
     *
     * @param param
     * @return
     */
    @RequestMapping("/teamCheckInSettings/select")
    public Result selectTeamCheckInSettings(@Valid @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return teamCheckInSettingsService.select(param);
    }


    /**
     * 查询用户消息公告
     *
     * @return
     */
    @RequestMapping("/messageAnnouncement/select")
    public Result selectUserMessage(@RequestHeader("token") String token) {
        return messageAnnouncementService.selectUserMessage(token);
    }


    /**
     * 读取消息
     *
     * @param data
     * @param result
     * @return
     */
    @RequestMapping("/messageAnnouncement/read")
    public Result readMessage(@Valid @RequestBody MessageAnnouncement data, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return messageAnnouncementService.readMessage(data.getId());
    }

    /**
     * 一键已读所有信息
     *
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/messageAnnouncement/readAll")
    public Result readAllMessage(BindingResult result,
                                 @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return messageAnnouncementService.readAllMessage(token);
    }

    /**
     * 获取用户关联的场所信息
     *
     * @param data
     * @param result
     * @return
     */
    @RequestMapping("/checkInPlace/selectUserPlace")
    public Result selectUserPlace(@Valid @RequestBody SearchParam data, BindingResult result,
                                  @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return checkInPlaceService.selectUserPlace(token);
    }

    /**
     * 更新场所信息 id name images remark
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/checkInPlace/update")
    public Result updateCheckInPlace(@Valid @RequestBody CheckInPlaceParam param, BindingResult result,
                                     @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return checkInPlaceService.update(param, token);
    }

    /**
     * 获取场所下的设备信息
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/place/equipment")
    public Result getEquipmentByPlace(@Valid @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return equipmentService.getEquipmentByPlace(param);
    }
}
