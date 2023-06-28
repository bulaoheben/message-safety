package com.zhengyuan.liunao.service;

import com.zhengyuan.liunao.entity.LSBEncrypt;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Service
public class HandleService {

	// 对24位真彩图进行信息嵌入
	public void implant_color(){
		int[][][] rgb_implant = new int[LSBEncrypt.width][LSBEncrypt.height][3];
		int count = 0;
		for(int w=0;w<LSBEncrypt.width;w++){
			for(int h=0;h<LSBEncrypt.height;h++){
				for(int x=0;x<3;x++){
					count++;
					if(count>LSBEncrypt.byteStr.length){
						rgb_implant[w][h][x] = LSBEncrypt.rgb[w][h][x];
					}else{
						// 对RGB三层最后一位写入的二进制嵌入信息值
						if(LSBEncrypt.byteStr[count-1]==Integer.parseInt(LSBEncrypt.rgb_byte[w][h][x].substring(7,8))){
							rgb_implant[w][h][x] = LSBEncrypt.rgb[w][h][x];
						}else {
							if(LSBEncrypt.byteStr[count-1]==0){
								rgb_implant[w][h][x] = LSBEncrypt.rgb[w][h][x]-1;
							}else{
								rgb_implant[w][h][x] = LSBEncrypt.rgb[w][h][x]+1;
							}
						}
					}
				}
				int newPixel = (rgb_implant[w][h][0] << 16) | (rgb_implant[w][h][1] << 8) | rgb_implant[w][h][2];
				LSBEncrypt.new_image.setRGB(w, h, newPixel);
			}
		}
		LSBEncrypt.setRgb_implant(rgb_implant);
	}

	// 对灰度图进行信息嵌入
	public void implant_grey(){
		int[][] grey_implant = new int[LSBEncrypt.width][LSBEncrypt.height];
		int count = 0;
		for(int w=0;w<LSBEncrypt.width;w++){
			for(int h=0;h<LSBEncrypt.height;h++){
				count++;
				if(count>LSBEncrypt.byteStr.length){
					grey_implant[w][h] = LSBEncrypt.grey[w][h];
				}else{
					// 对RGB三层最后一位写入的二进制嵌入信息值
					if(LSBEncrypt.byteStr[count-1]==Integer.parseInt(LSBEncrypt.grey_byte[w][h].substring(7,8))){
						grey_implant[w][h] = LSBEncrypt.grey[w][h];
					}else {
						if(LSBEncrypt.byteStr[count-1]==0){
							grey_implant[w][h] = LSBEncrypt.grey[w][h]-1;
						}else{
							grey_implant[w][h] = LSBEncrypt.grey[w][h]+1;
						}
					}
				}
				int newPixel = (grey_implant[w][h] << 16) | (grey_implant[w][h] << 8) | grey_implant[w][h];
				LSBEncrypt.new_image.setRGB(w, h, newPixel);
			}
		}
		LSBEncrypt.grey_implant = grey_implant;
	}

	//将rgb数组转为二进制
	public void invertRGBByte(int[][][] rgb,int width,int height){
		String[][][] rgb_byte = new String[width][height][3];
		for(int i=0;i<width;i++){
			for(int j=0;j<height;j++){
				for(int z=0;z<3;z++){
					String s = Integer.toBinaryString(rgb[i][j][z]);
					for (int a = s.length(); a < 8; a++) {
						s = "0"+ s;
					}
					rgb_byte[i][j][z] = s;
				}
			}
		}
		LSBEncrypt.setRgb_byte(rgb_byte);
	}

	// 提取嵌入信息
	public Map<String,String> extract(){
		Map<String,String> map = new HashMap<>();
		try {
			String url = "handleImg/output.bmp";
			BufferedImage image = ImageIO.read(new File(url));
			byte[] header = new byte[54];
			File file = new File(url);
			FileInputStream inputStream = new FileInputStream(file);
			inputStream.read(header);
			int bpp = ((int) header[28] & 0xff) | (((int) header[29] & 0xff) << 8);
			if(bpp == 24) {
				// 24位真彩图
				int width = image.getWidth();
				int height = image.getHeight();
				// 提取出的二进制信息,末尾是00000000结束
				String byte_info = "";
				int count = 0;
				for (int w = 0; w < width; w++) {
					for (int h = 0; h < height; h++) {
						int pixel = image.getRGB(w, h);//读取的是一个24位的数据
						//数据三个字节分别代表R、B、G
						int red = (pixel & 0xff0000) >> 16;//R
						int blue = (pixel & 0xff00) >> 8;//B
						int green = (pixel & 0xff);//G
						String s1 = Integer.toBinaryString(red);
						String s2 = Integer.toBinaryString(blue);
						String s3 = Integer.toBinaryString(green);
						for (int a = s1.length(); a < 8; a++) {
							s1 = "0"+ s1;
						}
						for (int a = s2.length(); a < 8; a++) {
							s2 = "0"+ s2;
						}
						for (int a = s3.length(); a < 8; a++) {
							s3 = "0"+ s3;
						}
						byte_info+=s1.substring(7,8);
						count++;
						if(count==8){
							if(byte_info.substring(byte_info.length()-8).equals("00000000")){
								map.put("true",byte_info);
								return map;
							}else{
								count=0;
							}
						}
						byte_info+=s2.substring(7,8);
						count++;
						if(count==8){
							if(byte_info.substring(byte_info.length()-8).equals("00000000")){
								map.put("true",byte_info);
								return map;
							}else{
								count=0;
							}
						}
						byte_info+=s3.substring(7,8);
						count++;
						if(count==8){
							if(byte_info.substring(byte_info.length()-8).equals("00000000")){
								map.put("true",byte_info);
								return map;
							}else{
								count=0;
							}
						}
					}
				}
			}else if(bpp == 8) {
				// 灰度图
				int width = image.getWidth();
				int height = image.getHeight();
				// 提取出的二进制信息,末尾是00000000结束
				String byte_info = "";
				int count = 0;
				for (int w = 0; w < width; w++) {
					for (int h = 0; h < height; h++) {
						int pixel = image.getRGB(w, h);
						//灰度值
						int grey = (pixel >> 16) & 0xFF;
						String s1 = Integer.toBinaryString(grey);
						for (int a = s1.length(); a < 8; a++) {
							s1 = "0"+ s1;
						}
						byte_info+=s1.substring(7,8);
						count++;
						if(count==8){
							if(byte_info.substring(byte_info.length()-8).equals("00000000")){
								map.put("true",byte_info);
								return map;
							}else{
								count=0;
							}
						}
					}
				}
			}else{
				map.put("false","图片格式类型不符合要求");
				return map;
			}
			inputStream.close();
		}catch (IOException e){
			map.put("false","选取路径文件不存在！");
			return map;
		}
		map.put("false","未提取出结束标识符00000000");
		return map;
	}

}
