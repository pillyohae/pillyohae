package com.example.pillyohae.front_test;

import com.example.pillyohae.order.entity.Order;
import com.example.pillyohae.order.repository.OrderRepository;
import com.example.pillyohae.user.entity.User;
import com.example.pillyohae.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequiredArgsConstructor
public class FrontController {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @GetMapping("/users/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("orders/{orderId}/toss/checkout")
    public String checkoutPage(Model model, Authentication authentication,
        @PathVariable UUID orderId) {
        // 사용자의 주문 정보 가져오기
        User user = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Order order = orderRepository.findById(orderId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found")
            );
        if (!order.getUser().equals(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "You are not allowed to check out");
        }
        // 모델에 데이터 추가
        model.addAttribute("orderId", order.getId());
        model.addAttribute("price", order.getTotalPrice());
        model.addAttribute("orderName", order.getOrderName());
        model.addAttribute("customerEmail", user.getEmail());
        model.addAttribute("customerName", user.getName());

        return "toss/checkout";
    }

    @GetMapping("/toss/success")
    public String successPage() {
        return "success"; // templates/toss/success.html 파일을 반환
    }

    @GetMapping("/toss/fail")
    public String failPage() {
        return "fail"; // templates/toss/fail.html 파일을 반환
    }

    @GetMapping("/products")
    public String productsPage() {
        return "products";
    }
}
