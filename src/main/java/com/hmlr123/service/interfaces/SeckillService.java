package com.hmlr123.service.interfaces;

import com.hmlr123.dto.Exposer;
import com.hmlr123.dto.SeckillExecution;
import com.hmlr123.entity.Seckill;
import com.hmlr123.exception.RepeatKillException;
import com.hmlr123.exception.SeckillCloseException;
import com.hmlr123.exception.SeckillException;
import org.springframework.stereotype.Controller;

import java.util.List;


/**
 * @ClassName: SeckillService
 * @Description: 服务层接口类
 * @Author: liwei
 * @Date: 2019/5/11 19:47
 * @Version: 1.0
 */
public interface SeckillService {
    /**
     * 理解一些概念：
     * po:我们为每一张数据库表写的一个实体类
     * vo:将页面或展示层需要的数据封装成一个实体类
     * bo:业务对象
     * dto:跟VO的概念有点混淆，也是相当于页面需要的数据封装成一个实体类
     * pojo:简单的无规则java对象
     */

    /**
     * 查询全部的秒杀记录
     * @return 数据库中的所有秒杀记录
     */
    List<Seckill> getSeckillList();

    /**
     * 查询单个秒杀的记录
     * @param seckillId 秒杀记录的ID
     * @return 根据ID查询出来的记录信息
     */
    Seckill getById(long seckillId);

    /**
     * 在秒杀开始时输出秒杀接口的地址，否则使出系统时间跟秒杀地址
     * @param seckillId
     * @return 根据对应的状态返回对应的状态实体
     */
    Exposer exportSeckillUrl(long seckillId);


    /**
     * 执行秒杀操作，可能成功可能失败
     * @param seckillId 秒杀商品的id
     * @param userPhone 用户手机号
     * @param md5 md5加密值
     * @return
     * @throws SeckillException
     * @throws RepeatKillException
     * @throws SeckillCloseException
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException;

    SeckillExecution executeSeckillProcedure(long seckillId,long userPhone,String md5);
}
