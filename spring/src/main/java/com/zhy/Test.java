package com.zhy;

import com.spring.ZhyApplicationContext;

public class Test {

    public static void main(String[] args) throws ClassNotFoundException {

        ZhyApplicationContext applicationContext = new ZhyApplicationContext(AppConfig.class);

        System.out.println(applicationContext.getBean("userService"));

    }
}
