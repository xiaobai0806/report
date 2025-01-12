package com.sjx.a_p.service;

import com.sjx.a_p.mapper.UserMapper;
import com.sjx.a_p.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public UserInfo selectUserByName(String userName){
        return userMapper.selectUserByName(userName);
    }
}
