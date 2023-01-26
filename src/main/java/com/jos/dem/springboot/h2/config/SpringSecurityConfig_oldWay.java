package com.jos.dem.springboot.h2.config;

import org.springframework.context.annotation.Configuration;

@Configuration
//@EnableWebSecurity
public class SpringSecurityConfig_oldWay{
//public class SpringSecurityConfig_oldWay extends WebSecurityConfigurerAdapter {

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf()
//                .disable()
//                .authorizeRequests()
//                .antMatchers(HttpMethod.OPTIONS, "/**")
//                .permitAll()
//                .anyRequest()
//                .authenticated()
//                .and()
//                .httpBasic();
//    }
}