package com.dfire.soa.proxy;

import java.lang.reflect.Method;

/**
 * @author gantang
 * @Date 2017/4/17
 */
public interface ProxyAdvice {
    void before(Object obj, Method method, Object[] args, Object proxy);

    void after(Object obj, Method method, Object[] args, Object proxy, Object result);
}
