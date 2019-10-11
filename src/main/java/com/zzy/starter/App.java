package com.zzy.starter;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.zzy"})
public class App {
  public static void main(String args[]) {
    SpringApplication springApplication = new SpringApplication(App.class);
    springApplication.run();
  }
}
