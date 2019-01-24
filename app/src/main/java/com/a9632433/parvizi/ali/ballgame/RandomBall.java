package com.a9632433.parvizi.ali.ballgame;

import android.widget.Toast;

import java.io.Serializable;
import java.util.Random;

public class RandomBall implements Serializable {
    private static int _id;
    public int id;

    // indicates whether this ball is the player
    public boolean isPlayer = false;

    /*
        canvas width and height
        needs to be initialized by the parent view once
        and all the balls use it
    */

    private static float _canvasWidth;
    private static float _canvasHeight;

    // indicates whether canvas with and height are initialized
    private static boolean isInitialized = false;

    // position
    public float x;
    public float y;

    // velocity
    public float dx;
    public float dy;

    // color of the ball
    ARGBColor color;

    // scalars
    public float radius = 50.0f;
    public float mass = 10.0f;

    // minimum initial speed
    public static float minSpeed = 2.0f;

    // helpers

    // Random object used through out the code
    public static Random rnd = new Random();
    public static float speedScaleFactor = 5;

    // initializer for the canvas width and height
    public static void initCanvasDimensions(float canvasWidth, float canvasHeight){
        _canvasWidth = canvasWidth;
        _canvasHeight = canvasHeight;
        isInitialized = true;
    }


    // generates random position, velocity and color
    public RandomBall() throws Exception{
        if (!isInitialized)
            throw new Exception("Canvas Width And Height Not Initialized");

        // set ball id
        _id++;
        id = _id;

        // position
        x = rnd.nextInt((int) _canvasWidth);
        y = rnd.nextInt((int) _canvasHeight);

        // velocity

        dx = rnd.nextFloat()  * speedScaleFactor + minSpeed;
        dy = rnd.nextFloat()  * speedScaleFactor + minSpeed;

        // color
        color = new ARGBColor();
    }
}
