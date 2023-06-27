package com.zhengyuan.liunao.controller.dealcontroller;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import com.zhengyuan.liunao.entity.LSBEncrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

@Controller
@RequestMapping("/Sys")
public class LSBEncryptController {
/*
	@Autowired
	LSBEncrypt encrypt;*/

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

//	//	上传待嵌入的图片
//	@ResponseBody
//	@RequestMapping(value = "/upImageUrl")
//	public String upImageUrl(String url,HttpSession httpSession) {
//
//		if (url==null){
//			return "设置失败";
//		}
//		//导入图片，检查宽高
//		try{
//			BufferedImage image = ImageIO.read(new File(url));
//			int width = image.getWidth();
//			int height = image.getHeight();
//			if(width%4!=0){
//				return "图片宽度不为4的倍数";
//			}else if(width>512||height>512){
//				return "图片大小超出要求（512*512）";
//			}
//
//			//对符合规定的图片进行处理
//			encrypt.set_originalPicPath(url);
//			encrypt.setWidth(width);
//			encrypt.setHeight(height);
//			int[][][] rgb = new int[width][height][3];
//			//将图像每个点的像素(R,G,B)存储在数组中
//			for (int w = 0; w < width; w++) {
//				for (int h = 0; h < height; h++) {
//					int pixel = image.getRGB(w, h);//读取的是一个24位的数据
//					//数据三个字节分别代表R、B、G
//					rgb[w][h][0] = (pixel & 0xff0000) >> 16;//R
//					rgb[w][h][1] = (pixel & 0xff00) >> 8;//B
//					rgb[w][h][2] = (pixel & 0xff);//G
//				}
//			}
//			return "设置成功";
//		}catch (IOException e){
//			return "选取路径文件不存在！";
//		}
//	}
}
