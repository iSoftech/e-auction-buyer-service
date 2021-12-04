package com.cognizant.fse.eauction.buyer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Seller Service Configuration class
 *
 * @author Mohamed Yusuff
 * @since 30/11/2021
 */
@Data
@ConfigurationProperties(prefix = "app.services.seller-service")
public class SellerServiceConfig {
    private String scheme;
    private String host;
    private Integer port = -1;
    private String productSearch;
}
