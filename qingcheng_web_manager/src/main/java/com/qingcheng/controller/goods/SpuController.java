package com.qingcheng.controller.goods;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.PageResult;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.goods.Spu;
import com.qingcheng.pojo.goods.SpuandSku;
import com.qingcheng.goods.SpuService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/spu")
public class SpuController {

    @Reference
    private SpuService spuService;

    @GetMapping("/findAll")
    public List<Spu> findAll(){
        return spuService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<Spu> findPage(int page, int size){
        return spuService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Spu> findList(@RequestBody Map<String,Object> searchMap){
        return spuService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<Spu> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        return  spuService.findPage(searchMap,page,size);
    }

    @GetMapping("/findById")
    public Spu findById(String id){
        return spuService.findById(id);
    }


    @PostMapping("/save")
    public Result save(@RequestBody SpuandSku spuandSku){
        spuService.saveSpuandSku (spuandSku);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Spu spu){
        spuService.update(spu);
        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(String id){
        spuService.delete(id);
        return new Result();
    }
    @GetMapping("/reduction")
    public Result reduction(String id){
        spuService.reduction(id);
        return  new Result ();
    }

    @GetMapping("/findSpuandSkuById")
    public SpuandSku findSpuandSkuById(String id){
        return spuService.findSpuandSkuById (id);
    }
    @GetMapping("/audit")
    public Result audit(@RequestBody Map<String,String> map){
        spuService.audit (map.get ("id"),map.get ("status"),map.get ("message"));
        return new Result ();
    }
    @GetMapping("/pull")
    public Result pull(String id){
       spuService.pull (id);
       return new Result ();
    }
    @GetMapping("/put")
    public Result put(String id){
        spuService.put (id);
        return new Result ();
    }
    @GetMapping("/putMany")
    public Result putMany(Long[] ids){
        int count = spuService.putMany (ids);
        return  new Result (0,"上架"+count+"个商品");
    }

}
