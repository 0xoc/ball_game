package com.a9632433.parvizi.ali.ballgame;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class LevelSelectorListAdapter extends ArrayAdapter {
    private int topLevel;
    private int layoutId;
    public LevelSelectorListAdapter(@NonNull Context context, int resource, int topLevel) {
        super(context, resource);
        this.topLevel = topLevel;
        layoutId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(layoutId,parent,false);

        TextView levelText = convertView.findViewById(R.id.levelText);
        levelText.setText(position + 1 + "");

        return convertView;
    }

    @Override
    public int getCount() {
        return topLevel;
    }
}
