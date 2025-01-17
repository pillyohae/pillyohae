package com.example.pillyohae.order.repository;

import com.example.pillyohae.order.dto.OrderDetailResponseDto;
import com.example.pillyohae.order.dto.OrderDetailSellerResponseDto;
import com.example.pillyohae.order.dto.OrderInfoDto;
import com.example.pillyohae.order.dto.OrderSellerInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OrderQueryRepository {

    Page<OrderInfoDto> findOrders(Long userId, LocalDateTime startAt, LocalDateTime endAt, Pageable pageable);

    Page<OrderSellerInfoDto> findSellerOrders(Long userId, LocalDateTime startAt, LocalDateTime endAt, Pageable pageable);

    List<OrderDetailResponseDto.OrderProductDto> findOrderProductsByOrderId(UUID orderId);

    OrderDetailResponseDto.OrderInfoDto findOrderDetailOrderInfoDtoByOrderId(UUID orderId);

    OrderDetailSellerResponseDto.OrderInfoDto findOrderDetailSellerInfoDtoByOrderId(UUID orderId);

    OrderDetailSellerResponseDto.OrderProductDto findOrderDetailSellerProductDtoByOrderId(UUID orderId, Long userId);
}
