package com.example.explodingthings.Screens;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.explodingthings.R;

import java.util.ArrayList;

public class Game extends AppCompatActivity {

    private ImageButton ib;
    private RelativeLayout ll;
    private Button btn;
    private ArrayList<View> cardList;

    private int screenWidth;
    private int cardWidth = 300;
    private int cardHeight = 400;
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        cardList = new ArrayList<>();
        ll = findViewById(R.id.ll);
        btn = findViewById(R.id.button_añadir);

        btn.setOnClickListener((card) -> {
            int margin = correctCardsPosition();
            addCard(margin);
        });
    }

    private void addCard(int margin){
        int c = R.drawable.nope;
        ib = new ImageButton(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(cardWidth, cardHeight);
        params.addRule(RelativeLayout.ALIGN_LEFT, i - 1);
        params.leftMargin = margin;
        ib.setImageResource(c);
        ib.setClickable(true);
        ib.setPadding(3, 3, 3, 3);
        ib.setId(i);
        ib.setLayoutParams(params);
        ll.addView(ib);
        cardList.add(ib);
        i++;
    }

    private int correctCardsPosition(){
        int num = cardList.size();
        int margin = 0;
        if (num > 1) {
            margin = (screenWidth - cardWidth) / (num - 1);
            if (num * cardWidth < screenWidth) {
                margin = cardWidth + 10;
            }
        }
        int id;
        for(View card : cardList){
            id = card.getId();
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(cardWidth, cardHeight);
            params.addRule(RelativeLayout.ALIGN_LEFT, id - 1);
            params.leftMargin = margin;
            card.setLayoutParams(params);
        }
        return margin;
    }

}