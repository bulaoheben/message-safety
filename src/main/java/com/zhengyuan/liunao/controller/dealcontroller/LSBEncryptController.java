package com.zhengyuan.liunao.controller.dealcontroller;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;

import com.zhengyuan.liunao.entity.LSBEncrypt;
import com.zhengyuan.liunao.service.HandleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import static java.lang.Character.getNumericValue;

@Controller
@RequestMapping("/Sys")
public class LSBEncryptController {

	@Autowired
	HandleService handleService;


	//发送24位真彩图
	@ResponseBody
	@RequestMapping(value = "/dealLogin")
	public String getInfo(MultipartFile file, HttpSession httpSession) {
		System.out.println(file);
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
			//判断文件类型(另一种方式
			File file = new File(url);
			FileInputStream inputStream = new FileInputStream(file);
			// Read the header fields
			byte[] header = new byte[54];
			inputStream.read(header);
			// Parse the header fields
			int fileSize = ((int) header[2] & 0xff) | (((int) header[3] & 0xff) << 8) | (((int) header[4] & 0xff) << 16) | (((int) header[5] & 0xff) << 24);
			int dataOffset = ((int) header[10] & 0xff) | (((int) header[11] & 0xff) << 8) | (((int) header[12] & 0xff) << 16) | (((int) header[13] & 0xff) << 24);
			int bpp = ((int) header[28] & 0xff) | (((int) header[29] & 0xff) << 8);

			// Determine image type based on bpp
			if(bpp == 24) {
				LSBEncrypt.new_image = image;
				System.out.println("This is a 24-bit true color bitmap.");
				LSBEncrypt.type=1;
				LSBEncrypt.maxCha=width*height*3;//真彩图的隐藏信息的大小bit是（长*宽*3）

				//对符合规定的图片进行处理
				LSBEncrypt.set_originalPicPath(url);
				LSBEncrypt.setWidth(width);//读出图片的宽和高
				LSBEncrypt.setHeight(height);
				int[][][] rgb = new int[width][height][3];//读出图片的数据
				String[][][] rgb_byte = new String[width][height][3];
				//将图像每个点的像素(R,G,B)存储在数组中,读出数据
				for (int w = 0; w < width; w++) {
					for (int h = 0; h < height; h++) {
						int pixel = image.getRGB(w, h);//读取的是一个24位的数据
						//数据三个字节分别代表R、B、G
						rgb[w][h][0] = (pixel & 0xff0000) >> 16;//R
						rgb[w][h][1] = (pixel & 0xff00) >> 8;//B
						rgb[w][h][2] = (pixel & 0xff);//G
						String s1 = Integer.toBinaryString(rgb[w][h][0]);
						String s2 = Integer.toBinaryString(rgb[w][h][1]);
						String s3 = Integer.toBinaryString(rgb[w][h][2]);
						for (int a = s1.length(); a < 8; a++) {
							s1 = "0"+ s1;
						}
						rgb_byte[w][h][0] = s1;
						for (int a = s2.length(); a < 8; a++) {
							s2 = "0"+ s2;
						}
						rgb_byte[w][h][1] = s2;
						for (int a = s3.length(); a < 8; a++) {
							s3 = "0"+ s3;
						}
						rgb_byte[w][h][2] = s3;
					}
				}
				LSBEncrypt.setRgb(rgb);
				LSBEncrypt.setRgb_byte(rgb_byte);
			} else if(bpp == 8) {
				LSBEncrypt.new_image = image;
				System.out.println("This is an 8-bit grayscale bitmap.");
				LSBEncrypt.type=2;
				LSBEncrypt.maxCha=width*height;//灰度图的隐藏信息是（长*宽）,即像素数

				//对符合规定的图片进行处理
				LSBEncrypt.set_originalPicPath(url);
				LSBEncrypt.setWidth(width);//读出图片的宽和高
				LSBEncrypt.setHeight(height);

				int[][] grey = new int[width][height];//读出图片的数据
				String[][] grey_byte = new String[width][height];
				for (int w = 0; w < width; w++) {
					for (int h = 0; h < height; h++) {
						int pixel = image.getRGB(w, h);
						//灰度值
						grey[w][h] = (pixel >> 16) & 0xFF;  // 提取红色通道值作为灰度值
						String s1 = Integer.toBinaryString(grey[w][h]);
						for (int a = s1.length(); a < 8; a++) {
							s1 = "0"+ s1;
						}
						grey_byte[w][h] = s1;
					}
				}
				LSBEncrypt.grey = grey;
				LSBEncrypt.grey_byte = grey_byte;

			} else {
				System.out.println("This is not a supported bitmap format.");
				return "图片格式不是BMP图";
			}
			inputStream.close();
			String maxchar=String.valueOf(LSBEncrypt.maxCha);

			return maxchar;
		}catch (IOException e){
			return "选取路径文件不存在！";
		}
	}



	//加密图片
	@ResponseBody
	@RequestMapping(value = "/encryptImage")
	public Map<String,String> encryptImage(String str){
		//定义返回结果
		Map<String,String> map = new HashMap<>();
		if(str.length()*8>LSBEncrypt.maxCha){
			map.put("state","超出最长长度显示");
			return map;
		}

		//字符串转换为二进制数组
		char[] strChar=str.toCharArray();
		int[] x=new int[8];//定义临时补变量长度的数组
		String strx=null;
		LSBEncrypt.byteStr=new int[strChar.length*8];//重新清0
		for(int i=0;i<strChar.length;i++){
			strx = Integer.toBinaryString(strChar[i]);
			if(strx.length()<8){//8位像素值补0
				int y=8-strx.length();
				for (int z=0;z<y;z++){
					x[z]=0;
				}
			}
			int h=0;
			for (int z=8-strx.length();z<8;z++){//补充值进去
				x[z]=getNumericValue((int)strx.charAt(h));
				h++;
			}
			System.out.println("字符是"+ Arrays.toString(x));
			System.arraycopy(x,0,LSBEncrypt.byteStr,i*8,8);
		}
		System.arraycopy("00000000",0,LSBEncrypt.byteStr,strChar.length*8,8);

		// 调用嵌入方法
		if(LSBEncrypt.type==1){
			handleService.implant_color();
		}else if(LSBEncrypt.type==2){
			handleService.implant_grey();
		}else{
			map.put("state","图片格式类型不符合规定");
		}

		// 保存嵌入信息后的图像
		try {
			ImageIO.write(LSBEncrypt.new_image, "bmp", new File("handleImg/output.bmp"));  // 保存路径和格式
		} catch (IOException e) {
			e.printStackTrace();
		}
		map.put("url","handleImg/output.bmp");


//		StringBuilder sb = new StringBuilder();
//		for (int num : LSBEncrypt.byteStr) {
//			sb.append(num);
//		}
//		String result = sb.toString();
//		String tempStr;
//		for (int i = 0; i < result.length(); i += 8) {
//			int endIndex = Math.min(i + 8, result.length());
//			tempStr = result.substring(i, endIndex);
//			System.out.println("原本是"+Arrays.toString(LSBEncrypt.byteStr));
//			System.out.println("加密回来是"+BinstrToChar(tempStr));
//		}
		return map;
	}

	//二进制数组转换为字符串
