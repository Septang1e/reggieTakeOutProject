package com.septangle.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.septangle.common.R;
import com.septangle.entity.Category;
import com.septangle.entity.Dish;
import com.septangle.service.CategoryService;
import com.septangle.service.DishesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishesService dishesService;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String>save(@RequestBody Category category)
    {
        log.info("category:{}",category);
        categoryService.save(category);
        return R.success("添加菜品分类成功");
    }
    /**
     * 分页管理
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page>page(int page,int pageSize)
    {
        log.info("分类管理：page={},pageSize={}",page,pageSize);

        Page<Category> pageInfo=new Page<>(page,pageSize);

        //创建条件筛选，根据sort来排序
        LambdaQueryWrapper<Category>queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort)
                        .eq(Category::getIsDeleted,0);

        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }
    @DeleteMapping
    public R<String>delete(@RequestParam Long[]ids) {
        String check = check(ids);
        if (check.equals("{}")) {
            for (Long id : ids) {
                LambdaUpdateWrapper<Category> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.set(Category::getIsDeleted,1).in(Category::getId,id);
                categoryService.update(updateWrapper);
            }
            return R.success("删除成功");
        }else
        {
            return R.error(check+"下还存在菜品,不能删除分类");
        }
    }

    /**
     * 检查分类下是否存在菜品，若存在则返回菜品名称
     * @param ids
     * @return
     */
    private String check(Long []ids)
    {
        StringBuilder names= new StringBuilder("{");
        for(Long id:ids)
        {
            //创建条件赛选，找出哪些分类下面还存在菜品
            LambdaQueryWrapper<Dish>queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(Dish::getCategoryId,id).eq(Dish::getIsDeleted,0);
            if(dishesService.getOne(queryWrapper)!=null)
            {
                names.append(categoryService.getById(id).getName()).append(',');
            }
        }
        names.append("}");
        if(!names.toString().equals("{}"))names.delete(names.length()-2,names.length()-1);
        return names.toString();
    }
}
