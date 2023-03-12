package org.randomlsb;

import org.randomlsb.Core.StegoCore;


public class Main {
    public static void main(String[] args) throws Exception {
        StegoCore core = new StegoCore();

        System.out.println("Message decode: " + core.Decode("55939902981.png", "secret_key"));
    }
}
