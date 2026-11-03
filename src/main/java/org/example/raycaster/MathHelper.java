package org.example.raycaster;

public class MathHelper {

    static float PI = (float ) 3.1415929;
    static float PI2 = PI / 2;
    static float PI3 = 3 * PI/ 2;
    static float DEGREE = 0.0174533f;

    public static float getSafeAngle(float angle){
        float outAngle = angle;
        if ( outAngle < 0) { outAngle += 2 * PI;}
        if ( outAngle > 2 * PI) { outAngle -= 2 * PI;}

        return outAngle;
    }

    public static float dist(float ax, float ay, float bx, float by){
        return (float) Math.sqrt((bx - ax) * (bx - ax) + (by- ay) * (by - ay));
    }


    public static float[] normalize2D(float x, float y)
    {
        float mag = (float) Math.sqrt((x*x) + (y*y));

        return new float[] {x / mag, y/ mag};

    }

    public static float magnitude(float x, float y)
    {
        return (float) Math.sqrt((x*x) + (y*y));
    }
}
