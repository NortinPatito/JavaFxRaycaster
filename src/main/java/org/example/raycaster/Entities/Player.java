package org.example.raycaster.Entities;

import javafx.scene.input.KeyCode;
import org.example.raycaster.Camera;
import org.example.raycaster.Level;
import org.example.raycaster.MathHelper;
import org.example.raycaster.RaycastResult;

import java.util.ArrayList;
import java.util.Map;

public class Player extends Entity{

    float velX;
    float velY;

    Camera playerCamera;

    Level level;

    // ============= Player Config ===========

    float playerRadius = 6f;

    // movement config
    float groundAccel = 20;
    float maxGroundSpeed = 120;
    float groundFriction = 6;

    // turn config
    float turnSpeed = 5f;

    int moveDirection = 0;

    public Player(Camera camera, Level inLevel){
        this.playerCamera = camera;
        this.level = inLevel;
    }


    @Override
    public void update(float delta) {



        // movement calculations

        accelerate(delta);


        friction(delta);


        // Collision Testing



        velocityCollision();




        // headbob
        float sn = (float) Math.abs( Math.sin(0f * 5));

        playerCamera.setPositionZ((sn * 1.5f) + 20);


        setPositionX(getPositionX() + (velX ));
        setPositionY(getPositionY() + (velY ));

        playerCamera.setPositionX(getPositionX());
        playerCamera.setPositionY(getPositionY());



    }


    private void velocityCollision(){
        float[] dir = MathHelper.normalize2D(velX,velY);
        float velAngle = (float) Math.atan2(dir[1],dir[0]);

        ArrayList<RaycastResult> result = level.castRay(getPositionX(),getPositionY(), velAngle);

        if(!result.isEmpty())
        {
            RaycastResult ray = result.getFirst();
            float speed = MathHelper.magnitude(velX,velY);
            if ( Math.abs(ray.getHitDist()) < playerRadius )
            {
                float d = velX * ray.getNormalX() + velY * ray.getNormalY();
                velX = velX - d * ray.getNormalX();
                velY = velY - d * ray.getNormalY();

                float penFactor = 1 ;

                //setPositionX(getPositionX() + (ray.getNormalX() * penFactor));
                //setPositionY(getPositionY() + (ray.getNormalY() * penFactor));

            }
        }
    }



    // player movement
    private void accelerate(float delta){

        float dirX = playerCamera.getDirectionX() * moveDirection;
        float dirY = playerCamera.getDirectionY() * moveDirection;

        if(MathHelper.magnitude(velX,velY) < maxGroundSpeed * delta)
        {
            velX += dirX * groundAccel * delta;
            velY += dirY * groundAccel * delta;
        }
    }

    private void friction(float delta){
        float speed = MathHelper.magnitude(velX,velY);
        if(speed > 0.001f)
        {


            float drop = speed * groundFriction * delta;

            float newSpeed = Math.max(0, speed - drop);

            velX *= newSpeed / speed;
            velY *= newSpeed / speed;
        }
    }


    // Inputs

    public void input(Map<KeyCode, Boolean> keys, float delta){
        if(keys.getOrDefault(KeyCode.W,false)) {
            moveDirection = 1;
        }else if(keys.getOrDefault(KeyCode.S,false)) {
            moveDirection = -1;
        }else{
            moveDirection = 0;
        }


        if(keys.getOrDefault(KeyCode.A,false)) {
            playerCamera.Turn(-turnSpeed * delta);
        }

        if(keys.getOrDefault(KeyCode.D,false)) {
            playerCamera.Turn(turnSpeed * delta);
        }
    }

}
