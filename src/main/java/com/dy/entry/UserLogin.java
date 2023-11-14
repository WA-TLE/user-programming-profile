package com.dy.entry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: dy
 * @Date: 2023/11/13 22:00
 * @Description:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLogin implements Serializable {
    private static final long serialVersionUID = -4445532022488884416L;
    private String userAccount;
    private String userPassword;
}
