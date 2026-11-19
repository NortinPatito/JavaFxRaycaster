package org.example.raycaster.Entities;

import org.example.raycaster.*;

public class TurnSprite extends Entity {

    Camera player;

    float eAngle = 0f;
    float directionX = 0;
    float directionY = 0;

    float animTimer;

    public TurnSprite(float inX, float inY, float inZ, Camera inPlayer)
    {
        setPositionX(inX);
        setPositionY(inY);
        setPositionZ(inZ);
        this.player = inPlayer;
        eAngle += 0.01f;
        updateDirection();
    }


    private void updateDirection()
    {
        this.directionX = (float) Math.cos(eAngle);
        this.directionY = (float) Math.sin(eAngle);
    }


    @Override
    public void update(float delta){
        animTimer += delta;
        animTimer = animTimer % 1;

        float[] toDir = MathHelper.normalize2D( player.getPositionX() - this.getPositionX(),  player.getPositionY() - this.getPositionY());
        float targetAngle = (float) Math.atan2(toDir[1],toDir[0]);
        float allowence = MathHelper.toRadians(5);
        if ( eAngle < targetAngle - allowence || eAngle > targetAngle + allowence )
        {
            updateAngle(MathHelper.lerp(eAngle,targetAngle, delta * 2));
        }else {
            float moveX = toDir[0] * (35 * delta);
            float moveY = toDir[1] * (35 * delta);
            setPositionX(getPositionX() + moveX);
            setPositionY(getPositionY() + moveY);
        }

    }


    private void updateAngle(float in){
        this.eAngle = in;
        updateDirection();
    }


    @Override
    public void render(Renderer renderer)
    {



        float[] toDir = MathHelper.normalize2D( player.getPositionX() - this.getPositionX(),  player.getPositionY() - this.getPositionY());

        float cross = -( directionX * toDir[1] - directionY * toDir[0]);
        float dot = MathHelper.dot(toDir[0],toDir[1], directionX, directionY);

        float b = (float) Math.atan2(cross,dot);
        int turnIndex = (int) (((((b + (2 * MathHelper.PI)) + MathHelper.toRadians(22.5f)) % ( 2 * MathHelper.PI ))) / MathHelper.toRadians(45));

        int animIndex = (int) (animTimer * 4);

        renderer.drawSprite(this.getPositionX(),this.getPositionY(),this.getPositionZ(), turnIndex,animIndex,turnIndex + 1,animIndex + 1, 40, TextureHandler.testEnemy);


        renderer.drawSprite(this.getPositionX() + (directionX * 25),this.getPositionY() + (directionY * 25),this.getPositionZ(), 0,0,1,1, 5, TextureHandler.sprites);
    }
}
