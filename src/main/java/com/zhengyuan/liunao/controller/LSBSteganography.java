package com.zhengyuan.liunao.controller;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class LSBSteganography {
    public static void main(String[] args) {
        embedMessage();
        String a = extractMessage();
        System.out.println("a:"+a);

    }

    public static BufferedImage embedMessage() {
        BufferedImage image = readImage("src/main/resources/static/image/originImg/test1.bmp");
        String message="isayas";
        String key = "secret";

        int width = image.getWidth();
        int height = image.getHeight();
        int numPixels = width * height;

        boolean[] messageBits = convertToBits(message);
        boolean[] keyBits = convertToBits(key);

        int[] pixelIndices = generateRandomIndices(numPixels, keyBits);

        int embeddedBitsCount = 0;
        for (int i = 0; i < numPixels && embeddedBitsCount < messageBits.length; ) {
            int pixelIndex = pixelIndices[i];
            int x = pixelIndex % width;
            int y = pixelIndex / width;
            int rgb = image.getRGB(x, y);

            boolean messageBit = messageBits[embeddedBitsCount];
            boolean keyBit = keyBits[embeddedBitsCount % keyBits.length];

            int modifiedRGB = modifyPixel(rgb, messageBit, keyBit);
            image.setRGB(x, y, modifiedRGB);

            embeddedBitsCount++;
            i++;
        }

        String stegoImagePath = "src/main/resources/static/image/handleImg/output.bmp";
        writeImage(image, stegoImagePath);

        System.out.println("加密完成");
        return image;
    }

    public static String extractMessage() {
        BufferedImage image = readImage("src/main/resources/static/image/handleImg/output.bmp");
        String key = "secret";
        int width = image.getWidth();
        int height = image.getHeight();
        int numPixels = width * height;

        boolean[] keyBits = convertToBits(key);

        int[] pixelIndices = generateRandomIndices(numPixels, keyBits);

        boolean[] extractedBits = new boolean[keyBits.length];
        int extractedBitsCount = 0;
        for (int i = 0; i < numPixels && extractedBitsCount < keyBits.length; i++) {
            int pixelIndex = pixelIndices[i];
            int x = pixelIndex % width;
            int y = pixelIndex / width;
            int rgb = image.getRGB(x, y);

            boolean keyBit = keyBits[extractedBitsCount % keyBits.length];
            boolean extractedBit = extractBit(rgb, keyBit);

            extractedBits[extractedBitsCount] = extractedBit;
            extractedBitsCount++;
        }

        return convertBitsToMessage(extractedBits);
    }

    public static boolean[] convertToBits(String message) {
        boolean[] messageBits = new boolean[message.length() * 8];
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            for (int j = 0; j < 8; j++) {
                messageBits[i * 8 + j] = ((c >> (7 - j)) & 1) == 1;
            }
        }
        return messageBits;
    }

    public static int[] generateRandomIndices(int numIndices, boolean[] keyBits) {
        Random random = new Random();
        random.setSeed(getKeySeed(keyBits));
        int[] indices = new int[numIndices];
        for (int i = 0; i < numIndices; i++) {
            indices[i] = i;
        }
        for (int i = numIndices - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = indices[i];
            indices[i] = indices[j];
            indices[j] = temp;
        }
        return indices;
    }

    public static long getKeySeed(boolean[] keyBits) {
        long seed = 0;
        for (int i = 0; i < keyBits.length; i++) {
            if (keyBits[i]) {
                seed |= (1L << i);
            }
        }
        return seed;
    }

    public static int modifyPixel(int rgb, boolean messageBit, boolean keyBit) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;

        if (keyBit == false) {
            red = modifyComponent(red, messageBit);
        } else {
            green = modifyComponent(green, messageBit);
        }

        return (red << 16) | (green << 8) | blue;
    }

    public static int modifyComponent(int component, boolean bit) {
        if (bit) {
            return (component | 1);
        } else {
            return (component & 0xFE);
        }
    }

    public static boolean extractBit(int rgb, boolean keyBit) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;

        if (keyBit == false) {
            return (red & 1) == 1;
        } else {
            return (green & 1) == 1;
        }
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
            ImageIO.write(image, "png", new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
