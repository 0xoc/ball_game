package com.a9632433.parvizi.ali.ballgame;

import java.util.Random;

public class RandomBall {

    public int id;
    private static int count = 0;

    public float x;
    public float y;

    public float dx;
    public float dy;
    public float speedScale;
    public float minSpeed;

    public float radius;
    public float radiusScale;
    public float minRadius;

    public float screenWidth;
    public float screenHeight;


    public boolean isPlayer = false;
    ARGBColor color;


    public void init(float screenWidth, float screenHeigh){
        Random rnd = new Random();

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeigh;

        // set random x and y
        this.x = rnd.nextInt( (int) screenWidth);
        this.y = rnd.nextInt(( int) screenHeigh );


        // initial randomly
        this.dx = this.speedScale * this.minSpeed;
        this.dy = rnd.nextFloat() * this.speedScale + this.minSpeed;
        this.radius = rnd.nextFloat() * this.radiusScale + this.minRadius;

        // color
        this.color = new ARGBColor();
    }

    public RandomBall(float speedScale, float minSpeed, float radiusScale, float minRadius) {
        this.speedScale = speedScale;
        this.minSpeed = minSpeed;
        this.radiusScale = radiusScale;
        this.minRadius = minRadius;

        id = count++;
    }

    public RandomBall(){ id = count++; }

    public void boundaryCheck(){
        // handle left and right
        if (this.x + this.radius >= screenWidth){
            this.x = screenWidth - this.radius;
            this.dx = - this.dx;
        } else if (this.x - this.radius <= 0){
            this.x = this.radius;
            this.dx = -this.dx;
        }

        // handle top and bottom
        if (this.y + this.radius >= screenHeight){
            this.y = screenHeight - this.radius;
            this.dy = - this.dy;
        } else if (this.y - this.radius <= 0){
            this.y = this.radius;
            this.dy = -this.dy;
        }
    }

    public void move(){
        // update x and y
        this.x += this.dx;
        this.y += this.dy;

        boundaryCheck();

    }

    public void move(float x, float y){
        this.x = x;
        this.y = y;

        boundaryCheck();
    }
}
