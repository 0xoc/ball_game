package com.a9632433.parvizi.ali.ballgame;

import java.util.ArrayList;
import java.util.Random;

public class ARGBColor {

    public static ArrayList<ARGBColor> colors;

    static {
        colors = new ArrayList<>();
        colors.add(new ARGBColor(228,249,245));
        colors.add(new ARGBColor(252,81,133));
        colors.add(new ARGBColor(17,153,158));

    }

    public int r;
    public int g;
    public int b;
    public int a;

    ARGBColor(){
        Random rnd = new Random();
        int position = rnd.nextInt(colors.size());
        ARGBColor color = colors.get(position);
        r = color.r;
        g = color.g;
        b = color.b;
        a = color.a;
    }

    public boolean equals(ARGBColor other) {
        boolean isSame;
        isSame = (other.a == this.a);
        isSame &= (other.r == this.r);
        isSame &= (other.g == this.g);
        isSame &= (other.b == this.b);

        return isSame;
    }

    public ARGBColor(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = 255;
    }
}
