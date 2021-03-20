package com.example.explodingthings.Screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.explodingthings.APIConnection.APIConnection;
import com.example.explodingthings.R;
import com.example.explodingthings.utils.GameArrayAdapter;

import java.util.ArrayList;

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
        api.gameListRequest(this);
        swipe.setRefreshing(true);

        swipe.setOnRefreshListener(() -> {
            api.gameListRequest(this);
        });

        mList.setOnItemClickListener((parent, view, position, id_lobby) -> {
            /*
            Intent intent = new Intent(this, Lobby.class);
            intent.putExtra("id_user",id_lobby);
            startActivity(intent);
             */
            Toast.makeText(this, id_lobby+"", Toast.LENGTH_SHORT).show();
        });
    }

    public void fillData(String[] userList, int[] lobbyList){
        GameArrayAdapter itemsAdapter =
                new GameArrayAdapter(this, R.layout.games_list_row, R.id.idGame, userList, lobbyList);
        mList.setAdapter(itemsAdapter);
        swipe.setRefreshing(false);
    }
}