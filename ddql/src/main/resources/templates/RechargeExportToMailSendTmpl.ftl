<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>充值记录</title>
    <style type="text/css">
        body {
            font-size: 15px;
            line-height: 25px;
            text-align: left;
        }
        .border-table {
            table-layout: fixed;
            border-collapse: collapse;
            border: none;
        }
        .border-table th {
            border: 1px black solid;
            word-wrap: break-word;
            /*overflow: hidden;
            white-space: nowrap;
            text-overflow:ellipsis;*/
        }
        .border-table td {
            border: 1px black solid;
            word-wrap: break-word;
            /*overflow: hidden;
            white-space: nowrap;
            text-overflow:ellipsis;*/
        }
    </style>
</head>
<body>
<div th:style="'width: 100%; margin: 0 auto;position: relative;'">
    <div style="display: flex; margin-bottom: 10px;">
        <div style="box-sizing:border-box; width: 100%; font-size: 20px; font-weight: 600; text-align: center;">
            充值记录
        </div>
    </div>

    <table style="width: 100%;text-align: center;margin: 0 auto;" class="border-table">
        <tr>
            <th style="width: 20%;">团体名称</th>
            <th style="width: 20%;">订单号</th>
            <th style="width: 15%;">操作人</th>
            <th style="width: 15%;">手机号</th>
            <th style="width: 10%;">充值金额(元)</th>
            <th style="width: 20%;">操作时间</th>
        </tr>
        <tr th:each="item,itemMapStat:${list}" th:if="${list}">
            <td th:text="${item.teamName}"> </td>
            <td th:text="${item.orderNo}"> </td>
            <td th:text="${item.nickName}"> </td>
            <td th:text="${item.phone}"> </td>
            <td th:text="${item.amount}"> </td>
            <td th:text="${#temporals.format(item.createTime, 'yyyy-MM-dd HH:mm:ss')}"> </td>
        </tr>
    </table>
</div>
</body>
</html>