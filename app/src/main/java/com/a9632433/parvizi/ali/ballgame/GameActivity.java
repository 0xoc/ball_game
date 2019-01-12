package com.a9632433.parvizi.ali.ballgame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        LinearLayout gameContainer = findViewById(R.id.gameContainer);

        MainGameView gameView = new MainGameView(this, 10);

        gameContainer.addView(gameView);

    }
}
