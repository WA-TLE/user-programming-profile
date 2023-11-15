package com.dy.entry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: dy
 * @Date: 2023/11/13 21:52
 * @Description:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegister implements Serializable {
    private static final long serialVersionUID = 4941586438036007387L;

    private String userAccount;
    private String userPassword;
    private String checkPassword;
    private String planetCode;



}
