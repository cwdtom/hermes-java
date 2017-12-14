package com.qurong.hermes;

import com.alibaba.fastjson.JSON;
import com.qurong.hermes.Hermes;
import com.qurong.hermes.entity.Center;
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
    private Center[] centers;
    @Resource
    private Hermes hermes;

    @RequestMapping("/")
    public String index() {
        return JSON.toJSONString(centers);
    }

    @RequestMapping("/callAdd")
    public String callAdd(Integer num) throws Exception {
        return hermes.call("client", "testAdd", num);
    }

    @RequestMapping("/callSub")
    public String callSub(Integer num) throws Exception {
        return hermes.call("client", "testSub", num);
    }
}
