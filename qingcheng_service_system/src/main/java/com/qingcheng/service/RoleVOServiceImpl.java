package com.qingcheng.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.dao.RoleVOMapper;
import com.qingcheng.pojo.system.Resource;
import com.qingcheng.pojo.system.RoleVO;
import com.qingcheng.system.RoleVOService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;


import java.util.List;

@Service
public class RoleVOServiceImpl implements RoleVOService {
    @Autowired
    private RoleVOMapper roleVOMapper;

    public List<Integer> findNameId(Integer id) {
        List <Integer> nameId = roleVOMapper.findNameId (id);

        return nameId;
    }

    public void update(RoleVO roleVO) {
        Integer id = roleVO.getId ();
        Example example = new Example (RoleVO.class);
        Example.Criteria criteria = example.createCriteria ();
        criteria.andEqualTo ("id",id);
        roleVOMapper.deleteByExample (example);
        List <Resource> list = roleVO.getResourceList ();
        for (Resource resource : list) {
            Integer resourceId = resource.getId ();
            roleVOMapper.update (id,resourceId);
        }
    }
}
