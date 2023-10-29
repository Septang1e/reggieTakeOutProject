package com.septangle.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.septangle.common.R;
import com.septangle.entity.Employee;
import com.septangle.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.lang3.StringUtils;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     *
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
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());//封装条件
        Employee emp = employeeService.getOne(queryWrapper);//getOne，获得唯一的一个数据，通过Username获取表单

        //3.如果没有查询到则返回登录失败的结果
        if (emp == null) {
            return R.error("登录失败");
        }
        //4.密码的比对
        if (!emp.getPassword().equals(password)) {
            return R.error("密码错误");
        }
        //5.查看员工状态
        if (emp.getStatus() == 0)//0禁用，1 启用
        {
            return R.error("账号已禁用");
        }
        //6.登录成功，将用户id放到Session中
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /*
     * 员工退出
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出");
    }
    /*
    1.完善登录功能 #使用过滤器或者拦截器(SpringMvc)
    2. 新增员工
    3. 员工信息分页查询
    4. 启用/禁用员工账号
    5. 编辑员工信息
    */
    /*  新增员工
    * 页面发送ajax请求，将新增员工页面
    *
    * */

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> saveNewStaff(HttpServletRequest request,@RequestBody Employee employee)
    {
        /*
        LambdaQueryWrapper<Employee>queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp=employeeService.getOne(queryWrapper);
        if(emp!=null)
        {
            log.info("添加用户失败：已存在该用户");
            return R.error("用户已存在");
        }
         */

        log.info("新增员工，员工信息：{}",employee.toString());

        //初始密码为123456,同时进行MD5加密处理
        String defaultPassword="123456";
        defaultPassword=DigestUtils.md5DigestAsHex(defaultPassword.getBytes());
        employee.setPassword(defaultPassword);

        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        //通过Session获取用户ID;
        Long empId= (Long)request.getSession().getAttribute("employee");
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);

        //更新数据库
        employeeService.save(employee);
        return R.success("新增员工成功");
    }


    /**
     * 页面发送ajax请求，将分页查询参数(page,pageSize,name)提交到服务端
     * 服务端Controller接收页面提交的数据并调用Service查询数据
     * Service调用Mapper操作数据库，查询分页数据
     * Controller将查询到的分页数据响应给页面(json)
     * 页面接收到分页数据并通过ElementUI的Table组件展示到屏幕上
     * @param
     * @return
     * @throws IOException
     */
    //页面需要records和total,而Page方法里含有这两个参数
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name) throws IOException {
        log.info("page={},pageSize={},name={}",page,pageSize,name);

        //构造分页条件(分页构造器)
        Page pageInfo=new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper();
        //添加过滤条件|添加排序条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name)//org.ap....
                .orderByDesc(Employee::getUpdateTime);

        //查询
        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

}
