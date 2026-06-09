package com.zemcho.ddql.service.product.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.DeleteParam;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.product.dto.TicketDto;
import com.zemcho.ddql.controller.product.excelhandle.TicketSheetWriteHandler;
import com.zemcho.ddql.controller.product.listener.ImportTicketListener;
import com.zemcho.ddql.controller.product.param.ProductParam;
import com.zemcho.ddql.controller.product.param.ProductSearchParam;
import com.zemcho.ddql.controller.product.param.StockParam;
import com.zemcho.ddql.controller.product.vo.CategoryVo;
import com.zemcho.ddql.controller.product.vo.ProductVo;
import com.zemcho.ddql.entity.order.ExchangeOrder;
import com.zemcho.ddql.entity.product.Product;
import com.zemcho.ddql.entity.product.ProductCategory;
import com.zemcho.ddql.entity.product.ProductCheckAdmin;
import com.zemcho.ddql.entity.product.ProductTicket;
import com.zemcho.ddql.mapper.product.*;
import com.zemcho.ddql.service.product.ProductService;
import com.zemcho.ddql.util.Constant;
import com.zemcho.ddql.util.excel.ExcelUtil;
import com.zemcho.ddql.util.redis.RedisLockUtil;
import com.zemcho.ddql.util.redis.RedisUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class IProductService implements ProductService {
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ProductCategoryMapper productCategoryMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ProductTicketMapper productTicketMapper;

    @Autowired
    private ProductCheckAdminMapper productCheckAdminMapper;

    @Autowired
    private RedisLockUtil redisLockUtil;

    /**
     * 新增商品
     *
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result saveProduct(ProductParam param) {
        // 检查支付金额和币额
        if (param.getPayWay().equals(0)) {
            // 金币支付
            if (param.getExchangeAmount() == null) {
                return Result.error("参数错误");
            }
            param.setPayAmount(0); // 清空现金金额
        } else if (param.getPayWay().equals(1)) {
            if (param.getExchangeAmount() == null || param.getPayAmount() == null) {
                return Result.error("参数错误");
            }
        } else if (param.getPayWay().equals(2)) {
            if (param.getPayAmount() == null) {
                return Result.error("参数错误");
            }
            param.setExchangeAmount(0); // 清空金币金额
        } else {
            return Result.error("参数错误");
        }

        String categoryIds = param.getCategoryIds();
        //获取商品类型id列表
        List<Integer> categoryList = Arrays.stream(categoryIds.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        if (categoryList == null || categoryList.isEmpty()) {
            return Result.error("请选择商品分类");
        }
        for (Integer categoryId : categoryList) {
            Boolean ifExists = categoryMapper.ifExists(categoryId);
            if (!ifExists) {
                return Result.error("商品分类不存在");
            }
        }

        if (param.getStatus() == 1) {
            param.setScheduledTime(LocalDateTime.now());
        }

        List<Integer> checkAdminIds = null;
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

        // 修复 BeanUtils 无法处理 Integer 到 Long 的类型转换问题，以及 payWay 大小写问题
        if (param.getPayAmount() != null) {
            product.setPayAmount(param.getPayAmount().longValue());
        }
        if (param.getPayWay() != null) {
            product.setPayWay(param.getPayWay());
        }

        // 轮播图集合转json
        product.setGalleryImages(JSON.toJSONString(param.getGalleryImages()));
        Integer id = product.getId();
        if (id == 0) {
            // 设置库存为0
            product.setStock(0);
            // 设置商品编号 ，SP开头+唯一标识
            product.setProductNo(generateSerialNumber());
            product.setCreateTime(LocalDateTime.now());
            product.setUpdateTime(LocalDateTime.now());
            // 新增商品
            productMapper.insert(product);
        } else {
            Product oldProduct = productMapper.selectById(id);
            if (oldProduct == null) {
                return Result.error("商品不存在");
            }
//            if(oldProduct.getStatus().equals(1)){
//                return Result.error("商品已上架,无法编辑");
//            }
            // 编辑商品
            product.setIsVirtual(null); // 不可修改虚拟商品标识
            productMapper.update(product);
            // 删除商品分类关联记录
            productCategoryMapper.deleteByProductId(id);
        }
        // 新增商品分类关联记录
        List<ProductCategory> productCategoryList = categoryList.stream().map(categoryId -> {
            ProductCategory productCategory = new ProductCategory();
            productCategory.setCategoryId(categoryId);
            productCategory.setProductId(product.getId());
            return productCategory;
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

    /**
     * 根据id删除商品
     *
     * @return
     */
    @Override
    @Transactional
    public Result deleteProduct(DeleteParam param) {
        Set<Integer> productIds = param.getDeleteIds();
        if (productIds == null || productIds.isEmpty()) {
            return Result.error("请选择要删除的商品");
        }
        ProductSearchParam searchParam = new ProductSearchParam();
        searchParam.setSearchIds(new ArrayList<>(productIds));
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
        productCategoryMapper.deleteByProductIds(productIds);
        productCheckAdminMapper.deleteByProductIds(productIds);
        return Result.success("操作成功");
    }

    /**
     * 更新库存
     *
     * @param param
     * @return
     */
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

    /**
     * 获取商品列表
     *
     * @param param
     * @return
     */
//    @Override
//    public Result selectList(ProductSearchParam param) {
//        // 查询所有数据
//        List<Product> products = productMapper.selectList(param);
//        // 手动分页计算
//        int pageNum = param.getPageNum();
//        int pageSize = param.getPageSize();
//        int total = products.size();
//        int startIndex = (pageNum - 1) * pageSize;
//        int endIndex = Math.min(startIndex + pageSize, total);
//
//        // 获取当前页数据
//        List<Product> pageProducts = new ArrayList<>();
//        if (startIndex < total) {
//            pageProducts = products.subList(startIndex, endIndex);
//        }
//        List<ProductVo> productVoList = pageProducts.stream().map(product -> {
//            ProductVo productVo = new ProductVo();
//            BeanUtils.copyProperties(product, productVo, "galleryImages");
//            productVo.setGalleryImages(JSON.parseArray(product.getGalleryImages(), String.class));
//            return productVo;
//        }).collect(Collectors.toList());
//        List<Integer> productIds = pageProducts.stream().map(Product::getId).collect(Collectors.toList());
//        List<CategoryVo> productCategoryList = categoryMapper.selectByProductIds(productIds);
//        Map<Integer, List<CategoryVo>> categoryMap = productCategoryList.stream().collect(Collectors.groupingBy
//        (CategoryVo::getProductId));
//        productVoList.forEach(productVo -> {
//            List<CategoryVo> categoryVos = categoryMap.get(productVo.getId());
//            productVo.setCategoryList(categoryVos);
//        });
//        // 构造分页信息
//        PageInfo<ProductVo> pageInfo = new PageInfo<>();
//        pageInfo.setList(productVoList);
//        pageInfo.setSize(productVoList.size());
//        pageInfo.setTotal(total);
//        pageInfo.setPageNum(pageNum);
//        pageInfo.setPageSize(pageSize);
//
//        return Result.success("操作成功", pageInfo);
//    }
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
        PageInfo<Product> originalPageInfo = new PageInfo<>(products);
        PageInfo<ProductVo> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(originalPageInfo, pageInfo, "list");
        pageInfo.setList(list);

        return Result.success("操作成功", pageInfo);
    }

    /**
     * 导出商品数据
     *
     * @param param
     * @param response
     */
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


    // 生成前缀为ALM的12位订单号 6位日期+6位自增数(Redis)
    private synchronized String generateSerialNumber() {
        String prefix = "SP";
        // 获取当日日期 格式是YYMMDD
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
        String date = currentDate.format(formatter);

        // Redis Key
        String key = Constant.PRODUCT_NO_SEQ + date;

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

    /**
     * 导入券码
     *
     * @param file
     * @param productId
     * @return
     */
    @Override
    public Result importTicket(MultipartFile file, Integer productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            return Result.error("商品不存在");
        }
        if (product.getIsVirtual().equals(0)) {
            return Result.error("请选择虚拟商品");
        }
        ImportTicketListener listener = new ImportTicketListener(productTicketMapper, productMapper, productId,
                redisUtil);

        // 上锁
        String lockKey = Constant.IMPORT_LOCK_PREFIX + "product_ticket:" + productId;
        Boolean lockFlag = redisLockUtil.tryLock(lockKey, 25, TimeUnit.SECONDS);
        if (!lockFlag) {
            return Result.error("该商品正在导入券码，请稍后再试");
        }

        try {
            EasyExcel.read(file.getInputStream(), TicketDto.class, listener)
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(2) // 跳过前两行
                    .doRead();
        } catch (Exception e) {
            // 解锁
            redisLockUtil.unlock(lockKey);

            log.error("导入券码失败", e);
            return Result.error("导入失败，文件格式有误");
        }

        // 解锁
        redisLockUtil.unlock(lockKey);

        if (listener.getErrorList().size() > 0) {
            return Result.error("导入失败，数据有误", listener.getErrorList());
        }
        return Result.success("导入成功");
    }

    /**
     * 导出券码
     *
     * @param param
     * @param response
     */
    @Override
    public void exportTicket(SearchParam param, HttpServletResponse response) {
        Integer productId = param.getSearchId();
        Product product = productMapper.selectById(productId);
        if (product == null) {
            return;
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
        String fileName = product.getProductNo() + "  " + product.getName() + "券码";
        String sheetName = "券码列表";
        try {
            // 防止中文乱码
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition",
                    "attachment;filename*=utf-8''" + encodedFileName + ExcelTypeEnum.XLSX.getValue());

            // 创建写对象
            ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream(), TicketDto.class)
                    .excelType(ExcelTypeEnum.XLSX)
                    .relativeHeadRowIndex(1)                // 从第二行开始写入数据
                    .registerWriteHandler(new TicketSheetWriteHandler(title))
                    .build();

            WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();

            // 写入数据行
            excelWriter.write(data, writeSheet);
            // 关闭写对象
            excelWriter.finish();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("导出订单列表失败", e);
        }
        log.info("导出订单列表成功");
    }


    /**
     * 根据id获取商品信息
     *
     * @param param
     * @return
     */
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
}
