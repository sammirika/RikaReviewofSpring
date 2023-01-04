package com.zhy;

import com.spring.ZhyApplicationContext;
import com.zhy.service.UserService;
import com.zhy.service.UserServiceImpl;

public class Test {

    public static void main(String[] args) throws ClassNotFoundException {

        ZhyApplicationContext applicationContext = new ZhyApplicationContext(AppConfig.class);

        UserService userService = (UserService) applicationContext.getBean("userService");

        userService.test();
    }
}
