package ru.randomlsb;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class StegoUtils {
   static int getSeedFromStrKey(String key){
        int seed=1;
        for (int i=0;i<key.length()-1;i++)
            seed*=(int)(key.charAt(i) * (key.charAt(i)-key.charAt(i+1))); // перемножением с разностью
        return seed;
    }
    static BufferedImage fetchImage(String path) throws Exception {
        File f = new File(path);
        return ImageIO.read(f);
    }

    static public void saveImgAsPNG(BufferedImage img,String path) throws IOException {
       File f = new File(path);
        ImageIO.write(img, "PNG", f);
    }
}
