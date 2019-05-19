package com.hmlr123.dao;

import com.hmlr123.entity.Seckill;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SeckillMapper {

    /**
     * 根据传过来的<code>seckillId</code>去减少商品库存
     * @param seckillId 秒杀商品ID
     * @param killTime 秒杀时间
     * @return
     */
    int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

    /**
     * 根据秒杀ID <code>seckillId</code>获取秒杀详细信息
     * @param seckillId
     * @return 参与秒杀的商品ID的数据
     */
    Seckill queryById(@Param("seckillId") long seckillId);

    /**
     * 根据偏移量去查询秒杀商品列表 分页查询？
     * @param offset 偏移量
     * @param limit  限制查询的数据个数
     * @return  符合偏移量查出来的数据个数
     */
    List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 使用存储过程执行秒杀
     * @param paramMap
     */
    void killByProcedure(Map<String,Object> paramMap);//POM:commons-collections





}
