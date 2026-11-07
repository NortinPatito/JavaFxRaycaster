package org.example.raycaster.MapEditor;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.stage.Stage;
import org.example.raycaster.MapLoader;
import org.example.raycaster.TileData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class MapEditor extends Application {

    // level params


    Canvas levelDisplay;

    VBox tileList;

    VBox tileDataList;

    // Level Data

    int[] level;
    int mapWidth = 12;
    Map<Integer, TileData> tileData = new HashMap<>();

    // ==== editor params ====

    int selectedTile = -1;

    Color[] indexColors = new Color[]{ Color.RED, Color.BLUE, Color.GREEN, Color.PURPLE,Color.CYAN, Color.YELLOW, Color.BURLYWOOD, Color.AZURE, Color.PINK, Color.CORNSILK, Color.INDIGO, Color.GAINSBORO};


    // display config

    double gridThick = 1;

    @Override
    public void start(Stage stage) throws IOException {

        // setup data



        // create gui
        VBox content = new VBox();

        // ======== tool bar buttons =========
        Button save = new Button("save");
        Button load = new Button("load");


        Label mapNameLabel = new Label("Map Name: ");
        TextField mapName = new TextField("");

        save.setOnAction(e -> saveMapFile(mapName.getText()));

        HBox toolbar = new HBox(save,load, mapNameLabel, mapName);

        content.getChildren().add(toolbar);


        HBox mainEditor = new HBox();

        // ========= level display ===========

        levelDisplay = new Canvas(400,400);

        levelDisplay.setOnMouseDragged( e -> OnMapMouse(e));
        levelDisplay.setOnMousePressed(e -> OnMapMouse(e));


        // ========== Tile and ent Editor ===========

        VBox editor = new VBox();
        HBox.setHgrow(editor, Priority.ALWAYS);
        editor.setMaxWidth(125);
        Label tilesLabel = new Label("Tiles");
        ScrollPane scrollPane = new ScrollPane();
        tileList = new VBox();
        // make scrolling work
        scrollPane.setContent(tileList);
        scrollPane.setFitToHeight(true);
        scrollPane.setPannable(true);
        scrollPane.setMaxHeight(150);
        scrollPane.setMinHeight(150);
        scrollPane.setFitToWidth(false);


        // tile creation and deletion buttons
        HBox tileButtons = new HBox();
        Button newTileButton = new Button("New");
        newTileButton.setOnAction(e -> newTileData());
        Button deleteTileButton = new Button("Delete");
        deleteTileButton.setOnAction(e -> deleteTileData());
        tileButtons.getChildren().addAll(newTileButton,deleteTileButton);

        // tile data editing buttons

        Label tileDataLabel = new Label("--Tile Data--");

        tileDataList = new VBox();



        editor.getChildren().addAll(tilesLabel,scrollPane,tileButtons,tileDataLabel, tileDataList);



        mainEditor.getChildren().addAll(editor,levelDisplay);

        content.getChildren().add(mainEditor);




        Scene scene = new Scene(content, 600, 500);
        stage.setTitle("Raycaster Level Editor");
        stage.setScene(scene);
        stage.show();

        // setup data

        createNewLevel();

    }


    public static void main(String[] args) {
        launch(args);
    }



    // ==== Update GUI Stuff ========

    // this updates the level display
    private void drawLevel(){

        Canvas canvas = levelDisplay;
        GraphicsContext gc = levelDisplay.getGraphicsContext2D();


        double width = canvas.getWidth();
        double height = canvas.getHeight();

        gc.clearRect(0,0,width,height);
        gc.setFill(Color.BLACK);
        gc.fillRect(0,0,width, height);




        for(int j = 0; j < level.length; j++)
        {
            int x = j % mapWidth;
            int y = (int) j / mapWidth;
            int tileIndex = level[j];

            if ( tileIndex < 0) { continue; }

            TileData data = tileData.get(tileIndex);

            double cX = (double) x / mapWidth * levelDisplay.getWidth();
            double cY = (double) y / mapWidth * levelDisplay.getHeight();



            double sX = (width / mapWidth);
            double sY = (width / mapWidth);

            int colorIndex = Math.min(tileIndex, indexColors.length - 1);
            gc.setFill(indexColors[colorIndex]);
            gc.fillRect(cX ,cY , sX,sY);

        }

        // create background grid
        gc.setFill(Color.GRAY);
        for ( int i = 0; i < (int) mapWidth; i++)
        {
            double pos = ((double) i / mapWidth) * width;
            gc.fillRect(pos,0,gridThick,height);

            gc.fillRect(0,pos,width,gridThick);
        }

    }


    private void refreshTileButtons(){
        tileList.getChildren().clear();
        int i = 0;

        for (Integer key : tileData.keySet())
        {
            TileData data = tileData.get(key);
            Button tileButton = new Button(data.getName());
            tileButton.setOnAction(e -> selectTileData(key));
            tileList.getChildren().add(tileButton);
            i++;
        }
    }

    private void refreshTileDataList(){
        tileDataList.getChildren().clear();

        if(tileData.containsKey(selectedTile))
        {
            TileData data = tileData.get(selectedTile);
            Label nameLabel = new Label("tile name: ");
            TextField tileNameField = new TextField(data.getName());
            tileNameField.setOnAction(e -> updateSelectTileName(tileNameField.getText()));


            Label textureLabel = new Label("Texture Index: ");
            TextField textureIndexField = new TextField(String.format("%d", data.getTextureIndex()));
            textureIndexField.setOnAction(e -> updateSelectTexture(Integer.parseInt(textureIndexField.getText())));


            tileDataList.getChildren().addAll(nameLabel,tileNameField, textureLabel,textureIndexField);
        }
    }

    // ====== input logic ============



    private void OnMapMouse(MouseEvent event){
        int tX = (int) ((event.getX() / levelDisplay.getWidth()) * mapWidth);
        int tY = (int) ((event.getY() / levelDisplay.getHeight()) * mapWidth);

        if (event.getButton() == MouseButton.PRIMARY ){
            // on clicking and moving

            setTile(tX,tY, selectedTile);

        }

        if (event.getButton() == MouseButton.SECONDARY ){
            // on clicking and moving

            setTile(tX,tY, -1);

        }


    }




    // ======== Actual Level Editing Logic =============

    private void createNewLevel(){
        level = new int[mapWidth * mapWidth];

        Arrays.fill(level, -1);


        drawLevel();
    }



    private void setTile(int tileX, int tileY, int tileType){
        if(tileX < 0 || tileX >= mapWidth || tileY < 0 || tileY >= mapWidth) {return;}
        int index = tileY * mapWidth + tileX;
        if(index >= 0 && index < level.length)
        {
            level[index] = tileType;

        }
        drawLevel();
    }


    private void newTileData(){
        TileData tile = new TileData();
        int tileIndex = 0;
        boolean foundIndex = false;
        while(!foundIndex){
            if(tileData.containsKey(tileIndex)){
                tileIndex++;
                continue;
            }
            foundIndex = true;
        }
        tile.setName(String.format("New Tile %d", tileIndex));
        tileData.put(tileIndex, tile);
        refreshTileButtons();
        selectTileData(tileIndex);
    }

    private void deleteTileData(){
        if ( tileData.containsKey(selectedTile)) {
            tileData.remove(selectedTile);
            refreshTileButtons();
            refreshTileDataList();
        }
    }

    private void selectTileData(Integer key){
        if ( tileData.containsKey(key)) {
            selectedTile = key;
            refreshTileDataList();
        }
    }

    // updating tile data vars

    private void updateSelectTileName(String newName){
        System.out.println("Name updated: " + newName);
        if ( tileData.containsKey(selectedTile)) {
            TileData data = tileData.get(selectedTile);
            data.setName(newName);
            refreshTileButtons();
        }
    }



    private void updateSelectTexture(int newId){
        if ( tileData.containsKey(selectedTile)) {
            tileData.get(selectedTile).setTextureIndex(newId);
        }
    }




    // ============= Save and load ============
    private void saveMapFile(String name){
        MapLoader.saveMapToFile(name,level, tileData);
        System.out.println("Saved");
    }



}
