package com.hmlr123.service;

import com.hmlr123.dao.SeckillMapper;
import com.hmlr123.dao.SuccessKilledMapper;
import com.hmlr123.dao.cache.RedisDao;
import com.hmlr123.dto.Exposer;
import com.hmlr123.dto.SeckillExecution;
import com.hmlr123.entity.Seckill;
import com.hmlr123.entity.SuccessKilled;
import com.hmlr123.enums.SeckillStatEnum;
import com.hmlr123.exception.RepeatKillException;
import com.hmlr123.exception.SeckillCloseException;
import com.hmlr123.exception.SeckillException;
import com.hmlr123.service.interfaces.SeckillService;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: SeckillServiceImpl
 * @Description: TODO
 * @Author: liwei
 * @Date: 2019/5/11 20:14
 * @Version: 1.0
 */
@Service
public class SeckillServiceImpl implements SeckillService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /*
    盐值，用于混淆视听 加在秒杀商品id的后面
     */
    private final String salt="thisIsSaltValue";

    @Autowired
    private SeckillMapper seckillMapper;

    @Autowired
    private SuccessKilledMapper successKilledMapper;

    @Autowired
    private RedisDao redisDao;

    /**
     * 查询所有秒杀记录
     * @return 数据库中所有的秒杀记录
     */
    @Override
    public List<Seckill> getSeckillList() {
        return seckillMapper.queryAll(0,4);
    }

    /**
     * 查询单个秒杀记录
     * @param seckillId 秒杀记录的ID
     * @return
     */
    @Override
    public Seckill getById(long seckillId) {
        return seckillMapper.queryById(seckillId);
    }

    /**
     * 在秒杀开启时输出秒杀接口的地址，否则输出系统时间跟秒杀地址
     * @param seckillId
     * @return 根据对应的状态返回对应的状态实体
     */
    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        //根据秒杀的id去查询是否存在这个商品

        //从redis缓存获取信息
        Seckill seckill=redisDao.getSeckill(seckillId);
        if (seckill==null){
            //从数据库获取
            seckill=seckillMapper.queryById(seckillId);//返回商品信息
            if (seckill==null){//商品不存在
                return new Exposer(false,seckillId);
            }else {
                redisDao.putSeckill(seckill);//将商品信息添加到Redis缓存中
            }
        }

        //判断是否还没到秒杀时间或者秒杀时间过了
        Date startTime=seckill.getStartTime();
        Date endTime=seckill.getEndTime();
        Date nowTime=new Date();
        if (nowTime.getTime()>startTime.getTime()&&nowTime.getTime()<endTime.getTime()){
            //秒杀开启
            String md5=getMd5(seckillId);
            return new Exposer(true,md5,seckillId);//开启秒杀接口，返回md5加密数据
        }
        return new Exposer(false,seckillId,nowTime.getTime(),startTime.getTime(),endTime.getTime());//将结果封装回去，交由前端js判断，显示错误信息
    }

    /**
     * 执行秒杀操作，失败抛出异常
     * @param seckillId 秒杀商品的id
     * @param userPhone 用户手机号
     * @param md5 md5加密值
     * @return 根据不同的结果返回不同的实体信息
     * @throws SeckillException
     * @throws RepeatKillException
     * @throws SeckillCloseException
     */
    @Transactional
    @Override
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {
        //数据被篡改
        if (md5 == null || !md5.equals(getMd5(seckillId))) {//重点在于盐值，如果对方获取盐值，可以通过同样的java md5加密模仿我们的md5
            logger.error("秒杀数据被篡改");
            throw new SeckillException("seckill data rewrite");
        }

        //执行秒杀业务
        Date nowTime=new Date();
        try{
            //记录购买行为
            //此处存在问题，当我们执行秒杀，向数据库写入数据的时候，我们没有传入state、create_time，主键seckillId,userPhone
            int insertCount=successKilledMapper.insertSuccessKilled(seckillId,userPhone);
            //根据写入的返回值判断是否错误
            //重复秒杀 根据秒杀ID 主键重复异常处理
            if (insertCount<=0){//写入数据库有没有其他错误？
                //重复秒杀
                throw new RepeatKillException("seckill repeated");
            }else {
                //减库存
                int reduceNumber = seckillMapper.reduceNumber(seckillId, nowTime);
                if (reduceNumber<=0){//库存完了
                    logger.warn("没有更新数据库记录，说明秒杀结束");
                    throw new SeckillException("seckill is close");
                }else {
                    //，秒杀成功
                    SuccessKilled successKilled=successKilledMapper.queryByIdWithSeckill(seckillId,userPhone);
                    return new SeckillExecution(seckillId,SeckillStatEnum.SUCCESS,successKilled);
                }
            }
        }catch (SeckillCloseException | RepeatKillException e){
            throw e;
        }
    }


    /*
    功能同上面一样 但是没有使用
    区别：上面executeSeckill使用事务控制出错回滚
    这个方法采用数据库的存储过程控制回滚。
    那种更好？

    使用存储过程更新商品数据，并返回状态码，即
    SUCCESS(1,"秒杀成功"),
    END(0,"秒杀结束"),
    REPEAT_KILL(-1,"重复秒杀"),
    INNER_ERROR(-2,"系统异常"),
    DATE_REWRITE(-3,"数据篡改");

    About CDN:
    CDN的全称是Content Delivery Network，即内容分发网络。CDN是构建在网络之上的内容分发网络，
    依靠部署在各地的边缘服务器，通过中心平台的负载均衡、内容分发、调度等功能模块，
    使用户就近获取所需内容，降低网络拥塞，提高用户访问响应速度和命中率。CDN的关键技术主要有内容存储和分发技术。
     */
    @Override
    public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5) {
        if (md5==null||!md5.equals(getMd5(seckillId))){
            return new SeckillExecution(seckillId,SeckillStatEnum.DATE_REWRITE);
        }
        Date killTime=new Date();
        Map<String,Object> map=new HashMap<>();
        map.put("seckillId",seckillId);
        map.put("phone",userPhone);
        map.put("killTime",killTime);
        map.put("result",null);//此处值应该是多少？
        //执行存储过程
        try {
            /*
            row_count()
            0：未修改数据；
            >0：表示修改的行数；
            <0：表示SQL错误或未执行修改SQL
             */
            seckillMapper.killByProcedure(map);
            //获取result
            Integer result = MapUtils.getInteger(map, "result", -2);//默认值-2 系统故障
            if (result==1){
                SuccessKilled successKilled=successKilledMapper.queryByIdWithSeckill(seckillId,userPhone);
                return new SeckillExecution(seckillId,SeckillStatEnum.SUCCESS,successKilled);
            }else {
                return new SeckillExecution(seckillId,SeckillStatEnum.stateOf(result));
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            return new SeckillExecution(seckillId,SeckillStatEnum.INNER_ERROR);
        }
    }

    /**
     * md5加密 私有方法 盐值怎么控制，自动更新？
     * @param seckillId
     * @return
     */
    private String getMd5(long seckillId){
        String base=seckillId+"/"+salt;
        return DigestUtils.md5DigestAsHex(base.getBytes());
    }
}
