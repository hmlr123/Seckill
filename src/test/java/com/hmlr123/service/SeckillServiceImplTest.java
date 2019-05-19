package com.hmlr123.service;


import com.hmlr123.dto.Exposer;
import com.hmlr123.dto.SeckillExecution;
import com.hmlr123.entity.Seckill;

import com.hmlr123.exception.RepeatKillException;
import com.hmlr123.exception.SeckillCloseException;
import com.hmlr123.service.interfaces.SeckillService;
import com.hmlr123.service.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @ClassName: SeckillServiceImplTest
 * @Description: TODO
 * @Author: liwei
 * @Date: 2019/5/11 20:27
 * @Version: 1.0
 */
//@RunWith就是一个运行器 @RunWith(SpringJUnit4ClassRunner.class),让测试运行于Spring测试环境
//@ContextConfiguration Spring整合JUnit4测试时，使用注解引入多个配置文件
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/applicationContext-dao.xml","classpath:spring/applicationContext-service.xml"})
public class SeckillServiceImplTest {
    private Logger logger= LoggerFactory.getLogger(this.getClass());

    //@Autowired 注释，它可以对类成员变量、方法及构造函数进行标注，完成自动装配的工作。 通过 @Autowired的使用来消除 set ，get方法
    /**
     * 其实在启动spring IoC时，容器自动装载了一个AutowiredAnnotationBeanPostProcessor后置处理器，
     * 当容器扫描到@Autowied、@Resource或@Inject时，就会在IoC容器自动查找需要的bean，并装配给该对象的属性
     */
    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList() throws Exception{
        List<Seckill> seckillList=seckillService.getSeckillList();
        System.out.println(seckillList.toString());
    }

    @Test
    public void getById() throws Exception{
        long seckillId=1000;
        Seckill byId=seckillService.getById(seckillId);
        System.out.println(byId.toString());
    }

    @Test
    public void exportSeckillUrl() throws Exception{
        long seckillId=1000;
        Exposer exposer=seckillService.exportSeckillUrl(seckillId);
        System.out.println(exposer.toString());
    }

    @Test
    public void excuteSeckill() throws Exception{
        long seckillId=1000;
        Exposer exposer=seckillService.exportSeckillUrl(seckillId);
        if (exposer.isExposed()){
            long userPhone=12222222221L;
            String md5=exposer.getMd5();
            try {
                SeckillExecution seckillExecution=seckillService.executeSeckill(seckillId,userPhone,md5);
                System.out.println(seckillExecution.toString());
            }catch (SeckillCloseException | RepeatKillException e){
                e.printStackTrace();
            }
        }else {
            System.out.println("秒杀未开启");
        }
    }

    @Test
    public void executeSeckillProcedureTest(){
        long sekillId=1001;
        long phone=13680115102L;
        Exposer exposer=seckillService.exportSeckillUrl(sekillId);
        if (exposer.isExposed()){
            String md5=exposer.getMd5();
            SeckillExecution execution=seckillService.executeSeckillProcedure(sekillId,phone,md5);
            System.out.println(execution.toString());
        }
    }
}
