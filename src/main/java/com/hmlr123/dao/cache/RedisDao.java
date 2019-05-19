package com.hmlr123.dao.cache;


import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.hmlr123.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @ClassName: RedisDao
 * @Description: 操作redis的类
 * @Author: liwei
 * @Date: 2019/5/11 17:19
 * @Version: 1.0
 */

//序列化是处理对象流的机制，就是将对象的内容进行流化，可以对流化后的对象进行读写操作，也可以将流化后的对象在网络间传输。反序列化就是将流化后的对象重新转化成原来的对象。
public class RedisDao {
    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    private final JedisPool jedisPool;

    private RuntimeSchema<Seckill> schema=RuntimeSchema.createFrom(Seckill.class);

    public RedisDao(String ip, int port) {
        jedisPool=new JedisPool(ip,port);
    }


    public Seckill getSeckill(long seckillId){
        //redis操作业务逻辑
        try (Jedis jedis=jedisPool.getResource()){
            String key="seckill:"+seckillId;
            /**
             * 自定义的方式序列化 缓存获取到。。。
             */
            byte[] bytes=jedis.get(key.getBytes());
            if (bytes!=null){
                //空对象
                Seckill seckill= schema.newMessage();
                ProtostuffIOUtil.mergeFrom(bytes,seckill,schema);
                //seckill被反序列化
                return seckill;
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return null;
    }

    public String putSeckill(Seckill seckill){
        //set Object(Seckill) ->序列化 -> byte[]
        try(Jedis jedis=jedisPool.getResource()){
            String key = "seckill:" + seckill.getSeckillId();
            byte[] bytes=ProtostuffIOUtil.toByteArray(seckill,schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
            //超时缓存
            int timeout=60*60;
            return jedis.setex(key.getBytes(),timeout,bytes);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return null;
    }
}
