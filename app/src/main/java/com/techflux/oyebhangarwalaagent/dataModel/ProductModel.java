package com.techflux.oyebhangarwalaagent.dataModel;

/**
 * Created by Lenovo on 06/05/2017.
 */
public class ProductModel {

    String mId;
    String dataType;
    String mOrderId;
    String dataId;
    String dataName;
    String dataQuantity;
    String dataImage;

    public ProductModel(String mId, String mOrderId,String dataId, String dataType, String dataName, String dataQuantity,String dataImage) {
        this.mId = mId;
        this.mOrderId = mOrderId;
        this.dataType = dataType;
        this.dataId = dataId;
        this.dataName = dataName;
        this.dataQuantity = dataQuantity;
        this.dataImage = dataImage;
    }


    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getmOrderId() {
        return mOrderId;
    }

    public void setmOrderId(String mOrderId) {
        this.mOrderId = mOrderId;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public String getDataQuantity() {
        return dataQuantity;
    }

    public void setDataQuantity(String dataQuantity) {
        this.dataQuantity = dataQuantity;
    }

    public String getDataImage() {
        return dataImage;
    }

    public void setDataImage(String dataImage) {
        this.dataImage = dataImage;
    }

}

