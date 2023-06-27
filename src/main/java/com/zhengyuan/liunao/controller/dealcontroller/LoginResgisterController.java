package com.zhengyuan.liunao.controller.dealcontroller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.zhengyuan.liunao.entity.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.fastjson.JSON;
import com.zhengyuan.liunao.entity.Admin;
import com.zhengyuan.liunao.entity.Company;
import com.zhengyuan.liunao.service.AdminService;
import com.zhengyuan.liunao.service.CompanyService;
import com.zhengyuan.liunao.service.ClientService;

import cn.hutool.crypto.SecureUtil;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/Sys")
public class LoginResgisterController {

	@Autowired
	AdminService adminService;

	@Autowired
	CompanyService companyService;

	@Autowired
	ClientService clientService;
	

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
