package com.sjx.a_p.model;

import lombok.Data;

import java.sql.Time;
import java.util.Date;

@Data
// 完整预约记录通过该类返回（该类不是一个存在的表）
public class ARecordResult {
    private Integer reservationId; // 预约ID
    private Integer roomId; // 会议室ID
    private String title; // 会议室标题，假设此字段未在查询中体现，可能是需要的额外字段
    private Time startTime; // 预约开始时间
    private Time endTime; // 预约结束时间
    private Date day; // 预约日期（对应SQL的ri.day字段）
    private Integer persons; // 参会人数（对应SQL的ri.persons字段）
    private String status; // 预约状态（对应SQL的ri.status字段）
    private Integer isPass; // 审批状态（对应SQL的ai.is_pass字段）
    private Time approvalTime; // 审批时间（对应SQL的ai.approval_time字段）
    private String reason; // 驳回原因（对应SQL的ai.reason字段）
    private String userName; // 用户名
    private String contacts; // 用户联系方式（对应SQL的ui.contacts字段）
}

