# About this project
这个项目是根据github上的https://github.com/nnngu/nguSeckill写的，作为自己阶段练习的demo
* 采用的框架是SSM，
* 数据缓存使用Redis，
* 数据库使用MySQL5.7（注意数据库需要使用5.7及以上，否则在在success_killed表中插入0值时会出错，同时还需要修改数据库my.ini文件中的sql_mode值，,定义了mysql应该支持的sql语法，数据校验等！我的值sql_mode=STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION），建议以后数据库使用5.7及以上，5.6和5.7相差挺大，部分特性在5.7中无法使用，包括语句调优工具trace在5.6中无法使用。
* Maven工程
* IDEA开发工具

# 开发流程
见https://github.com/nnngu/nguSeckill

# 业务分析
1. 分析各种状态    
对商品：最基本的商品信息  
对秒杀商品活动：尚未开始、已开始、库存完了、活动结束、系统错误。使用Enums枚举类型，对应存储过程返回值  
对用户：未注册、已注册、重复秒杀。对应用户秒杀异常  
对用户秒杀的商品：无效、成功、未付款、物流，，，数据库字段

2. 秒杀要做那些事？  
四个请求：  
http://localhost:8080/seckill/list获取商品列表  
http://localhost:8080/seckill/1000/detail商品详细信息  
'/seckill/' + seckillId + '/exposer';暴露请求接口，请求接口应该是随机生成md5  
'/seckill/' + seckillId + '/' + md5 + '/execution';执行秒杀，新增秒杀记录，修改库存  

3. 秒杀最大的问题高并发解决方案：
* 静态资源请求问题  
使用CDN解决
  
* 数据库重复请求查询问题  
使用Redis查询数据，减少数据库查询请求IO
  
* 多表操作优化  
1). 先插入操作，在修改，由于使用InnoDB存储引擎，行锁，在插入数据的时候可以配置插入操作不使用锁，直接插入到表的末端（因为秒杀过程中很少有行记录删除的情况，很少有表空洞）。  
2). 插入可以并行（主键得系统控制）
3). 如果update在前，就先加锁，然后执行update、在insert，再commit提交事务后释放锁。如果insert在前，不用加锁，然后update加锁，在执行update，在commit释放锁，减少锁的持有时间  
个人觉得系统存在问题，在秒杀快结束的时候，结束，同一时间很多用请求插入，造成插入的大量回滚，如何解决？
  
* 事务管理  
方案一：使用Spring的事务管理，耗时消耗在服务器与数据库的交互网络请求，至少有两次，网络延时大。同时自己每次请求返回的值都是数据实体，至少两个，GC垃圾回收耗时  
方案二：使用存储过程，服务器一次请求数据库，返回一个数据实体，网络延时小，GC小

* 错误信息统一管理  
使用存储过程，错误情况对应枚举数据

更详细的请看<a href='https://github.com/nnngu/LearningNotes/blob/master/nguSeckill/04%20Java%E9%AB%98%E5%B9%B6%E5%8F%91%E7%A7%92%E6%9D%80%E9%A1%B9%E7%9B%AE%E4%B9%8B%E9%AB%98%E5%B9%B6%E5%8F%91%E4%BC%98%E5%8C%96.md'>文档</a>

# 包结构
## 代码
com.hmlr123.dao:持久层，主要包括Mapper接口和redis缓存(cacahe)  
com.hmlr123.dto:返回给前端界面的数据实体  
com.hmlr123.entity:数据库实体  
com.hmlr123.enums:主要包括返回的枚举信息，用于持久层->服务层->控制层->前端  
com.hmlr123.exception:异常信息  
com.hmlr123.service:服务层  
com.hmlr123.web:控制层

## 配置文件
resources:配置资源文件

## web代码
web.resources.plugins:js插件库  
web.resources.script:自己写的js文件  
web.WEB-INF.jsp:jsp文件  
web.WEB-INF.tags:标签文件


