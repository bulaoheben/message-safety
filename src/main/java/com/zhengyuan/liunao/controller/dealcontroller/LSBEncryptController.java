package com.zhengyuan.liunao.controller.dealcontroller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;

import com.zhengyuan.liunao.entity.LSBEncrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/Sys")
public class LSBEncryptController {
	LSBEncrypt encrypt=new LSBEncrypt();

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

	//	上传待嵌入的图片
	@ResponseBody
	@RequestMapping(value = "/upImageUrl")
	public String upImageUrl(String url,HttpSession httpSession) {

		if (url==null){
			return "设置失败";
		}
		//导入图片，检查宽高
		try{
			BufferedImage image = ImageIO.read(new File(url));
			int width = image.getWidth();
			int height = image.getHeight();
			if(width%4!=0){
				return "图片宽度不为4的倍数";
			}else if(width>512||height>512){
				return "图片大小超出要求（512*512）";
			}

			//对符合规定的图片进行处理
			this.encrypt.set_originalPicPath(url);
			this.encrypt.setWidth(width);//读出图片的宽和高
			this.encrypt.setHeight(height);
			int[][][] rgb = new int[width][height][3];//读出图片的数据
			//将图像每个点的像素(R,G,B)存储在数组中,读出数据
			for (int w = 0; w < width; w++) {
				for (int h = 0; h < height; h++) {
					int pixel = image.getRGB(w, h);//读取的是一个24位的数据
					//数据三个字节分别代表R、B、G
					rgb[w][h][0] = (pixel & 0xff0000) >> 16;//R
					rgb[w][h][1] = (pixel & 0xff00) >> 8;//B
					rgb[w][h][2] = (pixel & 0xff);//G
				}
			}
			this.encrypt.setRgb(rgb);
			//判断文件类型(另一种方式
			File file = new File(url);
			FileInputStream inputStream = new FileInputStream(file);
			// Read the header fields
			byte[] header = new byte[54];
			inputStream.read(header);
			// Parse the header fields
			int fileSize = ((int) header[2] & 0xff) | (((int) header[3] & 0xff) << 8) | (((int) header[4] & 0xff) << 16) | (((int) header[5] & 0xff) << 24);
			int dataOffset = ((int) header[10] & 0xff) | (((int) header[11] & 0xff) << 8) | (((int) header[12] & 0xff) << 16) | (((int) header[13] & 0xff) << 24);
			int width2 = ((int) header[18] & 0xff) | (((int) header[19] & 0xff) << 8) | (((int) header[20] & 0xff) << 16) | (((int) header[21] & 0xff) << 24);
			int height2 = ((int) header[22] & 0xff) | (((int) header[23] & 0xff) << 8) | (((int) header[24] & 0xff) << 16) | (((int) header[25] & 0xff) << 24);
			int bpp = ((int) header[28] & 0xff) | (((int) header[29] & 0xff) << 8);

			// Determine image type based on bpp
			if(bpp == 24) {
				System.out.println("This is a 24-bit true color bitmap.");
			} else if(bpp == 8) {
				System.out.println("This is an 8-bit grayscale bitmap.");
			} else {
				System.out.println("This is not a supported bitmap format.");
			}
			inputStream.close();
			if(width==width2){
				System.out.println("管用");
				System.out.println(dataOffset);
			}else {
				System.out.println("不管用");
			}





			return "设置成功";
		}catch (IOException e){
			return "选取路径文件不存在！";
		}
	}
}