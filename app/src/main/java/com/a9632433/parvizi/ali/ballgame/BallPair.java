package com.a9632433.parvizi.ali.ballgame;

import java.io.Serializable;

public class BallPair implements Serializable {
    public RandomBall firstBall;
    public RandomBall secondBall;

    public BallPair(RandomBall firstBall, RandomBall secondBall) {
        this.firstBall = firstBall;
        this.secondBall = secondBall;
    }
}
