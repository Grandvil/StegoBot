package ru.randomlsb;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class StegoCore {
    ArrayList<Integer> idxPix = new ArrayList<Integer>();

    private int findFreePixel(int imgSize,Random rnd){
        // поиск свободного пикселя
        int numPix = 0;
        boolean findNumPix = true;
        while (findNumPix) {
            numPix = (int) rnd.nextInt(imgSize);
            // проверяем этот номер, использовался раньше?
            if (idxPix.contains(numPix)) continue;

            idxPix.add(numPix);
            findNumPix = false;
        }
        return numPix;
    }
    public BufferedImage Encrypt(BufferedImage img, String keyForSeed, String txt) throws Exception { ///*String pathImg*/

        Random rnd = new Random(StegoUtils.getSeedFromStrKey(keyForSeed));
        String txtCrypt=txt+'\0'; //end message for decoder

        //BufferedImage img=StegoUtils.fetchImage(pathImg);
        int imgSize = img.getWidth() * img.getHeight();

        idxPix.clear();
        // цикл шифрования
        for (char b : txtCrypt.toCharArray()) {
            //get rnd free num pixel
            int numPix=findFreePixel(imgSize,rnd);

            //cast symbol to int
            int thisChar = b;

            if (thisChar > 1000) thisChar -= 890;    // код кириллицы больше -> уменьшаем

            int i=numPix/img.getWidth();
            int j=numPix-(i*img.getWidth());

            int thisColor = img.getRGB(i,j);  // читаем пиксель

            // упаковка в RGB 323
            int newColor = (thisColor & 0xF80000);   // 11111000 00000000 00000000
            newColor |= (thisChar & 0xE0) << 11;     // 00000111 00000000 00000000
            newColor |= (thisColor & (0x3F << 10));  // 00000000 11111100 00000000
            newColor |= (thisChar & 0x18) << 5;      // 00000000 00000011 00000000
            newColor |= (thisColor & (0x1F << 3));   // 00000000 00000000 11111000
            newColor |= (thisChar & 0x7);            // 00000000 00000000 00000111

            img.setRGB(i,j,newColor);
        }
        idxPix.clear();

    return img;
}

    public String Decrypt(/*String pathImg*/BufferedImage img, String keyForSeed) throws Exception {

        Random rnd = new Random(StegoUtils.getSeedFromStrKey(keyForSeed));

        //BufferedImage img=StegoUtils.fetchImage(pathImg);
        int imgSize = img.getWidth() * img.getHeight();

        idxPix.clear();
        StringBuilder message= new StringBuilder();
        // цикл шифрования
        do {
            //get rnd free num pixel
            int numPix=findFreePixel(imgSize,rnd);

            int i=numPix/img.getWidth();
            int j=numPix-(i*img.getWidth());

            // читаем пиксель
            int thisColor = img.getRGB(i,j);

            // распаковка из RGB 323 обратно в байт
            int thisChar = 0;
            thisChar |= (thisColor & 0x70000) >> 11;  // 00000111 00000000 00000000 -> 00000000 00000000 11100000
            thisChar |= (thisColor & 0x300) >> 5;     // 00000000 00000011 00000000 -> 00000000 00000000 00011000
            thisChar |= (thisColor & 0x7);            // 00000000 00000000 00000111

            if (thisChar > 130) thisChar += 890;      // если код кириллицы больше -> увеличиваем обратно
            message.append((char) thisChar);            // пишем в буфер
        }while (message.charAt(message.length()-1)!='\0');

        idxPix.clear();
        message = new StringBuilder(message.substring(0, message.length() - 1));
        return message.toString();

    }
}
