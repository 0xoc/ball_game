package com.a9632433.parvizi.ali.ballgame;

import java.util.ArrayList;
import java.util.Random;

public class ARGBColor {
    public int r;
    public int g;
    public int b;
    public int a;

    ARGBColor(){
        Random rnd = new Random();
        int position = rnd.nextInt(3);
        this.r = (position == 0) ? 255: 0;
        this.g = (position == 1) ? 255: 0;
        this.b = (position == 2) ? 255: 0;
        this.a = 255;
    }

    public ARGBColor(int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }
}
