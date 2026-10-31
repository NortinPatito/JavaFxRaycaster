package org.example.raycaster;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public  class SkyTextures {

    public void loadTextures() {
        String resourcePath = "/cloudsky1.ppm";

        try (InputStream is = this.getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }
            ;

            try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
                 BufferedReader reader = new BufferedReader(isr)) {

                List<String> lines = reader.lines().toList();
                int lineCount = lines.size();
                sky1 = new int[lineCount];
                int i =0;

                for(String line : lines) {
                    int value = Integer.parseInt(line.toString());
                    sky1[i] = value;

                    i++;
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int[] sky1;

}
