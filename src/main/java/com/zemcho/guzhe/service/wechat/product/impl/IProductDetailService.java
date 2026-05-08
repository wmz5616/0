package com.zemcho.guzhe.service.wechat.product.impl;

import com.alibaba.fastjson.JSON;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.product.param.ProductParam;
import com.zemcho.guzhe.controller.product.vo.CategoryVo;
import com.zemcho.guzhe.controller.product.vo.ProductVo;
import com.zemcho.guzhe.entity.product.Product;
import com.zemcho.guzhe.entity.product.ProductCategoryRelation;
import com.zemcho.guzhe.entity.product.ProductCheckAdmin;
import com.zemcho.guzhe.mapper.product.ProductCategoryMapper;
import com.zemcho.guzhe.mapper.product.ProductCategoryRelationMapper;
import com.zemcho.guzhe.mapper.product.ProductMapper;
import com.zemcho.guzhe.service.wechat.product.ProductDetailService;
import com.zemcho.guzhe.util.Constant;
import com.zemcho.guzhe.util.redis.RedisUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author HXH
 */
@Service
public class IProductDetailService implements ProductDetailService {

    @Autowired
    ProductMapper productMapper;
    @Autowired
    ProductCategoryMapper categoryMapper;
    @Autowired
    private ProductCategoryRelationMapper productCategoryMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Override
    public Result selectDetail(SearchParam param, String token) {
        Integer productId = param.getSearchId();
        if (productId == null) {
            return Result.error("参数错误");
        }
        Product product = productMapper.selectById(productId);
        if (product == null) {
            return Result.error("商品不存在");
        }
        ProductVo productVo = new ProductVo();
        BeanUtils.copyProperties(product, productVo, "galleryImages");
        productVo.setGalleryImages(JSON.parseArray(product.getGalleryImages(), String.class));
        List<Integer> productIds = Arrays.asList(productId);
        List<CategoryVo> productCategoryList = categoryMapper.selectByProductIds(productIds);
        productVo.setCategoryList(productCategoryList);
        return Result.success("操作成功", productVo);
    }

    @Override
    public Result saveProduct(ProductParam param, String token) {
        String categoryIds = param.getCategoryIds();
        //获取商品类型id列表
        List<Integer> categoryList = Arrays.stream(categoryIds.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        if (categoryList == null || categoryList.isEmpty()) {
            return Result.error("请选择商品分类");
        }
        //判断商品分类id是否存在
        Integer shopId = param.getShopId();
        for (Integer categoryId : categoryList) {
            Boolean ifExists = categoryMapper.ifExists(categoryId,shopId);
            if (!ifExists) {
                return Result.error("商品分类不存在");
            }
        }

        //处理折扣与售价计算逻辑
        if (param.getOpenDiscount() == 1) {
            // 开启折扣：校验折扣力度（根据原型：大于0，小于10，支持1位小数）
            if (param.getDiscountNum() == null || param.getDiscountNum().compareTo(BigDecimal.ZERO) <= 0 || param.getDiscountNum().compareTo(new BigDecimal("10")) >= 0) {
                return Result.error("折扣力度必须在 0-10 之间");
            }

            BigDecimal priceDecimal = new BigDecimal(param.getPrice());
            BigDecimal finalDiscount = param.getDiscountNum().divide(new BigDecimal("10"), 2, BigDecimal.ROUND_HALF_UP);
            param.setAmount(priceDecimal.multiply(finalDiscount).intValue());

            // 处理折扣倒计时
            if (param.getOpenDiscountTime() == 1) {
                if (param.getDiscountTime() == null || param.getDiscountTime().trim().isEmpty()) {
                    return Result.error("开启折扣倒计时必须填写时间");
                }
            } else {
                // 关闭倒计时则清空字段
                param.setDiscountTime(null);
            }
        } else {
            // 关闭折扣：售价等于原价，清空折扣相关数据
            param.setAmount(param.getPrice());
            param.setDiscountNum(BigDecimal.valueOf(0));
            param.setOpenDiscountTime(0);
            param.setDiscountTime(String.valueOf(0));
        }

        //上架状态
        if (param.getStatus() == 1) {
            param.setScheduledTime(LocalDateTime.now());
        }
        if (param.getStatus() == 3) {
            if (param.getScheduledTime() == null) {
                return Result.error("请选择定时上架时间");
            }
        } else {
            // 如果是立即上架或下架，清空定时时间
            param.setScheduledTime(null);
        }
        //是虚拟商品
        if (param.getIsVirtual() == 1) {
            if (param.getTimeLimit() == null || param.getTimeLimit() <= 0) {
                return Result.error("有效期必须大于0");
            }
        }

        Product product = new Product();
        // 排除轮播图和库存
        BeanUtils.copyProperties(param, product, "galleryImages", "stock");
        // 轮播图集合转json
        product.setGalleryImages(JSON.toJSONString(param.getGalleryImages()));
        Integer id = product.getId();
        //如果是新增商品
        if (id == 0) {
            // 设置库存为0
            product.setStock(0);
            // 设置商品编号 ，SP开头+唯一标识
            product.setProductNo(generateSerialNumber());
            product.setCreateTime(LocalDateTime.now());
            product.setUpdateTime(LocalDateTime.now());
            // 新增商品
            productMapper.insert(product);
        }
        // 编辑商品
        else {
            Product oldProduct = productMapper.selectById(id);
            if (oldProduct == null) {
                return Result.error("商品不存在");
            }

            // 编辑商品
            product.setIsVirtual(null); // 不可修改虚拟商品标识
            productMapper.update(product);
            // 删除商品分类关联记录
            productCategoryMapper.deleteByProductId(id);
        }
        // 新增商品分类关联记录
        List<ProductCategoryRelation> productCategoryList = categoryList.stream().map(categoryId -> {
            ProductCategoryRelation productCategoryRelation = new ProductCategoryRelation();
            productCategoryRelation.setCategoryId(categoryId);
            productCategoryRelation.setProductId(product.getId());
            return productCategoryRelation;
        }).collect(Collectors.toList());
        productCategoryMapper.insertBatch(productCategoryList);

        return Result.success("操作成功");

    }
    // 生成前缀为ALM的12位订单号 6位日期+6位自增数(Redis)
    private synchronized String generateSerialNumber() {
        String prefix = "SP";
        // 获取当日日期 格式是YYMMDD
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
        String date = currentDate.format(formatter);

        // Redis Key
        String key = Constant.ORDER_NO_PREFIX + date;

        // 使用 Redis 的 INCR 实现原子自增
        Long num = redisUtil.incr(key, 1L);

        // 设置过期时间（可选）：确保当天有效即可，比如凌晨自动失效
        if (num == 1) {
            // 第一次生成时设置过期时间为 1 天
            redisUtil.expire(key, 1, TimeUnit.DAYS);
        }

        // 格式化为6位数字
        String formattedNum = String.format("%06d", num);

        return prefix + date + formattedNum;
    }
}
