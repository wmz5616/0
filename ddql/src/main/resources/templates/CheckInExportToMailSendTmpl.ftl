<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>打卡记录</title>
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
            <span th:text="${teamName}"> </span>打卡记录
        </div>
    </div>

    <table style="width: 100%;text-align: center;margin: 0 auto;" class="border-table">
        <tr>
            <th style="width: 15%;">打卡人</th>
            <th style="width: 15%;">打卡场地</th>
            <th style="width: 15%;">打卡地址</th>
            <th style="width: 10%;">打卡类型</th>
            <th style="width: 15%;">打卡时长(秒)</th>
            <th style="width: 10%;">打卡时间</th>
            <th style="width: 10%;">离场时间</th>
            <th style="width: 10%;">获得币数</th>
        </tr>
        <tr th:each="item,itemMapStat:${list}" th:if="${list}">
            <td th:text="${item.nickname}"> </td>
            <td th:text="${item.placeName}"> </td>
            <td th:text="${item.placeAddress}"> </td>
            <td th:text="${item.checkInMethod == 1 ? '场地打卡' : '扫码打卡'}"> </td>
            <td th:text="${item.checkInTime}"> </td>
            <td th:text="${#temporals.format(item.startTime, 'yyyy-MM-dd HH:mm:ss')}"> </td>
            <td th:text="${#temporals.format(item.endTime, 'yyyy-MM-dd HH:mm:ss')}"> </td>
            <td th:text="${item.healthCoin}"> </td>
        </tr>
    </table>
</div>
</body>
</html>