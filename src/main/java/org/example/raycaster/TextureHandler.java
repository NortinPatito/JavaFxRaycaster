package org.example.raycaster;

public class TextureHandler {
    public void loadTextures() {


        wallTexture = new Texture(ResourceLoader.loadTextureInt("/wallTextures.ppm"),8);
        treeSprite = ResourceLoader.loadTextureInt("/tree.ppm");
    }


    public static int[] treeSprite;

    public static Texture wallTexture;


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



