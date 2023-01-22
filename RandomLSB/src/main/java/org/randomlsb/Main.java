package org.randomlsb;

import org.randomlsb.Core.StegoCore;
import org.randomlsb.Core.StegoUtils;

import java.awt.image.BufferedImage;
import java.io.Console;
import java.io.IOException;

public class Main
{
    public static void main( String[] args ) throws Exception {
        StegoCore core = new StegoCore();
//        String message="hello world stego! А по русски?";
//        System.out.println("Message encode: "+message);
//        BufferedImage img = core.Encrypt("apples.png","secret_key",message);
//        StegoUtils.saveImgAsPNG(img,"2.png");
        System.out.println("Message decode: "+core.Decode("55939902981.png","secret_key"));
    }
}
