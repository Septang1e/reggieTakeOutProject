package com.septangle.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.septangle.entity.Dish;
import com.septangle.mapper.DishesMapper;
import com.septangle.service.DishesService;
import org.springframework.stereotype.Service;

@Service
public class DishesServiceImpl extends ServiceImpl<DishesMapper, Dish> implements DishesService {
}
