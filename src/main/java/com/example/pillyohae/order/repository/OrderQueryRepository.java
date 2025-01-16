package com.example.pillyohae.order.repository;

import com.example.pillyohae.order.dto.OrderDetailResponseDto;
import com.example.pillyohae.order.dto.OrderPageResponseDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OrderQueryRepository {
    List<OrderPageResponseDto.OrderInfoDto> findOrders(Long userId, LocalDateTime startAt, LocalDateTime endAt, Long pageNumber, Long pageSize);

    List<OrderDetailResponseDto.OrderProductDto> findOrderProductsByOrderId(UUID orderId);

    OrderDetailResponseDto.OrderInfoDto findOrderDetailOrderInfoDtoByOrderId(UUID orderId);

}
