package com.zhengyuan.liunao.entity;

import io.swagger.annotations.ApiModel;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;


public class LSBEncrypt {
	//原始图片路径
	private static String _originalPicPath = null;
	//隐藏信息路径
	private static String _hidingInfoPath = null;
	//原始图片的文件流
	private static FileInputStream _picStream = null;
	//隐藏信息的文件流
	private static FileOutputStream _infoStream = null;
	//图片类型,1为真彩图，2为256度灰度图
	public static int type = 0;
	//原始图片的宽和高
	public static int width;
	public static int height;
	public static int[][][] rgb;
	// rgb转为二进制的图片数组
	public static String[][][] rgb_byte;
	// 嵌入信息的二进制数组
	public static int[] byteStr;
	public static int maxCha=90;
	// 嵌入信息过后的十进制的图片数组
	public static int[][][] rgb_implant;

	// 灰度图数组
	public static int[][] grey;
	// rgb转为二进制的图片数组
	public static String[][] grey_byte;
	// 嵌入信息过后的十进制的图片数组
	public static int[][] grey_implant;

	// 传入最初图片的BufferedImage
	public static BufferedImage new_image;

	public static void setRgb_implant(int[][][] rgb_implant) {
		LSBEncrypt.rgb_implant = rgb_implant;
	}

	public static void setRgb_byte(String[][][] rgb_byte) {
		LSBEncrypt.rgb_byte = rgb_byte;
	}

	public static String get_originalPicPath() {
		return _originalPicPath;
	}

	public static void set_originalPicPath(String _originalPicPath) {
		LSBEncrypt._originalPicPath = _originalPicPath;
	}

	public static String get_hidingInfoPath() {
		return _hidingInfoPath;
	}

	public static void set_hidingInfoPath(String _hidingInfoPath) {
		LSBEncrypt._hidingInfoPath = _hidingInfoPath;
	}

	public static FileInputStream get_picStream() {
		return _picStream;
	}

	public static void set_picStream(FileInputStream _picStream) {
		LSBEncrypt._picStream = _picStream;
	}

	public static FileOutputStream get_infoStream() {
		return _infoStream;
	}

	public static void set_infoStream(FileOutputStream _infoStream) {
		LSBEncrypt._infoStream = _infoStream;
	}

	public static int getType() {
		return type;
	}

	public static void setType(int type) {
		LSBEncrypt.type = type;
	}

	public static int getWidth() {
		return width;
	}

	public static void setWidth(int width) {
		LSBEncrypt.width = width;
	}

	public static int getHeight() {
		return height;
	}

	public static void setHeight(int height) {
		LSBEncrypt.height = height;
	}

	public static int[][][] getRgb() {
		return rgb;
	}

	public static void setRgb(int[][][] rgb) {
		LSBEncrypt.rgb = rgb;
	}

	public static int[] getByteStr() {
		return byteStr;
	}

	public static void setByteStr(int[] byteStr) {
		LSBEncrypt.byteStr = byteStr;
	}


}
