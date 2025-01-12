package com.sjx.a_p.mapper;

import com.sjx.a_p.model.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    // 根据用户名，查询用户信息
    @Select("select * from user_info where user_name = #{userName}")
    UserInfo selectUserByName(String userName);
}
