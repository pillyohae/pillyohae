package com.example.pillyohae.front_test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontController {

    @GetMapping("/users/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/toss/checkout")
    public String checkoutPage() {
        return "toss/checkout"; // templates/toss/checkout.html 파일을 반환
    }

    @GetMapping("/toss/success")
    public String successPage() {
        return "success"; // templates/toss/checkout.html 파일을 반환
    }

    @GetMapping("/toss/fail")
    public String failPage() {
        return "fail"; // templates/toss/checkout.html 파일을 반환
    }

    @GetMapping("/products")
    public String productsPage() {
        return "products";
    }
}
