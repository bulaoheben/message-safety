package com.zhengyuan.liunao.controller.dealcontroller;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/Sys")
public class LoginResgisterController {


	//发送24位真彩图
	@ResponseBody
	@RequestMapping(value = "/dealLogin")
	public String getInfo(MultipartFile file, HttpSession httpSession) {
		try {
			// 处理上传的 BMP 文件
			if(!file.isEmpty()){

			}
			byte[] imageData = file.getBytes();

			// 上传成功
			return "BMP 文件上传成功";
		} catch (IOException e) {
			// 上传失败
			return "BMP 文件上传失败";
		}
	}
}
