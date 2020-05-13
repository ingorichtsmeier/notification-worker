package com.camunda.consulting.testEngine;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableProcessApplication
public class CamundaTestApplication {
  public static void main(String... args) {
    SpringApplication.run(CamundaTestApplication.class, args);
  }

}
