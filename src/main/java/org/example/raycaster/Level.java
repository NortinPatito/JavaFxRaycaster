package org.example.raycaster;

import java.util.ArrayList;

public class Level {

    // map sizing
    float mapCellSize = 100;

    // map definition
    int mapX = 12;
    int mapY = 12;
    int[] map = {
            1,1,1,1,1,1,1,1,1,1,1,1,
            1,0,0,1,0,0,0,0,0,0,0,1,
            1,0,0,1,0,0,0,0,0,0,0,1,
            1,0,0,1,1,1,1,3,3,0,0,1,
            1,0,0,0,0,0,0,0,0,0,0,1,
            1,0,0,0,0,0,0,0,0,0,0,1,
            9,0,0,0,0,2,0,0,0,0,0,9,
            9,0,0,0,0,2,0,0,4,4,0,9,
            9,0,0,0,0,2,0,0,0,0,0,9,
            9,0,0,0,0,0,0,0,0,3,3,1,
            9,0,0,0,0,0,0,0,0,3,3,1,
            1,1,1,1,1,1,1,1,1,1,1,1,
    };





    int[] ceilingMap = {
            1,1,1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,1,1,
            1,0,0,0,0,0,0,0,0,0,0,1,
            1,0,0,0,0,0,0,0,0,0,0,1,
            1,0,0,0,0,0,0,0,0,0,0,1,
            1,0,0,0,0,0,0,0,0,0,0,1,
            1,0,0,0,0,0,0,0,0,0,0,1,
            1,1,1,1,1,1,1,1,1,1,1,1,
    };







    public int[] getMap(){
        return this.map;
    }

    public int getTile(int index){
        if (index > 0 && index < map.length)
        {
            return map[index];
        }
        return 0;
    }

    public int getCeilingTile(int index){
        if (index > 0 && index < ceilingMap.length)
        {
            return ceilingMap[index];
        }
        return 0;
    }

    public float getMapCellSize(){
        return this.mapCellSize;
    }



    // dda implementation of casting a ray
    // more efficient algorithm than the first iteration i wrote
    // first one i wrote was poop
    // It checked x and y in different while loops and ran slow
    // ----- edit after fully implementing it ----------
    // yea this algorithm runs much better
    // should've used this from the start
    // ------- edit 2 --------
    // forgot to make the caster remember the last hit index
    // without doing this it will render inside faces of tiles
    // and that ruins transparency
    public ArrayList<RaycastResult> castRay(float inRayX, float inRayY, float inRayA) {
        float safeAngle = MathHelper.getSafeAngle(inRayA);
        float rayDirX = (float) Math.cos(safeAngle);
        float rayDirY = (float) Math.sin(safeAngle);

        float stepSizeX = (float) Math.sqrt(1 + (rayDirY / rayDirX) * (rayDirY / rayDirX)) * mapCellSize;
        float stepSizeY = (float) Math.sqrt(1 + (rayDirX / rayDirY) * (rayDirX / rayDirY)) * mapCellSize;

        float rayLengthX = 0;
        float rayLengthY = 0;

        int mapCheckX = (int) Math.floor((inRayX) / mapCellSize);
        int mapCheckY =  (int) Math.floor((inRayY) / mapCellSize);

        int stepX,stepY;

        int lastHitTile = -1;

        ArrayList<RaycastResult> output = new ArrayList<>();

        // starting condition checks
        // gets everything ready for iteration
        if ( rayDirX < 0)
        {
            stepX = -1;
            rayLengthX = (inRayX -( mapCheckX * mapCellSize));
        }else{
            stepX = 1;
            rayLengthX = ( (mapCheckX + 1 ) * mapCellSize) - inRayX;
        }

        if ( rayDirY < 0)
        {
            stepY = -1;
            rayLengthY = (inRayY -( mapCheckY * mapCellSize));
        }else{
            stepY = 1;
            rayLengthY = (( (mapCheckY + 1 ) * mapCellSize) - inRayY);
        }

        rayLengthX = rayLengthX / Math.abs(rayDirX);
        rayLengthY = rayLengthY / Math.abs( rayDirY);

        // Da loop
        boolean foundEnd = false;
        float maxDist = 1000; // Arbitrary value, just make it high
        float distance = 0;
        boolean side = false;
        // I use a limited render depth
        // basically after finding the first collision point
        // this is how many more it will look for
        // I limit this for performance
        // it can cause rendering some things to not show up if their behind multiple
        // walls or transparent objects but its worth for performance
        int maxRenderDepth = 4;

        int renderDepth = 0;

        float hitNormalX = 0;
        float hitNormalY = 0;

        // the whole raycasting thing
        while(!foundEnd && distance < maxDist && renderDepth < maxRenderDepth)
        {



            // do the walk through the map
            if(rayLengthX < rayLengthY)
            {
                mapCheckX += stepX;
                distance = rayLengthX;
                rayLengthX += stepSizeX;
                side = true;
                hitNormalX = stepX;
                hitNormalY = 0;
            }else
            {
                mapCheckY += stepY;
                distance = rayLengthY;
                rayLengthY += stepSizeY;
                side = false;
                hitNormalX = 0;
                hitNormalY = stepY;
            }

            // check if tile is solid
            if( mapCheckX >= 0 && mapCheckX < mapX && mapCheckY >= 0 && mapCheckY < mapY) {


                int tileIndex = mapCheckY * mapX + mapCheckX;
                if (map[tileIndex] >= 1 && tileIndex != lastHitTile) {
                    float distAdd = 0;
                    float dist = distance;


                    // test for thin walls
                    if ( map[tileIndex] == 9)
                    {
                        float xWall = ((float) mapCheckX + 0.5f) * mapCellSize;

                        float t = (xWall - inRayX) / rayDirX;

                        float tx = inRayX + rayDirX * t;
                        float ty = inRayY + rayDirY * t;


                         if( (int) (tx / mapCellSize) == mapCheckX && (int) (ty / mapCellSize ) == mapCheckY){
                             RaycastResult result = new RaycastResult(tx, ty,t, inRayA, true, mapCheckY * mapX + mapCheckX, true, hitNormalX, hitNormalY);
                             output.add(result);
                             continue;
                         }else{
                             continue;
                         }
                    }

                    float outDist = dist + distAdd;

                    float endX = inRayX + rayDirX * outDist;
                    float endY = inRayY + rayDirY * outDist;
                    RaycastResult result = new RaycastResult(endX, endY,outDist, inRayA, side, mapCheckY * mapX + mapCheckX, true, hitNormalX, hitNormalY);
                    output.add(result);
                    lastHitTile = tileIndex;
                    renderDepth++;
                }
            }
        }

        return output;
    }
}
