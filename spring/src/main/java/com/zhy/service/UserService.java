package com.zhy.service;


import com.spring.*;

@Component("userService")
//@Scope("protoType")
public class UserService implements InitializingBean {

    @Autowired
    private OrderService orderService;

    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public void test() {
        System.out.println(orderService);
        System.out.println(name);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("初始化");
    }

}
