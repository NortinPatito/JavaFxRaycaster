package org.example.raycaster;

public class Texture {
    int[] textureData;
    int size = 256;
    int sections = 1;
    int sectionRes;


    public Texture(int[] inData, int inSections)
    {
        this.textureData = inData;
        this.size =( int) Math.sqrt(textureData.length);
        this.sections = inSections;
        this.sectionRes = size / sections;

    }


    public int[] getSectionRGB(float u, float v, int textureIndex)
    {
        int pixelX = (int) (u * sectionRes);
        int pixelY = (int) (v * sectionRes);

        int fPxy = (textureIndex / sections) * sectionRes;
        int index = ((pixelY + fPxy) * size + pixelX) + ((textureIndex % sections) * sectionRes);
        if (index < textureData.length && index >= 0){
            int value = textureData[index];
            int r = (value >> 16) & 255;
            int g = (value >> 8) & 255;
            int b = value & 255;
            return new int[]{r,g,b};
        }
        return new int[]{0,0,0};
    }
}
