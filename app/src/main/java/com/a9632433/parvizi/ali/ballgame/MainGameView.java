package com.a9632433.parvizi.ali.ballgame;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.System.exit;

public class MainGameView extends View implements Serializable {

    // all balls
    ArrayList<RandomBall> balls = new ArrayList<>();
    ArrayList<BallPair> collideBalls = new ArrayList<>();
    RandomBall player;

    // paint to draw
    static Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int noBalls;                        // number of initial balls

    private int level;                          // game level
    private static int topLevel = 1;                   // top level
    private int score;                          // current score

    private boolean gameStarted = false;        // indicates whether the game is started or not
    private boolean gameEnded = false;          // indicates whether the game ended or not (won level or lost)
    private boolean finalStatus;                // true: game won | false: game lost

    private int elapsedTime = 0;                // elapsed time since the creation of the view

    private boolean playerSelected = false;     // indicates whether the player is selected or not

    private boolean newGame = true;             // if it's a new game or a saved game


    private Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);

    public MainGameView(Context context, int noBalls, int level) {
        super(context);
        this.noBalls = noBalls;
        this.level = level;
        init();
    }

    public MainGameView(Context context, GameState gameState){
        super(context);
         noBalls = gameState.noBalls;
         level = gameState.level;
         score = gameState.score;
         gameStarted = false;
         gameEnded = gameState.gameEnded;
         finalStatus = gameState.finalStatus;
         elapsedTime = gameState.elapsedTime;
         playerSelected = false;
         newGame = false;
         balls =gameState.balls;
         collideBalls = gameState.collideBalls;
         player = gameState.player;
         this.topLevel = gameState.topLevel;
         init();
    }

    public GameState getGameState(){
        // save the game state
        GameState state = new GameState(
                balls,
                collideBalls,
                player,
                noBalls,
                level,
                score,
                gameStarted,
                gameEnded,
                finalStatus,
                elapsedTime,
                false,
                false, this.topLevel);
        return state;
    }

    // main game initializer
    private void init(){

        // main game timer (tracks the time second by second)
        Timer mainTimer = new Timer();
        mainTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                periodicStateManager();
            }
        }, 0,1000);

        // main view touch listener
        setOnTouchListener(mainTouchListener);

        // used to get width and height
        addOnLayoutChangeListener(layoutChangeListener);
    }

    private boolean isBallSelected(float x, float y, RandomBall ball){
        float distance = (float) sqrt(pow(x - ball.x, 2) + pow(y-ball.y,2));
        return (distance <= ball.radius );
    }

    // main view touch listener
    OnTouchListener mainTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            // start the game as soon as the player touches the screen
            gameStarted = true;

            // if player is selected move it
            if (playerSelected){
                player.x = event.getX();
                player.y = event.getY();
            }

            if (event.getAction() == MotionEvent.ACTION_DOWN){
                if (isBallSelected(event.getX(), event.getY(), player)) // select the player
                    playerSelected = true;
            } else if (event.getAction() == MotionEvent.ACTION_DOWN)    // deselect the player
                playerSelected = false;

            return true;
        }
    };


    // executes every second by the timer
    private void periodicStateManager(){
        elapsedTime++;

        if (gameStarted && !gameEnded)
            score++;

        // randomly change players color every 5 seconds
        if (elapsedTime%5 == 0)
            player.color = new ARGBColor();


    }

    // initializer of random balls
    private void initRandomBalls() throws Exception{
        RandomBall.initCanvasDimensions(getWidth(), getHeight());
        RandomBall.speedScaleFactor = level * 2;
        // generate noBalls random balls
        for (int i = 0; i < noBalls; i++)
            balls.add(new RandomBall());
    }

    // player initializer
    private void initPlayer() throws Exception{

        // generate the player
        player = new RandomBall();
        player.isPlayer = true;
        player.radius = 100.0f;
        player.dx = 0; player.dy = 0;

        balls.add(player);
    }

    // used to make sure getWidth and getHeight work correctly
    OnLayoutChangeListener layoutChangeListener = new OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            // at this point width and height of the canvas are knows
            // therefor we can initialize random balls
            if (newGame) {
                try {
                    initRandomBalls();
                    initPlayer();
                } catch (Exception e) {
                    e.printStackTrace();
                    exit(-1);
                }
            }
            // will not be needed any more
            removeOnLayoutChangeListener(layoutChangeListener);
        }
    };

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // check if game ended
        // score >= 90 or no more balls left

        if (score >= 90 || balls.size() - 1 == 0)
        {
            gameEnded = true;
            finalStatus = true; // player won the level

            // play win sound
            MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.win);
            mp.start();

            if (level == topLevel)
                topLevel++;         // increase the top level
            return;
        }

        // empty the collideBall array list
        collideBalls.clear();

        for (int i = 0 ;i < balls.size() ; i++){
            RandomBall ball = balls.get(i);

            /*
                move the ball according to its velocity
                and then check the screen boundaries
            */

            if (!ball.isPlayer) {   // move the ball only if it's not the player
                ball.x += ball.dx;
                ball.y += ball.dy;
            }


            // check X axis
            if (ball.x + ball.radius >= getWidth()) {ball.x = getWidth() - ball.radius; ball.dx *= -1; }
            else if (ball.x - ball.radius <=0 ) { ball.x = ball.radius; ball.dx *= -1; }

            // check Y axis
            if (ball.y + ball.radius >= getHeight() ) {ball.y = getHeight() - ball.radius; ball.dy *= -1;}
            else if (ball.y - ball.radius <= 0) {ball.y = ball.radius; ball.dy *= -1; }


            // set the paint color to the balls color
            paint.setARGB(ball.color.a, ball.color.r, ball.color.g, ball.color.b);
            canvas.drawCircle(ball.x, ball.y, ball.radius, paint);

        }

        // detect ball collisions
        for (int i = 0 ;i < balls.size() ; i++) {
            RandomBall firstBall = balls.get(i);            // reference to the first ball

            for (int j = 0 ; j < balls.size() ; j ++){
                RandomBall secondBall = balls.get(j);       // reference to the second ball

                if (firstBall.id == secondBall.id)          // continue if the two balls are the same
                    continue;

                // distance of the centers of the two balls
                float centerDistance = (float) sqrt(pow(firstBall.x - secondBall.x,2) + pow(firstBall.y - secondBall.y,2));
                float tangentDistance = firstBall.radius + secondBall.radius;
                float overLap = tangentDistance - centerDistance;

                if (overLap >= 0) { // if the two ball collide
                    // move them apart just so enough that they don't collide any more

                    if (firstBall.isPlayer && gameStarted){    // if is player and game started check if it lost or won
                        if (firstBall.color.equals(secondBall.color)) {  // the player won

                            // Vibrate for 100 milliseconds
                            v.vibrate(100);

                            score += 5;
                            balls.remove(secondBall);   // remove the second ball
                        } else {    // the player lost
                            gameEnded = true;
                            finalStatus = false;
                            v.vibrate(300);
                            MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.zapsplat_multimedia_game_incorrect_buzz_tone_001_26397);
                            mp.start();
                        }
                    }

                    float ddX = (float) (0.5 * overLap * (firstBall.x  - secondBall.x) / centerDistance);
                    float ddY = (float) (0.5 * overLap * (firstBall.y  - secondBall.y) / centerDistance);

                    // move the first ball away
                    firstBall.x += ddX; firstBall.y += ddY;

                    // move the second ball away
                    secondBall.x -= ddX; secondBall.y -= ddY;

                    // add the two balls to collide balls array list to be processed later
                    collideBalls.add(new BallPair(firstBall, secondBall));

                }
            }
        }

        // resolve ball collisions
        for (int i = 0 ; i < collideBalls.size() ; i ++){
            RandomBall firstBall = collideBalls.get(i).firstBall;
            RandomBall secondBall = collideBalls.get(i).secondBall;

            float centerDistance = (float) sqrt(pow(firstBall.x - secondBall.x,2) + pow(firstBall.y - secondBall.y,2));

            // adjust new velocities
            float nx = (secondBall.x - firstBall.x) / centerDistance;
            float ny = (secondBall.y - firstBall.y) / centerDistance;

            // a physics formula for ball reflection
            float kx = (firstBall.dx - secondBall.dx);
            float ky = (firstBall.dy - secondBall.dy);
            float p = (float) (2.0 * (nx * kx + ny * ky) / (firstBall.mass + secondBall.mass));
            firstBall.dx = firstBall.dx - p * secondBall.mass * nx;
            firstBall.dy = firstBall.dy - p * secondBall.mass * ny;
            secondBall.dx = secondBall.dx + p * firstBall.mass * nx;
            secondBall.dy = secondBall.dy + p * firstBall.mass * ny;
        }

        if (!gameEnded)
            invalidate();
    }

    public int getScore() {return score;}
    public boolean isGameEnded() {return gameEnded;}
    public boolean getFinalStatus() {return finalStatus; }


    public int getLevel() {
        return this.level;
    }

    public int getTopLevel(){
        return this.topLevel;
    }
}