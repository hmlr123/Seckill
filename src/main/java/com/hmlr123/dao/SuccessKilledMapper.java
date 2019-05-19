package com.hmlr123.dao;

import com.hmlr123.entity.SuccessKilled;
import org.apache.ibatis.annotations.Param;

public interface SuccessKilledMapper {

    /**
     * 插入一条详细的购买信息
     * @param seckillId 秒杀商品ID
     * @param userPahone 购买用户的手机号
     * @return 成功插入就返回1，否则返回0
     */
    int insertSuccessKilled(@Param("seckillId") long seckillId,@Param("userPhone") long userPahone);

    /**
     * 根据用户秒杀的商品的id查询<code>SuccessKilled</code>的明细信息
     * @param seckillId 秒杀商品的ID
     * @param userPhone 购买用户的手机号
     * @return 秒杀商品的详细信息
     */
    SuccessKilled queryByIdWithSeckill(@Param("seckillId") long seckillId,@Param("userPhone") long userPhone);


}
