package com.jos.dem.springboot.h2.config;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@Configuration
public class MyConfiguration {
//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
//
//        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
////        entityManagerFactoryBean.setJpaVendorAdapter(vendorAdaptor());
////        entityManagerFactoryBean.setDataSource(dataSource());
////        entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
////        entityManagerFactoryBean.setPackagesToScan(ENTITYMANAGER_PACKAGES_TO_SCAN);
////        entityManagerFactoryBean.setJpaProperties(jpaHibernateProperties());
//
//        return entityManagerFactoryBean;
//    }
}
