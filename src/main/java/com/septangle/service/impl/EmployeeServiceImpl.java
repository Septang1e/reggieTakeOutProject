package com.septangle.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.septangle.entity.Employee;
import com.septangle.mapper.EmployeeMapper;
import com.septangle.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
