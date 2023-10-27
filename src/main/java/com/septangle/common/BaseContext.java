package com.septangle.common;

/**
 * 基于ThreadLocal封装的工具类，用户保存和获取当前已登录用户的id
 */
public class BaseContext {
    private static ThreadLocal<Long>userId = new ThreadLocal<>();
    public static void setCurrentId(Long id)
    {
        userId.set(id);
    }
    public static Long getCurrentId()
    {
        return userId.get();
    }
}
