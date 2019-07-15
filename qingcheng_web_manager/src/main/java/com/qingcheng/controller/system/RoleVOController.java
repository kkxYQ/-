package com.qingcheng.controller.system;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.system.RoleVO;
import com.qingcheng.service.system.RoleVOService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rolevo")
public class RoleVOController {

    @Reference
    private RoleVOService roleVOService;

    @GetMapping("/findByRoleId")
    public List<Integer> findAll(Integer id){
      return  roleVOService.findNameId (id);
    }
    @PostMapping("/update")
    public Result update(RoleVO roleVO){
        roleVOService.update (roleVO);
        return new Result ();
    }
}
