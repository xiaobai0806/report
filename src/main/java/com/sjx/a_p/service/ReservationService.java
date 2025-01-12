package com.sjx.a_p.service;

import com.sjx.a_p.mapper.ApprovalMapper;
import com.sjx.a_p.mapper.ReservationMapper;
import com.sjx.a_p.mapper.UserMapper;
import com.sjx.a_p.model.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalTime;
import java.util.List;

@Service
public class ReservationService {
    @Autowired
    private ReservationMapper reservationMapper;
    @Autowired
    private ApprovalMapper approvalMapper;
    @Autowired
    private UserMapper userMapper;

    // 查询用户所有的预约记录
    public List<ARecordResult> selectReservationByName(String userName){
        return reservationMapper.selectReservationByName(userName);
    }

    // 查询所有预约情况
    public List<ARecordResult> selectAllReservations(){
        return reservationMapper.selectAllReservations();
    }

    // 将用户填写的预约记录插入表中（未审批）
    public Result createReservation(ReservationInfo reservationInfo) {
        if(reservationInfo.getRoomId()==null || reservationInfo.getUserName()==null
                || reservationInfo.getStartTime()==null || reservationInfo.getEndTime()==null || reservationInfo.getDay() == null
                || reservationInfo.getPersons()==null){
            return Result.fail("请填写完整信息");
        }
        if (LocalTime.parse(reservationInfo.getStartTime().toString()).isAfter(LocalTime.parse(reservationInfo.getEndTime().toString()))) {
            return Result.fail("请输入正确的开始和结束时间");
        }
        String userName = reservationInfo.getUserName();
        UserInfo userInfo = userMapper.selectUserByName(userName);
        if(userInfo==null){
            Result.fail("请填写已存在的用户名");
        }
        // 检查用户是否有正在审批中的预约
        Integer pendingCount = approvalMapper.countPendingApprovals(userName);
        if (pendingCount > 0) {
            return Result.fail("已有预约正在审批中");
        }
        // 插入预约记录
        reservationMapper.insertReservation(reservationInfo);
        // 将预约记录同步到审批表
        ApprovalInfo approvalInfo = new ApprovalInfo();
        approvalInfo.setRoomId(reservationInfo.getRoomId());
        approvalInfo.setUserName(reservationInfo.getUserName());
        approvalInfo.setReservationId(reservationInfo.getReservationId());
        approvalMapper.insertPendingApproval(approvalInfo);
        return Result.success("预约提交成功，等待审批");
    }

    // 用户查看某条预约记录详情
    public List<ARecordResult> selectReservationByParams(Long reservationId, String userName, Long roomId){
        return reservationMapper.selectReservationByParams(reservationId, userName, roomId);
    }
    // 审批通过
    public Integer updateApprove(Integer reservationId){
        return reservationMapper.updateApprove(reservationId);
    }

    // 审批驳回
    public Integer updateRefuse(Integer reservationId){
        return reservationMapper.updateRefuse(reservationId);
    }

    // 删除预约记录
    public Integer deleteReservationById(Integer reservationId){
        return reservationMapper.deleteReservationById(reservationId);
    }
}
