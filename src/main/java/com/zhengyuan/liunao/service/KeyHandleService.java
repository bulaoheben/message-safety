package com.zhengyuan.liunao.service;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

@Service
public class KeyHandleService {

    public boolean[] convertToBits(String message) {
        boolean[] messageBits = new boolean[message.length() * 8];
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            for (int j = 0; j < 8; j++) {
                messageBits[i * 8 + j] = ((c >> (7 - j)) & 1) == 1;
            }
        }
        return messageBits;
    }

    public int[] generateRandomIndices(int numIndices, boolean[] keyBits) {
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

    public long getKeySeed(boolean[] keyBits) {
        long seed = 0;
        for (int i = 0; i < keyBits.length; i++) {
            if (keyBits[i]) {
                seed |= (1L << i);
            }
        }
        return seed;
    }

    public int modifyPixel(int rgb, boolean messageBit, boolean keyBit) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;

        if (keyBit == false) {
            if(messageBit){
                red = red | 1;
            }else{
                red = red & 0xFE;
            }
        } else {
            if(messageBit){
                green = green | 1;
            }else{
                green = green & 0xFE;
            }
        }
        return (red << 16) | (green << 8) | blue;
    }

    public boolean extractBit(int rgb, boolean keyBit) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;

        if (keyBit == false) {
            return (red & 1) == 1;
        } else {
            return (green & 1) == 1;
        }
    }

    public String convertBitsToMessage(boolean[] bits) {
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

    public BufferedImage readImage(String imagePath) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}
