package com.sjx.a_p.model;

import lombok.Data;

import java.sql.Time;
import java.util.Date;

@Data
// 存储用户的实际预约信息，并记录每次预约的具体时间和会议室信息
public class ReservationInfo {
    private Integer reservationId; // 预约ID
    private Integer roomId; // 会议室ID
    private String userName; // 用户名
    private Time startTime; // 预约开始时间
    private Time endTime; // 预约结束时间
    private Date day; // 预约日期
    private Integer persons; // 参会人数
    private Integer status; // 预约状态 (0: 未审批, 1: 审批通过, 2: 已驳回)
}
