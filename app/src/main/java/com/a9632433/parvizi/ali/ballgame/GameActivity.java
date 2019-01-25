package com.a9632433.parvizi.ali.ballgame;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;

public class GameActivity extends AppCompatActivity implements GameInitializer{
    LayoutInflater layoutInflater;

    // current score
    private MainGameView currentGame;

    // all scores
    private ArrayList<Integer> scores;

    // root view group
    ViewGroup root;

    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // main game root
        root = findViewById(R.id.mainRoot);

        // init layout inflater
        layoutInflater = getLayoutInflater();

    }

    @Override
    protected void onResume() {
        super.onResume();

        // load any previous game states

        try {
            FileInputStream fin = openFileInput("game.txt");
            ObjectInputStream iin = new ObjectInputStream(fin);
            GameState gameState = (GameState) iin.readObject();

            currentGame = new MainGameView(getApplicationContext(), gameState);
            iin.close();
            fin.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // remove all sub views first
        root.removeAllViews();
        createGameLevelSelector();

    }



    @Override
    protected void onPause() {
        super.onPause();
        // save game state

        if (currentGame == null)   // ignore if game ended or not initialized at all
            return;

        try {
            FileOutputStream fout = openFileOutput("game.txt", Context.MODE_PRIVATE);
            ObjectOutputStream oout = new ObjectOutputStream(fout);
            oout.writeObject(currentGame.getGameState());
            oout.close();
            fout.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // stop mp any ways
        if (mp != null)
            mp.stop();
    }

    public void createGameLevelSelector(){
        root.removeAllViews();

        // inflate the level selector layout
        final View levelSelectorView = layoutInflater.inflate(R.layout.game_level_selector, root, false);

        // add it to the root
        root.addView(levelSelectorView);

        // top avalibale game level, 0 if no game
        int topLevel = (currentGame == null)? 1 : currentGame.getTopLevel();

        // create the level selector list adapter
        RecyclerView.Adapter adapter = new LevelSelectorListAdapter(topLevel, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);


        // handle to the list
        RecyclerView levelsList = levelSelectorView.findViewById(R.id.levelList);

        // attach the adapter to it
        levelsList.setAdapter(adapter);

        // attach layout manager
        levelsList.setLayoutManager(layoutManager);

        ImageButton resumeGame = levelSelectorView.findViewById(R.id.resumeGameBtn);

        resumeGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentGame == null || currentGame.isGameEnded())
                    Toast.makeText(getApplicationContext(), "No ongoing game to resume", Toast.LENGTH_SHORT).show();
                else {
                    createNewGameInstance(-1);
                }
            }
        });
    }

    public void createNewGameInstance(int level){
        root.removeAllViews();

        // inflate the game layout
        final View gameView = layoutInflater.inflate(R.layout.game_play_layout,root,false);


        // add the game layout to the root layout
        root.addView(gameView);

        final FrameLayout gameContainer = gameView.findViewById(R.id.gameContainer);               // "game canvas"
        final TextView score = gameView.findViewById(R.id.score);                                  // game score text view

        if (level != -1)
            currentGame = new MainGameView(this,10,level);                            // a game instance


        // add the game to the game container
        gameContainer.addView(currentGame);

        // play backgournd music
        int id = (currentGame.getLevel() <= 5) ? R.raw.easy_level : R.raw.thriller;
        mp = MediaPlayer.create(getApplicationContext(), id);
        mp.start();

        // set level text
        TextView levelText = gameView.findViewById(R.id.level);

        levelText.setText(currentGame.getLevel() + "");

        // update the score and check for any final result
        final Timer checker = new Timer();
        checker.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        // update the score
                        score.setText(String.format("%d", currentGame.getScore()));

                        if (currentGame.isGameEnded()){    // the game is finished
                            // cancel this timer
                            checker.cancel();

                            // stop the sound
                            mp.stop();

                            // add the score
                            currentGame.addScore(currentGame.getLevel());

                            if (!currentGame.getFinalStatus()) {   // if player lost the game
                                // show lost page
                                createLostPage();

                            } else {    // if player won the game
                                createWinLevelPage();
                            }
                        }
                    }
                });
            }
        },0,100);


    }

    public void createLostPage(){
        root.removeAllViews();

        // inflate the lost page
        final View lostPage = layoutInflater.inflate(R.layout.game_lost_layout,root,false);
        root.addView(lostPage);

        // current score in lost page
        final TextView currentScoreText = lostPage.findViewById(R.id.currentScoreLost);
        currentScoreText.setText(currentGame.getScore() + "");

        // replay button
        final ImageButton replayButton = lostPage.findViewById(R.id.replayGameBtn);

        // menu button
        final ImageButton showMenu = lostPage.findViewById(R.id.showLevelSelectBtn);


        ArrayList<Integer> levelScores = new ArrayList<>();
        int count = 20;
        for (int i = 0; i < MainGameView.scores.size() && count > 0 ; i++ , count--){
            if (MainGameView.scores.get(i).level == currentGame.getLevel())
                levelScores.add(MainGameView.scores.get(i).score);
        }

        // high scores
        RecyclerView highScoreList = lostPage.findViewById(R.id.highScoreListLosts);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.Adapter adapter = new HighScoresAdapter(levelScores);

        highScoreList.setLayoutManager(layoutManager);
        highScoreList.setAdapter(adapter);


        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // detach the click listener
                replayButton.setOnClickListener(null);

                // remove the lost page view
                root.removeView(lostPage);

                // create a new game instance
                createNewGameInstance(currentGame.getLevel());
            }
        });

        showMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // detach the click listener
                showMenu.setOnClickListener(null);

                // remove the lost page view
                root.removeView(lostPage);

                // create a new game instance
                createGameLevelSelector();
            }
        });

    }

    public void createWinLevelPage(){
        root.removeAllViews();

        // inflate the win page
        View winPage = layoutInflater.inflate(R.layout.game_win_layout, root, false);

        // add it to root
        root.addView(winPage);

        // level text
        TextView levelText = winPage.findViewById(R.id.winLevelText);

        // set the level
        levelText.setText(currentGame.getLevel() +"");

        // show menu button
        final ImageButton menu = winPage.findViewById(R.id.winShowMenu);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // remove the listener
                menu.setOnClickListener(null);

                createGameLevelSelector();
            }
        });

        // next level
        final ImageButton nextLevel = winPage.findViewById(R.id.nextLevelBtn);

        nextLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // remove the click listener
                nextLevel.setOnClickListener(null);

                // initialize next level
                createNewGameInstance(currentGame.getLevel() + 1);
            }
        });
    }

    public void createHighLevelScorePage(int level){
        root.removeAllViews();

        // inflate the layout
        View highScores = layoutInflater.inflate(R.layout.game_level_highscore, root,false);

        // list of scores
        ArrayList<Integer> levelScores = new ArrayList<>();


        int count = 20;
        for (int i = 0; i < MainGameView.scores.size() && count > 0 ; i++ , count--){
            if (MainGameView.scores.get(i).level == level)
                levelScores.add(MainGameView.scores.get(i).score);
        }

        // add to root
        root.addView(highScores);

        TextView levelText = highScores.findViewById(R.id.highScoreLevel);
        RecyclerView highScoreList = highScores.findViewById(R.id.levelHighScores);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.Adapter adapter = new HighScoresAdapter(levelScores);

        highScoreList.setLayoutManager(layoutManager);
        highScoreList.setAdapter(adapter);

        // menu button
        ImageButton menu = highScores.findViewById(R.id.highScoreMenu);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGameLevelSelector();
            }
        });

        // level text
        TextView levelTextView = highScores.findViewById(R.id.highScoreLevel);
        levelTextView.setText(level + "");
    }

    @Override
    public void initGame(int level) {
        createNewGameInstance(level);
    }
}
