package com.qingcheng.dao;

import com.qingcheng.pojo.system.RoleVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface RoleVOMapper extends Mapper<RoleVO> {

    @Select ("SELECT  " +
            "r2.id  " +
            "FROM  " +
            "tb_role AS r1 ,  " +
            "tb_role_resource AS rr ,  " +
            "tb_resource AS r2  " +
            "WHERE  " +
            "#{id} = rr.role_id AND  " +
            "rr.resource_id = r2.id  " +
            "GROUP BY  " +
            "r2.id")
    public List<Integer> findNameId(Integer id);

    @Select ("insert into tb_role_resource values (role_id=#{id},resource_id=#{resourceId})")
    public void update(@Param ("id") Integer id, @Param ("resourceId") Integer resourceId);
}
