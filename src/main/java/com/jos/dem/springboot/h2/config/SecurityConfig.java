package com.jos.dem.springboot.h2.config;

import org.springframework.context.annotation.Configuration;

@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
public class SecurityConfig {
//    @Autowired
//    private JwtAuthFilter authFilter;
//
//    @Bean
//    //authentication
//    public UserDetailsService userDetailsService() {
////        UserDetails admin = User.withUsername("Basant")
////                .password(encoder.encode("Pwd1"))
////                .roles("ADMIN")
////                .build();
////        UserDetails user = User.withUsername("John")
////                .password(encoder.encode("Pwd2"))
////                .roles("USER","ADMIN","HR")
////                .build();
////        return new InMemoryUserDetailsManager(admin, user);
//        return new UserInfoUserDetailsService();
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        return http.csrf().disable()
//                .authorizeHttpRequests()
//                .requestMatchers("/products/new","/products/authenticate").permitAll()
//                .and()
//                .authorizeHttpRequests().requestMatchers("/products/**")
//                .authenticated().and()
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .authenticationProvider(authenticationProvider())
//                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
//                .build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public AuthenticationProvider authenticationProvider(){
//        DaoAuthenticationProvider authenticationProvider=new DaoAuthenticationProvider();
//        authenticationProvider.setUserDetailsService(userDetailsService());
//        authenticationProvider.setPasswordEncoder(passwordEncoder());
//        return authenticationProvider;
//    }
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }

}