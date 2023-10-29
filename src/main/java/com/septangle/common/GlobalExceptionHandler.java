package com.septangle.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

import static org.apache.commons.lang3.StringUtils.split;

/**
 * 全局异常捕获
 */
@ControllerAdvice(annotations = {RestController.class,Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 异常处理方法
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex)
    {
        log.error(ex.getMessage());
        if(ex.getMessage().contains("Duplicate entry"))
        {
            return R.error(split(ex.getMessage())[2]+"已存在");
        }
        return R.error("添加失败");
    }
}
