package com.zhengyuan.liunao.tools;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class UploadUtils {
    //定义一个上传文件的路径，webapp下的upload
//    private static final String path = UploadUtils.class.getClassLoader().getResource("").getPath();
//    private static final String BASE_PATH = path.substring(0, path.indexOf("/WEB-INF/"))+"\\upload";
    private static final String BASE_PATH = "resources/static/Sys/images/upload/";
    //定义文件服务器的访问地址
    private static  final String SERVER_PATH="http://localhost:8666/";
    public static String upload(MultipartFile file){
//        System.out.println(BASE_PATH);
        //获得上传文件的名称
        String filename = file.getOriginalFilename();
        String newFileName =filename;
        //创建文件实例对象
        File uploadFile = new File(BASE_PATH,newFileName);
        //判断当前文件是否存在
        if (!uploadFile.exists()){
            //如果不存在就创建一个文件夹
            uploadFile.mkdirs();
        }
        //执行文件上传的命令
        try {
//            file.transferTo(uploadFile);
            byte[] bytes = file.getBytes();
            //写入指定文件夹
            OutputStream out = new FileOutputStream(uploadFile);
            out.write(bytes);
        } catch (IOException e) {
            return null;
        }
        System.out.println("11111111111");
        System.out.println(newFileName);
        System.out.println(BASE_PATH);
//        return SERVER_PATH+newFileName;
        return BASE_PATH+newFileName;
    }


    public static File multipartFileToFile(MultipartFile file) throws Exception {
        File toFile = null;
        if (file.equals("") || file.getSize() <= 0) {
            file = null;
        } else {
            InputStream ins = null;
            ins = file.getInputStream();
            toFile = new File(file.getOriginalFilename());
            inputStreamToFile(ins, toFile);
            ins.close();
        }
        return toFile;

    }



    private static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
//            throw new ToLogException("读取文件错误", e);
        }
    }
}
