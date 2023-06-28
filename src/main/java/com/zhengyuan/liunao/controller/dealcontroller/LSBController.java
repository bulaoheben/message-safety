package com.zhengyuan.liunao.controller.dealcontroller;

import cn.hutool.crypto.SecureUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/Sys")
public class LSBController {

    @RequestMapping(value = "/upload")
    @ResponseBody
    public String registerCompanyDeal(@RequestBody String msg) {
        System.out.println("上传图片："+msg);



        return msg;
    }
}
