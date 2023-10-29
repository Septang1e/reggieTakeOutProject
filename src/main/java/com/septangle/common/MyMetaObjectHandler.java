package com.septangle.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自定义的元数据对象处理器
 *
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 插入自动填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充:insert...");
        log.info(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime",LocalDateTime.now());
        Long currentId=BaseContext.getCurrentId();
        metaObject.setValue("createUser", currentId);
        metaObject.setValue("updateUser",currentId);
    }

    /**
     * 更新自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充:update...");
        log.info(metaObject.toString());
        Long currentId=BaseContext.getCurrentId();
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUser",currentId);
    }
    /*
    客户端发送一次Http请求，对应地会在服务端都会分配一个新的线程来处理，在处理过程中涉及到下面类中的方法都属于相同的线程
    1.LoginCheckFilter的doFilter
    2.EmployeeController的update
    3.MyMetaObjectHandler中的updateFill方法
     */
}
