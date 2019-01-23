package com.a9632433.parvizi.ali.ballgame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        createNewGameInstance(1);
    }

    public void createNewGameInstance(int level){
        final FrameLayout gameContainer = findViewById(R.id.gameContainer);               // "game canvas"
        final TextView score = findViewById(R.id.score);                                  // game score text view
        final MainGameView game = new MainGameView(this,10,level);         // a game instance


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
                            if (game.getFinalStatus() == false) {   // if player lost the game
                                // remove this game instance
                                gameContainer.removeView(game);

                                // cancel this timer
                                checker.cancel();

                                // show high score list
                            } else {    // if player won the game

                            }
                        }
                    }
                });
            }
        },0,100);
    }
}
