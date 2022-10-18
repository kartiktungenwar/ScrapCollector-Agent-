package com.techflux.oyebhangarwalaagent.dataModel;

/**
 * Created by Lenovo on 09/06/2017.
 */
public class ProductRate {
    public String getProductRate() {
        return productRate;
    }

    public void setProductRate(String productRate) {
        this.productRate = productRate;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    String productId,productName,productRate;

    public ProductRate(String productId, String productName, String productRate) {
        this.productId = productId;
        this.productName = productName;
        this.productRate = productRate;
    }
}

