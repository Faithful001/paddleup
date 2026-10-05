package com.king.paddleup.domain.bid;

import com.king.paddleup.domain.bid.dto.BidPlacedEvent;
import com.king.paddleup.domain.bid.dto.CreateBidResponse;
import com.king.paddleup.domain.bid.dto.GetBidResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BidMapper {

    @Mapping(source = "createdAt", target = "placedAt")
    CreateBidResponse toCreateResponse(Bid bid);

    @Mapping(source = "createdAt", target = "placedAt")
    GetBidResponse toGetResponse(Bid bid);

    @Mapping(source = "id", target = "bidId")
    @Mapping(source = "createdAt", target = "placedAt")
    BidPlacedEvent toEvent(Bid bid);
}