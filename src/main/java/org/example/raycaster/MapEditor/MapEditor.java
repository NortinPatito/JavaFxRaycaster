package org.example.raycaster.MapEditor;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.raycaster.Level;
import org.example.raycaster.LevelLoader;
import org.example.raycaster.TileData;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class MapEditor extends Application {

    // level params


    Canvas levelDisplay;

    VBox tileList;

    VBox tileDataList;

    HBox editModeBox;

    // Level Data

    int[] level;
    int[] levelFloors;
    int[] levelCeiling;
    int mapWidth = 12;
    Map<Integer, TileData> tileData = new HashMap<>();


    // ==== editor params ====

    int selectedTile = -1;

    int editMode = 0;

    int selectedExtraIndex = 0;

    boolean showWallGhost = true;

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
        load.setOnAction(e -> loadMapFile(mapName.getText()));

        HBox toolbar = new HBox(save,load, mapNameLabel, mapName);



        HBox mainEditor = new HBox();

        // ========= level display ===========

        levelDisplay = new Canvas(400,400);

        levelDisplay.setOnMouseDragged( e -> OnMapMouse(e));
        levelDisplay.setOnMousePressed(e -> OnMapMouse(e));


        // ========== Tile Editor ===========

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
        Label tileDataLabelExtra = new Label("* must hit enter");

        tileDataList = new VBox();



        editor.getChildren().addAll(tilesLabel,scrollPane,tileButtons,tileDataLabel, tileDataLabelExtra, tileDataList);
        mainEditor.getChildren().addAll(editor,levelDisplay);

        // ========== Toolbar for ceiling and floor editing ============

        editModeBox = new HBox();

        refreshEditModeDisplay();

        CheckBox wallGhostCheck = new CheckBox("Show Wall Ghost");

        Label textureLabelCeil = new Label("Texture Index: ");
        TextField ceilingTextureField = new TextField("0");
        ceilingTextureField.setOnAction(e -> onFloorIndexChanged(ceilingTextureField));
        HBox textBox = new HBox(textureLabelCeil,ceilingTextureField);


        // add the finale ui elements
        content.getChildren().addAll(toolbar,mainEditor, editModeBox,textBox, wallGhostCheck);




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

            TileData data = tileData.get(tileIndex);

            double cX = (double) x / mapWidth * levelDisplay.getWidth();
            double cY = (double) y / mapWidth * levelDisplay.getHeight();
            double sX = (width / mapWidth);
            double sY = (width / mapWidth);

            int colorIndex = Math.min(tileIndex, indexColors.length - 1);
            if ( tileIndex >= 0) {
                Color oldColor = Color.hsb(colorIndex * 137.508, 0.4, 0.95);
                if (editMode == 0) {
                    gc.setFill(oldColor);
                    gc.fillRect(cX, cY, sX, sY);
                } else if (showWallGhost) {
                    Color newColor = new Color(oldColor.getRed(), oldColor.getGreen(), oldColor.getBlue(), 0.25);
                    gc.setFill(newColor);
                    gc.fillRect(cX, cY, sX, sY);
                }
            }


            // draw other maps for edit mode
            switch (editMode)
            {
                case 1:
                    int floorindex = Math.min(levelFloors[j], indexColors.length - 1);
                    if ( floorindex >= 0) {
                        gc.setFill(Color.hsb(floorindex * 137.508, 0.4, 0.95));
                        gc.fillRect(cX, cY, sX, sY);
                        break;
                    }
                case 2:
                    int ceilingIndex = Math.min(levelCeiling[j], indexColors.length - 1);
                    if ( ceilingIndex >= 0) {
                        gc.setFill(Color.hsb(ceilingIndex * 137.508, 0.4, 0.95));
                        gc.fillRect(cX, cY, sX, sY);
                        break;
                    }
            }

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


    private void refreshEditModeDisplay(){
        editModeBox.getChildren().clear();

        CheckBox wallModeButton = new CheckBox("Edit Walls");
        CheckBox floorModeButton = new CheckBox("Edit Floors");
        CheckBox ceilingModeButton = new CheckBox("Edit Ceilings");
        wallModeButton.setOnAction(e -> editModeChecked(wallModeButton, 0));
        floorModeButton.setOnAction(e -> editModeChecked(floorModeButton, 1));
        ceilingModeButton.setOnAction(e -> editModeChecked(ceilingModeButton, 2));

        switch (editMode)
        {
            case 0:
                wallModeButton.setSelected(true);
                break;
            case 1:
                floorModeButton.setSelected(true);
                break;
            case 2:
                ceilingModeButton.setSelected(true);
                break;
        }

        editModeBox.getChildren().addAll(wallModeButton,floorModeButton,ceilingModeButton);

    }



    // ====== input logic ============



    private void OnMapMouse(MouseEvent event){
        int tX = (int) ((event.getX() / levelDisplay.getWidth()) * mapWidth);
        int tY = (int) ((event.getY() / levelDisplay.getHeight()) * mapWidth);

        if (event.getButton() == MouseButton.PRIMARY ){
            // on clicking and moving


            if(editMode == 0) {
                setTile(tX, tY, selectedTile);
            }else {
                setTile(tX, tY, selectedExtraIndex);
            }

        }

        if (event.getButton() == MouseButton.SECONDARY ){
            // on clicking and moving

            setTile(tX,tY, -1);

        }


    }

    private void editModeChecked(CheckBox box, int newMode){
        if (box.isSelected())
        {
            updateEditMode(newMode);
        }else {
            refreshEditModeDisplay();
        }
    }

    private void onFloorIndexChanged(TextField field){
        String value = field.getText();
        if ( LevelLoader.isInt(value))
        {
            selectedExtraIndex = Integer.parseInt(value);
        }else{
            field.setText(String.format("%d", selectedExtraIndex));
        }
    }




    // ======== Actual Level Editing Logic =============

    private void createNewLevel(){
        level = new int[mapWidth * mapWidth];
        levelFloors = new int[mapWidth * mapWidth];
        levelCeiling = new int[mapWidth * mapWidth];

        Arrays.fill(level, -1);
        Arrays.fill(levelFloors, 0);
        Arrays.fill(levelCeiling, -1);


        drawLevel();
    }



    private void setTile(int tileX, int tileY, int tileType){
        if(tileX < 0 || tileX >= mapWidth || tileY < 0 || tileY >= mapWidth) {return;}
        int index = tileY * mapWidth + tileX;
        if(index >= 0 && index < level.length)
        {
            switch (editMode)
            {
                case 0:
                    level[index] = tileType;
                    break;
                case 1:
                    levelFloors[index] = Math.max(tileType,0 );
                    break;
                case 2:
                    levelCeiling[index] = tileType;
                    break;
            }

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



    private void updateEditMode(int newMode)
    {
        editMode = newMode;
        refreshEditModeDisplay();
        drawLevel();

    }


    // ============= Save and load ============
    private void saveMapFile(String name){

        boolean didSave = LevelLoader.saveMapToFile(name,level, levelFloors,levelCeiling, tileData);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("File Saving:");
        if(didSave) {
            alert.setHeaderText("File Saved Successfully");
            alert.setContentText("your file was saved");
        }else{
            alert.setHeaderText("There was an error saving your file");
            alert.setContentText("please try again");
        }
        alert.showAndWait();

    }

    private void loadMapFile(String name){
        Level loadedLevel = LevelLoader.loadMapFromFile(name);
        level = loadedLevel.getMap();
        levelFloors = loadedLevel.getMapFloors();
        levelCeiling = loadedLevel.getMapCeilings();
        tileData = loadedLevel.getTiles();


        drawLevel();
        refreshTileButtons();

    }




}
