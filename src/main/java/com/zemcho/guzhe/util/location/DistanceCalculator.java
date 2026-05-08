package com.zemcho.guzhe.util.location;

public class DistanceCalculator {
    private static final double EARTH_RADIUS_KM = 6371.0; // 地球平均半径（单位：千米）

    // 将角度转换为弧度
    private static double toRadians(double degree) {
        return degree * Math.PI / 180.0;
    }

    /**
     * 使用 Haversine 公式计算两个经纬度之间的球面距离（单位：千米）
     */
    public static double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double lat1Rad = toRadians(lat1);
        double lat2Rad = toRadians(lat2);
        double deltaLat = toRadians(lat2 - lat1);
        double deltaLon = toRadians(lon2 - lon1);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = EARTH_RADIUS_KM * c;

        // 保留两位小数
        return Math.round(distance * 100.0) / 100.0;
    }
}