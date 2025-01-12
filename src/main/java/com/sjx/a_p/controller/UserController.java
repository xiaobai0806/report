package com.sjx.a_p.controller;

import com.sjx.a_p.mapper.ApprovalMapper;
import com.sjx.a_p.mapper.MeetingMapper;
import com.sjx.a_p.model.*;
import com.sjx.a_p.service.ApprovalService;
import com.sjx.a_p.service.MeetingService;
import com.sjx.a_p.service.ReservationService;
import com.sjx.a_p.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private MeetingService meetingService;
    @Autowired
    private ApprovalService approvalService;

    /*
    用户登录页
     */
    // 登录接口
    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> payload) {
        String userName = payload.get("userName");
        String password = payload.get("password");

        if (!StringUtils.hasLength(userName) || !StringUtils.hasLength(password)) {
            return Result.fail("用户名或密码不能为空");
        }

        UserInfo userInfo = userService.selectUserByName(userName);
        if (userInfo == null) {
            return Result.fail("用户不存在");
        }
        if (!password.equals(userInfo.getPassword())) {
            return Result.fail("密码错误");
        }

        return Result.success("登录成功");
    }



    /*
    会议室列表页
    —— 填写具体时间段、参会人数，筛选可用会议室列表
    —— 预约，等待审批
     */

    // 筛选可用会议室列表
    @RequestMapping(value = "/selectAvailableRoomList", method = RequestMethod.POST)
    @ResponseBody
    public Result selectAvailableRoomList(@RequestBody Map<String, Object> params) {
        log.info("接收参数: {}", params);
        // 提取参数并检查
        Integer persons = (Integer) params.get("persons");
        String day = (String) params.get("day");
        String startTime = (String) params.get("startTime");
        String endTime = (String) params.get("endTime");
        if (persons == null || day == null || startTime == null || endTime == null) {
            return Result.fail("请输入完整信息");
        }
        try {
            // 转换时间和日期
            Date date = Date.valueOf(day);
            Time start = Time.valueOf(startTime);
            Time end = Time.valueOf(endTime);
            ReservationInfo reservationInfo = new ReservationInfo();
            reservationInfo.setPersons(persons);
            reservationInfo.setDay(date);
            reservationInfo.setStartTime(start);
            reservationInfo.setEndTime(end);
            log.info("查询条件: {}", reservationInfo);
            return Result.success(meetingService.selectAvailable(reservationInfo));
        } catch (IllegalArgumentException e) {
            log.error("参数格式错误:", e);
            return Result.fail("参数格式错误，请检查输入");
        }
    }


    // 预约，等待审批
    // 1. 前端提交填写预约信息到 预约记录表（reservation_info）（此时状态为未审批）
    // 2. 尝试将预约记录表的部分内容同步到审批表 approval_info, 如果用户名已在审批表中, 返回错误: 已有预约正在进行
    @RequestMapping("/createReservation")
    public Result createReservation(@RequestBody ReservationInfo reservationInfo) {
        log.info("接收到的预约信息: {}", reservationInfo);
        return reservationService.createReservation(reservationInfo);
    }

    // 查看某条记录的详情
    @RequestMapping("/selectReservationByParams")
    public Result selectReservationByParams(Long reservationId, String userName, Long roomId){
        if(reservationId==null||userName==null||roomId==null){
            return Result.fail("请输入完整的参数");
        }
        return Result.success(reservationService.selectReservationByParams(reservationId, userName, roomId));
    }

    /*
    会议室详情页
    —— 只能查看
     */
    // 根据会议室Id返回会议室信息
    @RequestMapping("/selectRoomByRoomId")
    public Result selectRoomByRoomId(Integer roomId){
        log.info("selectRoomByRoomId 输入的参数:{}", roomId);
        if(roomId == null){
            return Result.fail("请输入会议室Id");
        }
        MeetingRoomInfo meetingRoomInfo = meetingService.selectRoomByRoomId(roomId);
        log.info("响应:{}", meetingRoomInfo);
        if(meetingRoomInfo == null){
            return Result.fail("会议室不存在");
        }
        return Result.success(meetingRoomInfo);
    }

    /*
    预约列表页
    —— 可查询自己所有的预约情况（还未测试）
     */
    @RequestMapping("/selectReservationByName")
    public Result selectReservationByName(@RequestParam("userName") String userName){
        log.info("selectReservationByName 接收到的参数:{}", userName);
        UserInfo userInfo = userService.selectUserByName(userName);
        if (userInfo==null){
            return Result.fail("用户不存在");
        }
        log.info("响应:{}", reservationService.selectReservationByName(userName));
        return Result.success(reservationService.selectReservationByName(userName));
    }
}
