package com.example.explodingthings.screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.explodingthings.APIConnection.APIConnection;
import com.example.explodingthings.R;

public class GameList extends AppCompatActivity {

    private APIConnection api;

    private SwipeRefreshLayout swipe;
    private ListView mList;

    private int id_lobby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);
        api = new APIConnection(this);
        mList = findViewById(R.id.list);
        swipe = findViewById(R.id.swipeLayout);
        swipe.setRefreshing(true);

        swipe.setOnRefreshListener(() -> {
            api.gameListRequest(this);
        });

        mList.setOnItemClickListener((parent, view, position, id_lobby) -> {
            this.id_lobby = (int)id_lobby;
            api.joinLobbyRequest(10,(int)id_lobby,this);
        });
    }

    public void fillData(String[] lobbyList, int[] userList){
        // Tenemos que meter el numero de usuarios para que se vea en la lista
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(this,
                R.layout.games_list_row,R.id.idGame,lobbyList);
        mList.setAdapter(itemsAdapter);
        swipe.setRefreshing(false);
    }

    public void joinLobby(){
        Intent intent = new Intent(this, Lobby.class);
        intent.putExtra("id_lobby",id_lobby);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        api.gameListRequest(this);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent intent) {
        super.onActivityResult (requestCode, resultCode, intent);
        api.gameListRequest(this);
    }
}