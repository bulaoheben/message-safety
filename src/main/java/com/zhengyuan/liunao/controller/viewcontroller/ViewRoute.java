package com.zhengyuan.liunao.controller.viewcontroller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/Sys")
public class ViewRoute {

	/*登录相关*/
	@RequestMapping("/LSB_embed")
	public ModelAndView loginView(HttpSession httpSession) {
		ModelAndView mv = new ModelAndView("LSB_embed");

		mv.addObject("outputImg","");
		return mv;
	}
		/*添加噪声*/
	@RequestMapping("/addNoise")
	public String addNoise(HttpSession httpSession) {

		return "addNoise";
	}

	/*带密钥的LSB*/
	@RequestMapping("/Key_LSB")
	public String Key_LSB(HttpSession httpSession) {

		return "Key_LSB";
	}

	@RequestMapping("/LSB_extract")
	public String LSB_extract(HttpSession httpSession) {

		return "LSB_extract";
	}

	


	
}
