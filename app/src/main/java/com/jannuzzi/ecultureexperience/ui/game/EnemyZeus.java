package com.jannuzzi.ecultureexperience.ui.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.jannuzzi.ecultureexperience.R;

import java.util.Random;

public class EnemyZeus {
    Context context;
    Bitmap enemyZeus;
    int ex, ey;
    int enemyVelocity;
    Random random;

    public EnemyZeus(Context context) {
        this.context = context;
        enemyZeus = BitmapFactory.decodeResource(context.getResources(), R.drawable.zeus);
        random = new Random();
        ex = 200 + random.nextInt(400);
        ey = 0;
        enemyVelocity = 14 + random.nextInt(10);
    }

    public Bitmap getEnemyZeus(){
        return enemyZeus;
    }

    int getEnemyZeusWidth(){
        return enemyZeus.getWidth();
    }

    int getEnemyZeusHeight(){
        return enemyZeus.getHeight();
    }
}
