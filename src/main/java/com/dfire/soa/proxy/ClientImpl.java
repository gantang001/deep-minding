package com.dfire.soa.proxy;

/**
 * @author gantang
 * @Date 2017/4/17
 */
class ClientImpl implements ClientBase {
    public String send(String s) {
        return s.substring(0, s.length() / 2);
    }
}
