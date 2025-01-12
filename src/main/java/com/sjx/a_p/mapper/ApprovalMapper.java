package com.sjx.a_p.mapper;

import com.sjx.a_p.constants.Constant;
import com.sjx.a_p.model.ApprovalInfo;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ApprovalMapper {

    // 查询所有待审批的预约
    @Select("select * from approval_info where is_pass=0")
    ApprovalInfo selectAllApprove();

    // 查询用户是否有正在审批中的预约记录
    @Select("SELECT COUNT(*) FROM approval_info WHERE user_name = #{userName} AND is_pass = 0")
    Integer countPendingApprovals(String userName);

    // 将预约记录同步到审批表
    @Insert("INSERT INTO approval_info (room_id, user_name, is_pass, reservation_id) " +
            "VALUES (#{roomId}, #{userName}, 0, #{reservationId})")
    Integer insertPendingApproval(ApprovalInfo approvalInfo);

    // 通过 reservationId 更新审批记录为通过
    @Update("UPDATE approval_info " +
            "SET is_pass = 1, reason = NULL, approval_time = NOW() " +
            "WHERE reservation_id = #{reservationId}")
    Integer updateApproveByReservationId(Integer reservationId);

    // 通过 reservationId 更新审批记录为驳回，并记录理由
    @Update("UPDATE approval_info " +
            "SET is_pass = 2, reason = #{reason}, approval_time = NOW() " +
            "WHERE reservation_id = #{reservationId}")
    Integer updateRefuseByReservationId(@Param("reservationId") Integer reservationId, @Param("reason") String reason);

    // 删除审批表记录
    @Delete("DELETE FROM approval_info WHERE reservation_id = #{reservationId}")
    Integer deleteApprovalRecord(Integer reservationId);

}
