package com.a9632433.parvizi.ali.ballgame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;

public class GameActivity extends AppCompatActivity {
    LayoutInflater layoutInflater;

    // current score
    private int currentScore;

    // all scores
    private ArrayList<Integer> scores;

    // indicates that the player lost was dealt with (due to un sync thread issues)
    boolean lostDealtWith = false;

    // root view group
    ViewGroup root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // main game root
        root = findViewById(R.id.mainRoot);

        // init layout inflater
        layoutInflater = getLayoutInflater();


        createNewGameInstance(1);
    }

    public void createNewGameInstance(int level){

        // inflate the game layout
        final View gameView = layoutInflater.inflate(R.layout.game_play_layout,null);

        // add the game layout to the root layout
        root.addView(gameView);

        final FrameLayout gameContainer = gameView.findViewById(R.id.gameContainer);               // "game canvas"
        final TextView score = gameView.findViewById(R.id.score);                                  // game score text view
        final MainGameView game = new MainGameView(this,10,level);        // a game instance


        // add the game to the game container
        gameContainer.addView(game);

        // update the score and check for any final result
        final Timer checker = new Timer();
        checker.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        // update the score
                        score.setText(String.format("%d", game.getScore()));

                        if (game.isGameEnded()){    // the game is finished
                            if (!game.getFinalStatus()) {   // if player lost the game
                                // remove this game instance
                                root.removeView(gameView);

                                // set global game current game score
                                currentScore = game.getScore();

                                // cancel this timer
                                checker.cancel();

                                // show lost page
                                createLostPage();

                                // show high score list
                            } else {    // if player won the game

                            }
                        }
                    }
                });
            }
        },0,100);
    }

    public void createLostPage(){
        if (lostDealtWith)  // page has been generated before
            return;
        lostDealtWith = true;

        // inflate the lost page
        View lostPage = layoutInflater.inflate(R.layout.game_lost_layout,null);
        root.addView(lostPage);

        // current score in lost page
        TextView currentScoreText = lostPage.findViewById(R.id.currentScoreLost);
        currentScoreText.setText(currentScore + "");

    }
}
