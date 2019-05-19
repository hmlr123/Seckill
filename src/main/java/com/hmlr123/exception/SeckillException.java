package com.hmlr123.exception;

/**
 * @ClassName: SeckillException
 * @Description: 秒杀基础的异常
 * @Author: liwei
 * @Date: 2019/5/11 19:56
 * @Version: 1.0
 */
public class SeckillException extends RuntimeException{
    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     * @param message
     */
    public SeckillException(String message){
        super(message);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     * @param message
     * @param cause
     * incorporated 合并
     */
    public SeckillException(String message,Throwable cause){
        super(message,cause);
    }

}
