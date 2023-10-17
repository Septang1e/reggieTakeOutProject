package com.septangle.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.septangle.common.R;
import com.septangle.entity.Employee;
import com.septangle.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee)//request获得session，后者传出json数据
    {
        /*
         * 先把密码进行md5加密，根据用户名查询数据库
         * 再比对密码是否一致
         * 查看员工状态是否被禁用（锁定账号），表中status==1则没被锁定
         * 最后再将员工id返回至session中
        */
        /*1.将页面提交的密码进行MD5处理*/
        String password=employee.getPassword();
        password=DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee>queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());//封装条件
        Employee emp= employeeService.getOne(queryWrapper);//getOne，获得唯一的一个数据

        //3.如果没有查询到则返回登录失败的结果
        if(emp==null)
        {
            return R.error("登录失败");
        }
        //4.密码的比对
        if(!emp.getPassword().equals(password))
        {
            return R.error("密码错误");
        }
        //5.查看员工状态
        if(emp.getStatus()==0)//0禁用，1启用
        {
            return R.error("账号已禁用");
        }
        //6.登录成功，将用户id放到Session中
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }
}
