package com.zhy.service;


import com.spring.*;

@Component("userService")
//@Scope("protoType")
public class UserService implements BeanNameAware, InitializingBean {

    @Autowired
    private OrderService orderService;

    private String beanName;

    public void test() {
        System.out.println(orderService);
        System.out.println(beanName);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("初始化");
    }

    @Override
    public void setBeanName(String name) {
        beanName = name;
    }
}
