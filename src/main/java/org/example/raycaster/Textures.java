package org.example.raycaster;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Textures {
    public void loadTextures() {

        wallTextures = ResourceLoader.loadTexture("/wallTextures.ppm");
        treeSprite = ResourceLoader.loadTexture("/tree.ppm");
    }

    public static int[] wallTextures;

    public static int[] treeSprite;

    // remove this
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


    public static int[] getRGB(int pixelX, int pixelY){

        int index = ((pixelY) * 64 + pixelX) ;
        if (index < treeSprite.length && index >= 0){
            int value = treeSprite[index];
            int r = (value >> 16) & 255;
            int g = (value >> 8) & 255;
            int b = value & 255;
            return new int[]{r,g,b};
        }
        return new int[]{0,0,0};
    }

}



