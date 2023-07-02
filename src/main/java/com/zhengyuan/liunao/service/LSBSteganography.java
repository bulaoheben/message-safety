package com.zhengyuan.liunao.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class LSBSteganography {
    // Embeds message into the image using LSB steganography with random intervals
    public static boolean embedMessage(String imagePath, String message, String key) {
        try {
            BufferedImage image = ImageIO.read(new File(imagePath));
            int width = image.getWidth();
            int height = image.getHeight();
            int secretLength = message.length();

            // Generate pseudo-random interval sequence using the key
            int[] intervals = generateIntervals(key, secretLength);

            int charIndex = 0;

            // Iterate through each pixel
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = image.getRGB(x, y);

                    // Extract RGB components
                    int red = (pixel >> 16) & 0xFF;
                    int green = (pixel >> 8) & 0xFF;
                    int blue = pixel & 0xFF;

                    // Embed the message into the LSB of the pixel components
                    if (charIndex < secretLength) {
                        char ch = message.charAt(charIndex);
                        int interval = intervals[charIndex];

                        if (interval > 0) {
                            red = embedBit(red, ch, interval);
                            green = embedBit(green, ch, interval);
                            blue = embedBit(blue, ch, interval);
                        }
                        charIndex++;
                    }

                    // Update the pixel with modified RGB components
                    int updatedPixel = (red << 16) | (green << 8) | blue;
                    image.setRGB(x, y, updatedPixel);
                }
            }

            // Save the modified image
            String outputImagePath = "src/main/resources/static/image/handleImg/output.bmp";
            ImageIO.write(image, "bmp", new File(outputImagePath));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Extracts the embedded message from the image using LSB steganography with random intervals
    public static String extractMessage(String imagePath, String key, int secretLength) {
        try {
            BufferedImage image = ImageIO.read(new File(imagePath));
            int width = image.getWidth();
            int height = image.getHeight();
            StringBuilder extractedMessage = new StringBuilder();

            // Generate pseudo-random interval sequence using the key
            int[] intervals = generateIntervals(key, secretLength);

            int charIndex = 0;

            // Iterate through each pixel
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = image.getRGB(x, y);

                    // Extract RGB components
                    int red = (pixel >> 16) & 0xFF;
                    int green = (pixel >> 8) & 0xFF;
                    int blue = pixel & 0xFF;

                    // Extract the LSB of the pixel components using the intervals
                    if (charIndex < secretLength) {
                        int interval = intervals[charIndex];
                        if (interval > 0) {
                            char ch = extractBit(red, interval);
                            extractedMessage.append(ch);
                            charIndex++;
                        }
                    }
                    if (charIndex < secretLength) {
                        int interval = intervals[charIndex];
                        if (interval > 0) {
                            char ch = extractBit(green, interval);
                            extractedMessage.append(ch);
                            charIndex++;
                        }
                    }
                    if (charIndex < secretLength) {
                        int interval = intervals[charIndex];
                        if (interval > 0) {
                            char ch = extractBit(blue, interval);
                            extractedMessage.append(ch);
                            charIndex++;
                        }
                    }
                }
            }

            return extractedMessage.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Embeds a bit into the given pixel component using the specified interval
    private static int embedBit(int component, char bit, int interval) {
        // Clear the LSB with a bit mask
        component &= ~(1 << 0);

        // Set the LSB to the bit value
        if (bit == '1') {
            component |= (1 << 0);
        }

        // Skip the next (interval-1) pixels
        int updatedComponent = component;
        for (int i = 0; i < interval - 1; i++) {
            updatedComponent += 1;
        }

        return updatedComponent;
    }

    // Extracts the LSB from the given pixel component using the specified interval
    private static char extractBit(int component, int interval) {
        // Extract the LSB
        int bit = (component >> 0) & 0x01;

        // Skip the next (interval-1) pixels
        int updatedComponent = component;
        for (int i = 0; i < interval - 1; i++) {
            updatedComponent += 1;
        }

        return (char) (bit + '0');
    }

    // Generates pseudo-random intervals using the key
    private static int[] generateIntervals(String key, int length) {
        int[] intervals = new int[length];
        int keyHash = Math.abs(key.hashCode());

        // Use seed based on the key hash to ensure consistent intervals per key
        long seed = (long) keyHash;

        // Generate random intervals using the seed
        java.util.Random random = new java.util.Random(seed);
        for (int i = 0; i < length; i++) {
            intervals[i] = random.nextInt(10) + 1;  // Random interval between 1 and 10
        }

        return intervals;
    }

    public static void main(String[] args) {
        String imagePath = "src/main/resources/static/image/originImg/test9.bmp";
        String outputImagePath = "src/main/resources/static/image/handleImg/output.bmp";
        String message = "This is a secret message!";
        String key = "mySecretKey123";

        // Embed the message into the image
        boolean success = embedMessage(imagePath, message, key);
        if (success) {
            System.out.println("Message embedded successfully.");
        } else {
            System.out.println("Failed to embed message.");
        }

        // Extract the message from the image
        int secretLength = message.length();
        String extractedMessage = extractMessage(outputImagePath, key, secretLength);
        if (extractedMessage != null) {
            System.out.println("Extracted message: " + extractedMessage);
        } else {
            System.out.println("Failed to extract message.");
        }
    }
}
