package com.example.pillyohae.domain.order.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BuyerOrderSearchResponseDto {
    List<BuyerOrderInfo> orderInfos;
    PageInfo pageInfo;

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
