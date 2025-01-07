package com.example.pillyohae.domain.order.repository;

import com.example.pillyohae.domain.order.entity.QOrder;
import com.example.pillyohae.domain.order.entity.QOrderItem;

public class OrderQueryRepositoryImpl implements OrderQueryRepository {
    QOrder order = QOrder.order;
    QOrderItem orderItem = QOrderItem.orderItem;



}