//		String[] tempStr=result.split(" ");
//		char[] tempChar=new char[tempStr.length];
//		for(int i=0;i<tempStr.length;i++) {
//			tempChar[i]=BinstrToChar(tempStr[i]);
//		}
//		System.out.println(String.valueOf(tempChar));


	//添加随机噪声
	@ResponseBody
	@RequestMapping(value = "/randomNoise")
	public Map<String,String> randomNoise(){
		//定义返回结果
		Map<String,String> map = new HashMap<>();

		// 读取 BMP 图像
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(LSBEncrypt.get_originalPicPath()));
		} catch (IOException e) {
			return map;
		}

		// 添加随机噪声
		Random random = new Random();
		for (int y = 0; y < LSBEncrypt.height; y++) {
			for (int x = 0; x < LSBEncrypt.width; x++) {
				// 生成随机噪声
				int noise = random.nextInt(256);

				// 添加噪声到颜色的 RGB 分量上
				int red = LSBEncrypt.rgb[y][x][0] + noise;
				int green = LSBEncrypt.rgb[y][x][1] + noise;
				int blue = LSBEncrypt.rgb[y][x][2] + noise;

				// 限制 RGB 分量的范围在 0~255 之间
				red = Math.min(Math.max(red, 0), 255);
				green = Math.min(Math.max(green, 0), 255);
				blue = Math.min(Math.max(blue, 0), 255);

				// 创建新的颜色对象并设置噪声后的 RGB 值
				Color noisyColor = new Color(red, green, blue);

				// 将带有噪声的颜色值设置回图像的像素
				image.setRGB(y,x, noisyColor.getRGB());
			}
		}

		// 保存带有随机噪声的 BMP 图像
		try {
			String[] tempStr=LSBEncrypt.get_originalPicPath().split("\\\\");
			String noiseImageurl=tempStr[0];
			for (int i=1;i<tempStr.length-1;i++){
				noiseImageurl=noiseImageurl+"\\"+tempStr[i];
			}
			String x= tempStr[tempStr.length-1];
			String[] array = x.split("\\.");
			noiseImageurl=noiseImageurl+"\\"+array[0]+"_noise.bmp";
			ImageIO.write(image, "bmp", new File(noiseImageurl));
			System.out.println("添加随机噪声完成");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	//将二进制转换成字符
	public char BinstrToChar(String binStr){
		int[] temp=BinstrToIntArray(binStr);
		int sum=0;
		for(int i=0; i<temp.length;i++){
			sum +=temp[temp.length-1-i]<<i;
		}
		return (char)sum;
	}

	//将二进制字符串转换成int数组
	public int[] BinstrToIntArray(String binStr) {
		char[] temp=binStr.toCharArray();
		int[] result=new int[temp.length];
		for(int i=0;i<temp.length;i++) {
			result[i]=temp[i]-48;
		}
		return result;
	}
}