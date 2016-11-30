package com.example.tsleeve.swipeswap;

import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;

/**
 * Created by footb on 10/4/2016.
 */

public class Swipe {

    private Double price;
    private Long startTime;
    private Long endTime;
    private String owner_ID;
    private Integer diningHall;
    private Long postTime;
    private Type type;

    private Calendar calendar = Calendar.getInstance();

    public enum Type {
        SALE,
        REQUEST
    }

    /**
     * Constructs a Swipe with a price, start time, end time, owner ID, and dining hall.
     *
     * @param price      The price offered for the swipe
     * @param startTime  The start time of the window during which the swipe is offered
     * @param endTime    The end time of the window during which the swipe is offered
     * @param owner_ID   The ID of the user associated with the swipe
     * @param diningHall The integer value associated with the dining hall
     */
    public Swipe(Double price, Long startTime, Long endTime, String owner_ID, Integer diningHall, Type type) {
        this.price = price;
        this.startTime = startTime;
        this.endTime = endTime;
        this.owner_ID = owner_ID;
        this.diningHall = diningHall;
        this.postTime = calendar.getTimeInMillis();
        this.type = type;
    }

    public Swipe() { }

    /**
     * Sets the price of the swipe.
     *
     * @param price The price of the swipe
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * Sets a scheduled starting time of the window during which the swipe is offered.
     *
     * @param startTime The start time of the time interval during which the swipe is offered
     */
    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    /**
     * Sets a scheduled ending time of the window during which the swipe is offered.
     *
     * @param endTime The end time of the time interval during which the swipe is offered
     */
    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    /**
     * Sets the ID of the user who made the swipe request or sale.
     *
     * @param owner_ID The ID associated with the user selling or requesting the swipe
     */
    public void setOwner_ID(String owner_ID) {
        this.owner_ID = owner_ID;
    }

    /**
     * Sets the dining hall associated with the swipe.
     *
     * @param diningHall An integer value associated with the dining hall
     */
    public void setDiningHall(Integer diningHall) {
        this.diningHall = diningHall;
    }

    /**
     * Gets the price of the swipe
     *
     * @return The price of the swipe
     */
    public Double getPrice() {
        return price;
    }

    /**
     * Retrieves the start time of the window during which the swipe is offered.
     *
     * @return The start time of the window during which the swipe is offered
     */
    public Long getStartTime() {
        return startTime;
    }

    /**
     * Retrieves the end time of the window during which the swipe is offered.
     *
     * @return The end time of the window during which the swipe is offered
     */
    public Long getEndTime() {
        return endTime;
    }

    /**
     * Gets the ID of the user associated with the swipe.
     *
     * @return The ID of the user associated with the swipe
     */
    public String getOwner_ID() {
        return owner_ID;
    }

    /**
     * Gets the ID value of the dining hall associated with the swipe.
     *
     * @return The integer value associated with the dining hall
     */
    public Integer getDiningHall() {
        return diningHall;
    }

    /**
     * Gets the datetime (in milliseconds) that the swipe post was made.
     *
     * @return The time that the swip post was made.
     */
    public Long getPostTime() { return postTime; }

    /**
     * Gets the type of swipe post made - either sale or request
     *
     * @return The type of swipe post
     */
    public Type getType() { return type; }

    public void setPostTime(Long pt) {
        this.postTime = pt;
    }

    /**
     * Converts the swipe to a map data structure, containing the swipe's attributes.
     *
     * @return A <code>Map</code> representing the swipe, containing its attributes
     */
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
