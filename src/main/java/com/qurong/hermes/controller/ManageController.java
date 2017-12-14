package com.qurong.hermes.controller;

import com.qurong.hermes.entity.Center;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 管理接口
 * @author chenweidong
 */
@RestController
@RequestMapping(value = "/hermesManage", method = RequestMethod.GET)
public class ManageController {
    @Resource
    private Center[] centers;

    /**
     * 获取已注册的中心列表
     */
    @RequestMapping
    public Center[] index() {
        return centers;
    }
}
