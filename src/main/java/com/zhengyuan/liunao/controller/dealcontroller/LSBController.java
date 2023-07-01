package com.zhengyuan.liunao.controller.dealcontroller;

import cn.hutool.crypto.SecureUtil;
import com.zhengyuan.liunao.tools.UploadUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.zhengyuan.liunao.entity.DataJson;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/Sys")
public class LSBController {

//    @RequestMapping(value = "/upload")
//    @ResponseBody
//    public String registerCompanyDeal(MultipartFile file) {
//        System.out.println("上传图片："+file);
//
//
//
//        return "成功";
//    }


        @RequestMapping("/image")
        @ResponseBody
        public DataJson image(MultipartFile file) throws Exception {
            //调用工具类完成文件上传
//            String imagePath = UploadUtils.upload(file);
//           String imagePath =  "文件路径";
            File newfile = UploadUtils.multipartFileToFile(file); //存在根目录下
            System.out.println(file.getOriginalFilename());
            String imagePath = newfile.getPath();
            System.out.println(imagePath);
            DataJson dataJson = new DataJson();
//            if (imagePath != null){
                //创建一个HashMap用来存放图片路径
                HashMap hashMap = new HashMap();
                hashMap.put("文件名",file.getOriginalFilename());
                hashMap.put("src",imagePath);
//                hashMap.put("src",imagePath);
                dataJson.setCode(0);
                dataJson.setMsg("上传成功");
                dataJson.setData(hashMap);
                System.out.println("成功！！！！！！！");
//            }else{
//                dataJson.setCode(1);
//                dataJson.setMsg("上传失败");
//                System.out.println("失败！！！！！！！！");
//            }
            return dataJson;
        }
    }


