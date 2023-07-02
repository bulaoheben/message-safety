package com.zhengyuan.liunao.controller.dealcontroller;

import com.zhengyuan.liunao.service.KeyHandleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/Sys")
public class LSBKeyEncryptController {

    @Autowired
    KeyHandleService keyHandleService;

    @ResponseBody
    @RequestMapping(value = "/embedMessage")
    public Map<String,String> embedMessage(@RequestBody Map<String,String> map) {
        BufferedImage image = keyHandleService.readImage(map.get("url"));
        String message=map.get("message");
        String key = map.get("key");
        // 在message的开头存入message的长度
        int length = message.length();
        String binary = String.format("%8s", Integer.toBinaryString(length)).replace(' ', '0');
        // 创建boolean类型数组
        boolean[] boolArr = new boolean[binary.length()];
        for (int i = 0; i < binary.length(); i++) {
            boolArr[i] = binary.charAt(i) == '1'; // 将'1'转换为true，将'0'转换为false
        }

        int width = image.getWidth();
        int height = image.getHeight();
        int numPixels = width * height;

        boolean[] messageBits = keyHandleService.convertToBits(message);
        boolean[] keyBits = keyHandleService.convertToBits(key);

        // 将字符串长度二进制数组，拼到消息数组的前面
        boolean[] result = new boolean[boolArr.length + messageBits.length];
        System.arraycopy(boolArr, 0, result, 0, boolArr.length);
        System.arraycopy(messageBits, 0, result, boolArr.length, messageBits.length);

        int[] pixelIndices = keyHandleService.generateRandomIndices(numPixels, keyBits);

        int embeddedBitsCount = 0;
        for (int i = 0; i < numPixels && embeddedBitsCount < result.length; ) {
            int pixelIndex = pixelIndices[i];
            int x = pixelIndex % width;
            int y = pixelIndex / width;
            int rgb = image.getRGB(x, y);

            boolean messageBit = result[embeddedBitsCount];
            boolean keyBit = keyBits[embeddedBitsCount % keyBits.length];

            int modifiedRGB = keyHandleService.modifyPixel(rgb, messageBit, keyBit);
            image.setRGB(x, y, modifiedRGB);

            embeddedBitsCount++;
            i++;
        }

        String stegoImagePath = "src/main/resources/static/image/handleImg/output.bmp";
        try {
            ImageIO.write(image, "bmp", new File(stegoImagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("加密完成");
        Map<String,String> return_map = new HashMap<>();
        return_map.put("true","嵌入完成");
        return return_map;
    }

    @ResponseBody
    @RequestMapping(value = "/extractMessage")
    public String extractMessage(String key) {
        BufferedImage image = keyHandleService.readImage("src/main/resources/static/image/handleImg/output.bmp");
        int width = image.getWidth();
        int height = image.getHeight();
        int numPixels = width * height;

        boolean[] keyBits = keyHandleService.convertToBits(key);

        int[] pixelIndices = keyHandleService.generateRandomIndices(numPixels, keyBits);
        // 获取message的长度
        boolean[] extractedLengthBits = new boolean[8];
        int extractedLengthBitsCount = 0;
        for (int i = 0; i < numPixels && extractedLengthBitsCount < 8; i++) {
            int pixelIndex = pixelIndices[i];
            int x = pixelIndex % width;
            int y = pixelIndex / width;
            int rgb = image.getRGB(x, y);

            boolean keyBit = keyBits[extractedLengthBitsCount % keyBits.length];
            boolean extractedBit = keyHandleService.extractBit(rgb, keyBit);

            extractedLengthBits[extractedLengthBitsCount] = extractedBit;
            extractedLengthBitsCount++;
        }
        int length = 0; // 用于存储转换后的整数
        for (int i = 0; i < extractedLengthBits.length; i++) {
            length = (length << 1) | (extractedLengthBits[i] ? 1 : 0); // 将二进制数转换为整数
        }
        System.out.println("length"+length);

        boolean[] extractedBits = new boolean[length*8];
        int extractedBitsCount = 0;
        for (int i = 8; i < numPixels && extractedBitsCount < length*8; i++) {
            int pixelIndex = pixelIndices[i];
            int x = pixelIndex % width;
            int y = pixelIndex / width;
            int rgb = image.getRGB(x, y);

            boolean keyBit = keyBits[(extractedBitsCount+8) % keyBits.length];
            boolean extractedBit = keyHandleService.extractBit(rgb, keyBit);

            extractedBits[extractedBitsCount] = extractedBit;
            extractedBitsCount++;
        }

        return keyHandleService.convertBitsToMessage(extractedBits);
    }

}
