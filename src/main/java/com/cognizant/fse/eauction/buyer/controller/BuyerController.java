package com.cognizant.fse.eauction.buyer.controller;

import com.cognizant.fse.eauction.buyer.common.RestApiController;
import com.cognizant.fse.eauction.buyer.dto.BidBuyerRequest;
import com.cognizant.fse.eauction.buyer.dto.BidBuyerResponse;
import com.cognizant.fse.eauction.buyer.dto.BidRequest;
import com.cognizant.fse.eauction.buyer.dto.BidResponse;
import com.cognizant.fse.eauction.buyer.model.Bid;
import com.cognizant.fse.eauction.buyer.model.Buyer;
import com.cognizant.fse.eauction.buyer.service.BidService;
import com.cognizant.fse.eauction.buyer.service.BuyerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.cognizant.fse.eauction.buyer.util.BuyerHelper.buildBid;
import static com.cognizant.fse.eauction.buyer.util.BuyerHelper.buildBuyer;

/**
 * Buyer Controller to perform buyer actions
 *
 * @author Mohamed Yusuff
 * @since 28/11/2021
 */
@Api(tags = "E-Auction Buyer REST Controller")
@RestApiController("buyer")
@RequiredArgsConstructor
public class BuyerController {

    @Autowired
    private BidService bidService;
    @Autowired
    private BuyerService buyerService;

    /**
     * Returns all Bids
     *
     * @return a {@link List} of type {@link Bid}
     */
    @ApiOperation(value = "Show all Bids", response = Bid.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Bid.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Bid not found"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
    })
    @GetMapping("show-bids")
    @ResponseBody
    public ResponseEntity<List<Bid>> showBids() {
        List<Bid> bids = bidService.getAllBids();
        return ResponseEntity.ok(bids);
    }

    /**
     * Returns all Bids for the given Buyer
     *
     * @param buyerEmail refers to attribute {@code email}
     * @return a {@link List} of type {@link Bid}
     */
    @ApiOperation(value = "Show all Bids for an existing Buyer", response = Bid.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Bid.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Bid not found"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
    })
    @GetMapping("{buyer-email}/show-bids")
    @ResponseBody
    public ResponseEntity<List<Bid>> showBidsByBuyerId(@PathVariable("buyer-email") String buyerEmail) {
        Buyer buyer = buyerService.getBuyer(buyerEmail);
        List<Bid> bids = bidService.getAllBids(buyer.getId(), false);
        return ResponseEntity.ok(bids);
    }

    /**
     * Returns all Bids for the given Product Id
     *
     * @param productId refers to attribute {@code productId}
     * @return a {@link List} of type {@link Bid}
     */
    @ApiOperation(value = "[For US_04] Show all Bids for a given Product", response = Bid.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Bid.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Bid not found"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
    })
    @GetMapping("show-bids/{product-id}")
    @ResponseBody
    public ResponseEntity<List<BidResponse>> showBidsForProduct(@PathVariable("product-id") Integer productId) {
        List<BidResponse> bidResponses = new ArrayList<>(0);
        List<Bid> bids = bidService.getAllBids(productId, true);
        if (CollectionUtils.isNotEmpty(bids)) {
            List<Integer> buyerIds = bids.stream().map(Bid::getBuyerId).collect(Collectors.toList());
            Map<Integer, Buyer> buyerMap = buyerService.getBuyers(buyerIds);
            bids.stream().forEach(bid -> {
                BidResponse bidBuyerResponse = BidResponse.builder()
                        .id(bid.getId())
                        .productId(bid.getProductId())
                        .bidAmount(bid.getBidAmount())
                        .buyer(buyerMap.get(bid.getBuyerId()))
                        .build();
                bidResponses.add(bidBuyerResponse);
            });
        }
        return ResponseEntity.ok(bidResponses);
    }

    /**
     * Returns the newly added Bid and Buyer
     *
     * @param bidBuyerRequest of type {@link BidBuyerRequest}
     * @return the newly added bid and buyer of type {@link BidBuyerResponse}
     */
    @ApiOperation(value = "[US_03] Adds a new Buyer and Bid", response = BidBuyerResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = BidBuyerResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Bid not found"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
    })
    @PostMapping("place-bid")
    @ResponseBody
    public ResponseEntity<BidBuyerResponse> placeBid(@Validated @RequestBody final BidBuyerRequest bidBuyerRequest) {
        Buyer buyer = buyerService.addBuyer(buildBuyer(bidBuyerRequest));
        Bid bid = bidService.addBid(buildBid(bidBuyerRequest, buyer.getId()));
        BidBuyerResponse bidBuyerResponse = BidBuyerResponse.builder()
                .status(HttpStatus.OK)
                .bid(bid)
                .buyer(buyer)
                .build();
        return ResponseEntity.ok(bidBuyerResponse);
    }

    /**
     * Returns the newly added Bid
     *
     * @param buyerEmail refers to attribute {@code email}
     * @param bidRequest of type {@link BidRequest}
     * @return the newly added bid of type {@link Bid}
     */
    @ApiOperation(value = "[US_03_Enhanced] Adds a new Bid for an existing Buyer", response = Bid.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Bid.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Bid not found"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
    })
    @PostMapping("/{buyer-email}/place-bid")
    @ResponseBody
    public ResponseEntity<Bid> placeBidByBuyer(@PathVariable("buyer-email") String buyerEmail,
                                               @Validated @RequestBody final BidRequest bidRequest) {
        Buyer buyer = buyerService.getBuyer(buyerEmail);
        Bid bid = Bid.builder()
                .productId(bidRequest.getProductId())
                .bidAmount(bidRequest.getBidAmount())
                .buyerId(buyer.getId()).build();
        Bid newBid = bidService.addBid(bid);
        return ResponseEntity.ok(newBid);
    }

    /**
     * Updates the Bid Amount for the given product
     *
     * @param buyerEmail refers to attribute {@code email} of type {@link Buyer}
     * @param bidRequest of type {@link BidRequest}
     * @return the updated bid of type {@link Bid}
     */
    @ApiOperation(value = "[US_05_Enhanced] Updates the Bid Amount for the given Product", response = HttpStatus.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Bid.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Bid not found"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
    })
    @PutMapping("/{buyer-email}/update-bid")
    @ResponseBody
    public ResponseEntity<Bid> updateBid(@PathVariable("buyer-email") String buyerEmail,
                                                @Validated @RequestBody final BidRequest bidRequest) {
        Buyer buyer = buyerService.getBuyer(buyerEmail);
        Bid updatedBid = bidService.updateBid(buyer.getId(), bidRequest.getProductId(), bidRequest.getBidAmount());
        return ResponseEntity.ok(updatedBid);
    }
}
