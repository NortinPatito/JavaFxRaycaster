package org.example.raycaster;

public class Camera {
    private float positionX = 100f;
    private float positionY = 100f;
    private float positionZ = 20f;

    private float directionX;
    private float directionY;

    float fov = 75;

    private float angle;


    public void Camera(){
        calculateDirection();
    }


    private void calculateDirection(){
        this.directionX = (float) Math.cos(angle);
        this.directionY = (float) Math.sin(angle);
    }


    // camera movements

    public void MoveCameraForward(float amount){
        this.positionX += directionX * amount;
        this.positionY += directionY * amount;

    }

    public void Turn(float amount){
        setAngle(angle + amount);
    }


    // ========== SETTERS ============

    public void setAngle(float inAngle){
        this.angle = MathHelper.getSafeAngle(inAngle);
        calculateDirection();
    }

    public void setPositionZ(float inZ){
        this.positionZ = inZ;
    }
    public void setPositionX(float inX){
        this.positionX = inX;
    }
    public void setPositionY(float inY){
        this.positionY = inY;
    }


    // ========== GETTERS ============
    public float getPositionX(){
        return this.positionX;
    }

    public float getPositionY(){
        return this.positionY;
    }

    public float getPositionZ(){
        return this.positionZ;
    }



    public float getAngle(){
        return this.angle;
    }

    public float getDirectionX(){
        return this.directionX;
    }

    public float getDirectionY(){
        return this.directionY;
    }
}
