package com.zemcho.guzhe.service.product.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.DeleteParam;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.product.dto.TicketDto;
import com.zemcho.guzhe.controller.product.excelhandle.TicketSheetWriteHandler;
import com.zemcho.guzhe.controller.product.listener.ImportTicketListener;
import com.zemcho.guzhe.controller.product.param.ProductParam;
import com.zemcho.guzhe.controller.product.param.ProductSearchParam;
import com.zemcho.guzhe.controller.product.param.StockParam;
import com.zemcho.guzhe.controller.product.vo.CategoryVo;
import com.zemcho.guzhe.controller.product.vo.ProductVo;
import com.zemcho.guzhe.entity.product.Product;
import com.zemcho.guzhe.entity.product.ProductCategoryRelation;
import com.zemcho.guzhe.entity.product.ProductCheckAdmin;
import com.zemcho.guzhe.entity.product.ProductTicket;
import com.zemcho.guzhe.entity.shop.Shop;
import com.zemcho.guzhe.mapper.product.*;
import com.zemcho.guzhe.mapper.shop.ShopMapper;
import com.zemcho.guzhe.service.product.ProductService;
import com.zemcho.guzhe.util.excel.ExcelUtil;
import com.zemcho.guzhe.util.redis.RedisLockUtil;
import com.zemcho.guzhe.util.redis.RedisUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zemcho.guzhe.util.Constant;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

/**
 * @author HXH
 */
@Service
@Slf4j
public class IProductService implements ProductService {

    @Autowired
    private ProductCategoryMapper categoryMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductCategoryRelationMapper productCategoryMapper;
    @Autowired
    private ProductCheckAdminMapper productCheckAdminMapper;
    @Autowired
    private ProductTicketMapper productTicketMapper;
    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RedisLockUtil redisLockUtil;

