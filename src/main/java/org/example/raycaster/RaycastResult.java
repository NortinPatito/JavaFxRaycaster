package org.example.raycaster;

public class RaycastResult {
    private float hitX;
    private float hitY;
    private float hitDist;
    private float angle;
    boolean hitVerticle;
    private int hitTile;
    private boolean didHit;
    private float normalX;
    private float normalY;

    public RaycastResult(float inX, float inY, float inDist, float inAngle, boolean inHitVerticle, int inHitTile, boolean hit, float inNormX, float inNormY){
        this.hitX = inX;
        this.hitY = inY;
        this.hitDist = inDist;
        this.angle = inAngle;
        this.hitVerticle = inHitVerticle;
        this.hitTile = inHitTile;
        this.didHit = hit;
        this.normalX = inNormX;
        this.normalY = inNormY;
    }

    public float getHitDist(){
        return this.hitDist;
    }

    public float getRayAngle(){
        return this.angle;
    }

    public float getRayX(){
        return this.hitX;
    }

    public float getRayY(){
        return this.hitY;
    }

    public float getNormalX(){
        return this.normalX;
    }

    public float getNormalY(){
        return this.normalY;
    }

    public int getTileIndex(){
        return this.hitTile;
    }

    public boolean didHit(){
        return this.didHit;
    }

    public void setHitDist(float newDist){
        this.hitDist = newDist;
    }
}
