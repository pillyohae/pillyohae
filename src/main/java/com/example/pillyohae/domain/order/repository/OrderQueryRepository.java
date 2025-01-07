package com.example.pillyohae.domain.order.repository;

import com.example.pillyohae.domain.order.dto.BuyerOrderInfo;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface OrderQueryRepository {
    List<BuyerOrderInfo> findBuyerOrders(Long userId, LocalDateTime startAt, LocalDateTime endAt, Long pageNumber, Long pageSize);
}
