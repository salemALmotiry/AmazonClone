package com.example.amazonclone.Model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MerchantStock {

    @NotEmpty(message = "MerchantStock id cannot be null")
    @Size(min = 10, max = 50, message = "MerchantStock id must be between 10 and 50 characters")
    private String id;

    @NotEmpty(message = "Product id cannot be null")
    @Size(min = 10, max = 50, message = "Product id must be between 10 and 50 characters")
    private String productId;

    @NotEmpty(message = "Merchant id cannot be null")
    @Size(min = 10, max = 50, message = "Merchant id must be between 10 and 50 characters")
    private String merchantId;

    @NotNull(message = "Stock quantity cannot be null")
    @Min(value = 10, message = "Stock quantity must be at least 10")
    private Integer stock;
}
