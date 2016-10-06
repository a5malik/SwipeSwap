package com.example.tsleeve.myapplication;

import java.util.Date;

/**
 * Created by footb on 10/4/2016.
 */

public class Swipe {
    private int cost;
    private Date date;

    public void Swipe(int cost, Date date)
    {
        this.cost = cost;
        this.date = date;
    }

    public int getCost()
    {
        return cost;
    }
    public Date getDate()
    {
        return date;
    }

    public void setCost(int c)
    {
        cost = c;
    }
    public void setDate(Date d)
    {
        date = d;
    }
}
