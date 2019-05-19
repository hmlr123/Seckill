package com.hmlr123.exception;

/**
 * @ClassName: RepeatKillException
 * @Description: 重复秒杀的情况
 * @Author: liwei
 * @Date: 2019/5/11 20:03
 * @Version: 1.0
 */
public class RepeatKillException extends SeckillException{
    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
