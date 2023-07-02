package com.zhengyuan.liunao.controller.dealcontroller;

import java.awt.*;
import java.awt.image.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;

import com.zhengyuan.liunao.entity.LSBEncrypt;
import com.zhengyuan.liunao.service.HandleService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import static java.lang.Character.getNumericValue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/Sys")
public class LSBEncryptController {

	@Autowired
	HandleService handleService;

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
			ImageData imageData = new ImageData(url);
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
				LSBEncrypt.new_imageData = imageData;
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
						RGB rgb = imageData.palette.getRGB(imageData.getPixel(w, h));
						int gray = (rgb.red + rgb.green + rgb.blue) / 3; // 灰度公式：(R+G+B)/3

						//灰度值
						grey[w][h] = gray;
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
			String maxchar=String.valueOf((int)(LSBEncrypt.maxCha/8));

			return maxchar;
		}catch (IOException e){
			System.out.println("错误是"+e.toString());
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
		//判断末尾是否需要补0
		if(str.length()*8==LSBEncrypt.maxCha){
			LSBEncrypt.isappend=false;
		}

		//字符串转换为二进制数组
		char[] strChar=str.toCharArray();
		int[] x=new int[8];//定义临时补变量长度的数组
		String strx=null;
		if(LSBEncrypt.isappend==true){
			LSBEncrypt.byteStr=new int[strChar.length*8+8];//重新清0
		}else {
			LSBEncrypt.byteStr=new int[strChar.length*8];//重新清0，末尾不补0
		}

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
			//System.out.println("字符是"+ Arrays.toString(x));
			System.arraycopy(x,0,LSBEncrypt.byteStr,i*8,8);
		}
		if(LSBEncrypt.isappend==true){
			int[] y = new int[8];
			for(int i=0;i<8;i++){
				y[i]=0;
			}
			System.arraycopy(y,0,LSBEncrypt.byteStr,strChar.length*8,8);
		}

		String save_url = "src/main/resources/static/image/handleImg/output.bmp";
		// 调用嵌入方法
		if(LSBEncrypt.type==1){
			handleService.implant_color();
			// 保存嵌入信息后的图像
			try {
				ImageIO.write(LSBEncrypt.new_image, "bmp", new File(save_url));  // 保存路径和格式
			} catch (IOException e) {
				e.printStackTrace();
			}
			map.put("url","/image/handleImg/output.bmp");
		}else if(LSBEncrypt.type==2){
			handleService.implant_grey();
			// 保存嵌入信息后的图像
			ImageLoader imageLoader = new ImageLoader();
			imageLoader.data = new ImageData[] { LSBEncrypt.new_imageData };
			imageLoader.save(save_url, SWT.IMAGE_BMP);
			map.put("url","/image/handleImg/output.bmp");
		}else{
			map.put("state","图片格式类型不符合规定");
		}




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

	//提取嵌入信息
	@ResponseBody
	@RequestMapping(value = "/extractInfo")
	public Map<String,String> extractInfo(String url){
		Map<String,String> map = handleService.extract(url);
		Map<String,String> map_return = new HashMap<>();
		if(map.containsKey("false")){
			return map;
		}else{
			String byte_info = map.get("true");
			StringBuilder result = new StringBuilder(); // 存储转换结果的字符串
			int count = 1;
			for(int i=1;i<=byte_info.length();i++){
				if(i%8==0){
					if(count==byte_info.length()/8 && LSBEncrypt.isappend){
						break;
					}
					count++;
					int decimal = Integer.parseInt(byte_info.substring(i-8,i), 2); // 将二进制转换为十进制
					char character = (char) decimal; // 将十进制转换为字符
					result.append(character);
				}
			}
			map_return.put("true",result.toString());
			return map_return;
		}
	}


	//保存图片
	@ResponseBody
	@RequestMapping(value = "/saveImage")
	public Map<String,String> saveImage(String url2){
		Map<String,String> map = new HashMap<>();

			// 打开用于读取的URL连接
			File oldpaths = new File("handleImg/output.bmp");
			File newpaths = new File(url2);
			if (!newpaths.exists()) {
				//Files.copy(oldpaths.toPath(), newpaths.toPath());
				copyFile("handleImg/output.bmp", url2);
				System.out.println("文件移动成功（原文件不存在！");
			} else {
				newpaths.delete();
				copyFile("handleImg/output.bmp", url2);
				System.out.println("文件移动成功!（原文件存在)");
			}
			System.out.println("文件转移完成");
			map.put("result","图片已保存成功");
			return map;

	}


	private static boolean copyFile(String srcPath, String destDir) {
		boolean flag = false;

		File srcFile = new File(srcPath);
		if (!srcFile.exists()) { // 源文件不存在
			System.out.println("源文件不存在");
			return false;
		}
		// 获取待复制文件的文件名
		File file = new File(srcPath);
		String fileName = file.getName();
		// String fileName = srcPath.substring(srcPath.lastIndexOf("//")+2,
		// srcPath.length());
		// String fileName = srcPath
		// .substring(srcPath.lastIndexOf(File.separator));
		String destPath = destDir + fileName;
		if (destPath.equals(srcPath)) { // 源文件路径和目标文件路径重复
			System.out.println("源文件路径和目标文件路径重复!");
			return false;
		}
		File destFile = new File(destPath);
		if (destFile.exists() && destFile.isFile()) { // 该路径下已经有一个同名文件
			System.out.println("目标目录下已有同名文件!");
			return false;
		}

		File destFileDir = new File(destDir);
		destFileDir.mkdirs();
		try {
			FileInputStream fis = new FileInputStream(srcPath);
			FileOutputStream fos = new FileOutputStream(destFile);
			byte[] buf = new byte[1024];
			int c;
			while ((c = fis.read(buf)) != -1) {
				fos.write(buf, 0, c);
			}
			fis.close();
			fos.close();

			flag = true;
		} catch (IOException e) {
			//
		}

		if (flag) {
			System.out.println("复制文件成功!");
		}

		return flag;
	}


	@ResponseBody
	@RequestMapping(value = "/addnoise")
	public String addnoise(String url2) {
		// 读取 BMP 图像
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(LSBEncrypt.get_originalPicPath()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 添加随机噪声
		Random random = new Random();
		for (int y = 0; y < LSBEncrypt.height; y++) {
			for (int x = 0; x < LSBEncrypt.width; x++) {
				// 获取当前像素的颜色值
				Color color = new Color(image.getRGB(x, y));

				// 生成随机噪声
				int noise = random.nextInt(128);
				int noise2 = random.nextInt(2);

				// 添加噪声到颜色的 RGB 分量上
				int red, green, blue;
				if (noise2 ==0) {
					red = color.getRed() + noise;
					green = color.getGreen() + noise;
					blue = color.getBlue() + noise;
				} else {
					red = color.getRed() - noise;
					green = color.getGreen() - noise;
					blue = color.getBlue() - noise;
				}

				// 限制 RGB 分量的范围在 0~255 之间
				red = Math.min(Math.max(red, 0), 255);
				green = Math.min(Math.max(green, 0), 255);
				blue = Math.min(Math.max(blue, 0), 255);

				// 创建新的颜色对象并设置噪声后的 RGB 值
				Color noisyColor = new Color(red, green, blue);

				// 将带有噪声的颜色值设置回图像的像素
				image.setRGB(x, y, noisyColor.getRGB());
			}
		}

		// 保存带有随机噪声的 BMP 图像
//		String oldl = LSBEncrypt.get_originalPicPath();
//		String[] newl = oldl.split("\\\\");
//		String[] x = newl[newl.length-1].split("\\.");
//		String target = "";
//		for (int i = 0; i < newl.length - 1; i++) {
//			target = target + newl[i] + "\\";
//		}
//		System.out.println("bmp"+x);
//		target = target + x[0] + "_noise.bmp";

		try {
			//ImageIO.write(image, "bmp", new File(target));
			ImageIO.write(image, "bmp", new File("src/main/resources/static/image/handleImg/output.bmp"));
			System.out.println("添加随机噪声完成，已保存为 output.bmp");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "/image/handleImg/output.bmp";
	}

}