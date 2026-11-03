package org.example.raycaster;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import javafx.scene.input.KeyCode;
import org.example.raycaster.Entities.Ball;
import org.example.raycaster.Entities.Entity;
import org.example.raycaster.Entities.SpriteEntity;

public class Raycaster {




    Map<KeyCode, Boolean> keys;

    // ========== Config Variables ===========

    int WIDTH = 800;
    int HEIGHT = 512;


    ArrayList<Entity> entities;

    // renderer class

    Renderer renderer;

    Camera playerCamera;

    float bobber = 0;


    public Raycaster(){

    }



    public void updateInput(Map<KeyCode, Boolean> inKeys){
        this.keys = inKeys;
    }






    // Logic Stuff




    private void playerInput(){

        if(this.keys.getOrDefault(KeyCode.W,false)){
            playerCamera.MoveCameraForward(1f);
            bobber += 0.01f;
        }
        if(this.keys.getOrDefault(KeyCode.S,false)){
            playerCamera.MoveCameraForward(-1f);
            bobber -= 0.01f;
        }
        if(this.keys.getOrDefault(KeyCode.A,false)){
            //turnPlayer(-0.03f);
            playerCamera.Turn(-0.03f);
        }
        if(this.keys.getOrDefault(KeyCode.D,false)){
            //turnPlayer(0.03f);
            playerCamera.Turn(0.03f);
        }
    }

    public void begin(){
        // cannot call any rendering logic here
        playerCamera = new Camera();
        renderer = new Renderer(playerCamera);
        entities = new ArrayList<>();

        entities.add(new Ball(200,200,40, playerCamera, renderer.map));

    }

    // game update loop

    public void update(float delta){
        //  input here
        playerInput();



        // headbob

        float sn = (float) Math.abs( Math.sin(bobber * 5));

        playerCamera.setPositionZ((sn * 1.5f) + 20);


        for (Entity e : entities)
        {
            e.update(delta);
        }
    }






    // Rendering Call
    public int[] render(){
        // clear buffer
        renderer.clearPixelBuffer();







        // Render Everything
        renderer.DrawFloor();
        renderer.DrawWalls();


        for (Entity e : entities)
        {
            e.render(renderer);
        }



        return renderer.getRenderedPixels();
    }
}