# 总结
1. 数据需要封装，数据库表数据封装entity，返回给前端的数据封装dto    
2. 异常处理，统一异常继承，子异常，实现异常的区分exception  
3. 数据库操作错误与系统的操作错误统一处理enums
4. 测试类的编写，在测试类中加载配置文件  
@RunWith(SpringJUnit4ClassRunner.class)加载器  
@ContextConfiguration({"classpath:/spring/applicationContext-dao.xml"})配置文件加载    
5. IDEA的使用  
6. redis的序列化  
```java
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

```
7. js 异步请求封装
```javascript
var seckill = {

    //封装秒杀相关ajax的url
    URL: {
        now: function () {
            return '/seckill/time/now';
        },
        exposer: function (seckillId) {
            return '/seckill/' + seckillId + '/exposer';
        },
        execution: function (seckillId, md5) {
            return '/seckill/' + seckillId + '/' + md5 + '/execution';
        }
    },

    //验证手机号
    validatePhone: function (phone) {
        if (phone && phone.length == 11 && !isNaN(phone)) {
            return true;//直接判断对象会看对象是否为空,空就是undefine就是false; isNaN 非数字返回true
        } else {
            return false;
        }
    },

    //详情页秒杀逻辑
    detail: {
        //详情页初始化
        init: function (params) {
            //手机验证和登录,计时交互
            //规划我们的交互流程
            //在cookie中查找手机号
            var userPhone = $.cookie('userPhone');
            console.log(userPhone);
            //验证手机号
            if (!seckill.validatePhone(userPhone)) {
                //绑定手机 控制输出
                var killPhoneModal = $('#killPhoneModal');
                killPhoneModal.modal({
                    show: true,//显示弹出层
                    backdrop: 'static',//禁止位置关闭
                    keyboard: false//关闭键盘事件
                });

                $('#killPhoneBtn').click(function () {
                    var inputPhone = $('#killPhoneKey').val();
                    console.log("inputPhone: " + inputPhone);
                    if (seckill.validatePhone(inputPhone)) {
                        //电话写入cookie(7天过期)
                        $.cookie('userPhone', inputPhone, {expires: 7, path: '/seckill'});
                        //验证通过　　刷新页面
                        window.location.reload();
                    } else {
                        //todo 错误文案信息抽取到前端字典里
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">手机号错误!</label>').show(300);
                    }
                });
            }

            //已经登录
            //计时交互
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            $.get(seckill.URL.now(), {}, function (result) {
                if (result && result['success']) {
                    var nowTime = result['data'];
                    //时间判断 计时交互
                    seckill.countDown(seckillId, nowTime, startTime, endTime);
                } else {
                    console.log('result: ' + result);
                    alert('result: ' + result);
                }
            });
        }
    },

    handlerSeckill: function (seckillId, node) {
        //获取秒杀地址,控制显示器,执行秒杀
        node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');

        $.get(seckill.URL.exposer(seckillId), {}, function (result) {
            //在回调函数种执行交互流程
            if (result && result['success']) {
                var exposer = result['data'];
                if (exposer['exposed']) {
                    //开启秒杀
                    //获取秒杀地址
                    var md5 = exposer['md5'];
                    var killUrl = seckill.URL.execution(seckillId, md5);
                    console.log("killUrl: " + killUrl);
                    //绑定一次点击事件
                    $('#killBtn').one('click', function () {
                        //执行秒杀请求
                        //1.先禁用按钮
                        $(this).addClass('disabled');//,<-$(this)===('#killBtn')->
                        //2.发送秒杀请求执行秒杀
                        $.post(killUrl, {}, function (result) {
                            if (result && result['success']) {
                                var killResult = result['data'];
                                var state = killResult['state'];
                                var stateInfo = killResult['stateInfo'];
                                //显示秒杀结果
                                node.html('<span class="label label-success">' + stateInfo + '</span>');
                            }
                        });
                    });
                    node.show();
                } else {
                    //未开启秒杀(浏览器计时偏差)
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end = exposer['end'];
                    seckill.countDown(seckillId, now, start, end);
                }
            } else {
                console.log('result: ' + result);
            }
        });

    },

    countDown: function (seckillId, nowTime, startTime, endTime) {
        console.log(seckillId + '_' + nowTime + '_' + startTime + '_' + endTime);
        var seckillBox = $('#seckill-box');
        if (nowTime > endTime) {
            //秒杀结束
            seckillBox.html('秒杀结束!');
        } else if (nowTime < startTime) {
            //秒杀未开始,计时事件绑定
            var killTime = new Date(startTime + 1000);//todo 防止时间偏移
            seckillBox.countdown(killTime, function (event) {
                //时间格式
                var format = event.strftime('秒杀倒计时: %D天 %H时 %M分 %S秒 ');
                seckillBox.html(format);
            }).on('finish.countdown', function () {
                //时间完成后回调事件
                //获取秒杀地址,控制现实逻辑,执行秒杀
                console.log('______fininsh.countdown');
                seckill.handlerSeckill(seckillId, seckillBox);
            });
        } else {
            //秒杀开始
            seckill.handlerSeckill(seckillId, seckillBox);
        }
    }

}
```




