package org.example.raycaster;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Textures {
    public void loadTextures() {
        String resourcePath = "/walltextures.ppm";

        try (InputStream is = this.getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }
            ;

            try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
                 BufferedReader reader = new BufferedReader(isr)) {

                List<String> lines = reader.lines().toList();
                int lineCount = lines.size();
                wallTextures = new int[lineCount / 3];
                int i =0;
                int pixelIndex = 0;
                int r = 0, g =0, b= 0;

                for(String line : lines) {
                    int value = Integer.parseInt(line.toString());
                    switch(i){
                        case 0:
                            r = value;
                            i++;
                            break;
                        case 1:
                            g = value;
                            i++;
                            break;
                        case 2:
                            b = value;
                            // bit shift the colors to save on memory
                            int rgb = (r << 16) | (g << 8) | b;
                            wallTextures[pixelIndex] = rgb;
                            pixelIndex++;
                            System.out.println(pixelIndex);
                            i = 0;
                            break;
                    }
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int[] wallTextures;

    public static int[] getWallRGB(int pixelX, int pixelY, int textureIndex){

        int fPxy = (textureIndex / 8) * 32;
        int index = ((pixelY + fPxy) * 256 + pixelX) + ((textureIndex % 8) * 32) ;
        if (index < wallTextures.length && index >= 0){
            int value = wallTextures[index];
            int r = (value >> 16) & 255;
            int g = (value >> 8) & 255;
            int b = value & 255;
            return new int[]{r,g,b};
        }
        return new int[]{0,0,0};
    }


}



