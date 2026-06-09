package com.zemcho.guzhe.controller.sys;

import com.zemcho.guzhe.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transaction/test")
public class DBTestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/count")
    public Result getCount() {
        try {
            Long flowCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM reconciliation_transaction_flow", Long.class);
            Long summaryCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM reconciliation_transaction_summary", Long.class);
            return Result.success("查询成功", "Flow Count: " + flowCount + ", Summary Count: " + summaryCount);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }
}
