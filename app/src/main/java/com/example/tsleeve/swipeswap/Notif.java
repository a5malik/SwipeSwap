package com.example.tsleeve.swipeswap;

/**
 * Created by footb on 11/26/2016.
 */

public class Notif {
    private String fromUser;
    private Notification.Message m_type;
    private Long swipePostTime;
    private Double price;
    private Long startTime;
    private Long endTime;
    private String owner_ID;
    private Integer diningHall;
    private Long postTime;
    private Swipe.Type type;


    public Notif() {
        super();
    }

    public Notif(String fromUser, Notification.Message m_type, Long swipePostTime, Double price, Long startTime, Long endTime, String owner_ID, Integer diningHall, Long postTime, Swipe.Type type) {
        this.fromUser = fromUser;
        this.m_type = m_type;
        this.price = price;
        this.startTime = startTime;
        this.endTime = endTime;
        this.owner_ID = owner_ID;
        this.diningHall = diningHall;
        this.postTime = postTime;
        this.type = type;
    }

    public Notif(String fromUser, Notification.Message m_type, Swipe s) {
        this.fromUser = fromUser;
        this.m_type = m_type;
        this.price = s.getPrice();
        this.startTime = s.getStartTime();
        this.endTime = s.getEndTime();
        this.owner_ID = s.getOwner_ID();
        this.diningHall = s.getDiningHall();
        this.postTime = s.getPostTime();
        this.type = s.getType();
    }

    public String getFromUser() {

        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public Notification.Message getM_type() {
        return m_type;
    }

    public void setM_type(Notification.Message m_type) {
        this.m_type = m_type;
    }

    public Long getSwipePostTime() {
        return swipePostTime;
    }

    public void setSwipePostTime(Long swipePostTime) {
        this.swipePostTime = swipePostTime;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getOwner_ID() {
        return owner_ID;
    }

    public void setOwner_ID(String owner_ID) {
        this.owner_ID = owner_ID;
    }

    public Integer getDiningHall() {
        return diningHall;
    }

    public void setDiningHall(Integer diningHall) {
        this.diningHall = diningHall;
    }

    public Long getPostTime() {
        return postTime;
    }

    public void setPostTime(Long postTime) {
        this.postTime = postTime;
    }

    public Swipe.Type getType() {
        return type;
    }

    public void setType(Swipe.Type type) {
        this.type = type;
    }

    public Swipe getSwipe() {
        Swipe swipe = new Swipe(price, startTime, endTime, owner_ID, diningHall, type);
        swipe.setPostTime(postTime);
        return swipe;
    }
}
