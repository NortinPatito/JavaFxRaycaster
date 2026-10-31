package org.example.raycaster;

import javafx.animation.Animation;
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



        Timeline loop = new Timeline(new KeyFrame(Duration.millis(1000.0 / 120), e -> update(pixelWriter, pixelFormat, raycaster)));
        loop.setCycleCount(Animation.INDEFINITE);
        loop.play();

        Scene scene = new Scene(pane, WIDTH, HEIGHT);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    int t;
    private void update(PixelWriter pixelWriter, PixelFormat pixelFormat, Raycaster raycaster){
        raycaster.updateInput(keys);
        int[] pixels = raycaster.render();
        pixelWriter.setPixels(0, 0, WIDTH, HEIGHT, pixelFormat, pixels, 0, WIDTH);


    }

}