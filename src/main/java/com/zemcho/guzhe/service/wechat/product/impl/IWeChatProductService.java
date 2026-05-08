package com.zemcho.guzhe.service.wechat.product.impl;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.ChangeParam;
import com.zemcho.guzhe.common.param.DeleteParam;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.config.jwt.JWTUtil;
import com.zemcho.guzhe.controller.product.param.ProductSearchParam;
import com.zemcho.guzhe.entity.cas.CasUser;
import com.zemcho.guzhe.entity.product.Product;
import com.zemcho.guzhe.entity.product.ProductCategory;
import com.zemcho.guzhe.mapper.cas.CasUserMapper;
import com.zemcho.guzhe.mapper.product.ProductCategoryMapper;
import com.zemcho.guzhe.mapper.product.ProductMapper;
import com.zemcho.guzhe.mapper.shop.ShopMapper;
import com.zemcho.guzhe.service.wechat.product.WeChatProductService;
import com.zemcho.guzhe.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author HXH
 */
@Service
public class IWeChatProductService implements WeChatProductService {
    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private CasUserMapper casUserMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductCategoryMapper productCategoryMapper;
    @Override
    public Result select(ProductSearchParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return Result.error("参数错误");
        }
        CasUser casUser = casUserMapper.selectById(userId);
        if (casUser == null) {
            return Result.error("用户不存在");
        }

        Integer searchId = param.getShopId();
        if(searchId == null){
            return new Result(10002, "参数错误");
        }
        if(shopMapper.selectById(searchId) == null){
            return new Result(10002, "该商家不存在");
        }
        List<Product> products = productMapper.selectList(param);
        return Result.success("操作成功", products);

    }

    @Override
    public Result listCategory(SearchParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return Result.error("参数错误");
        }
        CasUser casUser = casUserMapper.selectById(userId);
        if (casUser == null) {
            return Result.error("用户不存在");
        }

        Integer searchId = param.getSearchField4();
        if(searchId == null){
            return new Result(10002, "参数错误");
        }
        if(shopMapper.selectById(searchId) == null){
            return new Result(10002, "该商家不存在");
        }
        List<ProductCategory> productCategories = productCategoryMapper.selectList(param);
        return Result.success("操作成功", productCategories);
    }

    @Override
    public Result selectByCategoryId(SearchParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return Result.error("参数错误");
        }
        CasUser casUser = casUserMapper.selectById(userId);
        if (casUser == null) {
            return Result.error("用户不存在");
        }
        Integer searchId1= param.getSearchId();
        if(searchId1 == null){
            return new Result(10002, "参数错误");
        }
        if(shopMapper.selectById(searchId1) == null){
            return new Result(10002, "该商家不存在");
        }
        List<Integer> searchIds = param.getSearchIds();
        if(searchIds!= null && !searchIds.isEmpty()) {
            for (Integer searchId : searchIds) {
                if(!productCategoryMapper.ifExists(searchId,searchId1)){
                    return new Result(10002, "该分类不存在");
                }
            }
        }
        List<Product> products=productMapper.selectByCategoryId(param);
        return Result.success("操作成功", products);
    }

    @Override
    public Result delete(DeleteParam param,String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return Result.error("参数错误");
        }
        CasUser casUser = casUserMapper.selectById(userId);
        if (casUser == null) {
            return Result.error("用户不存在");
        }
        Set<Integer> deleteIds = param.getDeleteIds();
        for (Integer deleteId : deleteIds) {
            if(!productMapper.ifExistsProduct(deleteId)){
                return new Result(10002, "该商品不存在");
            }
        }
        productMapper.deleteByIds(deleteIds);
        return Result.success("操作成功");
    }

    @Override
    public Result updateStatus(ChangeParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return Result.error("参数错误");
        }
        CasUser casUser = casUserMapper.selectById(userId);
        if (casUser == null) {
            return Result.error("用户不存在");
        }

        List<Integer> ids = new ArrayList<>(param.getChangeIds());

        productMapper.updateStatusByIds(ids, param.getStatus());

        return Result.success("操作成功");

    }
}
