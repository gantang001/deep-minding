package com.dfire.soa.proxy;

import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Method;

/**
 * @author gantang
 * @Date 2017/4/17
 */
public abstract class AspectInterceptor {

    protected ProxyAdvice[] proxyAdvice;

    protected void doBefore(Object obj, Method method, Object[] args, Object proxyBase) {
        if (ArrayUtils.isNotEmpty(proxyAdvice)) {
            for (ProxyAdvice proxy : proxyAdvice) {
                proxy.before(obj, method, args, proxyBase);
            }
        }
    }

    protected void doAfter(Object obj, Method method, Object[] args, Object proxyBase, Object result) {
        if (ArrayUtils.isNotEmpty(proxyAdvice)) {
            for (ProxyAdvice proxy : proxyAdvice) {
                proxy.after(obj, method, args, proxyBase, result);
            }
        }
    }
}
