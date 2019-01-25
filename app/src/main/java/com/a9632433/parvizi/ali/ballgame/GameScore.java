package com.a9632433.parvizi.ali.ballgame;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

public class GameScore implements Serializable {
    public int level;
    public int score;

    public GameScore(int level, int score) {this.level = level; this.score = score;}

    static class SortScoreByLevel implements Comparator<GameScore> {

        @Override
        public int compare(GameScore o1, GameScore o2) {
            return o1.level - o2.level;
        }
    }

    public static class SortScoreByScore implements Comparator<GameScore> {
        @Override
        public int compare(GameScore o1, GameScore o2) {
            return o2.score - o1.score;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameScore gameScore = (GameScore) o;
        return level == gameScore.level &&
                score == gameScore.score;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {

        return Objects.hash(level, score);
    }
}
