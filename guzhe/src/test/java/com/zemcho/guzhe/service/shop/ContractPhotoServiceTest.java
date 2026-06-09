package com.zemcho.guzhe.service.shop;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.entity.shop.Shop;
import com.zemcho.guzhe.mapper.shop.ShopMapper;
import com.zemcho.guzhe.service.shop.ShopManagerService;
import com.zemcho.guzhe.service.shop.impl.IShopService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContractPhotoServiceTest {

    @Mock
    private ShopMapper shopMapper;

    @Mock
    private ShopManagerService shopManagerService;

    @InjectMocks
    private IShopService shopService;

    private SearchParam searchParam;

    @BeforeEach
    void setUp() {
        searchParam = new SearchParam();
        searchParam.setSearchId(1);
    }

    @Test
    void testGetContract_Success() {
        Shop shop = new Shop();
        shop.setId(1);
        shop.setName("测试商家");
        shop.setContract("https://example.com/upload/1.jpg,https://example.com/upload/2.jpg,https://example.com/upload/3.jpg");
        shop.setStatus(1);
        shop.setCreateTime(LocalDateTime.now());

        when(shopMapper.selectById(anyInt())).thenReturn(shop);

        Result result = shopService.getContract(searchParam, null, false);

        assertTrue(result.success());
        assertEquals("获取成功", result.getMsg());
        assertNotNull(result.getData());

        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> data = (java.util.Map<String, Object>) result.getData();
        assertEquals("https://example.com/upload/1.jpg,https://example.com/upload/2.jpg,https://example.com/upload/3.jpg", data.get("contract"));
    }

    @Test
    void testGetContract_NullShopId() {
        SearchParam param = new SearchParam();
        param.setSearchId(null);

        Result result = shopService.getContract(param, null, false);

        assertFalse(result.success());
        assertEquals("参数错误", result.getMsg());
    }

    @Test
    void testGetContract_ShopNotFound() {
        when(shopMapper.selectById(anyInt())).thenReturn(null);

        Result result = shopService.getContract(searchParam, null, false);

        assertFalse(result.success());
        assertEquals("商家不存在", result.getMsg());
    }

    @Test
    void testGetContract_EmptyContract() {
        Shop shop = new Shop();
        shop.setId(1);
        shop.setName("测试商家");
        shop.setContract(null);
        shop.setStatus(1);

        when(shopMapper.selectById(anyInt())).thenReturn(shop);

        Result result = shopService.getContract(searchParam, null, false);

        assertTrue(result.success());
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> data = (java.util.Map<String, Object>) result.getData();
        assertNull(data.get("contract"));
    }

    @Test
    void testGetContract_MultiplePhotos() {
        Shop shop = new Shop();
        shop.setId(1);
        shop.setName("测试商家");
        shop.setContract("url1.jpg,url2.jpg,url3.jpg,url4.jpg,url5.jpg,url6.jpg");
        shop.setStatus(1);

        when(shopMapper.selectById(anyInt())).thenReturn(shop);

        Result result = shopService.getContract(searchParam, null, false);

        assertTrue(result.success());
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> data = (java.util.Map<String, Object>) result.getData();
        String contract = (String) data.get("contract");
        assertNotNull(contract);
        assertEquals(6, contract.split(",").length);
    }
}
