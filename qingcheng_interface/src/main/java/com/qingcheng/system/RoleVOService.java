package com.qingcheng.system;


import com.qingcheng.pojo.system.RoleVO;

import java.util.List;

/**
 * 角色权限中间表
 */
public interface RoleVOService {
    /**
     * 页面跳转查询已经勾选的
     */
    public List<Integer> findNameId(Integer id);

    /**
     * 页面提交的
     */
    public void update(RoleVO roleVO);
}
