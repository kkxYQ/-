package com.qingcheng.controller.goods;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.qingcheng.entity.PageResult;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.goods.Album;
import com.qingcheng.goods.AlbumService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/album")
public class AlbumController {

    @Reference
    private AlbumService albumService;

    @GetMapping("/findAll")
    public List<Album> findAll(){
        return albumService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<Album> findPage(int page, int size){
        return albumService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Album> findList(@RequestBody Map<String,Object> searchMap){
        return albumService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<Album> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        return  albumService.findPage(searchMap,page,size);
    }

    @GetMapping("/findById")
    public Album findById(Long id){
        return albumService.findById(id);
    }


    @PostMapping("/add")
    public Result add(@RequestBody Album album){
        albumService.add(album);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(Long id,@RequestBody Album album){
        //对象转换成json字符串
        String s = JSONObject.toJSONString (album);
        List list=new ArrayList ();
        list.add (s);

        Album albumid = albumService.findById (id);//根据id查出对象
        album.setId (id);//给传入得对象设置id

        String imageItems = albumid.getImageItems ();//根据原来得对象查出是否存在相册
//        Map<String,String> map = JSON.parseObject (imageItems, Map.class);
//        //新旧数据拼接
//        map.put (album.getTitle (),album.getImageItems ());

        if(imageItems==null||"".equals (imageItems)){//进行判断

            album.setImageItems (list.toString ());//设置传递并添加的相册
        }else{

            String replace = imageItems.replace ("]", "," + s + "]");//进行字符拼接
            album.setImageItems (replace.toString ());////设置传递并添加的相册
        }
        list.clear ();
        albumService.update (album);//进行更新

        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(Long id){
        albumService.delete(id);
        return new Result();
    }


}
