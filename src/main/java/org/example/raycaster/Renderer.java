package org.example.raycaster;

import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.Arrays;

public class Renderer {



    int WIDTH = 800;
    int HEIGHT = 512;




    // =========== RENDERING CONFIG ============

    int rayCount = 250;
    float fov = 75;

    int floorRes = 2; // this is how big a floors pixel size is



    // If you change these some 𝓬𝓻𝓪𝔃𝔂 stuff will happen
    float PI = (float ) 3.1415929;
    float PI2 = PI / 2;
    float PI3 = 3 * PI/ 2;
    float DEGREE = 0.0174533f;

    // predefined map variables

    int mapX = 12;
    int mapY = 12;
    int mapCellSize = 75;

    int[] map = {
            1,1,1,1,1,1,1,1,1,1,1,1,
            1,0,0,1,0,0,0,0,0,0,0,1,
            1,0,0,1,0,0,0,0,0,0,0,1,
            1,0,0,1,1,1,1,0,0,0,0,1,
            1,0,0,0,0,0,0,0,0,0,0,1,
            1,0,0,0,0,0,0,0,0,0,0,1,
            1,0,0,0,0,0,0,0,0,0,0,1,
            1,0,0,0,0,0,0,0,0,0,0,1,
            1,0,0,0,0,2,0,0,0,0,0,1,
            1,0,0,0,0,0,0,0,0,0,0,1,
            1,0,0,0,0,0,0,0,0,0,0,1,
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


    // create a depth map for the screen
    float[] depth = new float[rayCount];

    boolean[] ceilingMask;



    // renderer output
    int[] pixels;

    Camera activeCamera;



    int wallHeight = (int) (50);

    int floorHeight = (int) (0);




    float d =  (WIDTH / 2f) / (float) Math.tan( DEGREE *  fov / 2);

    // Main Constructor for renderer

    public Renderer(Camera inCamera){
        this.activeCamera = inCamera;
        pixels = new int[WIDTH * HEIGHT];
        ceilingMask = new boolean[WIDTH / 2 * HEIGHT / 2];
    }


    // Render 2d Map view

    private void draw2dMap(){




        for(int y=0; y < mapY; y++){
            for( int x=0; x < mapX; x++){
                int r,g,b;
                if( map[y * mapX + x] == 1) {
                    r = 255;
                    g = 255;
                    b = 0;
                }else {
                    r = 0;
                    g = 0;
                    b = 0;
                }

                setSquare(x * mapCellSize ,y * mapCellSize, mapCellSize, mapCellSize,r,g,b);

                //setPixel(x,y,r,g,b);
            }
        }


    }


    private void draw2dPlayer(){
        int squareSize = mapCellSize;
        int drawX = (int) (activeCamera.getPositionX());
        int drawY = (int) (activeCamera.getPositionY());
        int playerSize = mapCellSize / 2;
        setSquare(drawX - playerSize / 2 ,drawY - playerSize / 2, playerSize,playerSize,0,255,0);




        int linesize = 55;
        setLine(drawX, drawY, drawX + (int ) ( activeCamera.getDirectionX() * linesize), drawY + (int) ( activeCamera.getDirectionY() * linesize), 0,255,0);

        castRay(activeCamera.getPositionX(),activeCamera.getPositionY(),activeCamera.getAngle());

        //System.out.printf("Player Pos: X:%f Y:%f%n", plx,ply);
    }





    // Logic Stuff

    private float dist(float ax, float ay, float bx, float by){
        return (float) Math.sqrt((bx - ax) * (bx - ax) + (by- ay) * (by - ay));
    }


    // dda implementation of casting a ray
    // more efficient algorithm than the first iteration i wrote
    // first one i wrote was poop
    // It checked x and y in different while loops and ran slow
    // ----- edit after fully implementing it ----------
    // yea this algorithm runs much better
    // should've used this from the start
    private ArrayList<RaycastResult> castRay(float inRayX, float inRayY, float inRayA) {
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
        // basically this controls how many more tiles it will check after finding the collision
        // this could cause something like lets say a shorter wall behind a transparent fence to not show
        //  the wall all the way at the end, but its good for performance
        int maxRenderDepth = 3;

        int renderDepth = 0;

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
            }else
            {
                mapCheckY += stepY;
                distance = rayLengthY;
                rayLengthY += stepSizeY;
                side = false;
            }

            // check if tile is solid
            if( mapCheckX >= 0 && mapCheckX < mapX && mapCheckY >= 0 && mapCheckY < mapY) {
                if (map[mapCheckY * mapX + mapCheckX] >= 1) {
                    float endX = inRayX + rayDirX * distance;
                    float endY = inRayY + rayDirY * distance;
                    RaycastResult result = new RaycastResult(endX, endY,distance, inRayA, side, mapCheckY * mapX + mapCheckX, true);
                    output.add(result);
                    renderDepth++;
                }
            }
        }

