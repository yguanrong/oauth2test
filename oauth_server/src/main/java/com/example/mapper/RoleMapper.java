package com.example.mapper;

import com.example.dto.SysRole;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RoleMapper {
    @Select("select r.id,r.role_name roleName ,r.role_desc roleDesc " +
            "FROM sys_role r left join sys_user_role ur on r.id = ur.role_id " +
            "WHERE ur.role_uid = #{uid} ")
    public List<SysRole> findByUid(Integer uid);
}
