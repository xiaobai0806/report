package com.sjx.a_p.service;

import com.sjx.a_p.mapper.MeetingMapper;
import com.sjx.a_p.model.MeetingRoomInfo;
import com.sjx.a_p.model.ReservationInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
public class MeetingService {
    @Autowired
    private MeetingMapper meetingMapper;

    // 查询已审批通过（已预约）(不可用)的会议室
    public List<MeetingRoomInfo> selectBookedRoom(){
        return meetingMapper.selectBookedRoom();
    }

    // 查找可用会议室
    public List<MeetingRoomInfo> selectAvailable(ReservationInfo reservationInfo){
        return meetingMapper.selectAvailable(reservationInfo);
    }

    // 查询所有会议室信息
    public List<MeetingRoomInfo> selectAllRoom(){
        return meetingMapper.selectAllRoom();
    }

    // 根据会议室ID查询会议室详情
    public MeetingRoomInfo selectRoomByRoomId(Integer roomId){
        log.info("查询的会议室信息: {}", meetingMapper.selectRoomByRoomId(roomId));
        return meetingMapper.selectRoomByRoomId(roomId);
    }

    // 添加会议室信息
    public Integer insertRoom(MeetingRoomInfo meetingRoomInfo){
        return meetingMapper.insertRoom(meetingRoomInfo);
    }

    // 更新会议室信息
    public Integer updateMeeting(MeetingRoomInfo meetingRoomInfo){
        return meetingMapper.updateMeeting(meetingRoomInfo);
    }
    // 删除会议室
    public Integer deleteMeeting(Integer roomId){
        return meetingMapper.deleteMeeting(roomId);
    }
    // 更新会议室的图片URL
    public Integer updatePhotoUrl(Integer roomId, String photoUrl) {
        log.info("service 层 updatePU 接收的参数:{},{}", roomId, photoUrl);
        MeetingRoomInfo meetingRoomInfo = meetingMapper.selectRoomByRoomId(roomId);
        log.info("查询到的会议室:{}", meetingRoomInfo);
        if (meetingRoomInfo == null) {
            throw new RuntimeException("会议室ID不存在");
        }
        log.info("执行的 SQL: UPDATE meeting_room_info SET photo_url = {} WHERE room_id = {}", photoUrl, roomId);
        return meetingMapper.updatePhotoUrl(roomId, photoUrl);
    }
}
