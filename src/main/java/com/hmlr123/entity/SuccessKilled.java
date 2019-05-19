package com.hmlr123.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 成功秒杀类 一个人可以秒杀多个商品
 */
public class SuccessKilled implements Serializable {
    private static final long serialVersionUID = 1834437127882846202L;

    //秒杀id
    private long seckillId;
    //用户电话号
    private long userPhone;
    //秒杀状态
    private short state;
    //秒杀时间（成功）
    private LocalDateTime createTime;

    private Seckill seckill;

    public SuccessKilled() {
    }

    public SuccessKilled(long seckillId, long userPhone, short state, LocalDateTime createTime, Seckill seckill) {
        this.seckillId = seckillId;
        this.userPhone = userPhone;
        this.state = state;
        this.createTime = createTime;
        this.seckill = seckill;
    }

    public Seckill getSeckill() {
        return seckill;
    }

    public void setSeckill(Seckill seckill) {
        this.seckill = seckill;
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public long getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(long userPhone) {
        this.userPhone = userPhone;
    }

    public short getState() {
        return state;
    }

    public void setState(short state) {
        this.state = state;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "com.hmlr123.entity.SuccessKilled{" +
                "秒杀ID=" + seckillId +
                ", 用户电话=" + userPhone +
                ", 秒杀状态=" + state +
                ", 秒杀时间=" + createTime +
                ", 秒杀商品=" + seckill +
                "}\n";
    }
}
