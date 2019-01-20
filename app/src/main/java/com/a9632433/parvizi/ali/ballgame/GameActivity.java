package com.a9632433.parvizi.ali.ballgame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        FrameLayout gameContainer = findViewById(R.id.gameContainer);
        final MainGameView gameView = new MainGameView(this, 1);
        gameContainer.addView(gameView);

        final TextView score = findViewById(R.id.score);

        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                score.setText(gameView.score + "");
            }
        },0,10);
    }
}
