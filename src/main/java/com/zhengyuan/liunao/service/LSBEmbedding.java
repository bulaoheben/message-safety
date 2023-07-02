package com.zhengyuan.liunao.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

public class LSBEmbedding {
    public static void main(String[] args) {
        String coverImagePath = "src/main/resources/static/image/originImg/test9.bmp";
        String secretMessage = "This is a secret message";
        String key = "secretKey";

        BufferedImage coverImage = readImage(coverImagePath);
        int[] messageBits = convertMessageToBits(secretMessage);
        int[] keyBits = convertKeyToBits(key);

        embedMessage(coverImage, messageBits, keyBits);

        String stegoImagePath = "src/main/resources/static/image/handleImg/output.bmp";
        writeImage(coverImage, stegoImagePath);
        System.out.println("Message embedded successfully!");
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

    public static void writeImage(BufferedImage image, String imagePath) {
        try {
            ImageIO.write(image, "bmp", new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int[] convertMessageToBits(String message) {
        int[] bits = new int[message.length() * 8];
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            for (int j = 0; j < 8; j++) {
                bits[i * 8 + j] = (c >> (7 - j)) & 1;
            }
        }
        return bits;
    }

    public static int[] convertKeyToBits(String key) {
        int[] bits = new int[key.length() * 8];
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            for (int j = 0; j < 8; j++) {
                bits[i * 8 + j] = (c >> (7 - j)) & 1;
            }
        }
        return bits;
    }

    public static void embedMessage(BufferedImage image, int[] messageBits, int[] keyBits) {
        int width = image.getWidth();
        int height = image.getHeight();
        int numPixels = width * height;
        int numBitsToEmbed = messageBits.length;

        Random random = new Random(keyBits.length);
        int[] pixelIndices = new int[numPixels];
        for (int i = 0; i < numPixels; i++) {
            pixelIndices[i] = i;
        }
        for (int i = numPixels - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = pixelIndices[i];
            pixelIndices[i] = pixelIndices[j];
            pixelIndices[j] = temp;
        }

        int embeddedBits = 0;
        for (int i = 0; i < numPixels && embeddedBits < numBitsToEmbed; i++) {
            int pixelIndex = pixelIndices[i];
            int x = pixelIndex % width;
            int y = pixelIndex / width;
            int rgb = image.getRGB(x, y);
            int modifiedRgb = embedBit(rgb, messageBits[embeddedBits], keyBits[embeddedBits % keyBits.length]);
            image.setRGB(x, y, modifiedRgb);
            embeddedBits++;
        }
    }

    public static int embedBit(int rgb, int bit, int keyBit) {
        int alpha = (rgb >> 24) & 0xFF;
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;

        if (keyBit == 0) {
            red = (red & 0xFE) | bit;
        } else {
            green = (green & 0xFE) | bit;
        }

        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
}
