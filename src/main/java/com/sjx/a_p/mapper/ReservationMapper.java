package com.sjx.a_p.mapper;

import com.sjx.a_p.model.ARecordResult;
import com.sjx.a_p.model.ReservationInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ReservationMapper {
    // 查询用户所有的预约记录
    @Select("SELECT \n" +
            "    ri.reservation_id,\n" +
            "    ri.room_id,\n" +
            "    ri.user_name,\n" +
            "    ri.start_time,\n" +
            "    ri.end_time,\n" +
            "    ri.day ,\n" +
            "    ri.persons,\n" +
            "    ri.status ,\n" +
            "    ai.is_pass ,\n" +
            "    ai.approval_time,\n" +
            "    ai.reason ,\n" +
            "    ui.contacts ,\n" +
            "    mri.title \n"+
            "FROM \n" +
            "    reservation_info ri\n" +
            "LEFT JOIN " +
            "    approval_info ai ON ri.reservation_id = ai.reservation_id " +
            "LEFT JOIN \n" +
            "    user_info ui ON ri.user_name = ui.user_name\n" +
            "LEFT JOIN \n" +
            "    meeting_room_info mri ON ri.room_id = mri.room_id\n" +
            "WHERE \n" +
            "    ri.user_name = #{userName}"+
            "ORDER BY " +
            "    ri.day, ri.start_time")
    List<ARecordResult> selectReservationByName(String userName);

    // 查询所有预约情况
    @Select("SELECT \n" +
            "    ri.reservation_id,\n" +
            "    ri.room_id,\n" +
            "    ri.user_name,\n" +
            "    ri.start_time,\n" +
            "    ri.end_time,\n" +
            "    ri.day,\n" +
            "    ri.persons,\n" +
            "    ri.status,\n" +
            "    ai.is_pass,\n" +
            "    ai.approval_time,\n" +
            "    ai.reason,\n" +
            "    ui.contacts,\n" +
            "    mri.title\n" +
            "FROM \n" +
            "    reservation_info ri\n" +
            "LEFT JOIN " +
            "    approval_info ai ON ri.reservation_id = ai.reservation_id \n" +
            "LEFT JOIN " +
            "    user_info ui ON ri.user_name = ui.user_name \n" +
            "LEFT JOIN " +
            "    meeting_room_info mri ON ri.room_id = mri.room_id \n" +
            "ORDER BY " +
            "    ri.day, ri.start_time")
    List<ARecordResult> selectAllReservations();

    // 用户查看某条预约记录的详情
    @Select("SELECT \n" +
            "    ri.reservation_id,\n" +
            "    ri.room_id,\n" +
            "    ri.user_name,\n" +
            "    ri.start_time,\n" +
            "    ri.end_time,\n" +
            "    ri.day,\n" +
            "    ri.persons,\n" +
            "    ai.is_pass,\n" +
            "    ai.approval_time,\n" +
            "    ai.reason,\n" +
            "    ui.contacts,\n" +
            "    mri.title\n" +
            "FROM \n" +
            "    reservation_info ri\n" +
            "LEFT JOIN " +
            "    approval_info ai ON ri.reservation_id = ai.reservation_id \n" +
            "LEFT JOIN " +
            "    user_info ui ON ri.user_name = ui.user_name \n" +
            "LEFT JOIN " +
            "    meeting_room_info mri ON ri.room_id = mri.room_id \n" +
            "WHERE \n" +
            "    ri.reservation_id = #{reservationId} AND \n" +
            "    ri.user_name = #{userName} AND \n" +
            "    ri.room_id = #{roomId} \n" +
            "ORDER BY " +
            "    ri.day, ri.start_time")
    List<ARecordResult> selectReservationByParams(@Param("reservationId") Long reservationId, @Param("userName") String userName,
                                                  @Param("roomId") Long roomId);



    // 将用户填写的预约记录插入表中（未审批）
    @Insert("INSERT INTO reservation_info (room_id, user_name, start_time, end_time, day, persons) " +
            "VALUES (#{roomId}, #{userName}, #{startTime}, #{endTime}, #{day}, #{persons})")
    @Options(useGeneratedKeys = true, keyProperty = "reservationId")
    Integer insertReservation(ReservationInfo reservationInfo);

    // 审批通过后更新预约状态
    @Update("UPDATE reservation_info " +
            "SET status = 1 " +
            "WHERE reservation_id = #{reservationId}")
    Integer updateApprove(Integer reservationId);


    // 审批驳回，同时更新预约状态
    @Update("UPDATE reservation_info " +
            "SET status = 2 " +
            "WHERE reservation_id = #{reservationId}")
    Integer updateRefuse(Integer reservationId);

    // 删除预约记录
    @Delete("DELETE FROM reservation_info WHERE reservation_id = #{reservationId}")
    Integer deleteReservationById(Integer reservationId);
}
