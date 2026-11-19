package org.example.raycaster.Entities;

import org.example.raycaster.Renderer;
import org.example.raycaster.TextureHandler;

public class SpriteEntity extends Entity{

    private int tX,tY,etX,etY;

    public SpriteEntity(int inSTX, int inSTY, int inETX, int inETY)
    {
        this.tX = inSTX;
        this.tY = inSTY;
        this.etX = inETX;
        this.etY = inETY;
    }

    public SpriteEntity(float inX, float inY, float inZ)
    {
        setPositionX(inX);
        setPositionY(inY);
        setPositionZ(inZ);
    }

    @Override
    public void render(Renderer renderer)
    {


        renderer.drawSprite(this.getPositionX(),this.getPositionY(),this.getPositionZ(), 0,0,1,1, 15, TextureHandler.sprites);
    }
}
