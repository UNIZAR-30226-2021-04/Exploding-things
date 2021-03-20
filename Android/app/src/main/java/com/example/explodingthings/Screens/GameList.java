package com.example.explodingthings.Screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.explodingthings.APIConnection.APIConnection;
import com.example.explodingthings.R;

public class GameList extends AppCompatActivity {

    private APIConnection api;

    private SwipeRefreshLayout swipe;
    private ListView mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);
        api = new APIConnection(this);
        mList = findViewById(R.id.list);
        swipe = findViewById(R.id.swipeLayout);
        fillData();

        swipe.setOnRefreshListener(() -> {
            //api.gameListRequest();
            swipe.setRefreshing(false);
        });
    }

    private void fillData(){
        String[] val = new String[]{"pepe","jose","julian"};

        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<>(this, R.layout.games_list_row, R.id.idGame, val);
        mList.setAdapter(itemsAdapter);
    }
}