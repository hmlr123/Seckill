package com.hmlr123.web;

import com.hmlr123.dto.Exposer;
import com.hmlr123.dto.SeckillExecution;
import com.hmlr123.dto.SeckillResult;
import com.hmlr123.entity.Seckill;
import com.hmlr123.enums.SeckillStatEnum;
import com.hmlr123.exception.RepeatKillException;
import com.hmlr123.exception.SeckillCloseException;
import com.hmlr123.exception.SeckillException;
import com.hmlr123.service.interfaces.SeckillService;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.jws.WebParam;
import javax.swing.*;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: SeckillController
 * @Description: TODO
 * @Author: liwei
 * @Date: 2019/5/12 21:55
 * @Version: 1.0
 */
@Component
@RequestMapping("/seckill")//url:模块/资源/{}/细分
public class SeckillController {


    @Autowired
    private SeckillService seckillService;

    /**
     * 获取参与秒杀的商品的信息
     * @param model
     * @return
     */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public String list(Model model){
        //获取列表页
        List<Seckill> list=seckillService.getSeckillList();
        model.addAttribute("list",list);
        return "list";
    }

    /**
     * 根据商品id查看商品的详细信息
     * @param seckillId 秒杀id
     * @param model   数据模型
     * @return 返回链接请求
     */
    @RequestMapping(value = "/{seckillId}/detail",method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId,Model model){
        //没有id 重定向。。。
        if (seckillId==null){
            return "redirect:/seckill/list";
        }

        //商品不存在 转发
        Seckill seckill=seckillService.getById(seckillId);
        if (seckill==null){
            return "forward:/seckill/list";
        }

        model.addAttribute("seckill",seckill);
        return "detail";
    }



    /*
    RequestBody：
    @ResponseBody是作用在方法上的，@ResponseBody 表示该方法的返回结果直接写入 HTTP response body 中，
    一般在异步获取数据时使用【也就是AJAX】，在使用 @RequestMapping后，返回值通常解析为跳转路径，
    但是加上 @ResponseBody 后返回结果不会被解析为跳转路径，而是直接写入 HTTP response body 中。
    比如异步获取 json 数据，加上 @ResponseBody 后，会直接返回 json 数据。
    @RequestBody 将 HTTP 请求正文插入方法中，使用适合的 HttpMessageConverter 将请求体写入某个对象。

    RequestBody:
    @RequestBody是作用在形参列表上，用于将前台发送过来固定格式的数据【xml 格式或者 json等】封装为对应的 JavaBean 对象，
    封装时使用到的一个对象是系统默认配置的 HttpMessageConverter进行解析，然后封装到形参上。

    produces:它的作用是指定返回值类型，不但可以设置返回值类型还可以设定返回值的字符编码；
    consumes:指定处理请求的提交内容类型（Content-Type），例如application/json, text/html;
     */

    //ajax ,json暴露秒杀接口的方法
    @RequestMapping(value = "/{seckillId}/exposer",
            method = RequestMethod.GET,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId){
        SeckillResult<Exposer> result;
        try {
            Exposer exposer=seckillService.exportSeckillUrl(seckillId);
            result=new SeckillResult<Exposer>(true,exposer);
        }catch (Exception e){
            e.printStackTrace();
            result=new SeckillResult<Exposer>(false,e.getMessage());
        }
        return result;//返回json数据
    }


    //执行秒杀
    @RequestMapping(value = "/{seckillId}/{md5}/execution",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
                                                   @PathVariable("md5") String md5,
                                                   @CookieValue(value = "userPhone",required = false) Long userPhone){
        /*
        @CookieValue:
        　　1、value：参数名称
        　　2、required：是否必须
        　　3、defaultValue：默认值
         */
        if (userPhone==null){
            return new SeckillResult<SeckillExecution>(false,"未注册");
        }

        //根据用户手机号，秒杀商品的id跟md5进行秒杀商品，每异常就是秒杀成功
        try {
            SeckillExecution execution=seckillService.executeSeckillProcedure(seckillId,userPhone,md5);
            return new SeckillResult<>(true,execution);
        }catch (RepeatKillException e){
            SeckillExecution execution=new SeckillExecution(seckillId, SeckillStatEnum.REPEAT_KILL);
            return new SeckillResult<>(true,execution);
        }catch (SeckillCloseException e){
            //秒杀结束
            SeckillExecution execution=new SeckillExecution(seckillId,SeckillStatEnum.END);
            return new SeckillResult<>(true,execution);
        }catch (SeckillException e){
            //不能判断的异常
            SeckillExecution execution=new SeckillExecution(seckillId,SeckillStatEnum.INNER_ERROR);
            return new SeckillResult<>(true,execution);

        }
    }


    //获取系统时间
    @RequestMapping(value = "/time/now",method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time(){
        Date now=new Date();
        return new SeckillResult<Long>(true,now.getTime());
    }
}
