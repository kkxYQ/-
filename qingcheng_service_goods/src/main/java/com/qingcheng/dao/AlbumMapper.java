package com.qingcheng.dao;

import com.qingcheng.pojo.goods.Album;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

public interface AlbumMapper extends Mapper<Album> {


    void updateByPrimaryKeySelective(Example example);
}

