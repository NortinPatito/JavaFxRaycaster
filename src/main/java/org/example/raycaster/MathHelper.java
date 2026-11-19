package org.example.raycaster;

public class MathHelper {

    public static float PI = (float ) 3.1415929;
    static float PI2 = PI / 2;
    static float PI3 = 3 * PI/ 2;
    public static float DEGREE = PI / 180;

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

    public static float[] projectToNormal(float x, float y, float nX, float nY)
    {
        float mag = (float) Math.sqrt((x*x) + (y*y));

        return new float[] {x / mag, y/ mag};

    }

    public static float magnitude(float x, float y)
    {
        return (float) Math.sqrt((x*x) + (y*y));
    }


    public static float dot(float x1, float y1, float x2, float y2){
        return (x1 * x2) + (y1 * y2);
    }

    public static float lerp(float a, float b, float t)
    {
        return a + ( b -a ) * t;
    }


    public static float toRadians(float in)
    {
        return in * DEGREE;
    }

    // Math conversion checks

    public static boolean IsInt(String str){
        if ( str == null || str.isEmpty() ) {return false;}
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean IsFloat(String str){
        if ( str == null || str.isEmpty() ) {return false;}
        try {
            Float.parseFloat(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }





}
