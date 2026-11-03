package org.example.raycaster.Entities;

import org.example.raycaster.*;

import java.util.ArrayList;


public class Ball extends Entity{


    float velX = 0, velY = 0, velZ = 0;

    Camera player;
    Map map;


    // ==== ball config ========


    float ballRadius = 15f;

    float kickForce = 2f;


    // do once bools

    boolean bHitFloor = false;
    boolean bFirstKick = false;




    public Ball()
    {

    }

    public Ball(float inX, float inY, float inZ, Camera inPlayer, Map inMap)
    {
        setPositionX(inX);
        setPositionY(inY);
        setPositionZ(inZ);
        this.player = inPlayer;
        this.map = inMap;
    }

    @Override
    public void update(float delta){

        if(getPositionZ() > 0){
            velZ -= 9.8f * delta;
            bHitFloor = false;
        }else{
            if(!bHitFloor) {
                velZ = velZ * -0.8f;
                bHitFloor = true;
            }
        }

        float plDist = MathHelper.dist(getPositionX(),getPositionY(),player.getPositionX(),player.getPositionY());


        // player kicking logic
        if(plDist < ballRadius)
        {
            if(!bFirstKick)
            {
                velX = player.getDirectionX() * kickForce;
                velY = player.getDirectionY() * kickForce;
                velZ += 1;
                bFirstKick = true;
            }
        }else {
            bFirstKick = false;
        }




        velX *= 0.99f ;
        velY *= 0.99f;


        // wall bouncing
        float[] dir = MathHelper.normalize2D(velX,velY);
        float ang = (float) Math.atan2(dir[1],dir[0]);


        ArrayList<RaycastResult> raycast = map.castRay(this.getPositionX(),this.getPositionY(), ang );
        if(!raycast.isEmpty())
        {
            RaycastResult hit = raycast.getFirst();

            float velMag = MathHelper.magnitude(velX, velY);

            if (Math.abs(hit.getHitDist()) < ballRadius + velMag)
            {


                float d = velX * hit.getNormalX() + velY * hit.getNormalY();

                velX = velX - 2 * d * hit.getNormalX();
                velY = velY - 2 * d * hit.getNormalY();
            }

        }


        setPositionZ(getPositionZ() + (velZ ));
        setPositionX(getPositionX() + (velX ));
        setPositionY(getPositionY() + (velY ));
    }

    @Override
    public void render(Renderer renderer)
    {
        renderer.drawSprite(this.getPositionX(),this.getPositionY(),this.getPositionZ());
    }
}
