package com.example.pillyohae.domain.order.dto;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
public class BuyerOrderSearchResponseDto {

    private final List<BuyerOrderInfo> orderInfos;
    private final PageInfo pageInfo;

    public BuyerOrderSearchResponseDto(List<BuyerOrderInfo> orderInfos, PageInfo pageInfo) {
        this.orderInfos = orderInfos;
        this.pageInfo = pageInfo;
    }

    @Getter
    @NoArgsConstructor
    public static class PageInfo {
        private Long pageNumber;
        private Long pageSize;

        public PageInfo(Long pageNumber, Long pageSize) {
            this.pageNumber = pageNumber;
            this.pageSize = pageSize;
        }
    }
}
