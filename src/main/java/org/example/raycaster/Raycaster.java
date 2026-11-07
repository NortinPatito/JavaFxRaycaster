package org.example.raycaster;

import java.util.Map;
import java.util.ArrayList;

import javafx.scene.input.KeyCode;
import org.example.raycaster.Entities.Ball;
import org.example.raycaster.Entities.Entity;
import org.example.raycaster.Entities.Player;

public class Raycaster {




    Map<KeyCode, Boolean> keys;

    // ========== Config Variables ===========

    int WIDTH = 800;
    int HEIGHT = 512;


    ArrayList<Entity> entities;

    // renderer class

    Renderer renderer;

    Camera playerCamera;

    Player player;

    float bobber = 0;


    public Raycaster(){

    }



    public void updateInput(Map<KeyCode, Boolean> inKeys){
        this.keys = inKeys;
    }






    // Logic Stuff




    private void playerInput(){

    }

    public void begin(){
        // cannot call any rendering logic here
        playerCamera = new Camera();
        renderer = new Renderer(playerCamera);
        entities = new ArrayList<>();
        player = new Player(playerCamera, renderer.level);

        entities.add(new Ball(200,200,40, playerCamera, renderer.level));
        entities.add(player);

        MapLoader.loadMapFromFile("test");

    }

    // game update loop

    public void update(float delta){
        //  input here
        playerInput();

        player.input(keys, delta);




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
