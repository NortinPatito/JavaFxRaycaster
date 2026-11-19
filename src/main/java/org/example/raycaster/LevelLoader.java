package org.example.raycaster;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevelLoader {





    // ================ load from file ===========
    public static Level loadMapFromFile(String fileName){
        try {
            String content = Files.readString(Path.of(fileName + ".shmoe"));

            List<String> lines = content.lines().toList();
            System.out.println("Loaded File");

            int[] tileMap = new int[32 * 32];
            int[] floorMap = new int[32 * 32];
            int[] ceilingMap = new int[32 * 32];
            Map<Integer,TileData> tileData = new HashMap<>();

            for(String line : lines)
            {
                String[] chunk = line.split(":");

                if(chunk[0].startsWith("level"))
                {
                    parseTileArray(chunk[1], tileMap);
                }

                if(chunk[0].startsWith("floor"))
                {
                    parseTileArray(chunk[1], floorMap);
                }

                if(chunk[0].startsWith("ceiling"))
                {
                    parseTileArray(chunk[1], ceilingMap);
                }

                if(chunk[0].startsWith("tile"))
                {
                    parseTile(chunk[1], tileData);
                }


            }

            return new Level(32, tileMap,floorMap,ceilingMap, tileData);

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


            if(i < outArray.length && isInt(number)){
                outArray[i] = Integer.parseInt(number);
                i++;
            }
        }
    }


    private static void parseTile(String dataChunk, Map<Integer,TileData> dataMap)
    {
        String[] numbers = dataChunk.split(",");

        int index = 0;
        TileData data = new TileData();

        int i = 0;
        for ( String value : numbers)
        {
            if(value.equals(" ")) { continue;}

            int intValue = 0;
            float floatValue = 0;

            if(isInt(value)) {
                intValue = Integer.parseInt(value);
            }
            if(MathHelper.IsFloat(value)){
                floatValue = Float.parseFloat(value);
            }

            switch (i){
                case 0:
                    data.setName(value);
                    break;
                case 1:
                    index = intValue;
                    break;
                case 2:
                    data.setTextureIndex(intValue);
                    break;
                case 3:
                    data.setWallHeight(floatValue);
                    break;
            }
            i++;
        }
        dataMap.put(index,data);
    }


    public static boolean isInt(String str){
        if ( str == null || str.isEmpty() )
        {
            return false;
        }

        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }



    // ========== Save to file ==============

    public static boolean saveMapToFile(String mapName, int[] tiles, int[] floors, int[] ceilings, Map<Integer, TileData> tileData)
    {
        String mapData = "level:";


        for(int tile : tiles)
        {
            mapData += String.format(",%d", tile);
        }

        mapData += String.format("%nfloor:");
        for(int floor : floors)
        {
            mapData += String.format(",%d", floor);
        }

        mapData += String.format("%nceiling:");
        for(int ceiling : ceilings)
        {
            mapData += String.format(",%d", ceiling);
        }


        for(int key : tileData.keySet())
        {
            TileData data = tileData.get(key);
            mapData += String.format("%ntile:%s,%d,%d,%.2f",data.getName(),key,data.getTextureIndex(),data.getWallHeight());
        }



        try {
            // Overwrites the file if it exists, or creates a new one
            Files.writeString(Path.of(mapName+ ".shmoe"), mapData);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
