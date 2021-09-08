package com.oldman.demo_b;

import java.time.Duration;

import com.oldman.demo_api.Employee;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class DemoApplication_B {

  public static void main(String[] args) {
    setupEnv();
    SpringApplication.run(DemoApplication_B.class, args);
  }

  public static void setupEnv() {
    String CERTS_PATH = System.getenv("CERTS_PATH");
    System.setProperty("javax.net.ssl.keyStore", CERTS_PATH + "/localhost.jks");
    System.setProperty("javax.net.ssl.keyStorePassword", "potatoes");
    System.setProperty("javax.net.ssl.trustStore", CERTS_PATH + "/localhost-trust.jks");
    System.setProperty("javax.net.ssl.trustStorePassword", "changeit");

    // System.setProperty("javax.net.debug", "ssl");
    // System.setProperty("javax.net.debug", "all");
  }

  @Bean
  public WebClient localApiClient() {
    return WebClient.create("https://localhost:8443");
  }

  @Bean
  public CommandLineRunner run(WebClient localApiClient) throws Exception {
    String keystore = System.getProperty("javax.net.ssl.keyStore");
    System.out.println("And the keystore is: " + keystore);

    return args -> {
      Employee employee = fetch(localApiClient);
      System.out.println("Fetched employee: " + employee);
    };
  }

  private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(3);

  public Employee fetch(WebClient localApiClient) {
    return localApiClient.get().uri("/fetch").retrieve().bodyToMono(Employee.class).block(REQUEST_TIMEOUT);
  }

}
