package com.sjx.a_p.model;

import lombok.Data;

import java.sql.Time;
import java.util.Date;

@Data
public class MeetingRoomInfo {
    private Integer roomId; // 会议室ID
    private String title; // 会议主题
    private Integer area; // 面积（单位：平方米）
    private Integer persons; // 参会人数
    private String photoUrl; // 会议室照片链接
    private Integer isAvailable = 1; // 1表示可用，2表示不可用
}
