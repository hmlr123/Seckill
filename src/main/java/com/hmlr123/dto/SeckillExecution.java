package com.hmlr123.dto;

import com.hmlr123.entity.SuccessKilled;
import com.hmlr123.enums.SeckillStatEnum;

/**
 * @ClassName: SeckillExecution
 * @Description: 封装秒杀后结果，是否秒杀成功
 * @Author: liwei
 * @Date: 2019/5/11 20:21
 * @Version: 1.0
 */
public class SeckillExecution {
    /*
    秒杀商品ID
     */
    private long seckillId;
    /*
    执行秒杀结果的状态
     */
    private int state;
    /*
    状态的明文标示
     */
    private String stateInfo;
    /*
    秒杀成功时，需要传递秒杀结果的对象回去
     */
    private SuccessKilled successKilled;

    /*
    秒杀成功返回实体
     */
    public SeckillExecution(long seckillId, SeckillStatEnum statEnum, SuccessKilled successKilled) {
        this.seckillId = seckillId;
        this.successKilled = successKilled;
        this.state=statEnum.getState();
        this.stateInfo=statEnum.getInfo();
    }

    /*
    秒杀失败返回实体
     */
    public SeckillExecution(long seckillId, SeckillStatEnum statEnum) {
        this.seckillId = seckillId;
        this.state=statEnum.getState();
        this.stateInfo=statEnum.getInfo();
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public SuccessKilled getSuccessKilled() {
        return successKilled;
    }

    public void setSuccessKilled(SuccessKilled successKilled) {
        this.successKilled = successKilled;
    }

    @Override
    public String toString() {
        return "SeckillExecution{" +
                "秒杀商品ID=" + seckillId +
                ", 秒杀状态=" + state +
                ", 秒杀状态信息='" + stateInfo + '\'' +
                ", 秒杀的商品=" + successKilled +
                '}';
    }
}
