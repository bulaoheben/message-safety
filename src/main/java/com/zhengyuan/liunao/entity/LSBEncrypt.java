package com.zhengyuan.liunao.entity;

import io.swagger.annotations.ApiModel;

import java.io.FileInputStream;
import java.io.FileOutputStream;


@ApiModel(value = "LSBEncrypt",description = "LSB加密")
public class LSBEncrypt {
	//原始图片路径

	private String _originalPicPath = null;
	//隐藏信息路径
	private String _hidingInfoPath = null;
	//原始图片的文件流
	private FileInputStream _picStream = null;
	//隐藏信息的文件流
	private FileOutputStream _infoStream = null;
	//图片类型,1为真彩图，2为256度灰度图
	public int type = 0;
	//原始图片的宽和高
	public int width;
	public int height;
	public int[][][] rgb;
	public int maxCha;

	public LSBEncrypt(String path1) {
		this._originalPicPath=path1;
	}

	public LSBEncrypt(){
		super();
	}

	public void set_originalPicPath(String Inurl){
		this._originalPicPath=Inurl;
	}
	public void setWidth(int weight){
		this.width=weight;
	}
	public void setHeight(int height){
		this.height=height;
	}

	public void setRgb(int[][][] rgb){
		this.rgb=rgb;
	}
	public void setType(int type){
		this.type=type;
	}
}
