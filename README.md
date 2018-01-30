# Maven

    <dependency>
        <groupId>com.github.zjzcn</groupId>
        <artifactId>esbatis</artifactId>
        <version>1.0.1</version>
    </dependency>

# Exemple

## 1. Mapper file
    <?xml version="1.0" encoding="UTF-8" ?>
    <mapper namespace="com.github.esbatis.test.DemoDao">
    <index id="index" method="put" url="demo/demo/${demo.id}">
        {
            "id" : ${demo.id},
            "age" : ${demo.age},
            "date" : "${demo.date}"
        }
    </index>

    <search id="findDemo" method="post" url="/demo/demo/_search?pretty=true ">
        {
            "query": {
                "bool": {
                    "must": [
                        {
                            "term": {
                                "id": "${id}"
                            }
                        }
                    ]
                }
            }
        }
    </search>

    <update id="update" method="post" url="/demo/demo/1/_update">
        {
            "doc":{
            "id" : ["4", "2"]
            }
        }
    </update>

    <bulk id="bulk" method="post" url="_bulk">
        { "index" : { "_index" : "demo", "_type" : "demo", "_id" : "AV5cARtspYdDC3WQaBdK1" } }
        { "id" : [1] }
        { "delete" : { "_index" : "demo", "_type" : "demo", "_id" : "AV5cARtspYdDC3WQaBdK" } }
    </bulk>

    <mget id="mget" method="post" url="_mget">
    {
    "docs" : [
        {
        "_index" : "demo",
        "_type" : "demo",
        "_id" : "60"
        }
        ]
    }
    </mget>

    <update_by_query id="updateByQuery" method="post" url="demo/demo/_update_by_query">
        {
        "query": {
        "match": {
        "message": "some message"
        }
        }
        }
    </update_by_query>

    <index id="insertPolygon" method="put" url="/demo/demo1/1?pretty=true ">
        {
            "name" : "sh",
            "location" : {
                "type" : "polygon",
                "coordinates" : [[
                [4.88330,52.38617],
                [4.87463,52.37254],
                [4.87875,52.36369],
                [4.88939,52.35850],
                [4.89840,52.35755],
                [4.91909,52.36217],
                [4.92656,52.36594],
                [4.93368,52.36615],
                [4.93342,52.37275],
                [4.92690,52.37632],
                [4.88330,52.38617]
                ]]
            }
        }
    </index>
    </mapper>
*Placeholder is ${}, not #{}.

## 2. DemoDao file(Parameter must have @Param)
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

    <bean class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
        <property name="locations">
            <array>
                <value>classpath:config.properties</value>
            </array>
        </property>
    </bean>

    <bean id="restClient" class="com.github.esbatis.client.RestClient">
        <constructor-arg name="hosts" value="${es.hosts}" />
    </bean>

    <bean id="mapperFactory" class="com.github.esbatis.spring.MapperFactoryBean">
        <property name="restClient" ref="restClient" />
        <property name="mapperLocations" >
            <list>
                <value>classpath*:mapper/*.xml</value>
            </list>
        </property>
        <property name="executorFilters">
            <list>
                <bean class="com.github.esbatis.test.TimeSpanFilter" />
            </list>
        </property>
    </bean>

    <bean id="scannerConfigurer" class="com.github.esbatis.spring.MapperScannerConfigurer">
        <property name="mapperFactoryBeanId" value="mapperFactory" />
        <property name="basePackage" value="com.github.esbatis.test" />
        <property name="annotationClass" value="org.springframework.stereotype.Repository" />
    </bean>
    </beans>

## 6. Executor filter
    public class TimeSpanFilter implements ExecutorFilter {

    private ThreadLocal<Long> timestamp = new ThreadLocal<>();

    @Override
    public void exception(FilterContext context) {
        System.out.println("------------exception----------");
        timestamp.remove();
        context.getException().printStackTrace();
    }

    @Override
    public void before(FilterContext context) {
        timestamp.set(System.currentTimeMillis());
    }

    @Override
    public void after(FilterContext context) {
        Long start = timestamp.get();
        timestamp.remove();
        System.out.println("time span = " + (System.currentTimeMillis() - start));
    }
    }


## 7. Date format
ES built in format: strict_date_optional_time<br>
Date type format(ISO8601): yyyy-MM-dd'T'HH:mm:ss.SSS'Z'

## 8. Result handler
    @Result(UserResultHandler.class)
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
