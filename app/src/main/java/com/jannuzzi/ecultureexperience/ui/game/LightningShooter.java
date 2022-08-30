package com.jannuzzi.ecultureexperience.ui.game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import com.jannuzzi.ecultureexperience.R;

import java.util.ArrayList;
import java.util.Random;

public class LightningShooter extends View {
    Context context;
    Bitmap background, lifeImage;
    Handler handler;
    long UPDATE_MILLIS = 30;
    static int screenWidth, screenHeight;
    int points = 0;
    int life = 3;
    Paint scorePaint;
    int TEXT_SIZE = 80;
    boolean paused = false;
    OurGladiator ourGladiator;
    EnemyZeus enemyZeus;
    Random random;
    ArrayList<Shot> enemyShots, ourShots;
    Explosion explosion;
    ArrayList<Explosion> explosions;
    boolean enemyShotAction = false;
    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
           invalidate();
        }
    };


    public LightningShooter(Context context) {
        super(context);
        this.context = context;
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        random = new Random();
        enemyShots = new ArrayList<>();
        ourShots = new ArrayList<>();
        explosions = new ArrayList<>();
        ourGladiator = new OurGladiator(context);
        enemyZeus = new EnemyZeus(context);
        handler = new Handler();
        background = BitmapFactory.decodeResource(context.getResources(), R.drawable.background_game);
        lifeImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.life);
        scorePaint = new Paint();
        scorePaint.setColor(Color.BLACK);
        scorePaint.setTextSize(TEXT_SIZE);
        scorePaint.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Draw background, Points and life on Canvas
        canvas.drawBitmap(background, 0, 0, null);
        canvas.drawText("Pt: " + points, 50, TEXT_SIZE+20, scorePaint);
        for(int i=life; i>=1; i--){
            canvas.drawBitmap(lifeImage, screenWidth- 50 - lifeImage.getWidth() * i, 45, null);
        }
        // When life becomes 0, stop game and launch GameOver Activity with points
        if(life == 0){
            paused = true;
            handler = null;
            Intent intent = new Intent(context, GameOver.class);
            intent.putExtra("points", points);
            context.startActivity(intent);
            ((Activity) context).finish();
        }
        // Move enemyZeus
        enemyZeus.ex += enemyZeus.enemyVelocity;
        // If enemyZeus collides with right wall, reverse enemyVelocity
        if(enemyZeus.ex + enemyZeus.getEnemyZeusWidth() >= screenWidth){
            enemyZeus.enemyVelocity *= -1;
        }
        // If enemyZeus collides with left wall, again reverse enemyVelocity
        if(enemyZeus.ex <=0){
            enemyZeus.enemyVelocity *= -1;
        }
        // Till enemyShotAction is false, enemy should fire shots from random travelled distance
        if(enemyShotAction == false){
            if(enemyZeus.ex >= 200 + random.nextInt(400)){
                Shot enemyShot = new Shot(context, enemyZeus.ex + enemyZeus.getEnemyZeusWidth() / 2, enemyZeus.ey );
                enemyShots.add(enemyShot);
                // We're making enemyShotAction to true so that enemy can take a short at a time
                enemyShotAction = true;
            }
            if(enemyZeus.ex >= 400 + random.nextInt(800)){
                Shot enemyShot = new Shot(context, enemyZeus.ex + enemyZeus.getEnemyZeusWidth() / 2, enemyZeus.ey );
                enemyShots.add(enemyShot);
                // We're making enemyShotAction to true so that enemy can take a short at a time
                enemyShotAction = true;
            }
            else{
                Shot enemyShot = new Shot(context, enemyZeus.ex + enemyZeus.getEnemyZeusWidth() / 2, enemyZeus.ey );
                enemyShots.add(enemyShot);
                // We're making enemyShotAction to true so that enemy can take a short at a time
                enemyShotAction = true;
            }
        }
        // Draw the enemy zeus
        canvas.drawBitmap(enemyZeus.getEnemyZeus(), enemyZeus.ex, enemyZeus.ey, null);
        // Draw our gladiator between the left and right edge of the screen
        if(ourGladiator.ox > screenWidth - ourGladiator.getOurGladiatorWidth()){
            ourGladiator.ox = screenWidth - ourGladiator.getOurGladiatorWidth();
        }else if(ourGladiator.ox < 0){
            ourGladiator.ox = 0;
        }
        // Draw our gladiator
        canvas.drawBitmap(ourGladiator.getOurGladiator(), ourGladiator.ox, ourGladiator.oy, null);
        // Draw the enemy shot downwards our gladiator and if it's being hit, decrement life, remove
        // the shot object from enemyShots ArrayList and show an explosion.
        // Else if, it goes away through the bottom edge of the screen also remove
        // the shot object from enemyShots.
        // When there is no enemyShots no the screen, change enemyShotAction to false, so that enemy
        // can shot.
        for(int i=0; i < enemyShots.size(); i++){
            enemyShots.get(i).shy += 15;
            canvas.drawBitmap(enemyShots.get(i).getShot(), enemyShots.get(i).shx, enemyShots.get(i).shy, null);
            if((enemyShots.get(i).shx >= ourGladiator.ox)
                && enemyShots.get(i).shx <= ourGladiator.ox + ourGladiator.getOurGladiatorWidth()
                && enemyShots.get(i).shy >= ourGladiator.oy
                && enemyShots.get(i).shy <= screenHeight){
                life--;
                enemyShots.remove(i);
                explosion = new Explosion(context, ourGladiator.ox, ourGladiator.oy);
                explosions.add(explosion);
            }else if(enemyShots.get(i).shy >= screenHeight){
                enemyShots.remove(i);
            }
            if(enemyShots.size() < 1){
                enemyShotAction = false;
            }
        }
        // Draw our gladiator shots towards the enemy. If there is a collision between our shot and enemy
        // zeus, increment points, remove the shot from ourShots and create a new Explosion object.
        // Else if, our shot goes away through the top edge of the screen also remove
        // the shot object from enemyShots ArrayList.
        for(int i=0; i < ourShots.size(); i++){
            ourShots.get(i).shy -= 15;
            canvas.drawBitmap(ourShots.get(i).getShot(), ourShots.get(i).shx, ourShots.get(i).shy, null);
            if((ourShots.get(i).shx >= enemyZeus.ex)
               && ourShots.get(i).shx <= enemyZeus.ex + enemyZeus.getEnemyZeusWidth()
               && ourShots.get(i).shy <= enemyZeus.getEnemyZeusWidth()
               && ourShots.get(i).shy >= enemyZeus.ey){
                points++;
                ourShots.remove(i);
                explosion = new Explosion(context, enemyZeus.ex, enemyZeus.ey);
                explosions.add(explosion);
            }else if(ourShots.get(i).shy <=0){
                ourShots.remove(i);
            }
        }
        // Do the explosion
        for(int i=0; i < explosions.size(); i++){
            canvas.drawBitmap(explosions.get(i).getExplosion(explosions.get(i).explosionFrame), explosions.get(i).eX, explosions.get(i).eY, null);
            explosions.get(i).explosionFrame++;
            if(explosions.get(i).explosionFrame > 8){
                explosions.remove(i);
            }
        }
        // If not paused, we’ll call the postDelayed() method on handler object which will cause the
        // run method inside Runnable to be executed after 30 milliseconds, that is the value inside
        // UPDATE_MILLIS.
        if(!paused)
            handler.postDelayed(runnable, UPDATE_MILLIS);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int touchX = (int)event.getX();
        // When event.getAction() is MotionEvent.ACTION_UP, if ourShots arraylist size < 1,
        // create a new Shot.
        // This way we restrict ourselves of making just one shot at a time, on the screen.
        if(event.getAction() == MotionEvent.ACTION_UP){
            if(ourShots.size() < 1){
                Shot ourShot = new Shot(context, ourGladiator.ox + ourGladiator.getOurGladiatorWidth() / 2, ourGladiator.oy);
                ourShots.add(ourShot);
            }
        }
        // When event.getAction() is MotionEvent.ACTION_DOWN, control ourGladiator
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            ourGladiator.ox = touchX;
        }
        // When event.getAction() is MotionEvent.ACTION_MOVE, control ourGladiator
        // along with the touch.
        if(event.getAction() == MotionEvent.ACTION_MOVE){
            ourGladiator.ox = touchX;
        }
        // Returning true in an onTouchEvent() tells Android system that you already handled
        // the touch event and no further handling is required.
        return true;
    }
}
