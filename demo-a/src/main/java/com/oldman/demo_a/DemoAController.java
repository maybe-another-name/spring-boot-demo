package com.oldman.demo_a;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.oldman.demo_api.Employee;

@RestController
public class DemoAController {

  @GetMapping("/fetch")
  Employee fetch() {
    return Employee.builder().name("potatoeman").build();
  }
}