        return output;
    }





    public void DrawWalls(){

        int pixelSizeX = WIDTH / rayCount;
        for (int i = 0; i < rayCount; i++) {

            float angleMult = ((float) i / rayCount) * 2 - 1;
            float rayAngle = activeCamera.getAngle() + (float) Math.atan(angleMult * (float) Math.tan((fov * DEGREE) / 2));


            ArrayList<RaycastResult> rays = castRay(activeCamera.getPositionX(), activeCamera.getPositionY(), rayAngle);

            for (RaycastResult ray : rays.reversed() ) {
                if (!ray.didHit()) continue;

                // get angle from player to ray
                float ca = MathHelper.getSafeAngle(activeCamera.getAngle()) - ray.getRayAngle();

                // correct ray distance to remove fish eye
                float raydist = ray.getHitDist() * (float) Math.cos(ca);

                // store depth values in array for sprites

                depth[i] = ray.getHitDist();

                // Note to future self:
                // That ((mapCellSize * HEIGHT)/raydist) line, you don't need to use window height
                // you could add A arbetrary value there and use that have variable wall heights
                // could only really be used for adding height to buildings as theres no rendering for tops of floors
                // but could be an interesting technique
                // say maybe a scene with a fence and some walls past it
                // alot of opportunity there


                float wallMulti = (map[ray.getTileIndex()] > 1) ? 2.5f : 1f;

                int ScreenYDown = (int) (((HEIGHT / 2f) - ((floorHeight) - (activeCamera.getPositionZ()) * d )/ raydist));
                int ScreenYUp = (int) (((HEIGHT / 2f) - ((float) (floorHeight + (wallHeight ) * wallMulti - (activeCamera.getPositionZ() )) * d ) / raydist));
                int ScreenYMaxCeiling = (int) (((HEIGHT / 2f) - ((float) (floorHeight + (wallHeight ) - (activeCamera.getPositionZ() )) * d) / raydist));





                float uvYStep = 1f / (ScreenYDown - ScreenYUp);
                float uvYOffset = 0;

                //set yUv before clamping


                float uvY = ScreenYUp;


                // clamp after calculating uv step
                // basically clips the line height


                ScreenYDown = Math.min(Math.max(ScreenYDown, 0), HEIGHT);
                ScreenYUp = Math.min(Math.max(ScreenYUp, 0), HEIGHT);

                // get the screen x coordinate
                int lineX = (int) (((float) i / rayCount) * WIDTH);


                // uvs, god awful way to do it

                uvY = ScreenYUp - uvY;
                uvY *= uvYStep;
                float uvX = 0;
                // calculate uvX
                if (!ray.hitVerticle) {
                    uvX = (ray.getRayX() / mapCellSize) % 1;
                    if (ray.getRayAngle() < PI) {
                        uvX =  1- uvX;
                    }
                } else {
                    uvX = (ray.getRayY() / mapCellSize) % 1;
                    if (ray.getRayAngle() > PI2 && ray.getRayAngle() < PI3) {
                        uvX = 1 - uvX;
                    }
                }

                // shading factor

                float shade = ray.hitVerticle ? 0.5f : 1f;


                // draw the wall line
                for (int y = ScreenYUp; y < ScreenYDown; y++) {


                    // should we mask this
                    int maskIndex = (y / floorRes) * (WIDTH / floorRes) + (lineX / floorRes);
                    boolean mask = ceilingMask[maskIndex];
                    if ( mask && y < ScreenYMaxCeiling) {

                        uvY += uvYStep;
                        if (uvY > 32) {
                            uvY = 32;
                        }

                        continue;}



                    int pixelX = (int) (((uvX) * 32f) % 32) + 32;
                    int pixelY = (int) ((uvY * 32f) % 32);


                    int[] rgb = Textures.getWallRGB(pixelX,pixelY);


                    setSquare(lineX, y, pixelSizeX, pixelSizeX, (int) (rgb[0]), (int) (rgb[1]), (int) (rgb[2]));
                    //setSquare(lineX, y, pixelSizeX, pixelSizeX, (int) (uvX * 255), (int) (uvY * 255), (int) (0));


                    uvY += uvYStep;
                    if (uvY >= 1) {
                        uvY = 1;
                    }
                }

            }
        }

    }





    public void DrawFloor(){
        // draw floors and ceiling

        // this was painfull

        float rayAng1 = MathHelper.getSafeAngle(activeCamera.getAngle() - (DEGREE * (fov / 2)));
        float rayAng2 = MathHelper.getSafeAngle(activeCamera.getAngle() + (DEGREE * (fov / 2)));

        float scaling = 1f / (float) Math.cos(DEGREE * (fov / 2));

        // problem somwhere here
        float rayDirX0 =  (float)  Math.cos(rayAng1) * scaling;
        float rayDirY0 =  (float) Math.sin(rayAng1) * scaling;
        float rayDirX1 = (float)  Math.cos(rayAng2) * scaling;
        float rayDirY1 =  (float) Math.sin(rayAng2) * scaling;


        for ( int y = 0; y < HEIGHT / floorRes; y++ ) {



            boolean isFloor = ( (y * floorRes) > HEIGHT / 2);

            float heightFactor = 0;
            if ( isFloor){
                heightFactor = activeCamera.getPositionZ() - floorHeight;
            }else{
                heightFactor = floorHeight + wallHeight - activeCamera.getPositionZ() ;
            }

            // Calculations for floor projection
            float rowDistance = (float) Math.abs((heightFactor  * d)  / ((y * floorRes) - ((float) HEIGHT / 2f)));

            float floorStepX = (rowDistance / mapCellSize) * (rayDirX1 - rayDirX0) / WIDTH;
            float floorStepY = (rowDistance / mapCellSize) * (rayDirY1 - rayDirY0) / WIDTH;

            float floorX = (activeCamera.getPositionX() / mapCellSize) + (rowDistance / mapCellSize) * rayDirX0;
            float floorY = (activeCamera.getPositionY() / mapCellSize)+ (rowDistance / mapCellSize) * rayDirY0;





            float shade = isFloor ? 0.5f : 1f;

            for (int x = 0; x < WIDTH / floorRes; x++) {


                int cellX = (int)(floorX);
                int cellY = (int)(floorY);

                // get the texture coordinate from the fractional part
                int tx = (int)(32 * (floorX - cellX) ) & (32 - 1);
                int ty = (int)(32 * (floorY - cellY)) & (32 - 1);

                int tileX = (int) floorX;
                int tileY = (int) floorY;
                int tileValue = 0;

                if(tileX >= 0 && tileX < mapX && tileY >= 0 && tileY < mapY){

                    tileValue = ceilingMap[tileY * mapX + tileX];
                }

                floorX += floorStepX * floorRes;
                floorY += floorStepY * floorRes;



                // this basically makes a mask
                // that will tell the wall render if it should clip to ceiling height or not
                // couldn't think of a better solution



                float shader = 0;
                int depthMapVlaue = (int) (y * (WIDTH / floorRes) + x);
                if (tileValue > 0 && !isFloor) {
                    ceilingMask[depthMapVlaue] = true;
                    shader = 1;
                } else {
                    ceilingMask[depthMapVlaue] = false;

                }





                if( tileValue > 0 || isFloor) {

                    int pixelX = (int) (tx % 32);
                    int pixelY = (int) (ty % 32);

                    int[] rgb = Textures.getWallRGB(pixelX,pixelY);


                    setSquare(x * floorRes, y * floorRes, floorRes, floorRes, (int) ( rgb[0]) , (int) (rgb[1]), (int) (rgb[2]));

                }else{


                    float uvRot = (activeCamera.getAngle() / PI2 );

                    float skyU = ((float) (x) * floorRes) / (float) WIDTH;
                    skyU += uvRot / 2;
                    float skyY = ((float) y * floorRes) / (float) HEIGHT;
                    skyY = Math.floorMod((int) (skyY * 80), 80);
                    skyU = Math.floorMod((int) (skyU * 120), 120);
                    int pixel = ( ( (int) skyY * 120 + (int) skyU) * 3);
                    int r = 0, g = 0, b = 0;


                    if (pixel >= 0 && pixel < SkyTextures.sky1.length) {

                        r = SkyTextures.sky1[pixel];
                        g = SkyTextures.sky1[pixel + 1];
                        b = SkyTextures.sky1[pixel + 2];
                    }
                    setSquare(x * floorRes, y * floorRes, floorRes, floorRes, (int) (r ), (int) (g ), (int) (b ));
                }
            }
        }
    }


    public void drawSprite(float x, float y, float z){
        float spriteX = x - activeCamera.getPositionX();
        float spriteY = y - activeCamera.getPositionY();
        float spriteZ = (z + (float) floorHeight );



        // math makes the point rotate around the player
        // tbh I don't understand it too well
        float cs = (float) -Math.cos(activeCamera.getAngle());
        float sn = (float) -Math.sin(activeCamera.getAngle());

        float a = spriteY * cs - spriteX * sn;
        float b = spriteX * cs + spriteY * sn;

        spriteX = a;
        spriteY = b;

        // and boom we have screen coordinates
        // that scaling factor is arbitrary

        if ( b < 0) {

            float fovVal = (((float) WIDTH / 2f) / (float) Math.tan(DEGREE * (fov / 2)));
            float ScreenX = (WIDTH / 2f) + (fovVal * a / b);
            float ScreenY = (HEIGHT / 2f) - (fovVal * (spriteZ)  / b);



            int spritePXS = 2;
            float spriteWorldSize = 300;
            int spriteSize = (int) ( (spriteWorldSize * mapCellSize) / -b  ) ;

            int startX = (int) ScreenX - spriteSize / 2;
            int endX = (int) ScreenX + spriteSize / 2;
            // clamp the values so the loop doesn't go too far
            startX = Math.max(Math.min(startX,WIDTH),0) / spritePXS;
            endX = Math.max(Math.min(endX,WIDTH),0) / spritePXS;



            int startY = (int) ScreenY - spriteSize / 2;
            int endY = (int) ScreenY + spriteSize / 2;
            // clamp the values so the loop doesn't go too far
            startY = Math.max(Math.min(startY,HEIGHT),0) / spritePXS;
            endY = Math.max(Math.min(endY,HEIGHT),0) / spritePXS;


            for (int sx = startX; sx <= endX; sx++) {
                if(sx > WIDTH) { break;}


                // depth only needs to be sampled horizontally
                int depthIndex = (int) ((sx * spritePXS / (float) WIDTH) * rayCount);
                float zVal = 0;
                if (depthIndex > -1 && depthIndex < rayCount) {
                    zVal = depth[depthIndex];
                }
                float v = (((sx * spritePXS) - ScreenX) + (spriteSize / 2f)) / spriteSize;

                // make sure sprite is actually renderable
                if (b * -1 < zVal) {


                    for (int sy = startY ; sy <= endY  ; sy++)
                    {
                        if (sy > HEIGHT) {break;}
                        float u = (((sy * spritePXS) - ScreenY) + (spriteSize / 2f)) / spriteSize;

                        int pixel = (((int) ((u * 16) % 16) * 16 + ((int) (v * 16) % 16)) * 3);

                        int cr =0, cg= 0, cb =0;
                        /*
                        if (pixel >= 0 && pixel < Textures.grassblock.length) {
                            cr = Textures.stoneblock[pixel];
                            cg = Textures.stoneblock[pixel + 1];
                            cb = Textures.stoneblock[pixel + 2];
                        }

                         */


                        setSquare((int) sx * spritePXS, (int) sy * spritePXS, spritePXS, spritePXS, cr, cg, cb);
                    }
                }
            }
        }
    }




    // Logic to draw pixels to the screen

    // set pixel color

    public int[] getRenderedPixels(){
        return this.pixels;
    }

    public void clearPixelBuffer(){
        Arrays.fill(this.pixels,0);
    }



    private void setPixel(int x, int y, int r, int g, int b)
    {
        if ( x > WIDTH - 1|| y > HEIGHT - 1 || x < 0 || y < 0) return;

        //System.out.printf("R: %d G: %d B: %d%n",r,g,b);

        int pixelIndex = y * WIDTH + x;
        if(pixelIndex > pixels.length - 1 || pixelIndex <= 0 ) return;
        pixels[pixelIndex] = (255 << 24) | ( r << 16) | (g << 8) | b;
    }

    private void setSquare(int startX, int startY, int sizeX, int sizeY, int r, int g, int b)
    {
        for (int x = 0; x <= sizeX; x++){
            for (int y = 0; y <= sizeY; y++){
                setPixel(startX + x, startY + y, r,g,b);
            }
        }
    }


    private void setLine(int startX, int startY, int endX, int endY, int r, int g, int b)
    {
        int dx = Math.abs(endX - startX);
        int dy = Math.abs(endY - startY);

        // Determine the step direction for each axis
        int sx = (startX < endX) ? 1 : -1;
        int sy = (startY < endY) ? 1 : -1;

        int err = dx - dy;
        int checks = 0;
        while (checks < 512) {
            // Write the pixel to your grid
            setPixel(startX, startY, r,g,b);

            // Break the loop when the destination point is reached
            if (startX == endX && startY == endY) {
                break;
            }

            int e2 = 2 * err;

            // Move along the X axis
            if (e2 > -dy) {
                err -= dy;
                startX += sx;
            }

            // Move along the Y axis
            if (e2 < dx) {
                err += dx;
                startY += sy;
            }
            checks++;
        }
    }


}
