package com.example.mapper;

import com.example.dto.SysUser;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by YangGuanRong
 * date: 2023/1/3
 */
public interface UserMapper {
    @Select("select * from sys_user where user_name=#{username}")
    @Results({
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "roles", column = "id", javaType = List.class,
                    many = @Many(select = "com.example.mapper.RoleMapper.findByUid"))
    })
    public SysUser findByUsername(String username);
}
