package com.example.pillyohae.domain.order.repository;

import com.example.pillyohae.domain.order.dto.BuyerOrderDetailInfo;
import com.example.pillyohae.domain.order.dto.BuyerOrderInfo;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface OrderQueryRepository {
    List<BuyerOrderInfo> findBuyerOrders(Long userId, LocalDateTime startAt, LocalDateTime endAt, Long pageNumber, Long pageSize);

    List<BuyerOrderDetailInfo.BuyerOrderItemInfo> findBuyerOrderDetail(UUID orderId);
}
