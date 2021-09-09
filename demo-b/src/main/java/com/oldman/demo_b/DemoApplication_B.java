package com.oldman.demo_b;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.Duration;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import com.oldman.demo_api.Employee;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.ResourceUtils;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import reactor.netty.http.client.HttpClient;

@SpringBootApplication
public class DemoApplication_B {

  public static void main(String[] args) {
    SpringApplication.run(DemoApplication_B.class, args);
  }

  private static final String CERTS_PATH = System.getenv("CERTS_PATH");

  @Bean
  public WebClient localApiClient() {
    HttpClient httpClient = HttpClient.create().secure(sslSpec -> sslSpec.sslContext(getMutualAuthSslContext()));
    ClientHttpConnector clientHttpConnector = new ReactorClientHttpConnector(httpClient);
    WebClient webClient = WebClient.builder().clientConnector(clientHttpConnector).baseUrl("https://localhost:8443")
        .build();
    return webClient;
  }

  @Bean
  public CommandLineRunner run(WebClient localApiClient) throws Exception {
    return args -> {
      Employee employee = fetch(localApiClient);
      System.out.println("Fetched employee: " + employee);
    };
  }

  private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(3);

  public Employee fetch(WebClient localApiClient) {
    return localApiClient.get().uri("/fetch").retrieve().bodyToMono(Employee.class).block(REQUEST_TIMEOUT);
  }

  public static SslContext getMutualAuthSslContext() {

    try (
        FileInputStream keyStoreFileInputStream = new FileInputStream(
            ResourceUtils.getFile(CERTS_PATH + "/client.jks"));
        FileInputStream trustStoreFileInputStream = new FileInputStream(
            ResourceUtils.getFile(CERTS_PATH + "/localhost-trust.jks"));) {

      ResourceUtils.getFile(CERTS_PATH + "/localhost-trust.jks");
      KeyStore keyStore = KeyStore.getInstance("jks");
      keyStore.load(keyStoreFileInputStream, "potatoes".toCharArray());
      KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
      keyManagerFactory.init(keyStore, "potatoes".toCharArray());

      KeyStore trustStore = KeyStore.getInstance("jks");
      trustStore.load(trustStoreFileInputStream, "changeit".toCharArray());
      TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
      trustManagerFactory.init(trustStore);

      return SslContextBuilder.forClient().keyManager(keyManagerFactory).trustManager(trustManagerFactory).build();
    } catch (KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException
        | UnrecoverableKeyException e) {
      throw new RuntimeException(e);
    }
  }

}
