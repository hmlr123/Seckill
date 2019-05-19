package com.hmlr123.dto;

/**
 * @ClassName: Exposer
 * @Description: 暴露秒杀地址接口
 * @Author: liwei
 * @Date: 2019/5/11 20:16
 * @Version: 1.0
 */
public class Exposer {
    /*
    是否开启秒杀
     */
    private boolean exposed;

    /*
    秒杀地址加密
     */
    private String md5;

    /*
    id为seckillId的商品秒杀地址
     */
    private long seckillId;

    /*
    系统当前时间
     */
    private long now;

    /*
    秒杀开始时间
     */
    private long start;

    /*
    秒杀结束时间
     */
    private long end;

    public Exposer() {
    }

    public Exposer(boolean exposed, long seckillId) {
        this.exposed = exposed;
        this.seckillId = seckillId;
    }

    public Exposer(boolean exposed, String md5, long seckillId) {
        this.exposed = exposed;
        this.md5 = md5;
        this.seckillId = seckillId;
    }

    public Exposer(boolean exposed, long seckillId, long now, long start, long end) {
        this.exposed = exposed;
        this.seckillId = seckillId;
        this.now = now;
        this.start = start;
        this.end = end;
    }

    public boolean isExposed() {
        return exposed;
    }

    public void setExposed(boolean exposed) {
        this.exposed = exposed;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public long getNow() {
        return now;
    }

    public void setNow(long now) {
        this.now = now;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "Exposer{" +
                "秒杀状态=" + exposed +
                ", md5加密值='" + md5 + '\'' +
                ", 秒杀ID=" + seckillId +
                ", 当前时间=" + now +
                ", 开始时间=" + start +
                ", 结束时间=" + end +
                "}\n";
    }
}
