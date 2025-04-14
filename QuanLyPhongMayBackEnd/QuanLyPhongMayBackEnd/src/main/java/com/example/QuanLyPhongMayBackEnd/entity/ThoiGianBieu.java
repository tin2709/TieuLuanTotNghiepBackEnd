package com.example.QuanLyPhongMayBackEnd.entity;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class ThoiGianBieu {

    private static final Map<Integer, KhoangThoiGian> thoiGianTheoTiet = new HashMap<>();

    static {
        thoiGianTheoTiet.put(1, new KhoangThoiGian(LocalTime.of(7, 15), LocalTime.of(8, 00)));
        thoiGianTheoTiet.put(2, new KhoangThoiGian(LocalTime.of(8, 00), LocalTime.of(8, 45)));
        thoiGianTheoTiet.put(3, new KhoangThoiGian(LocalTime.of(8, 45), LocalTime.of(9, 30)));
        thoiGianTheoTiet.put(4, new KhoangThoiGian(LocalTime.of(9, 30), LocalTime.of(10, 15)));
        thoiGianTheoTiet.put(5, new KhoangThoiGian(LocalTime.of(10, 15), LocalTime.of(11, 00)));
        thoiGianTheoTiet.put(6, new KhoangThoiGian(LocalTime.of(11, 00), LocalTime.of(11, 45)));
        thoiGianTheoTiet.put(7, new KhoangThoiGian(LocalTime.of(12, 15), LocalTime.of(13, 00)));
        thoiGianTheoTiet.put(8, new KhoangThoiGian(LocalTime.of(13, 00), LocalTime.of(13, 45)));
        thoiGianTheoTiet.put(9, new KhoangThoiGian(LocalTime.of(13, 45), LocalTime.of(14, 30)));
        thoiGianTheoTiet.put(10, new KhoangThoiGian(LocalTime.of(14, 30), LocalTime.of(15, 15)));
        thoiGianTheoTiet.put(11, new KhoangThoiGian(LocalTime.of(15, 15), LocalTime.of(16, 00)));
        thoiGianTheoTiet.put(12, new KhoangThoiGian(LocalTime.of(16, 00), LocalTime.of(16, 45)));
    }

    public static KhoangThoiGian getKhoangThoiGianTheoTiet(int tiet) {
        return thoiGianTheoTiet.get(tiet);
    }

    public static class KhoangThoiGian {
        private LocalTime startTime;
        private LocalTime endTime;

        public KhoangThoiGian(LocalTime startTime, LocalTime endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public LocalTime getStartTime() {
            return startTime;
        }

        public LocalTime getEndTime() {
            return endTime;
        }
    }
}
