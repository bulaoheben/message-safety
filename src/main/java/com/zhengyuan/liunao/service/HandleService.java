package com.zhengyuan.liunao.service;

import com.zhengyuan.liunao.entity.LSBEncrypt;
import org.springframework.stereotype.Service;


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

}
