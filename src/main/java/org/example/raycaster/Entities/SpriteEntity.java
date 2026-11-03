package org.example.raycaster.Entities;

import org.example.raycaster.Renderer;

public class SpriteEntity extends Entity{



    public SpriteEntity()
    {

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
        renderer.drawSprite(this.getPositionX(),this.getPositionY(),this.getPositionZ());
    }
}
