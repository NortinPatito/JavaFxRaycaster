package org.example.raycaster;

public class TileData {
    private int textureIndex;
    private boolean thin, thinSide;
    boolean transparent;
    float wallHeight = 1;
    String name;

    public TileData(){

    }

    public void setName(String inName){
        this.name = inName;
    }

    public String getName(){
        return this.name;
    }

    public int getTextureIndex(){
        return this.textureIndex;
    }

    public float getWallHeight()
    {
        return this.wallHeight;
    }

    public void setTextureIndex(int in)
    {
        this.textureIndex = in;
    }

    public void setWallHeight(float newHeight){
        this.wallHeight = newHeight;
    }

}
