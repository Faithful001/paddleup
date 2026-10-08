package com.king.paddleup.domain.auction;

import com.king.paddleup.domain.auction.dto.GetAuctionResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuctionMapper {
    GetAuctionResponse toGetResponse(Auction auction);

    List<GetAuctionResponse> toGetResponses(List<Auction> auctions);
}
