package com.zhengyuan.liunao.service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.zhengyuan.liunao.service.LSBEmbedding.convertMessageToBits;
import static com.zhengyuan.liunao.service.LSBEmbedding.readImage;

public class MessageExtractor {


    public static void main(String[] args) {
        String coverImagePath = "src/main/resources/static/image/handleImg/output.bmp";
        String key = "secretKey";

        BufferedImage coverImage = readImage(coverImagePath);




        String text = extractMessage(coverImage, key);;

        System.out.println(text);
    }

    public static BufferedImage readImage(String imagePath) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }


    public static String extractMessage(BufferedImage image, String key) {
        int width = image.getWidth();
        int height = image.getHeight();
        int numPixels = width * height;

        boolean[] keyBits = convertKeyToBits(key);
        boolean[] extractedBits = new boolean[numPixels];

        int[] pixelIndices = new int[numPixels];
        for (int i = 0; i < numPixels; i++) {
            pixelIndices[i] = i;
        }

        // Fisher-Yates shuffle
        for (int i = numPixels - 1; i > 0; i--) {
            int j = (int) (Math.random() * (i + 1));
            int temp = pixelIndices[i];
            pixelIndices[i] = pixelIndices[j];
            pixelIndices[j] = temp;
        }

        int extractedBitsCount = 0;
        for (int i = 0; i < numPixels && extractedBitsCount < keyBits.length; i++) {
            int pixelIndex = pixelIndices[i];
            int x = pixelIndex % width;
            int y = pixelIndex / width;
            int rgb = image.getRGB(x, y);
            boolean extractedBit = extractBit(rgb, keyBits[extractedBitsCount % keyBits.length]);
            extractedBits[extractedBitsCount] = extractedBit;
            extractedBitsCount++;
        }

        return convertBitsToMessage(extractedBits);
    }

    public static boolean[] convertKeyToBits(String key) {
        boolean[] keyBits = new boolean[key.length() * 8];
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            for (int j = 0; j < 8; j++) {
                keyBits[i * 8 + j] = ((c >> (7 - j)) & 1) == 1;
            }
        }
        return keyBits;
    }

    public static boolean extractBit(int rgb, boolean keyBit) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;

        boolean extractedBit;
        if (keyBit == false) {
            extractedBit = (red & 1) == 1;
        } else {
            extractedBit = (green & 1) == 1;
        }

        return extractedBit;
    }

    public static String convertBitsToMessage(boolean[] bits) {
        StringBuilder messageBuilder = new StringBuilder();
        for (int i = 0; i < bits.length; i += 8) {
            int asciiValue = 0;
            for (int j = 0; j < 8; j++) {
                if (bits[i + j]) {
                    asciiValue |= (1 << (7 - j));
                }
            }
            messageBuilder.append((char) asciiValue);
        }
        return messageBuilder.toString();
    }
}