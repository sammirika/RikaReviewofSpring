package com.zhy.service;

import com.spring.BeanPostProcessor;
import com.spring.Component;

@Component
public class ZhyBeanPostProcessor implements BeanPostProcessor {


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.out.println("初始化前");
        if (beanName.equals("userService")){
            ((UserService)bean).setName("稚予好漂亮");
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("初始化后");
        return bean;
    }
}
