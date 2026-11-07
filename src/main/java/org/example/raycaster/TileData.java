package org.example.raycaster;

public class TileData {
    private int textureIndex;
    private boolean thin, thinSide;
    boolean transparent;
    int wallHeight;
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

    public void setTextureIndex(int in)
    {
        this.textureIndex = in;
    }

}
