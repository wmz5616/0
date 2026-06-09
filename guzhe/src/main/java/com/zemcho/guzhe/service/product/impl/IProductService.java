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
import com.zemcho.guzhe.controller.product.param.*;
import com.zemcho.guzhe.controller.product.vo.CategoryVo;
import com.zemcho.guzhe.controller.product.vo.ProductVo;
import com.zemcho.guzhe.entity.product.Product;
import com.zemcho.guzhe.entity.product.ProductCategoryRelation;
import com.zemcho.guzhe.entity.product.ProductCheckAdmin;
import com.zemcho.guzhe.entity.product.ProductSpec;
import com.zemcho.guzhe.entity.product.ProductSpecPrice;
import com.zemcho.guzhe.entity.product.ProductSpecValue;
import com.zemcho.guzhe.entity.product.ProductTicket;
import com.zemcho.guzhe.entity.shop.Shop;
import com.zemcho.guzhe.mapper.product.*;
import com.zemcho.guzhe.mapper.shop.ShopMapper;
import com.zemcho.guzhe.service.product.ProductService;
import com.zemcho.guzhe.service.wechat.subscribe.WechatSubscribeMessageService;
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
    private ProductSpecMapper productSpecMapper;
    @Autowired
    private ProductSpecValueMapper productSpecValueMapper;
    @Autowired
    private ProductSpecPriceMapper productSpecPriceMapper;
    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RedisLockUtil redisLockUtil;

    @Autowired
    private WechatSubscribeMessageService wechatSubscribeMessageService;

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
            // 处理折扣倒计时
            if (param.getOpenDiscountTime() == 1) {
                if (param.getDiscountTime() == null || param.getDiscountTime().trim().isEmpty()) {
                    return Result.error("开启折扣倒计时必须填写时间");
                }
            } else {
                // 关闭倒计时则清空字段
                param.setDiscountTime("");
            }
        } else {
            param.setDiscountNum(BigDecimal.valueOf(0));
            param.setOpenDiscountTime(0);
            param.setDiscountTime("");
        }

        List<Integer> checkAdminIds = param.getCheckAdminIds();
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
            // 保存到商品信息表
            productMapper.insert(product);
            // 保存商品的规格类型和规格值
            if (param.getIsVirtual() == 0) {
                saveProductSpecs(product, param);
            }
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
            // 更新商品的规格类型和规格值（非虚拟商品）
            if (oldProduct.getIsVirtual() == 0) {
                // 删除旧的规格数据（先删除规格值，再删除规格类型）
                List<Integer> typeIds = productSpecMapper.selectIdsByProductId(id);
                if (typeIds != null && !typeIds.isEmpty()) {
                    productSpecValueMapper.deleteByTypeIds(typeIds);
                }
                productSpecPriceMapper.deleteByProductId(id);
                productSpecMapper.deleteByProductId(id);
            }
            // 如果新商品是非虚拟商品，保存规格数据
            if (param.getIsVirtual() == 0) {
                saveProductSpecs(product, param);
            }
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

    /**
     * 保存商品规格类型、规格值及规格价格库存
     * 优先使用specList嵌套结构，兼容旧的productSpec+productSpecValue分离结构
     */
    private void saveProductSpecs(Product product, ProductParam param) {
        // 优先使用嵌套结构
        List<ProductSpecParam> specList = param.getSpecList();
        if (specList != null && !specList.isEmpty()) {
            saveSpecsWithNested(product, specList);
            return;
        }

        // 兼容旧的分离结构
        List<ProductSpec> specTypes = param.getProductSpec();
        List<ProductSpecValue> specValues = param.getProductSpecValue();
        if (specTypes == null || specTypes.isEmpty() || specValues == null || specValues.isEmpty()) {
            return;
        }
        saveSpecsWithSeparated(product, specTypes, specValues);
    }

    /**
     * 使用嵌套结构保存规格
     */
    private void saveSpecsWithNested(Product product, List<ProductSpecParam> specList) {
        Integer productId = product.getId();

        // 1. 遍历规格类型，记录每个类型对应的所有规格值（新增和修改分开）
        Map<Integer, List<ProductSpecValue>> valuesByTypeIndex = new LinkedHashMap<>();
        Map<Integer, List<ProductSpecValue>> updateValuesByTypeIndex = new LinkedHashMap<>();
        Map<Integer, Integer> typeIndexToId = new HashMap<>();       // typeIndex -> DB ID
        Map<Integer, String> typeIndexToName = new LinkedHashMap<>(); // typeIndex -> typeName

        for (int i = 0; i < specList.size(); i++) {
            ProductSpecParam specParam = specList.get(i);
            typeIndexToName.put(i, specParam.getTypeName());

            // 保存/更新规格类型
            ProductSpec spec = new ProductSpec();
            spec.setProductId(productId);
            spec.setTypeName(specParam.getTypeName());
            spec.setSort(specParam.getSort());

            if (specParam.getId() != null && specParam.getId() > 0) {
                // 修改已有类型
                spec.setId(specParam.getId());
                productSpecMapper.update(spec);
                typeIndexToId.put(i, specParam.getId());
            } else {
                // 新增类型
                productSpecMapper.insert(spec);
                typeIndexToId.put(i, spec.getId());
            }

            // 处理该类型下的规格值
            List<ProductSpecParam.SpecValueItem> valueItems = specParam.getSpecValues();
            if (valueItems == null || valueItems.isEmpty()) {
                continue;
            }

            Integer savedTypeId = typeIndexToId.get(i);
            for (ProductSpecParam.SpecValueItem item : valueItems) {
                ProductSpecValue v = new ProductSpecValue();
                v.setTypeId(savedTypeId);
                v.setValueName(item.getValueName());
                v.setSort(item.getSort() != null ? item.getSort() : 0);

                if (item.getId() != null && item.getId() > 0) {
                    // 修改已有规格值
                    v.setId(item.getId());
                    updateValuesByTypeIndex.computeIfAbsent(i, k -> new ArrayList<>()).add(v);
                } else {
                    // 新增规格值
                    valuesByTypeIndex.computeIfAbsent(i, k -> new ArrayList<>()).add(v);
                }
            }
        }

        // 2. 批量新增规格值
        if (!valuesByTypeIndex.isEmpty()) {
            List<ProductSpecValue> batchInsert = new ArrayList<>();
            for (List<ProductSpecValue> values : valuesByTypeIndex.values()) {
                batchInsert.addAll(values);
            }
            productSpecValueMapper.batchInsert(batchInsert);
        }

        // 3. 逐条更新规格值
        for (List<ProductSpecValue> values : updateValuesByTypeIndex.values()) {
            for (ProductSpecValue value : values) {
                productSpecValueMapper.update(value);
            }
        }

        // 4. 合并所有规格值用于生成笛卡尔积组合
        Map<Integer, List<ProductSpecValue>> allValuesByTypeIndex = new LinkedHashMap<>();
        for (Map.Entry<Integer, List<ProductSpecValue>> entry : valuesByTypeIndex.entrySet()) {
            allValuesByTypeIndex.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).addAll(entry.getValue());
        }
        for (Map.Entry<Integer, List<ProductSpecValue>> entry : updateValuesByTypeIndex.entrySet()) {
            allValuesByTypeIndex.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).addAll(entry.getValue());
        }

        // 5. 生成笛卡尔积并保存
        List<ProductSpecPrice> specPrices = buildCombinationsFromNested(
                typeIndexToName, allValuesByTypeIndex, typeIndexToId, product);
        if (!specPrices.isEmpty()) {
            productSpecPriceMapper.batchInsert(specPrices);
        }

        // 6. 更新商品总库存
        int totalStock = productSpecPriceMapper.sumStockByProductId(productId);
        product.setStock(totalStock);
        productMapper.update(product);
    }

    /**
     * 根据嵌套结构的规格生成笛卡尔积组合
     */
    private List<ProductSpecPrice> buildCombinationsFromNested(
            Map<Integer, String> typeIndexToName,
            Map<Integer, List<ProductSpecValue>> valuesByTypeIndex,
            Map<Integer, Integer> typeIndexToId,
            Product product) {

        // 按类型索引顺序构建规格值分组
        List<List<ProductSpecValue>> valueGroups = new ArrayList<>();
        for (int i = 0; i < typeIndexToName.size(); i++) {
            List<ProductSpecValue> values = valuesByTypeIndex.get(i);
            if (values != null && !values.isEmpty()) {
                valueGroups.add(values);
            }
        }
        if (valueGroups.isEmpty()) {
            return Collections.emptyList();
        }

        List<List<ProductSpecValue>> combinations = new ArrayList<>();
        cartesianProduct(valueGroups, 0, new ArrayList<>(), combinations);

        List<ProductSpecPrice> prices = new ArrayList<>();
        int sort = 0;
        for (List<ProductSpecValue> combination : combinations) {
            ProductSpecPrice specPrice = new ProductSpecPrice();
            specPrice.setProductId(product.getId());

            String specValueIds = combination.stream()
                    .map(v -> String.valueOf(v.getId()))
                    .collect(Collectors.joining(","));
            specPrice.setSpecValueIds(specValueIds);

            String specCombination = combination.stream()
                    .map(ProductSpecValue::getValueName)
                    .collect(Collectors.joining(","));
            specPrice.setSpecCombination(specCombination);

            specPrice.setPrice(product.getPrice());
            specPrice.setAmount(product.getAmount());
            specPrice.setStock(0);
            specPrice.setStatus(product.getStatus());
            specPrice.setSort(sort++);
            specPrice.setCreateTime(LocalDateTime.now());

            if (sort == 1) {
                product.setSpecification(specCombination);
            }
            prices.add(specPrice);
        }
        return prices;
    }

    /**
     * 旧的分离结构保存规格
     */
    private void saveSpecsWithSeparated(Product product, List<ProductSpec> specTypes, List<ProductSpecValue> specValues) {
        // 1. 保存规格类型
        Map<Integer, Integer> typeIndexToId = new HashMap<>();
        for (int i = 0; i < specTypes.size(); i++) {
            ProductSpec spec = specTypes.get(i);
            spec.setProductId(product.getId());
            if (spec.getId() != null && spec.getId() > 0) {
                productSpecMapper.update(spec);
                typeIndexToId.put(i, spec.getId());
            } else {
                productSpecMapper.insert(spec);
                typeIndexToId.put(i, spec.getId());
            }
        }

        // 2. 按typeId分组并保存规格值
        Map<Integer, List<ProductSpecValue>> valuesByType = specValues.stream()
                .collect(Collectors.groupingBy(ProductSpecValue::getTypeId));

        for (Map.Entry<Integer, List<ProductSpecValue>> entry : valuesByType.entrySet()) {
            Integer typeIndex = entry.getKey();
            Integer savedTypeId = typeIndexToId.get(typeIndex);
            if (savedTypeId == null) continue;
            for (ProductSpecValue value : entry.getValue()) {
                value.setTypeId(savedTypeId);
                if (value.getId() != null && value.getId() > 0) {
                    productSpecValueMapper.update(value);
                } else {
                    productSpecValueMapper.insert(value);
                }
            }
        }

        // 3. 生成笛卡尔积
        List<ProductSpecPrice> specPrices = buildCombinationsFromSeparated(specTypes, valuesByType, typeIndexToId, product);
        if (!specPrices.isEmpty()) {
            productSpecPriceMapper.batchInsert(specPrices);
        }

        // 4. 更新总库存
        int totalStock = productSpecPriceMapper.sumStockByProductId(product.getId());
        product.setStock(totalStock);
        productMapper.update(product);
    }

    /**
     * 根据分离结构生成笛卡尔积组合
     */
    private List<ProductSpecPrice> buildCombinationsFromSeparated(
            List<ProductSpec> specTypes,
            Map<Integer, List<ProductSpecValue>> valuesByType,
            Map<Integer, Integer> typeIndexToId,
            Product product) {

        List<List<ProductSpecValue>> valueGroups = new ArrayList<>();
        for (ProductSpec spec : specTypes) {
            Integer dbTypeId = spec.getId();
            if (dbTypeId == null || dbTypeId == 0) {
                for (int j = 0; j < specTypes.size(); j++) {
                    if (specTypes.get(j) == spec) {
                        dbTypeId = typeIndexToId.get(j);
                        break;
                    }
                }
            }
            if (dbTypeId != null) {
                List<ProductSpecValue> values = valuesByType.get(dbTypeId);
                if (values != null && !values.isEmpty()) {
                    valueGroups.add(values);
                }
            }
        }
        if (valueGroups.isEmpty()) return Collections.emptyList();

        List<List<ProductSpecValue>> combinations = new ArrayList<>();
        cartesianProduct(valueGroups, 0, new ArrayList<>(), combinations);

        List<ProductSpecPrice> prices = new ArrayList<>();
        int sort = 0;
        for (List<ProductSpecValue> combination : combinations) {
            ProductSpecPrice specPrice = new ProductSpecPrice();
            specPrice.setProductId(product.getId());
            specPrice.setSpecValueIds(combination.stream().map(v -> String.valueOf(v.getId())).collect(Collectors.joining(",")));
            specPrice.setSpecCombination(combination.stream().map(ProductSpecValue::getValueName).collect(Collectors.joining(",")));
            specPrice.setPrice(product.getPrice());
            specPrice.setAmount(product.getAmount());
            specPrice.setStock(0);
            specPrice.setStatus(product.getStatus());
            specPrice.setSort(sort++);
            specPrice.setCreateTime(LocalDateTime.now());
            if (sort == 1) product.setSpecification(specPrice.getSpecCombination());
            prices.add(specPrice);
        }
        return prices;
    }

    /**
     * 递归计算笛卡尔积
     */
    private void cartesianProduct(List<List<ProductSpecValue>> groups, int depth,
                                   List<ProductSpecValue> current,
                                   List<List<ProductSpecValue>> result) {
        if (depth == groups.size()) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (ProductSpecValue value : groups.get(depth)) {
            current.add(value);
            cartesianProduct(groups, depth + 1, current, result);
            current.remove(current.size() - 1);
        }
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
        String shopNme = "";
        Integer shopId = param.getShopId();
        if (shopId != null) {
            Shop shop = shopMapper.selectById(shopId);
            if(shop!=null){
                shopNme = shop.getName();
            }else {
                shopNme = "无";
            }
        }
        String fileName = shopNme + "商品列表";

        // 按商品编号分组，汇总库存
        Map<String, Product> productMap = new LinkedHashMap<>();
        if (products != null && !products.isEmpty()) {
            for (Product item : products) {
                String productNo = item.getProductNo();
                if (productMap.containsKey(productNo)) {
                    // 如果已存在该商品编号，则累加库存
                    Product existing = productMap.get(productNo);
                    existing.setStock(existing.getStock() + item.getStock());
                } else {
                    // 首次出现，添加到map
                    productMap.put(productNo, item);
                }
            }
        }

        List<ProductVo> list = new ArrayList<>();
        if (!productMap.isEmpty()) {
            List<Product> deduplicatedProducts = new ArrayList<>(productMap.values());
            List<Integer> productIds = deduplicatedProducts.stream().map(Product::getId).collect(Collectors.toList());
            List<CategoryVo> productCategoryList = categoryMapper.selectByProductIds(productIds);
            Map<Integer, List<CategoryVo>> categoryMap = new HashMap<>();
            if (productCategoryList != null && !productCategoryList.isEmpty()) {
                categoryMap = productCategoryList.stream().collect(Collectors.groupingBy(CategoryVo::getProductId));
            }
            for (Product item : deduplicatedProducts) {
                ProductVo productVo = new ProductVo();
                BeanUtils.copyProperties(item, productVo, "galleryImages");
                productVo.setGalleryImages(JSON.parseArray(item.getGalleryImages(), String.class));

                List<CategoryVo> categoryVos = categoryMap.get(productVo.getId());
                productVo.setCategoryList(categoryVos);

                list.add(productVo);
            }
        }
        ExcelUtil.exportToWeb(response, list, fileName, "商品信息", ProductVo.class);
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
        wechatSubscribeMessageService.notifyLowStockToShopManagers(product, param.getStock());
        return Result.success("操作成功");
    }

    @Override
    public Result updateStockAndPrice(ProductStockUpdateParam param) {
        List<ProductStockUpdateParam.ProductSpecParam> specList = param.getSpecList();
        if (specList == null || specList.isEmpty()) {
            return Result.error("请选择要修改的商品规格");
        }

        for (ProductStockUpdateParam.ProductSpecParam specParam : specList) {
            Integer productId = specParam.getProductId();
            if (productId == null) {
                continue;
            }

            Product product = productMapper.selectById(productId);
            if (product == null) {
                continue;
            }

            // 更新价格
            if (specParam.getPrice() != null) {
                product.setPrice(specParam.getPrice());
            }
            if (specParam.getAmount() != null) {
                product.setAmount(specParam.getAmount());
            }

            // 更新库存（非虚拟商品）
            if (!product.getIsVirtual().equals(1) && specParam.getStock() != null) {
                if (specParam.getStock() < 0) {
                    return Result.error("库存不能小于0");
                }
                product.setStock(specParam.getStock());
            }

            // 更新上架状态
            if (specParam.getStatus() != null) {
                if (specParam.getStatus() == 1) {
                    // 上架时设置上架时间
                    product.setScheduledTime(LocalDateTime.now());
                }
                product.setStatus(specParam.getStatus());
            }

            productMapper.update(product);

            // 库存低于阈值时通知商家
            if (specParam.getStock() != null && !product.getIsVirtual().equals(1)) {
                wechatSubscribeMessageService.notifyLowStockToShopManagers(product, specParam.getStock());
            }
        }

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

        String title = product.getProductNo() + "  " + product.getName() + "  券码查看";
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
