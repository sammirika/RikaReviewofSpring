package com.spring;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ZhyApplicationContext {

    private Class configClass;

    // key 代表 beanName object代表bean对象,单例bean可以由此获得，单例bean只初始化一次，后面直接从spring中取缓存对象
    private ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();

    // beandefinitionMap保存对应的
    private ConcurrentHashMap<String, BeanDefinition> beandefinitionMap = new ConcurrentHashMap<>();

    private List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

    public ZhyApplicationContext(Class configClass) throws ClassNotFoundException {
        this.configClass = configClass;
        scan(configClass, beandefinitionMap);

        for (Map.Entry<String, BeanDefinition> entry : beandefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            //如果是单例类
            if (beanDefinition.getScope().equals("singleton")) {
                Object bean = CreatBean(beanName, beanDefinition);
                singletonObjects.put(beanName, bean);
            }
        }

    }

    private Object CreatBean(String beanName, BeanDefinition beanDefinition) {

        // 反射拿对象
        Class clazz = beanDefinition.getClazz();


        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();

            // 依赖注入
            for (Field declaredFiled : clazz.getDeclaredFields()) {
                // 如果有这个注解
                if (declaredFiled.isAnnotationPresent(Autowired.class)) {
                    Object bean = getBean(declaredFiled.getName());
                    declaredFiled.setAccessible(true);
                    declaredFiled.set(instance, bean);
                }
            }

            // aware 回调
            // 判断实例是否实现了BeanNameAware这个接口
            if (instance instanceof BeanNameAware) {
                ((BeanNameAware) instance).setBeanName(beanName);
            }

            // 初始化前
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessBeforeInitialization(instance, beanName);
            }

            // 初始化
            if (instance instanceof InitializingBean) {
                ((InitializingBean) instance).afterPropertiesSet();
            }

            // 初始化后

            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessAfterInitialization(instance, beanName);
            }

            return instance;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void scan(Class configClass, ConcurrentHashMap<String, BeanDefinition> beandefinitionMap) {
        // 解析配置类
        // ComponentScan注解-->扫描路径-->扫描
        ComponentScan componentScanAnnotations = (ComponentScan) configClass.getDeclaredAnnotation(ComponentScan.class);
        // 扫描路径
        String path = componentScanAnnotations.value(); //"com.zhy.service"
        System.out.println(path);

        // 进行扫描
        // BootStrap->> jre/lib  Ext-> jre/ext/lib  App->classPath-->

        ClassLoader classLoader = ZhyApplicationContext.class.getClassLoader(); //app
        // app类加载器获取路径
        path = path.replace(".", "/");
        URL resource = classLoader.getResource(path);
        System.out.println(path);
        File file = new File(resource.getFile());
        // 如果是目录
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                // 绝对路径/Users/zhouhongyuan/Desktop/VBlog/spring/target/classes/com/zhy/service/UserServiceImpl.class
                String fileName = f.getAbsolutePath();
                if (fileName.endsWith(".class")) {
                    String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class"));
                    className = className.replace("/", ".");
                    try {
                        Class<?> clazz = classLoader.loadClass(className);
                        //如果有这个注解
                        if (clazz.isAnnotationPresent(Component.class)) {

                            if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                                BeanPostProcessor instance = (BeanPostProcessor) clazz.getDeclaredConstructor().newInstance();
                                beanPostProcessorList.add(instance);
                            }

                            // 表示当前类是一个bean
                            // bean初始化取决于作用域，要去解析类，判断当前bean是单例bean还是原型（prototype）bean
                            //beanDefinition
                            Component componentAnnotation = clazz.getDeclaredAnnotation(Component.class);
                            String beanName = componentAnnotation.value();

                            BeanDefinition beanDefinition = new BeanDefinition();
                            beanDefinition.setClazz(clazz);


                            // 检查是否存在这个注解
                            if (clazz.isAnnotationPresent(Scope.class)) {
                                Scope scopeAnnotation = clazz.getDeclaredAnnotation(Scope.class);
                                beanDefinition.setScope(scopeAnnotation.value());
                            } else {
                                beanDefinition.setScope("singleton");
                            }
                            beandefinitionMap.put(beanName, beanDefinition);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public Object getBean(String beanName) {
        if (beandefinitionMap.containsKey(beanName)) {
            BeanDefinition beanDefinition = beandefinitionMap.get(beanName);
            if (beanDefinition.getScope().equals("singleton")) {
                // 单例从缓存中获取
                Object o = singletonObjects.get(beanName);
                return o;
            } else {
                // 如何创建bean
                Object bean = CreatBean(beanName, beanDefinition);
                return bean;
            }
        } else {
            throw new NullPointerException("不存在对应bean");
        }
    }
}
