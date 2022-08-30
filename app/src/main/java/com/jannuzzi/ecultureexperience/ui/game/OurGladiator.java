package com.jannuzzi.ecultureexperience.ui.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.jannuzzi.ecultureexperience.R;

import java.util.Random;

public class OurGladiator {
    Context context;
    Bitmap ourGladiator;
    int ox, oy;
    Random random;

    public OurGladiator(Context context) {
        this.context = context;
        ourGladiator = BitmapFactory.decodeResource(context.getResources(), R.drawable.gladia);
        random = new Random();
        ox = random.nextInt(LightningShooter.screenWidth);
        oy = LightningShooter.screenHeight - ourGladiator.getHeight();
    }

    public Bitmap getOurGladiator(){
        return ourGladiator;
    }

    int getOurGladiatorWidth(){
        return ourGladiator.getWidth();
    }
}
