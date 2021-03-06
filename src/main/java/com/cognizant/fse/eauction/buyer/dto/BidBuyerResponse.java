package com.cognizant.fse.eauction.buyer.dto;

import com.cognizant.fse.eauction.buyer.model.Bid;
import com.cognizant.fse.eauction.buyer.model.Buyer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * Bid Buyer Response Class is to handle data transfer
 *
 * @author Mohamed Yusuff
 * @since 29/11/2021
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BidBuyerResponse implements Serializable {

    private static final long serialVersionUID = 955728933773177448L;

    private HttpStatus status;
    private Bid bid;
    private Buyer buyer;
}
