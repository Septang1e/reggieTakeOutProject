package com.septangle.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.septangle.common.R;
import com.septangle.entity.Dish;
import com.septangle.entity.Employee;
import com.septangle.service.DishesService;
import com.septangle.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;

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

    //起售+起售
    @PostMapping("/status/{status}")
    public R<String>statusHandlerStart(HttpServletRequest request,@PathVariable int status,@RequestParam Long []ids)
    {
        return statusHandler(request,ids,status);
    }

    /**
     * 140.82.114.4
     * 151.101.193.6
     * 停售与起售通用方法
     * @param status
     * @param ids
     * @return
     */
    private R<String>statusHandler(HttpServletRequest request,Long[]ids,int status)
    {
        log.info("正在批量处理菜品, ids=[{}], status={}",ids,status);
        //一个一个地进行更新
        /*for(Long id:ids){
            Long empId=(Long)request.getSession().getAttribute("employee");
            Dish dish= dishesService.getById(id);
            dish.setStatus(status);

            dish.setUpdateTime(LocalDateTime.now());
            dish.setUpdateUser(empId);
            dishesService.updateById(dish);
        }
        */
        ///*
         for(Long id:ids) {
             Long empId = (Long) request.getSession().getAttribute("employee");

             LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
             updateWrapper
                     .set(Dish::getStatus, status)
                     .in(Dish::getId, id);
             dishesService.update(updateWrapper);
         }
         //*/
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
                .eq(Dish::getIsDeleted,0)
                .orderByDesc(Dish::getCreateTime);

        //排序，使用筛选条件
        dishesService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }
    /*
    删除菜品
    1.通过URL中的id找到该菜品
    2.直接删除
     */
    @DeleteMapping
    public R<String>deleteDishes(HttpServletRequest request,@RequestParam Long []ids)
    {
        log.info("正在删除菜品{}", ids);

        for(Long id:ids)
        {
            //记录更改人id
            Long empId=(Long)request.getSession().getAttribute("employee");

            //创建条件筛选器
            LambdaUpdateWrapper<Dish>updateWrapper=new LambdaUpdateWrapper<>();
            //写删除条件，为了方便找回，删除仅改变IsDeleted的值，0为未删除，1为删除
            updateWrapper.set(Dish::getIsDeleted,1)
                    .in(Dish::getId,id);
            //更新数据
            dishesService.update(updateWrapper);
        }
        return R.success("删除成功");
    }
    /*
    查找员工信息
     */
    @GetMapping("{ids}")
    public R<Dish>dishesInfoHandler(@PathVariable Long ids)
    {
        log.info("正在查询菜品...");
        Dish dish=dishesService.getById(ids);
        if(dish!=null)
        {
            return R.success(dish);
        }
        return R.error("没有找到该菜品");
    }
}
