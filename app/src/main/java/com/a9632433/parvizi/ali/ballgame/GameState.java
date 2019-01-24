package com.a9632433.parvizi.ali.ballgame;

import android.graphics.Paint;

import java.io.Serializable;
import java.util.ArrayList;

public class GameState implements Serializable {
    // all balls
    public ArrayList<RandomBall> balls = new ArrayList<>();
    public ArrayList<BallPair> collideBalls = new ArrayList<>();
    public RandomBall player;

    public int noBalls;                        // number of initial balls

    public int level;                          // game level
    public int score;                          // current score

    public boolean gameStarted = false;        // indicates whether the game is started or not
    public boolean gameEnded = false;          // indicates whether the game ended or not (won level or lost)
    public boolean finalStatus;                // true: game won | false: game lost

    public int elapsedTime = 0;                // elapsed time since the creation of the view

    public boolean playerSelected = false;     // indicates whether the player is selected or not

    public boolean newGame = true;

    public GameState(ArrayList<RandomBall> balls, ArrayList<BallPair> collideBalls, RandomBall player, int noBalls, int level, int score, boolean gameStarted, boolean gameEnded, boolean finalStatus, int elapsedTime, boolean playerSelected, boolean newGame) {
        this.balls = balls;
        this.collideBalls = collideBalls;
        this.player = player;
        this.noBalls = noBalls;
        this.level = level;
        this.score = score;
        this.gameStarted = gameStarted;
        this.gameEnded = gameEnded;
        this.finalStatus = finalStatus;
        this.elapsedTime = elapsedTime;
        this.playerSelected = playerSelected;
        this.newGame = newGame;
    }


}
