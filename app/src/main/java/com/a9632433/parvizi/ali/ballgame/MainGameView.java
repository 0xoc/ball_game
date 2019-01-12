package com.a9632433.parvizi.ali.ballgame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.ArrayList;

import static java.lang.Math.sqrt;

public class MainGameView extends View {
    private ArrayList<RandomBall> balls = new ArrayList<>();
    private boolean firstTime = true;
    private int N;
    public int score = 0;
    public boolean gameStarted = false;

    private static Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public MainGameView(Context context, int n) {
        super(context);
        N = n;

        float minSpeed = -5.0f;
        float speedScale = 20.0f;
        float minRadius = 50.0f;
        float radiusScale = 20.0f;

        for (int i = 0 ; i < N ;i ++){
            balls.add(new RandomBall(speedScale, minSpeed, radiusScale, minRadius));
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (firstTime){
            firstTime = false;
            for (int i = 0 ;i < N;i++){
                balls.get(i).init(getWidth(), getHeight());
            }
        }

        // draw each ball
        for (int i = 0 ; i < N ;i ++){
            RandomBall ball = balls.get(i);
            paint.setARGB(ball.color.a, ball.color.r, ball.color.g, ball.color.b);
            canvas.drawCircle(ball.x, ball.y, ball.radius, paint);
        }

        invalidate();
    }

    public void reflectBalls(){
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

                        if (firstBall.isPlayer){
                            if (!gameStarted)
                                continue;
                            if (secondBall.color.equals(firstBall.color)){
                                //Toast.makeText(getContext(), "fuckersss", Toast.LENGTH_LONG).show();
                                balls.remove(secondBall);
                                score += 5;
                                continue;
                            } else {
                                Intent intent = new Intent(getContext(), Lost.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                getContext().startActivity(intent);
                                ((Activity) getContext()).finish();
                            }
                        }

                        float directionX = (firstBall.x - secondBall.x) / (float) sqrt(centersDistance);
                        float directionY = (firstBall.y - secondBall.y) / (float) sqrt(centersDistance);
                        float fDistance = (float)sqrt(centersDistance);
                        float overLap = (fDistance- firstBall.radius - secondBall.radius) * 0.5f;

                        collide.add(new BallPair(firstBall, secondBall));


                        firstBall.x -= overLap * directionX;
                        firstBall.y -= overLap * directionY;

                        secondBall.x += overLap * directionX;
                        secondBall.y += overLap * directionY;
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
    }

    @Override
    public void invalidate() {
        super.invalidate();
        reflectBalls();
        for (int i = 0; i < N; i++) {
            RandomBall ball = balls.get(i);
            if (!ball.isPlayer)
                ball.move();
        }
    }
}
