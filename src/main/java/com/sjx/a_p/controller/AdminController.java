package com.sjx.a_p.controller;

import com.sjx.a_p.constants.Constant;
import com.sjx.a_p.model.*;
import com.sjx.a_p.service.ApprovalService;
import com.sjx.a_p.service.MeetingService;
import com.sjx.a_p.service.ReservationService;
import com.sjx.a_p.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private MeetingService meetingService;
    @Autowired
    private ApprovalService approvalService;

    /*
    会议室列表页
    —— 筛选所有会议室列表

     */

    // 返回所有会议室
    @RequestMapping("/allRoom")
    public Result selectAllRoom(){
        return Result.success(meetingService.selectAllRoom());
    }


    /*
    会议室详情页
    —— 可增加、删除、修改会议室
     */

    // 根据会议室Id返回会议室信息
    @RequestMapping("/selectRoomByRoomId")
    public Result selectRoomByRoomId(@RequestParam("roomId") Integer roomId) {
        log.info("selectRoomByRoomId 输入的参数:{}", roomId);

        if (roomId == null) {
            return Result.fail("请输入会议室Id");
        }

        MeetingRoomInfo meetingRoomInfo = meetingService.selectRoomByRoomId(roomId);
        log.info("响应:{}", meetingRoomInfo);

        if (meetingRoomInfo == null) {
            return Result.fail("会议室不存在");
        }
        return Result.success(meetingRoomInfo);
    }


    // 增加会议室
    @RequestMapping("/insertRoom")
    public Result insertRoom(@RequestBody MeetingRoomInfo meetingRoomInfo) {
        log.info("insertRoom 接收的参数: {}", meetingRoomInfo);

        if (meetingRoomInfo.getArea() == null || meetingRoomInfo.getPersons() == null) {
            return Result.fail("面积或可容纳人数不能为空");
        }
        // 判断会议室ID是否已存在
        MeetingRoomInfo existingRoom = meetingService.selectRoomByRoomId(meetingRoomInfo.getRoomId());
        if (existingRoom != null) {
            return Result.fail("该会议室ID已存在");
        }
        // 插入会议室
        int updatedRows = meetingService.insertRoom(meetingRoomInfo);
        if (updatedRows <= 0) {
            return Result.fail("更新数据库失败");
        }
        return Result.success("添加成功");
    }

    // 删除会议室
    @DeleteMapping("/deleteRoom")
    public Result deleteRoom(@RequestParam Integer roomId){
        if (roomId==null){
            return Result.fail("请输入roomId");
        }
        int deleteRows = meetingService.deleteMeeting(roomId);
        if (deleteRows <= 0) {
            return Result.fail("更新数据库失败");
        }
        return Result.success("添加成功");
    }

    // 上传图片（将文件存储在 a_p/uploadRoomImage，并更新数据库）
    @RequestMapping("/uploadPhoto")
    public Result uploadPhoto(
            Integer roomId,
            @RequestParam("file") MultipartFile file) {
        log.info("参数: {}", roomId);
        try {
            if (file.isEmpty()) {
                return Result.fail("文件不能为空");
            }

            // 获取项目根目录下的上传目录路径
            Path projectRootDir = Paths.get(System.getProperty("user.dir"));
            Path uploadDir = projectRootDir.resolve("uploadRoomImage");

            // 确保目标目录存在
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();

            // 文件类型校验
            List<String> allowedExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif");
            if (!allowedExtensions.contains(extension)) {
                return Result.fail("不支持的文件类型");
            }

            // 生成唯一文件名
            String uniqueFileName = UUID.randomUUID().toString() + "_" + System.currentTimeMillis() + extension;
            Path savePath = uploadDir.resolve(uniqueFileName).toAbsolutePath();
            log.info("保存路径: {}", savePath);

            // 保存文件
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, savePath);
            }

            // 文件的相对路径（供前端访问）
            String fileUrl = "" + uniqueFileName;

            // 更新数据库中的图片 URL
            int updatedRows = meetingService.updatePhotoUrl(roomId, fileUrl);
            log.info("SQL更新影响的行数: {}", updatedRows);
            if (updatedRows > 0) {
                log.info("上传成功, 文件名: {}, roomId: {}, 文件URL: {}", uniqueFileName, roomId, fileUrl);
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("message", "文件上传成功");
                resultMap.put("url", fileUrl);
                return Result.success(resultMap);
            } else {
                // 如果数据库更新失败，则删除已保存的文件
                Files.delete(savePath);
                return Result.fail("数据库未更新，请检查 roomId 是否有效");
            }
        } catch (IOException e) {
            log.error("文件系统异常, roomId: {}, fileName: {}", roomId, file.getOriginalFilename(), e);
            return Result.fail("文件上传失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("文件上传发生异常, roomId: {}, fileName: {}", roomId, file.getOriginalFilename(), e);
            return Result.fail("文件上传失败: " + e.getMessage());
        }
    }

    // 删除会议室
    @RequestMapping("/deleteMeeting")
    public Result deleteMeeting(Integer roomId){
        log.info("删除会议室");
        MeetingRoomInfo meetingRoomInfo = meetingService.selectRoomByRoomId(roomId);
        if(meetingRoomInfo==null){
            return Result.fail("该会议室不存在");
        }
        meetingRoomInfo.setIsAvailable(Constant.UNAVAILABLE_REFUSE_APPROVED);
        int updatedRows = meetingService.updateMeeting(meetingRoomInfo);
        if(updatedRows<=0){
            return Result.fail("更新数据库失败");
        }
        return Result.success("删除成功");
    }


    // 修改会议室
    @RequestMapping("/updateMeeting")
    public Result updateMeeting(@RequestBody MeetingRoomInfo meetingRoomInfo) {
        log.info("修改会议室信息: {}", meetingRoomInfo);

        // 检查会议室是否存在
        MeetingRoomInfo existingRoom = meetingService.selectRoomByRoomId(meetingRoomInfo.getRoomId());
        if (existingRoom == null) {
            return Result.fail("该会议室不存在");
        }

        // 更新会议室信息
        int updatedRows = meetingService.updateMeeting(meetingRoomInfo);
        if (updatedRows <= 0) {
            return Result.fail("更新数据库失败");
        }
        return Result.success("更新成功");
    }


    /*
    预约列表页
    —— 审批预约
    —— 查看所有预约情况
    —— 删除预约情况
     */

    // 查询所有预约记录
    @RequestMapping("/selectAllReservations")
    public Result selectAllReservations(){
        List<ARecordResult> reservationInfos = reservationService.selectAllReservations();
        return Result.success(reservationInfos);
    }

    // 查看某条记录的详情
    @RequestMapping("/selectReservationByParams")
    public Result selectReservationByParams(Long reservationId, String userName, Long roomId){
        if(reservationId==null||userName==null||roomId==null){
            return Result.fail("请输入完整的参数");
        }
        return Result.success(reservationService.selectReservationByParams(reservationId, userName, roomId));
    }

