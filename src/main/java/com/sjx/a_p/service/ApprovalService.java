package com.sjx.a_p.service;

import com.sjx.a_p.mapper.ApprovalMapper;
import com.sjx.a_p.model.ApprovalInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApprovalService {
    @Autowired
    private ApprovalMapper approvalMapper;

    // 查询所有待审批的预约
    public ApprovalInfo selectAllApprove(){
        return approvalMapper.selectAllApprove();
    }

    // 审批通过
    public Integer updateApproveByReservationId(Integer reservationId){
        return approvalMapper.updateApproveByReservationId(reservationId);
    }

    // 审批驳回，并记录理由
    public Integer updateRefuseByReservationId(Integer reservationId, String reason){
        return approvalMapper.updateRefuseByReservationId(reservationId,reason);
    }

    // 删除审批表记录
    public Integer deleteApprovalRecord(Integer reservationId){
        return approvalMapper.deleteApprovalRecord(reservationId);
    }
}
