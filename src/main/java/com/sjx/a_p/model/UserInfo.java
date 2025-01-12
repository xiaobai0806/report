package com.sjx.a_p.model;

import lombok.Data;

@Data
public class UserInfo {
    private String userName;
    private String password;
    private String contacts; // 联系人
    private Integer telenum; // 电话号码
}
