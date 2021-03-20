package com.example.explodingthings.utils;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.HashMap;

/**
 * Clase adapter para la lista de la clase GameList
 */
public class GameArrayAdapter extends ArrayAdapter<String> {

    //Guarda un Hashmap con <idLobby,idUser>
    HashMap<String, Integer> mIdMap = new HashMap<>();

    public GameArrayAdapter(Context context, int layoutResourceId, int textViewResourceId,
                            String[] objects, int[] objectsId) {
        super(context, layoutResourceId, textViewResourceId, objects);
        for (int i = 0; i < objects.length; ++i) {
            mIdMap.put(objects[i], objectsId[i]);
        }
    }

    @Override
    public long getItemId(int position) {
        String item = getItem(position);
        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
