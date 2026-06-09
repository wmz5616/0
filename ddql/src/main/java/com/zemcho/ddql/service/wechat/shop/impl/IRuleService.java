package com.zemcho.ddql.service.wechat.shop.impl;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.DeleteParam;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.config.jwt.JWTUtil;
import com.zemcho.ddql.controller.business.param.ShopManagerParam;
import com.zemcho.ddql.entity.business.Shop;
import com.zemcho.ddql.entity.business.ShopManager;
import com.zemcho.ddql.entity.cas.CasUser;
import com.zemcho.ddql.mapper.business.ShopManagerMapper;
import com.zemcho.ddql.mapper.business.ShopMapper;
import com.zemcho.ddql.mapper.cas.CasUserMapper;
import com.zemcho.ddql.service.wechat.shop.RuleService;
import com.zemcho.ddql.util.Constant;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HXH
 */
@Service
public class IRuleService implements RuleService {

    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private ShopManagerMapper shopManagerMapper;
    @Autowired
    private CasUserMapper casUserMapper;
    @Override
    public Result updateShopStatus(SearchParam param, String token) {
        // 查找商家
        if (param.getSearchId() == null) {
            return Result.error("参数错误");
        }
        Shop shop = shopMapper.selectById(param.getSearchId());
        if (shop == null) {
            return Result.error("该商家不存在");
        }
        shop.setShopStatus(2);
        shop.setStatus(0);//商家状态变为禁用
        shop.setReceiptStatus(0);//收款配置功能关闭
        shopMapper.update(shop);
        return Result.success("操作成功");
    }

    @Override
    public Result selectByPhone(SearchParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return Result.error("参数错误");
        }

        CasUser casUser = casUserMapper.selectById(userId);
        if (casUser == null) {
            return Result.error("用户不存在");
        }
        String phone = casUser.getPhone();
        Integer result = shopManagerMapper.countHeadManagerByShopIdAndPhone(param.getSearchId(), phone);
        if(result<=0){
            return Result.error("无权访问");
        }
        // 查找商家
        if (param.getSearchId() == null) {
            return Result.error("参数错误");
        }
        Shop shop = shopMapper.selectById(param.getSearchId());
        if (shop == null) {
            return Result.error("该商家不存在");
        }
        List<ShopManager> shopManager = shopManagerMapper.selectByPhoneAndShopId(param);
        return Result.success("操作成功",shopManager);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result update(SearchParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return Result.error("参数错误");
        }

        CasUser casUser = casUserMapper.selectById(userId);
        if (casUser == null) {
            return Result.error("用户不存在");
        }
        Shop shop = shopMapper.selectById(param.getSearchId());
        if (shop == null) {
            return Result.error("该商家不存在");
        }
        String phone = casUser.getPhone();
        param.setKeyword(phone);
        List<ShopManager> shopManager = shopManagerMapper.selectByPhoneAndShopId(param);
        shopManager.get(0).setHeadManager(0);//将自己设置为非店长
        Integer sort = shopManager.get(0).getSort();
        String userPhone = param.getSearchStrField1();
        param.setKeyword(userPhone);
        List<ShopManager> shopManager1 = shopManagerMapper.selectByPhoneAndShopId(param);
        Integer sort1 = shopManager1.get(0).getSort();
        //修改排序
        shopManager.get(0).setSort(sort1);
        shopManager1.get(0).setSort(sort);
        shopManager1.get(0).setHeadManager(1);//设置该用户为店长
        shopManagerMapper.update(shopManager.get(0));
        shopManagerMapper.update(shopManager1.get(0));
        return Result.success("操作成功");
    }
}
