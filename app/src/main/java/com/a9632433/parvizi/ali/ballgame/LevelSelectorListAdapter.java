package com.a9632433.parvizi.ali.ballgame;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.System.exit;

public class LevelSelectorListAdapter extends RecyclerView.Adapter<LevelSelectorListAdapter.ViewHolder> {

    private int topLevel;
    Context context;
    GameInitializer initializer;

    public LevelSelectorListAdapter(int topLevel, GameInitializer initializer) {
        this.topLevel = topLevel;
        this.initializer = initializer;
    }

    @Override
    public LevelSelectorListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.level_select_item_layout, parent, false);

        // save a refrance of parent context
        this.context = parent.getContext();

        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(LevelSelectorListAdapter.ViewHolder holder, final int position) {
        holder.levelText.setText(position + 1 + "");
        holder.levelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializer.initGame(position+1);
            }
        });

        holder.levelHighScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializer.createHighLevelScorePage(position+1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return topLevel;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView levelText;
        public ImageButton levelHighScore;

        public ViewHolder(View itemView) {
            super(itemView);
            levelText = itemView.findViewById(R.id.levelText);
            levelHighScore = itemView.findViewById(R.id.levelHighScoreBtn);
        }
    }
}
