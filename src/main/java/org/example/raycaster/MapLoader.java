package org.example.raycaster;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapLoader {


    // ================ load from file ===========
    public static Level loadMapFromFile(String fileName){
        try {
            String content = Files.readString(Path.of(fileName + ".shmoe"));

            List<String> lines = content.lines().toList();
            System.out.println("Loaded File");

            int[] tileMap = new int[16 * 16];
            Map<Integer,TileData> tileData = new HashMap<>();

            for(String line : lines)
            {
                String[] chunk = line.split(":");

                if(chunk[0].startsWith("level"))
                {
                    parseTileArray(chunk[1], tileMap);
                }

                if(chunk[0].startsWith("tile"))
                {
                    parseTile(chunk[1], tileData);
                }


            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    //private void parse

    private static void parseTileArray(String dataChunk, int[] outArray)
    {
        String[] numbers = dataChunk.split(",");


        int i = 0;
        for ( String number : numbers)
        {
            if(number.equals(" ")) { continue;}

            if(i < outArray.length){
                try{
                outArray[i] = Integer.parseInt(number);
                System.out.println(outArray[i]);
                i++;
                } catch (NumberFormatException e) {
                    continue;
                }
            }
        }
    }


    private static void parseTile(String dataChunk, Map<Integer,TileData> dataMap)
    {
        String[] numbers = dataChunk.split(",");

        int index = 0;
        TileData data = new TileData();

        int i = 0;
        for ( String number : numbers)
        {
            if(number.equals(" ")) { continue;}

            try{
                int value = Integer.parseInt(number);
                switch (i){
                    case 0:
                        index = value;
                        break;
                    case 1:
                        data.setTextureIndex(value);
                        break;
                }
                i++;
            } catch (NumberFormatException e) {
                continue;
            }
        }

        dataMap.put(index,data);
    }




    // ========== Save to file ==============

    public static void saveMapToFile(String mapName, int[] tiles, Map<Integer, TileData> tileData)
    {
        String mapData = "level:";


        for(int tile : tiles)
        {
            mapData += String.format(",%d", tile);
        }



        for(int key : tileData.keySet())
        {
            TileData data = tileData.get(key);
            mapData += String.format("%ntile:%d,%d",key,data.getTextureIndex());
        }



        try {
            // Overwrites the file if it exists, or creates a new one
            Files.writeString(Path.of(mapName+ ".shmoe"), mapData);
            System.out.println("File saved successfully to: ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
