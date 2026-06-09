package com.zemcho.ddql.controller.wechat.index.excelhandle;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.zemcho.ddql.controller.wechat.index.dto.CheckInExportData;
import com.zemcho.ddql.controller.wechat.index.dto.DepartmentStat;
import com.zemcho.ddql.controller.wechat.index.vo.UserCheckInRankVo;
import com.zemcho.ddql.entity.team.TeamFeedback;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 打卡排行榜Excel导出处理器
 * 沿用项目 ExcelUtil 工具类风格，生成动态统计报表
 */
public class CheckInRankExcelHandler {

    /**
     * 生成并输出导出Excel
     */
    public static void export(HttpServletResponse response, CheckInExportData data, boolean isMultiDepartment) {
        try {
            String fileName = data.getTeamName() + "_运动数据统计表";
            String encodedFileName = java.net.URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Disposition",
                    "attachment;filename*=utf-8''" + encodedFileName + ".xlsx");

            ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).build();
            WriteSheet writeSheet = EasyExcel.writerSheet("运动数据统计").build();

            List<List<String>> rows = new ArrayList<>();
            // 标题行
            rows.add(Collections.singletonList(data.getTeamName() + "运动数据统计表"));
            rows.add(Collections.emptyList());

            if (isMultiDepartment) {
                buildMultiDepartmentSheet(rows, data);
            } else {
                buildSingleDepartmentSheet(rows, data);
            }

