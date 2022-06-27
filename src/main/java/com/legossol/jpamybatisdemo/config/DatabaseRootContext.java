package com.legossol.jpamybatisdemo.config;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
//mybatis 설정
@MapperScan(value = "com.legossol.jpamybatisdemo.repository.batis",
    sqlSessionTemplateRef = "testSqlSessionFactory")
//JPA설정
@EnableJpaRepositories(
    basePackages = "com.legossol.jpamybatisdemo.repository.jp",
    entityManagerFactoryRef = "entityManagerFactory",
    transactionManagerRef = "transactionManager")

public class DatabaseRootContext {
    private static final String DEFAULT_NAMING_STRATEGY = "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy";
    /*
    * 공통적으로 사용하게 되는 데이터 소스 yml에서 값을 properties로 직접 불러와서 만들어도 상관없다.
    * 예제의 경우 간단하게 prefix로 구성
    * */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource(){
        return DataSourceBuilder.create().build();
    }
    /*
    * ======================================================mybatis 설정 시작======================================================
    * mybatis 사용을 위한 sqlSession bean 등록
    * */
    @Bean(name = "dataFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource")DataSource dataSource, ApplicationContext applicationContext) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setConfigLocation(applicationContext.getResource("classpath:/mybatis/mybatis-config.xml"));
        sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:/mybatis/services/*Repository.xml"));
        return sqlSessionFactoryBean.getObject();
    }
    /**
     * SqlSessionTemplate : SqlSession을 구현하고 코드에서 SqlSession을 대체하는 역할을 한다. 마이바티스 예외처리나 세션의 생명주기 관리
     * @param testSqlSessionFactory
     */
    // mybatis 사용을 위한 sessionFactory bean 등록
    @Bean(name="testSqlSessionFactory")
    public SqlSessionTemplate apiSqlSessionTemplate(@Qualifier("dataFactory") SqlSessionFactory testSqlSessionFactory) {
        return new SqlSessionTemplate(testSqlSessionFactory);
    }
    //======================================================mybatis 설정 끝======================================================

    /**
     * ======================================================JPA 설정 시작======================================================
     * LocalContainerEntityManagerFactoryBean EntityManager를 생성하는 팩토리 SessionFactoryBean과 동일한 역할,
     * Datasource와 mapper를 스캔할 .xml 경로를 지정하듯이 datasource와 엔티티가 저장된 폴더 경로를 매핑해주면 된다.
     *
     */
    @Primary
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder) {
        Map<String, String> propertiesHashMap = new HashMap<>();
        propertiesHashMap.put("hibernate.physical_naming_strategy",DEFAULT_NAMING_STRATEGY);

        LocalContainerEntityManagerFactoryBean rep =
            builder.dataSource(dataSource())
                .packages("com.legossol.jpamybatisdemo.domain")
                //domain을 관리할 패키지 경로 명시 (domain = DO 파일)
                .properties(propertiesHashMap)
                .build();
        return rep;
    }
    //======================================================mybatis 설정 끝======================================================
    //===========================================공통 trasaction 관리를 위한 매니저 빈 등록===========================================
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(EntityManagerFactoryBuilder builder) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory(builder).getObject());
    return transactionManager;
    }
    //=======================================공통 trasaction 관리를 위한 매니저 빈 등록 완료===========================================

}


//    // jpa 설정
//    @Primary
//    @Bean( name = "jpaEntityManagerFactory" )
//    public LocalContainerEntityManagerFactoryBean jpaEntityManagerFactory() {
//        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
//        entityManagerFactoryBean.setDataSource(testDataSource());
////        entityManagerFactoryBean.setPersistenceUnitName("jpa-mysql");
//        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
//        return entityManagerFactoryBean;
//    }



//    @Primary
//    @Bean
//    public EntityManagerFactory entityManagerFactory(DataSource dataSource) {
//        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
//        em.setDataSource(dataSource);
//        em.setPackagesToScan("com.legossol.jpamybatisdemo.domain");
//        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        em.setJpaVendorAdapter(vendorAdapter);
//        final HashMap<String, Object> properties = new HashMap<>();
//        properties.put("hibernate.physical_naming_strategy", SpringPhysicalNamingStrategy.class.getName());
//        em.setJpaPropertyMap(properties);
//        em.afterPropertiesSet();
//
//        return em.getObject();
//    }

//    @Primary
//    @Bean( name = "testJpaEntityManagerFactory" )
//    public LocalContainerEntityManagerFactoryBean jpaEntityManagerFactory(
//        EntityManagerFactoryBuilder builder,
//        @Qualifier("dataSource") DataSource dataSource ) {
//        return builder.dataSource(dataSource).packages("com.legossol.jpamybatisdemo.domain").build();
//    }
// transactional 설정




//    /**
//     *  JpaTransactionManager : EntityManagerFactory를 전달받아 JPA에서 트랜잭션을 관리
//     */
//    // transactional 설정
//    @Bean(name = "transactionManager")
//    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
//        JpaTransactionManager transactionManager = new JpaTransactionManager();
//        transactionManager.setEntityManagerFactory(entityManagerFactory);
//        return transactionManager;
//        //mybatis transactional
//        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
//        dataSourceTransactionManager.setDataSource(testDataSource());
//        //JPA
//        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
//        jpaTransactionManager.setEntityManagerFactory(jpaEntityManagerFactory().getObject() );
//
//        //chain
//        ChainedTransactionManager transactionManager = new ChainedTransactionManager(jpaTransactionManager,dataSourceTransactionManager);
//        return transactionManager;