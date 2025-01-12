package com.sjx.a_p.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
// 审批表
public class ApprovalInfo {
    private Integer roomId; // 会议室ID
    private String userName; // 预约的用户名
    private String reason; // 驳回理由
    private Integer isPass; // 是否通过预约状态 (0: 未审批, 1: 审批通过, 2: 已驳回)
    private Timestamp approvalTime; // 审批时间
    private Integer reservationId; // 预约ID
}
