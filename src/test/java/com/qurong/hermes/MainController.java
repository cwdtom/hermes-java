package com.qurong.hermes;

import com.alibaba.fastjson.JSON;
import com.qurong.hermes.entity.Constant;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 测试
 *
 * @author chenweidong
 */
@RestController
@RequestMapping(method = RequestMethod.GET)
public class MainController {
    @Resource
    private TestService testService;

    @RequestMapping("/")
    public String index() {
        return JSON.toJSONString(Constant.centers);
    }

    @RequestMapping("/callAdd")
    public Integer callAdd(Integer num) {
        return testService.add(num);
    }

    @RequestMapping("/callSub")
    public Integer callSub(Integer num) {
        return testService.sub(num);
    }

    @RequestMapping("/callMul")
    public Integer callAdd(Integer num, Integer mul) {
        return testService.mul(num, mul);
    }
}
