package com.dfire.soa.proxy;


import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author gantang
 * @Date 2017/4/17
 */
public class CglibInvocationHandler extends AspectInterceptor implements MethodInterceptor {

    public static <T> T newProxyInstance(Class<T> targetInstanceClazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(targetInstanceClazz);
        enhancer.setCallback(new CglibInvocationHandler());
        return (T) enhancer.create();
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        doBefore(obj, method, args, proxy);
        Object result = proxy.invokeSuper(obj, args);
        doAfter(obj, method, args, proxy, result);
        return result;
    }
}
