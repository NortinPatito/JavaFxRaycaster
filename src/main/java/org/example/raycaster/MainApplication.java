package org.example.raycaster;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.input.KeyCode;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

public class MainApplication extends Application {

    int WIDTH = 800;
    int HEIGHT = 512;

    private long lastTime = 0;


    Map<KeyCode, Boolean> keys = new HashMap<>();

    @Override
    public void start(Stage stage) throws IOException {
        StackPane pane = new StackPane();
        Canvas canvas = new Canvas(WIDTH,HEIGHT);
        canvas.setFocusTraversable(true);
        WritableImage writableImage = new WritableImage(WIDTH, HEIGHT);
        ImageView imageView = new ImageView(writableImage);
        imageView.setSmooth(false); // Keeps pixels sharp

        pane.getChildren().add(canvas);
        pane.getChildren().add(imageView);

        PixelWriter pixelWriter = writableImage.getPixelWriter();
        PixelFormat<IntBuffer> pixelFormat = PixelFormat.getIntArgbInstance();


        SkyTextures skyTextures = new SkyTextures();
        skyTextures.loadTextures();

        Textures textures = new Textures();
        textures.loadTextures();


        // setup input

        canvas.setOnKeyPressed(e -> this.keys.put(e.getCode(),true));
        canvas.setOnKeyReleased(e -> this.keys.put(e.getCode(),false));

        Raycaster raycaster = new  Raycaster();

        raycaster.begin();



        // MAIN GAME LOOP
        // i hate how javafx does it
        AnimationTimer loop = new AnimationTimer() {
            @Override
            public void handle(long currentTime) {
                if (lastTime == 0) {
                    lastTime = currentTime;
                    return;
                }

                double deltaTime = (currentTime - lastTime) / 1_000_000_000.0;
                lastTime = currentTime;



                // update input
                raycaster.updateInput(keys);

                //

                raycaster.update((float) deltaTime);


                // render everything
                int[] pixels = raycaster.render();
                pixelWriter.setPixels(0, 0, WIDTH, HEIGHT, pixelFormat, pixels, 0, WIDTH);

            }
        };

        loop.start();


        Scene scene = new Scene(pane, WIDTH, HEIGHT);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }


}