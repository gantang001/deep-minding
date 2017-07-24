package com.dfire.soa.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author gantang
 * @Date 2017/4/17
 */
public class JdkInvocationHandler extends AspectInterceptor implements InvocationHandler {
    private Object target;


    JdkInvocationHandler(Object target, ProxyAdvice... proxyAdvice) {
        this.target = target;
        this.proxyAdvice = proxyAdvice;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        doBefore(target, method, args,proxy);
        Object result = method.invoke(target, args);
        doAfter(target, method, args,proxy,result);
        return result;
    }


}
