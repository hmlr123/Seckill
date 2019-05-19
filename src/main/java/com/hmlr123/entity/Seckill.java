package com.hmlr123.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品实体类
 */
public class Seckill implements Serializable {
    private static final long serialVersionUID = 1834437127882846202L;

    //商品库存ID
    private long seckillId;
    //商品名称
    private String name;
    //商品数量（剩余数量）
    private int number;
    //秒杀开始时间
    private Date startTime;
    //秒杀结束时间
    private Date endTime;
    //创建时间
    private Date createTime;

    public Seckill() {
    }

    public Seckill(long seckillId, String name, int number, Date startTime, Date endTime, Date createTime) {
        this.seckillId = seckillId;
        this.name = name;
        this.number = number;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createTime = createTime;
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "com.hmlr123.entity.Seckill{" +
                "主键ID=" + seckillId +
                ", 商品名称='" + name + '\'' +
                ", 商品数量=" + number +
                ", 秒杀开始时间=" + startTime +
                ", 秒杀结束时间=" + endTime +
                ", 创建时间=" + createTime +
                "}\n";
    }
}
