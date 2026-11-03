package org.example.raycaster.Entities;

import org.example.raycaster.Renderer;

public class Entity {
    private boolean radius;

    private float positionX = 0,positionY = 0,positionZ = 0;

    public Entity()
    {

    }




    public void update(float delta)
    {}

    public void render(Renderer renderer){}

    // ========= Getters & Setters ===========

    public float getPositionX()
    {
        return this.positionX;
    }

    public float getPositionY()
    {
        return this.positionY;
    }

    public float getPositionZ()
    {
        return this.positionZ;
    }


    public void setPositionX(float in)
    {
        this.positionX = in;
    }

    public void setPositionY(float in)
    {
        this.positionY = in;
    }

    public void setPositionZ(float in)
    {
        this.positionZ = in;
    }
}
