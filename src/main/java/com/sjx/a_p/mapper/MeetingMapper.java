package com.sjx.a_p.mapper;

import com.sjx.a_p.model.MeetingRoomInfo;
import com.sjx.a_p.model.ReservationInfo;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface MeetingMapper {
    // 查询已审批通过（已预约）（不可用）的会议室
    @Select("SELECT " +
            "    m.room_id,                         -- 会议室ID\n" +
            "    m.title,                           -- 会议室主题\n" +
            "    m.area,                            -- 会议室面积\n" +
            "    m.persons,                          -- 会议室最大容纳人数\n" +
            "    m.photo_url                        -- 会议室照片链接\n" +
            "    m.is_available                     -- 会议室可用标志\n" +
            "FROM " +
            "    meeting_room_info m\n" +
            "LEFT JOIN " +
            "    reservation_info r ON m.room_id = r.room_id\n" +
            "LEFT JOIN " +
            "    user_info u ON r.user_name = u.user_name\n" +
            "WHERE " +
            "    r.status = 1 -- 筛选已审批通过的预约记录\n" +
            "ORDER BY " +
            "    m.room_id, r.start_time")
    List<MeetingRoomInfo> selectBookedRoom();

    // 根据id查询会议室详情
    @Select("select * from meeting_room_info where room_id=#{roomId}")
    MeetingRoomInfo selectRoomByRoomId(Integer roomId);

    // 查询所有会议室信息
    @Select("select * from meeting_room_info")
    List<MeetingRoomInfo> selectAllRoom();

    // 查找可用会议室
    @Select("SELECT room_id, title, area, persons, photo_url\n" +
            "FROM meeting_room_info\n" +
            "WHERE persons >= #{persons}         -- 会议室的容纳人数要大于或等于用户需求\n" +
            "  AND is_available = 1              -- 会议室必须是可用的\n" +
            "  AND room_id NOT IN (\n" +
            "      SELECT room_id \n" +
            "      FROM reservation_info\n" +
            "      WHERE day = #{day} \n" +
            "        AND status = 1                 -- 确保预约已通过\n" +
            "        AND (start_time < #{endTime} AND end_time > #{startTime})  -- 确保没有时间冲突\n" +
            "  )\n")
    @Results({
            @Result(property = "roomId", column = "room_id"),
            @Result(property = "title", column = "title"),
            @Result(property = "area", column = "area"),
            @Result(property = "persons", column = "persons"),
            @Result(property = "photoUrl", column = "photo_url"),
            @Result(property = "isAvailable", column = "is_available")
    })
    List<MeetingRoomInfo> selectAvailable(ReservationInfo reservationInfo);

    // 添加会议室信息
    @Insert("INSERT INTO meeting_room_info (room_id, title, area, persons, photo_url, is_available)\n" +
            "VALUES (#{roomId}, #{title}, #{area}, #{persons}, #{photoUrl}, #{isAvailable})\n")
    Integer insertRoom(MeetingRoomInfo meetingRoomInfo);

    // 更新会议室信息
    Integer updateMeeting(MeetingRoomInfo meetingRoomInfo);

    // 删除会议室信息
    @Delete("delete from meeting_room_info where room_id = #{roomId}")
    Integer deleteMeeting(Integer roomId);

    // 更新会议室信息（根据ID更新图片URL）
    @Update("UPDATE meeting_room_info SET photo_url = #{photoUrl} WHERE room_id = #{roomId}")
    Integer updatePhotoUrl(@Param("roomId") Integer roomId, @Param("photoUrl") String photoUrl);
}
