package com.king.paddleup.domain.bid;

import com.king.paddleup.domain.bid.dto.BidPlacedEvent;
import com.king.paddleup.domain.bid.dto.CreateBidResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BidMapper {

    @Mapping(source = "createdAt", target = "placedAt")
    CreateBidResponse toResponse(Bid bid);

    @Mapping(source = "id", target = "bidId")
    @Mapping(source = "createdAt", target = "placedAt")
    BidPlacedEvent toEvent(Bid bid);
}