package com.github.cwdtom.hermes;

import com.alibaba.fastjson.JSON;
import com.github.cwdtom.hermes.entity.Centers;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 测试
 *
 * @author chenweidong
 * @since 1.0.0
 */
@RestController
@RequestMapping(method = RequestMethod.GET)
public class Controller {
    @Resource
    private Service service;
    @Resource
    private Centers centers;

    /**
     * 获取注册中心列表
     */
    @RequestMapping("/")
    public String index() {
        return JSON.toJSONString(centers.getCenters());
    }

    /**
     * 加法
     */
    @RequestMapping("/callAdd")
    public Integer callAdd(Integer num) {
        return service.add(num);
    }

    /**
     * 减法
     */
    @RequestMapping("/callSub")
    public Integer callSub(Integer num) {
        return service.sub(num);
    }

    /**
     * 乘法
     */
    @RequestMapping("/callMul")
    public Integer callAdd(Integer num, Integer mul) {
        return service.mul(num, mul);
    }

    /**
     * 返回实体
     */
    @RequestMapping("/callReturnObject")
    public Entity callReturnObject() {
        return service.returnObject();
    }
}
