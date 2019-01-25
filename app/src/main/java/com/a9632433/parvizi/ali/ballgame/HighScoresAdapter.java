package com.a9632433.parvizi.ali.ballgame;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class HighScoresAdapter extends RecyclerView.Adapter<HighScoresAdapter.ViewHolder> {

    private ArrayList<Integer> scores;

    public HighScoresAdapter(ArrayList<Integer> scores) {

            this.scores = scores;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.high_score_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.score.setText(scores.get(position) + "");
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView score;

        public ViewHolder(View itemView) {
            super(itemView);
            score = itemView.findViewById(R.id.scoreText);
        }
    }
}
