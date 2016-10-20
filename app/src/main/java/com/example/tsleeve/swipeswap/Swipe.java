package com.example.tsleeve.swipeswap;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by footb on 10/4/2016.
 */

public class Swipe {

    public Double price;
    public Long startTime;
    public Long endTime;
    public String owner_ID;
    public Integer diningHall;


    public Swipe(Double price, Long startTime, Long endTime, String owner_ID, Integer diningHall) {
        this.price = price;
        this.startTime = startTime;
        this.endTime = endTime;
        this.owner_ID = owner_ID;
        this.diningHall = diningHall;
    }

    public Swipe() {

    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public void setOwner_ID(String owner_ID) {
        this.owner_ID = owner_ID;
    }

    public void setDiningHall(Integer diningHall) {
        this.diningHall = diningHall;
    }

    public Double getPrice() {
        return price;
    }

    public Long getStartTime() {
        return startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public String getOwner_ID() {
        return owner_ID;
    }

    public Integer getDiningHall() {
        return diningHall;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("price", price);
        result.put("startTime", startTime);
        result.put("endTime", endTime);
        result.put("owner_ID", owner_ID);
        result.put("diningHall", diningHall);
        return result;
    }

}
