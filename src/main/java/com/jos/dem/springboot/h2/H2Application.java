package com.jos.dem.springboot.h2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EntityScan("com.jos.dem.springboot.h2.model")
//@EnableJpaRepositories("com.jos.dem.springboot.h2.repository")
public class H2Application {

  private Logger log = LoggerFactory.getLogger(this.getClass());

  public static void main(String[] args) {
    SpringApplication.run(H2Application.class, args);
  }

//  @Bean
//  CommandLineRunner start(PersonRepository personRepository){
//    return args -> {
//      Person person  = new Person(4L, "josdem", "joseluis.delacruz@gmail.com", "","","","","","");
//      Person person2  = new Person(5L, "aaa", "aaa.delacruz@gmail.com","","","","","","");
//      Person person3  = new Person(6L, "bbb", "bbb.delacruz@gmail.com","","","","","","");
//      personRepository.save(person);
//      personRepository.save(person2);
//      personRepository.save(person3);
//      personRepository.save(person);
//      personRepository.findAll().forEach(p -> log.info("person: {}", p));
//    };
//  }

}
