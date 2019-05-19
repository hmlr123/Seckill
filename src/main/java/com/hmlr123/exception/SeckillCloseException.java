package com.hmlr123.exception;

/**
 * @ClassName: SeckillCloseException
 * @Description: 秒杀已经关闭异常，当秒杀结束就会出现这个异常；解决秒杀关闭后被秒杀的情况
 * @Author: liwei
 * @Date: 2019/5/11 20:01
 * @Version: 1.0
 */
public class SeckillCloseException extends SeckillException{

    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}
