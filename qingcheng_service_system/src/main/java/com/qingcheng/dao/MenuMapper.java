package com.qingcheng.dao;

import com.qingcheng.pojo.system.Menu;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface MenuMapper extends Mapper<Menu> {

    @Select ("select * from tb_menu where id in( " +
            "select menu_id from tb_resource_menu where resource_id in( " +
            "select resource_id from tb_role_resource where role_id in( " +
            "select role_id from tb_admin_role where admin_id in( " +
            "select id from tb_admin where login_name=#{name} " +
            "       ) " +
            "    ) " +
            "  ) " +
            ") " +
            "UNION " +
            "select * from tb_menu where id in( " +
            "select parent_id from  tb_menu where id in( " +
            "select menu_id from tb_resource_menu where resource_id in( " +
            "select resource_id from tb_role_resource where role_id in( " +
            "select role_id from tb_admin_role where admin_id in( " +
            "select id from tb_admin where login_name=#{name} " +
            "        ) " +
            "      ) " +
            "    ) " +
            "  ) " +
            ") " +
            "UNION " +
            "select * from tb_menu where id in ( " +
            "select parent_id from tb_menu where id in( " +
            "select parent_id from  tb_menu where id in( " +
            "select menu_id from tb_resource_menu where resource_id in( " +
            "select resource_id from tb_role_resource where role_id in( " +
            "select role_id from tb_admin_role where admin_id in( " +
            "select id from tb_admin where login_name=#{name} " +
            "          ) " +
            "        ) " +
            "      ) " +
            "    ) " +
            ") " +
            ") ")
    public List<Menu> findNameList(@Param ("name") String name);

}
