package org.example.raycaster;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import javafx.scene.input.KeyCode;

public class Raycaster {




    Map<KeyCode, Boolean> keys;

    // ========== Config Variables ===========

    int WIDTH = 800;
    int HEIGHT = 512;



    // renderer class

    Renderer renderer;

    Camera playerCamera;


    public Raycaster(){

    }



    public void updateInput(Map<KeyCode, Boolean> inKeys){
        this.keys = inKeys;
    }






    // Logic Stuff

    private float dist(float ax, float ay, float bx, float by){
        return (float) Math.sqrt((bx - ax) * (bx - ax) + (by- ay) * (by - ay));
    }




    private void playerInput(){

        if(this.keys.getOrDefault(KeyCode.W,false)){
            playerCamera.MoveCameraForward(1f);
        }
        if(this.keys.getOrDefault(KeyCode.S,false)){
            playerCamera.MoveCameraForward(-1f);
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

    }


    // Rendering Call
    public int[] render(){
        // clear buffer
        renderer.clearPixelBuffer();



        //  input here
        playerInput();




        renderer.DrawFloor();
        renderer.DrawWalls();

        renderer.drawSprite(60,60,0);



        return renderer.getRenderedPixels();
    }
}
