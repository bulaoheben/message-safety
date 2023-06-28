package com.zhengyuan.liunao.service;

import com.zhengyuan.liunao.entity.LSBEncrypt;
import org.springframework.stereotype.Service;


@Service
public class HandleService {

	// 对24位真彩图进行信息嵌入
	public String implant(){
		int[][][] rgb_implant = new int[LSBEncrypt.width][LSBEncrypt.height][3];
		for(int i=0;i< LSBEncrypt.byteStr.length;i++){
			int w = i/LSBEncrypt.width;
			int h = i%LSBEncrypt.height;
			// 对RGB三层写入一样的二进制嵌入信息值
			if(LSBEncrypt.byteStr[i]==Integer.parseInt(LSBEncrypt.rgb_byte[w][h][0].substring(7,8))){
				rgb_implant[w][h][0] = LSBEncrypt.rgb[w][h][0];
			}else {
				if(LSBEncrypt.byteStr[i]==0){
					rgb_implant[w][h][0] = LSBEncrypt.rgb[w][h][0]-1;
				}else{
					rgb_implant[w][h][0] = LSBEncrypt.rgb[w][h][0]+1;
				}
			}
			if(LSBEncrypt.byteStr[i]==Integer.parseInt(LSBEncrypt.rgb_byte[w][h][1].substring(7,8))){
				rgb_implant[w][h][1] = LSBEncrypt.rgb[w][h][1];
			}else {
				if(LSBEncrypt.byteStr[i]==0){
					rgb_implant[w][h][1] = LSBEncrypt.rgb[w][h][1]-1;
				}else{
					rgb_implant[w][h][1] = LSBEncrypt.rgb[w][h][1]+1;
				}
			}
			if(LSBEncrypt.byteStr[i]==Integer.parseInt(LSBEncrypt.rgb_byte[w][h][2].substring(7,8))){
				rgb_implant[w][h][2] = LSBEncrypt.rgb[w][h][2];
			}else {
				if(LSBEncrypt.byteStr[i]==0){
					rgb_implant[w][h][2] = LSBEncrypt.rgb[w][h][2]-1;
				}else{
					rgb_implant[w][h][2] = LSBEncrypt.rgb[w][h][2]+1;
				}
			}
		}
		LSBEncrypt.setRgb_implant(rgb_implant);
		return "true";
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