    @Override
    public Result saveProduct(ProductParam param) {
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
        List<Integer> checkAdminIds = null;
        //是虚拟商品
        if (param.getIsVirtual() == 1) {
            if (param.getTimeLimit() == null || param.getTimeLimit() <= 0) {
                return Result.error("有效期必须大于0");
            }
            checkAdminIds = param.getCheckAdminIds();
            if (checkAdminIds == null || checkAdminIds.isEmpty()) {
                return Result.error("请选择核销人员");
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

        productCheckAdminMapper.deleteByProductIds(Arrays.asList(product.getId()));
        if (checkAdminIds != null && !checkAdminIds.isEmpty()) {
            List<ProductCheckAdmin> productCheckAdmins = new ArrayList<>();
            for (Integer checkAdminId : checkAdminIds) {
                ProductCheckAdmin productCheckAdmin = new ProductCheckAdmin();
                productCheckAdmin.setAdminId(checkAdminId);
                productCheckAdmin.setProductId(product.getId());
                productCheckAdmins.add(productCheckAdmin);
            }
            productCheckAdminMapper.insertAll(productCheckAdmins);
        }

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

    @Override
    public Result selectList(ProductSearchParam param) {
        int pageNum = param.getPageNum();
        int pageSize = param.getPageSize();

        PageHelper.startPage(pageNum, pageSize);
        List<Product> products = productMapper.selectList(param);
        List<ProductVo> list = new ArrayList<>();
        if (products != null && !products.isEmpty()) {
            List<Integer> productIds = products.stream().map(Product::getId).collect(Collectors.toList());
            List<CategoryVo> productCategoryList = categoryMapper.selectByProductIds(productIds);
            Map<Integer, List<CategoryVo>> categoryMap = new HashMap<>();
            if (productCategoryList != null && !productCategoryList.isEmpty()) {
                categoryMap = productCategoryList.stream().collect(Collectors.groupingBy(CategoryVo::getProductId));
            }
            for (Product item : products) {
                ProductVo productVo = new ProductVo();
                BeanUtils.copyProperties(item, productVo, "galleryImages");
                productVo.setGalleryImages(JSON.parseArray(item.getGalleryImages(), String.class));

                List<CategoryVo> categoryVos = categoryMap.get(productVo.getId());
                productVo.setCategoryList(categoryVos);

                list.add(productVo);
            }
        }
        PageInfo<ProductVo> pageInfo = new PageInfo<>(list);

        return Result.success("操作成功", pageInfo);
    }

    @Override
    public void productDataExport(ProductSearchParam param, HttpServletResponse response) {
        List<Product> products = productMapper.selectList(param);
        List<ProductVo> list = new ArrayList<>();
        if (products != null && !products.isEmpty()) {
            List<CategoryVo> productCategoryList = categoryMapper.selectByProductIds(null);
            Map<Integer, List<CategoryVo>> categoryMap = new HashMap<>();
            if (productCategoryList != null && !productCategoryList.isEmpty()) {
                categoryMap = productCategoryList.stream().collect(Collectors.groupingBy(CategoryVo::getProductId));
            }
            for (Product item : products) {
                ProductVo productVo = new ProductVo();
                BeanUtils.copyProperties(item, productVo, "galleryImages");
                productVo.setGalleryImages(JSON.parseArray(item.getGalleryImages(), String.class));

                List<CategoryVo> categoryVos = categoryMap.get(productVo.getId());
                productVo.setCategoryList(categoryVos);

                list.add(productVo);
            }
        }
        ExcelUtil.exportToWeb(response, list, "商品信息", "商品信息", ProductVo.class);
    }

    @Override
    public Result getProduct(SearchParam param) {
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

        if (product.getIsVirtual() == 1) {
            List<Integer> checkAdminIds = productCheckAdminMapper.selectAdminIdByProductId(productId);
            productVo.setCheckAdminIds(checkAdminIds);
        }

        return Result.success("操作成功", productVo);
    }

    @Override
    public Result deleteProduct(DeleteParam param) {
        Set<Integer> productIds = param.getDeleteIds();
        if (productIds == null || productIds.isEmpty()) {
            return Result.error("请选择要删除的商品");
        }
        List<Integer> ids = new ArrayList<>(productIds);
        ProductSearchParam searchParam = new ProductSearchParam();
        searchParam.setSearchIds(ids);
        List<Product> products = productMapper.selectList(searchParam);
        for (Product product : products) {
            Integer status = product.getStatus();
            if (status != 2) {
                return Result.error("商品已上架,无法删除");
            }
        }
        // 删除商品
        productMapper.deleteByIds(productIds);
        // 删除商品分类关联
        productCategoryMapper.deleteByProductIds(ids);

        productCheckAdminMapper.deleteByProductIds(ids);
        return Result.success("操作成功");
    }

    @Override
    public Result updateStock(StockParam param) {
        Integer productId = param.getProductId();
        Product product = productMapper.selectById(productId);
        if (product == null) {
            return Result.error("商品不存在");
        }
        if (product.getIsVirtual().equals(1)) {
            return Result.error("请选择非虚拟商品");
        }
        if (product.getStatus().equals(1)) {
            return Result.error("商品已上架,无法修改库存");
        }
        if (param.getStock() < 0) {
            return Result.error("库存不能小于0");
        }
        product.setStock(param.getStock());
        productMapper.update(product);
        return Result.success("操作成功");
    }

    @Override
    public void exportTicket(SearchParam param, HttpServletResponse response) {
        Integer productId = param.getSearchId();
        Product product = productMapper.selectById(productId);
        if (product == null) {
            return;
        }

        // 1. 获取商家名称用于拼接文件名
        String shopName = "";
        if (product.getShopId() != null) {

             Shop shop = shopMapper.selectById(product.getShopId());
             if (shop != null)
                 shopName = shop.getName();
        }

        List<ProductTicket> list = productTicketMapper.selectByProductId(productId, null);
        List<TicketDto> data = list.stream().map(productTicket -> {
            TicketDto ticketDto = new TicketDto();
            ticketDto.setSort(productTicket.getSort());
            ticketDto.setTicket(productTicket.getTicket());
            ticketDto.setStatus(productTicket.getStatus());
            return ticketDto;
        }).collect(Collectors.toList());

        String title = product.getProductNo() + "  " + product.getName() + "  券码导入";
        // 2. 修改文件名格式：商家名称 + 商品名称 + 券码
        String fileName = shopName + "_" + product.getName() + "_券码";
        String sheetName = "券码列表";
        try {
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition",
                    "attachment;filename*=utf-8''" + encodedFileName + ExcelTypeEnum.XLSX.getValue());

            ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream(), TicketDto.class)
                    .excelType(ExcelTypeEnum.XLSX)
                    .relativeHeadRowIndex(1)
                    .registerWriteHandler(new TicketSheetWriteHandler(title))
                    .build();

            WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();
            excelWriter.write(data, writeSheet);
            excelWriter.finish();
        } catch (Exception e) {
            log.error("导出券码失败", e);
        }
    }

    @Override
    public Result importTicket(MultipartFile file, Integer productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            return Result.error("商品不存在");
        }
        if (product.getIsVirtual() == null || product.getIsVirtual().equals(0)) {
            return Result.error("请选择虚拟商品");
        }

        ImportTicketListener listener = new ImportTicketListener(productTicketMapper, productMapper, productId, redisUtil);
        String lockKey = Constant.IMPORT_LOCK_PREFIX + "product_ticket:" + productId;
        Boolean lockFlag = redisLockUtil.tryLock(lockKey, 25, TimeUnit.SECONDS);
        if (!lockFlag) {
            return Result.error("该商品正在导入券码，请稍后再试");
        }

        try {
            EasyExcel.read(file.getInputStream(), TicketDto.class, listener)
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(2)
                    .doRead();
        } catch (Exception e) {
            redisLockUtil.unlock(lockKey);
            log.error("导入券码失败", e);
            return Result.error("导入失败，文件格式有误");
        }

        redisLockUtil.unlock(lockKey);

        if (listener.getErrorList().size() > 0) {
            return Result.error("导入失败，数据有误", listener.getErrorList());
        }

        // 3. 导入成功后，同步更新商品库存
        // 根据原型提示：添加了多少个券码就有多少库存
        // 重新统计该商品下所有有效的券码数量
        List<ProductTicket> allTickets = productTicketMapper.selectByProductId(productId, null);
        if (allTickets != null) {
            product.setStock(allTickets.size());
            productMapper.update(product); // 确保 ProductMapper 中有 update 方法且能更新 stock

            // 刷新 Redis 缓存，确保迁移或重启后数据一致
            refreshTicketCache(productId, allTickets);
        }

        return Result.success("导入成功");
    }

    /**
     * 刷新券码缓存（用于迁移恢复或导入后同步）
     */
    private void refreshTicketCache(Integer productId, List<ProductTicket> tickets) {
        String key = Constant.PRODUCT_TICKET_LIST + productId;
        redisUtil.del(key);
        if (tickets != null && !tickets.isEmpty()) {
            List<String> ticketList = tickets.stream()
                    .filter(t -> t.getStatus() == 1) // 只缓存未使用的
                    .map(ProductTicket::getTicket)
                    .collect(Collectors.toList());
            if (!ticketList.isEmpty()) {
                redisUtil.rightPushAll(key, ticketList);
            }
        }
    }
}
