package com.a9632433.parvizi.ali.ballgame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class MainGameView extends View {
    private ArrayList<RandomBall> balls = new ArrayList<>();
    private boolean firstTime = true;
    private int N;
    public int score = 0;
    public boolean gameStarted = false;
    int timeElapsed = 0;
    RandomBall player;

    private static Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    boolean playerPicked = false;

    public MainGameView(Context context, int n) {
        super(context);
        N = n;

        float minSpeed = 1.0f;
        float speedScale = 5.0f;
        float minRadius = 30.0f;
        float radiusScale = 50.0f;


        for (int i = 0 ; i < N ;i ++){
            balls.add(new RandomBall(speedScale, minSpeed, radiusScale, minRadius));
        }

        player = new RandomBall(0.0f, 0.0f, 1.0f, 120.0f);
        player.isPlayer = true;
        balls.add(player);

        N = balls.size();
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeElapsed ++;
                if (timeElapsed % 5 == 0){
                    player.color = new ARGBColor();
                }

                if (gameStarted)
                    score++;
            }
        },0,1000);

        OnTouchListener listener = new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gameStarted = true;

                if (playerPicked){
                    player.x = event.getX();
                    player.y = event.getY();
                }

                if (event.getAction() == MotionEvent.ACTION_DOWN){

                    if (isSelected(event.getX(), event.getY(), player))
                        playerPicked = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP)
                    playerPicked = false;

                return true;
            }
        };
        setOnTouchListener(listener);
    }

    boolean isSelected(float x, float y, RandomBall ball){
        float distance = (float) sqrt(pow(ball.x - x,2) + pow(ball.y - y, 2));
        return (distance < ball.radius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (firstTime){
            firstTime = false;
            for (int i = 0 ;i < N;i++)
                balls.get(i).init(getWidth(), getHeight());
            player.init(getWidth(), getHeight());
        }

        // draw each ball
        for (int i = 0 ; i < balls.size() ;i ++){
            RandomBall ball = balls.get(i);
            paint.setARGB(ball.color.a, ball.color.r, ball.color.g, ball.color.b);
            canvas.drawCircle(ball.x, ball.y, ball.radius, paint);

            // draw player
            if (ball.isPlayer) {
                paint.setARGB(ball.color.a, ball.color.r, ball.color.g, ball.color.b);
                canvas.drawCircle(ball.x, ball.y, ball.radius, paint);

                paint.setColor(Color.WHITE);
                paint.setTextSize(60);
                canvas.drawText("PLAYER", ball.x - ball.radius + 10, ball.y + ball.radius / 4, paint);
            }
        }


        ArrayList<BallPair> collide = new ArrayList<BallPair>();

        // set balls reflection
        for (int i = 0 ; i < balls.size() ; i++) {
            RandomBall firstBall = balls.get(i);
            for (int j = 0 ; j < balls.size() ; j++) {
                RandomBall secondBall = balls.get(j);

                if (firstBall.id != secondBall.id){
                    float centersDistance = (firstBall.x - secondBall.x) * (firstBall.x - secondBall.x) + (firstBall.y - secondBall.y) * (firstBall.y - secondBall.y);
                    float radiusDistance = (firstBall.radius + secondBall.radius) * (firstBall.radius + secondBall.radius);
                    if (centersDistance <= radiusDistance){


                        float directionX = (firstBall.x - secondBall.x) / (float) sqrt(centersDistance);
                        float directionY = (firstBall.y - secondBall.y) / (float) sqrt(centersDistance);
                        float fDistance = (float)sqrt(centersDistance);
                        float overLap = (fDistance- firstBall.radius - secondBall.radius) * 0.5f;

                        firstBall.x -= overLap * directionX;
                        firstBall.y -= overLap * directionY;

                        secondBall.x += overLap * directionX;
                        secondBall.y += overLap * directionY;

                        if (firstBall.isPlayer){
                            if (!gameStarted)
                                continue;
                            if (secondBall.color.equals(firstBall.color)){
                                Toast.makeText(getContext(), "same color", Toast.LENGTH_LONG).show();
                                balls.remove(secondBall);
                                score += 5;
                                continue;
                            } else {
                                Toast.makeText(getContext(), "lost", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getContext(), Lost.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                getContext().startActivity(intent);
                                ((Activity) getContext()).finish();
                            }
                        }

                        collide.add(new BallPair(firstBall, secondBall));

                    }
                }
            }
        }

        for (int i = 0 ;i < collide.size(); i++){

            RandomBall firstBall = collide.get(i).firstBall;
            RandomBall secondBall = collide.get(i).secondBall;

            float centersDistance = (firstBall.x - secondBall.x) * (firstBall.x - secondBall.x) + (firstBall.y - secondBall.y) * (firstBall.y - secondBall.y);
            float radiusDistance = (firstBall.radius + secondBall.radius) * (firstBall.radius + secondBall.radius);
            float fDistance = (float)sqrt(centersDistance);

            // Normal
            float nx = (firstBall.x - secondBall.x) / fDistance;
            float ny = (firstBall.y - secondBall.y) / fDistance;

            // Tangent
            float tx = - ny;
            float ty = nx;

            // dot product tangent
            float dpTan1 = firstBall.dx * tx + firstBall.dy * ty;
            float dpTan2 = secondBall.dx * tx + secondBall.dy* ty;

            // dot product normal

            float dpNorm1 = firstBall.dx * nx + firstBall.dy * ny;
            float dpNorm2 = secondBall.dx * nx + secondBall.dy * ny;

            // conservation of momentum

            float m1 = (dpNorm1 * (firstBall.radius - secondBall.radius) + 2.0f * secondBall.radius * dpNorm2) / (firstBall.radius + secondBall.radius);
            float m2 = (dpNorm2 * (secondBall.radius - firstBall.radius) + 2.0f * firstBall.radius * dpNorm1)/ (firstBall.radius + secondBall.radius);

            // update ball speed

            firstBall.dx = tx * dpTan1 + nx*m1;
            firstBall.dy = ty * dpTan1 + ny*m1;

            secondBall.dx = tx * dpTan2 + nx * m2;
            secondBall.dy = ty * dpTan2 + ny * m2;
        }
        player.boundaryCheck();
        invalidate();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        for (int i = 0; i < balls.size(); i++) {
            RandomBall ball = balls.get(i);
            if (!ball.isPlayer)
                ball.move();
        }
    }
}
