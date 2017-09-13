# Maven

    <dependency>
        <groupId>com.github.zjzcn</groupId>
        <artifactId>esbatis</artifactId>
        <version>1.0.0</version>
    </dependency>

# Exemple

## 1. Mapper file
    <mapper namespace="com.github.esbatis.test.DemoDao">
        <index id="index" method="put" url="demo/demo/${demo.id}">
        {
            "id" : ${demo.id},
            "age" : "${demo.age}",
            "created_at" : "${demo.createdAt}",
            "updated_at" : "${demo.updatedAt}"
        }
        </index>
    </mapper>
*Placeholder is ${}, not #{}.

## 2. DemoDao file
    @Repository
    public interface DemoDao {
        Long index(@Param("demo") Demo demo);
    }

## 3. Data model
    public class Demo {
    private Long id;
    private String createdAt;
    private String updatedAt;
    private List<Long> age;
    }

## 4. Spring integration
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
        DemoDao demoDao = applicationContext.getBean(DemoDao.class);
        Demo demo = new Demo();
        demo.setId(3L);
        demo.setCreatedAt(LocalDate.now().toString());
        demo.setUpdatedAt(LocalDate.now().toString());

        demoDao.index(demo);
        
## 5. Spring config
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans-4.0.xsd">

    <bean id="mapperFactory" class="com.github.esbatis.mapper.MapperFactory">
        <property name="httpHosts" value="http://xxxxx:9200,http://xxxxx:9200" />
        <property name="executorFilters">
            <list>
                <bean class="com.github.esbatis.test.TimeSpanFilter" />
            </list>
        </property>
    </bean>

    <bean id="scannerConfigurer" class="com.github.esbatis.spring.MapperScannerConfigurer">
        <property name="mapperFactory" ref="mapperFactory" />
        <property name="mapperLocations" >
            <list>
                <value>classpath*:mapper/*.xml</value>
            </list>
        </property>
        <property name="basePackage" value="com.github.esbatis.test" />
        <property name="annotationClass" value="org.springframework.stereotype.Repository" />
    </bean>
    </beans>

## 6. Executor filter
    public class TimeSpanFilter implements ExecutorFilter {
    private ThreadLocal<Long> timestamp = new ThreadLocal<>();
    
    @Override
    public void before(MappedStatement ms, Map<String, Object> parameterMap) {
        timestamp.set(System.currentTimeMillis());
    }

    @Override
    public void after(MappedStatement ms, Map<String, Object> parameterMap, String result) {
        Long start = timestamp.get();
        System.out.println("time=" + (System.currentTimeMillis() - start));
        timestamp.remove();
    }
    }

## 7. Date format
ES built in format: strict_date_optional_time<br>
Date type format(ISO8601): yyyy-MM-dd'T'HH:mm:ss.SSS'Z'

## 8. Result handler
    @ResultHandlerType(UserResultHandler.class)
    Integer avgUser(@Param("index") String index, @Param("type") String type,
                    @Param("list") List<String> list, @Param("user") User user);
    
    // result handler
    public class UserResultHandler implements ResultHandler<Integer> {
    @Override
    public Integer handleResult(String result) {
        System.out.println(result);
        return 10;
    }
}