//    // 查询所有待审批的预约
//    @RequestMapping("/selectAllApprove")
//    public Result selectAllApprove(){
//        return Result.success(approvalService.selectAllApprove());
//    }

    // 审批预约
    @RequestMapping("/InsertApprove")
    public Result InsertApprove(@RequestBody ApprovalInfo approvalInfo){
        log.info("输入的参数approvalInfo:{}", approvalInfo);
        Integer isPass = approvalInfo.getIsPass();
        Integer reservationId = approvalInfo.getReservationId();
        String reason = approvalInfo.getReason();

        // 管理员操作审批表approval_info, 如果选择预约通过（参数输入1），则:
        // 不能填写驳回理由
        // 查找预约记录表reservation_info对应的那条记录，将status预约状态改为1
        // 更新审批表对应的记录

        // 管理员操作审批表approval_info, 如果选择预约驳回（参数输入2），则:
        // 必须填写驳回理由
        // 查找预约记录表reservation_info对应的那条记录，将status预约状态改为2
        // 更新审批表对应的记录

        if (isPass == 1) {
            // 审批通过逻辑
            // 校验是否填写驳回理由
            if (reason != null && !reason.isEmpty()) {
                return Result.fail("审批通过时不能填写驳回理由");
            }
            // 更新预约状态为通过
            reservationService.updateApprove(reservationId);
            // 更新审批表对应记录
            approvalService.updateApproveByReservationId(reservationId);
            return Result.success("预约审批通过");
        } else if (isPass == 2) {
            // 审批驳回逻辑
            // 校验是否填写了驳回理由
            if (reason == null || reason.isEmpty()) {
                return Result.fail("审批驳回时必须填写驳回理由");
            }
            // 更新预约状态为驳回
            reservationService.updateRefuse(reservationId);
            // 更新审批表对应记录
            approvalService.updateRefuseByReservationId(reservationId, reason);
            return Result.success("预约已驳回");
        } else {
            // 非法输入处理
            return Result.fail("非法参数: isPass 必须为 1（通过）或 2（驳回）");
        }
    }

//    // 删除预约情况
//    @RequestMapping("/deleteReservationById")
//    public Result deleteReservationById(Integer reservationId){
//        log.info("deleteReservationById 接收的参数 reservationId:{}", reservationId);
//        Integer updateRows = reservationService.deleteReservationById(reservationId);
//        if(updateRows<=0){
//            return Result.fail("更新数据库失败");
//        }
//        return Result.success("删除成功");
//    }

}