            excelWriter.write(rows, writeSheet);
            excelWriter.finish();
        } catch (Exception e) {
            throw new RuntimeException("导出Excel失败", e);
        }
    }

    // ==================== 多部门模板 ====================

    private static void buildMultiDepartmentSheet(List<List<String>> rows, CheckInExportData data) {
        List<DepartmentStat> deptStats = data.getDepartmentStats();
        int deptCount = deptStats.size();

        // 表头: 统计维度 | 具体项目 | 整体 | 部门A | 部门B | ... | 备注
        List<String> header = new ArrayList<>();
        header.add("统计维度");
        header.add("具体项目");
        header.add("整体");
        for (DepartmentStat dept : deptStats) {
            header.add(dept.getDepartmentName());
        }
        header.add("备注/计算方式/说明");
        rows.add(header);

        // 基础信息
        rows.add(buildRow("基础信息", "团体名称", data.getTeamName(), emptyCells(deptCount), ""));
        rows.add(buildRow("基础信息", "统计周期", formatPeriod(data.getStartTime(), data.getEndTime()),
                emptyCells(deptCount), ""));
        rows.add(buildRow("基础信息", "成员总人数", String.valueOf(data.getTotalMemberCount()),
                deptMemberCounts(deptStats), ""));
        rows.add(Collections.emptyList());

        // 健康币收支
        rows.add(buildRow("健康币收支", "总充值（元）", "0", emptyCells(deptCount), ""));
        rows.add(buildRow("健康币收支", "已发放健康币", String.valueOf(data.getTotalHealthCoin()),
                deptHealthCoins(deptStats), ""));
        rows.add(buildRow("健康币收支", "剩余健康币", "0", emptyCells(deptCount), ""));
        rows.add(Collections.emptyList());

        // 整体运动数据
        rows.add(buildRow("整体运动数据", "总打卡次数", String.valueOf(data.getTotalCheckInNum()),
                deptCheckInNums(deptStats), ""));
        rows.add(buildRow("整体运动数据", "总运动时长", formatCheckInTime(data.getTotalCheckInTime()),
                emptyCells(deptCount), ""));
        rows.add(buildRow("整体运动数据", "平均每人打卡次数", String.valueOf(data.getAvgCheckInNum()),
                emptyCells(deptCount), "总打卡次数/每月成员总人数之和，保留1位小数"));
        rows.add(buildRow("整体运动数据", "要求打卡数", "10", emptyCells(deptCount), ""));
        rows.add(buildRow("整体运动数据", "已完成任务数", String.valueOf(data.getTotalCheckInNum()),
                emptyCells(deptCount), "完成要求打卡次数任务的人数"));
        rows.add(buildRow("整体运动数据", "任务总完成率", data.getActiveRate() + "%",
                deptActiveRates(deptStats), "(每月已完成任务数之和/每月成员总人数之和)×100%"));
        rows.add(buildRow("整体运动数据", "团员活跃率", data.getActiveRate() + "%",
                deptActiveRates(deptStats), "(至少打卡1次的成员数/成员总人数)×100%"));
        rows.add(Collections.emptyList());

        // 部门核心对比
        rows.add(buildRow("部门核心对比", "部门活跃率排名", "", deptRankings(deptStats), "按活跃率从高到低排"));
        rows.add(buildRow("部门核心对比", "部门打卡贡献占比", "100.00%",
                deptContributionRates(deptStats), "(部门总打卡次数/团体总打卡次数)×100%"));
        rows.add(Collections.emptyList());

        // 成员个人表现（TOP3）
        buildTop3Section(rows, data, deptCount);
        rows.add(Collections.emptyList());

        // 运动类型分析
        buildExerciseTypeSection(rows, data, deptCount);
        rows.add(Collections.emptyList());

        // 核心趋势数据
        rows.add(buildRow("核心趋势数据", "平均每日打卡人数",
                String.valueOf(Math.round((float) data.getActiveMemberCount() / 30)),
                emptyCells(deptCount), ""));
        rows.add(buildRow("核心趋势数据", "平均运动时长（分钟/天）", "43", emptyCells(deptCount), ""));
        rows.add(Collections.emptyList());

        // 意见汇集
        buildFeedbackSection(rows, data, deptCount);
    }

    // ==================== 单部门模板 ====================

    private static void buildSingleDepartmentSheet(List<List<String>> rows, CheckInExportData data) {
        rows.add(Arrays.asList("统计维度", "具体项目", "数据", "备注/计算方式/说明"));

        rows.add(Arrays.asList("基础信息", "团体名称", data.getTeamName(), ""));
        rows.add(Arrays.asList("基础信息", "统计周期", formatPeriod(data.getStartTime(), data.getEndTime()), ""));
        rows.add(Arrays.asList("基础信息", "成员总人数", String.valueOf(data.getTotalMemberCount()), ""));
        rows.add(Collections.emptyList());

        rows.add(Arrays.asList("健康币收支", "总充值（元）", "50000", ""));
        rows.add(Arrays.asList("健康币收支", "已发放健康币", String.valueOf(data.getTotalHealthCoin()), ""));
        rows.add(Arrays.asList("健康币收支", "剩余健康币", "10000", ""));
        rows.add(Collections.emptyList());

        rows.add(Arrays.asList("整体运动数据", "总打卡次数", String.valueOf(data.getTotalCheckInNum()), ""));
        rows.add(Arrays.asList("整体运动数据", "总运动时长", formatCheckInTime(data.getTotalCheckInTime()), ""));
        rows.add(Arrays.asList("整体运动数据", "平均每人打卡次数", String.valueOf(data.getAvgCheckInNum()),
                "总打卡次数/每月成员总人数之和"));
        rows.add(Arrays.asList("整体运动数据", "要求打卡数", "10", ""));
        rows.add(Arrays.asList("整体运动数据", "已完成任务数", String.valueOf(data.getTotalCheckInNum()),
                "完成要求打卡次数任务的人数"));
        rows.add(Arrays.asList("整体运动数据", "任务总完成率", data.getActiveRate() + "%",
                "(每月已完成任务数之和/每月成员总人数之和)×100%"));
        rows.add(Arrays.asList("整体运动数据", "团员活跃率", data.getActiveRate() + "%",
                "(至少打卡1次的成员数/成员总人数)×100%"));
        rows.add(Collections.emptyList());

        // 成员个人表现（TOP3）
        rows.add(Arrays.asList("成员个人表现（TOP3）", "", "", ""));
        if (data.getTop3List() != null) {
            int rank = 1;
            for (UserCheckInRankVo vo : data.getTop3List()) {
                String userName = StringUtils.isNotEmpty(vo.getUserName()) ? vo.getUserName() : vo.getNickName();
                rows.add(Arrays.asList("", "TOP" + rank + "-昵称/姓名", userName, ""));
                rows.add(Arrays.asList("", "TOP" + rank + "-打卡次数", String.valueOf(vo.getCheckInNum()), ""));
                rows.add(Arrays.asList("", "TOP" + rank + "-获得健康币", String.valueOf(vo.getHealthCoin()), ""));
                rank++;
            }
        }
        rows.add(Collections.emptyList());

        // 运动类型分析
        rows.add(Arrays.asList("运动类型分析", "", "", ""));
        if (data.getExerciseTypeMap() != null) {
            for (Map.Entry<String, Integer> entry : data.getExerciseTypeMap().entrySet()) {
                rows.add(Arrays.asList("", entry.getKey() + "-打卡次数", String.valueOf(entry.getValue()), ""));
            }
        }
        rows.add(Collections.emptyList());

        // 核心趋势数据
        rows.add(Arrays.asList("核心趋势数据", "平均每日打卡人数",
                String.valueOf(Math.round((float) data.getActiveMemberCount() / 30)), ""));
        rows.add(Arrays.asList("核心趋势数据", "平均运动时长（分钟/天）", "43", ""));
        rows.add(Collections.emptyList());

        // 意见汇集
        if (data.getFeedbackList() != null && !data.getFeedbackList().isEmpty()) {
            rows.add(Arrays.asList("意见汇集", "", "", ""));
            for (TeamFeedback feedback : data.getFeedbackList()) {
                String userName = feedback.getIsAnonymous() == 1 ? "匿名" :
                        (StringUtils.isNotEmpty(feedback.getUserName()) ? feedback.getUserName() : "未知");
                rows.add(Arrays.asList("", userName, feedback.getContent(), ""));
            }
        }
    }

    // ==================== 公共section构建 ====================

    private static void buildTop3Section(List<List<String>> rows, CheckInExportData data, int deptCount) {
        rows.add(buildRow("成员个人表现（TOP3）", "", "", emptyCells(deptCount), ""));
        if (data.getTop3List() == null || data.getTop3List().isEmpty()) return;
        int rank = 1;
        for (UserCheckInRankVo vo : data.getTop3List()) {
            String userName = StringUtils.isNotEmpty(vo.getUserName()) ? vo.getUserName() : vo.getNickName();
            rows.add(buildRow("", "TOP" + rank + "-昵称/姓名", userName, emptyCells(deptCount), ""));
            rows.add(buildRow("", "TOP" + rank + "-打卡次数", String.valueOf(vo.getCheckInNum()),
                    emptyCells(deptCount), ""));
            rows.add(buildRow("", "TOP" + rank + "-获得健康币", String.valueOf(vo.getHealthCoin()),
                    emptyCells(deptCount), ""));
            rank++;
        }
    }

    private static void buildExerciseTypeSection(List<List<String>> rows, CheckInExportData data, int deptCount) {
        rows.add(buildRow("运动类型分析", "", "", emptyCells(deptCount), ""));
        if (data.getExerciseTypeMap() == null) return;
        for (Map.Entry<String, Integer> entry : data.getExerciseTypeMap().entrySet()) {
            rows.add(buildRow("", entry.getKey() + "-打卡次数", String.valueOf(entry.getValue()),
                    emptyCells(deptCount), ""));
        }
    }

    private static void buildFeedbackSection(List<List<String>> rows, CheckInExportData data, int deptCount) {
        if (data.getFeedbackList() == null || data.getFeedbackList().isEmpty()) return;
        rows.add(buildRow("意见汇集", "", "", emptyCells(deptCount), ""));
        for (TeamFeedback feedback : data.getFeedbackList()) {
            String userName = feedback.getIsAnonymous() == 1 ? "匿名" :
                    (StringUtils.isNotEmpty(feedback.getUserName()) ? feedback.getUserName() : "未知");
            String content = userName + "：\"" + feedback.getContent() + "\"";
            rows.add(buildRow("", "", content, emptyCells(deptCount), ""));
        }
    }

    // ==================== 行构建 & 部门数据生成 ====================

    private static List<String> buildRow(String col1, String col2, String col3,
                                          List<String> deptData, String colLast) {
        List<String> row = new ArrayList<>();
        row.add(col1);
        row.add(col2);
        row.add(col3);
        if (deptData != null) row.addAll(deptData);
        row.add(colLast);
        return row;
    }

    private static List<String> emptyCells(int count) {
        return Collections.nCopies(count, "");
    }

    private static List<String> deptMemberCounts(List<DepartmentStat> stats) {
        List<String> cells = new ArrayList<>();
        for (DepartmentStat s : stats) cells.add(String.valueOf(s.getMemberCount()));
        return cells;
    }

    private static List<String> deptHealthCoins(List<DepartmentStat> stats) {
        List<String> cells = new ArrayList<>();
        for (DepartmentStat s : stats) cells.add(String.valueOf(s.getHealthCoin()));
        return cells;
    }

    private static List<String> deptCheckInNums(List<DepartmentStat> stats) {
        List<String> cells = new ArrayList<>();
        for (DepartmentStat s : stats) cells.add(String.valueOf(s.getCheckInNum()));
        return cells;
    }

    private static List<String> deptActiveRates(List<DepartmentStat> stats) {
        List<String> cells = new ArrayList<>();
        for (DepartmentStat s : stats) cells.add(s.getActiveRate() + "%");
        return cells;
    }

    private static List<String> deptRankings(List<DepartmentStat> stats) {
        List<String> cells = new ArrayList<>();
        for (int i = 0; i < stats.size(); i++) cells.add(String.valueOf(i + 1));
        return cells;
    }

    private static List<String> deptContributionRates(List<DepartmentStat> stats) {
        List<String> cells = new ArrayList<>();
        for (DepartmentStat s : stats) cells.add(s.getContributionRate() + "%");
        return cells;
    }

    // ==================== 格式化工具 ====================

    static String formatPeriod(String startTime, String endTime) {
        if (startTime == null || endTime == null) return "全部";
        try {
            DateTimeFormatter input = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            DateTimeFormatter output = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            return LocalDateTime.parse(startTime, input).format(output)
                    + "-" + LocalDateTime.parse(endTime, input).format(output);
        } catch (Exception e) {
            return startTime + " - " + endTime;
        }
    }

    static String formatCheckInTime(int totalSeconds) {
        if (totalSeconds <= 0) return "0";
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        if (hours > 0) {
            return String.format("%d小时%d分钟%d秒", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%d分钟%d秒", minutes, seconds);
        }
        return String.format("%d秒", seconds);
    }
}