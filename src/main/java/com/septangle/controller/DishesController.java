package com.septangle.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.septangle.common.R;
import com.septangle.entity.Dish;
import com.septangle.service.DishesService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishesController {
    @Autowired
    private DishesService dishesService;

    /**
     * 批量处理停售，起售
     * status==1时 起售
     * @param ids
     * @return
     */

    //起售
    @PostMapping("/status/1")
    public R<String>statusHandlerStart(@RequestParam Long []ids)
    {
        return statusHandler(1,ids);
    }
    //停售
    @PostMapping("/status/0")
    public R<String>statusHandlerStop(@RequestParam Long []ids) {
        return statusHandler(0,ids);
    }

    /**
     * 停售与起售通用方法
     * @param status
     * @param ids
     * @return
     */
    private R<String>statusHandler(int status,Long[]ids)
    {
        log.info("正在批量处理菜品, ids=[{}], status={}",ids,status);
        //一个一个地进行更新
        for(Long id:ids){
            //创建条件筛选
            LambdaUpdateWrapper<Dish>updateWrapper=new LambdaUpdateWrapper<>();
            updateWrapper.
                    set(Dish::getId,status)
                    .in(Dish::getId,id);
            //通过筛选条件进行更新
            dishesService.update(updateWrapper);
        }
        return R.success("更改成功");
    }
    /*
    获取菜品信息
     * 页面发送ajax请求，将分页查询参数(page,pageSize,name)提交到服务端
     * 服务端Controller接收页面提交的数据并调用Service查询数据
     * Service调用Mapper操作数据库，查询分页数据
     * Controller将查询到的分页数据响应给页面(json)
     * 页面接收到分页数据并通过ElementUI的Table组件展示到屏幕上
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page={}, pageSize={}, name={}",page,pageSize,name);

        //构造分页条件
        Page pageInfo=new Page(page,pageSize);
        //创建条件筛选|过滤+排序
        LambdaQueryWrapper<Dish>queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name)
                .orderByDesc(Dish::getCreateTime);

        //排序，使用筛选条件
        dishesService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }
}
