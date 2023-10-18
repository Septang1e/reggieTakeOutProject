package com.septangle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.septangle.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishesMapper extends BaseMapper<Dish> {
}
