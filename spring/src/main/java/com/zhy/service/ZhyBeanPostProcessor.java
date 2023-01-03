package com.zhy.service;

import com.spring.BeanPostProcessor;
import com.spring.Component;

@Component("zhyBeanPostProcessor")
public class ZhyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {

        //随意自定义
        System.out.println("初始化前");
        if (beanName.equals("UserService")) {
            ((UserService) bean).setBeanName("sammirika");

        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("初始化后");
        return bean;
    }
}
