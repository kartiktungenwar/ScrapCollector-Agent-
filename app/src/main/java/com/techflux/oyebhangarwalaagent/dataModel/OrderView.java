package com.techflux.oyebhangarwalaagent.dataModel;

/**
 * Created by Lenovo on 07/06/2017.
 */
public class OrderView {

    String userId;
    String userName;
    String userEmail;
    String userAddress;
    String userMobile;
    String userOrderId;

    String userLandmark;

    public OrderView(String userId, String userName, String userEmail, String userAddress, String userMobile,String userLandmark, String userOrderId) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userAddress = userAddress;
        this.userMobile = userMobile;
        this.userLandmark = userLandmark;
        this.userOrderId = userOrderId;

    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getUserLandmark() {
        return userLandmark;
    }

    public void setUserLandmark(String userLandmark) {
        this.userLandmark = userLandmark;
    }

    public String getUserOrderId() {
        return userOrderId;
    }

    public void setUserOrderId(String userOrderId) {
        this.userOrderId = userOrderId;
    }
}
