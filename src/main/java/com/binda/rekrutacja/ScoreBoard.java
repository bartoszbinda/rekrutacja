package com.binda.rekrutacja;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ScoreBoard {

    private final Map<String, Match> matches = new LinkedHashMap<>();

    public void startGame(String homeTeam, String awayTeam) {
        String key = matchKey(homeTeam, awayTeam);
        if (matches.containsKey(key)) {
            throw new IllegalStateException(
                "Match between " + homeTeam + " and " + awayTeam + " is already in progress"
            );
        }
        matches.put(key, new Match(homeTeam, awayTeam));
    }

    public void finishGame(String homeTeam, String awayTeam) {
        String key = matchKey(homeTeam, awayTeam);
        if (!matches.containsKey(key)) {
            throw new IllegalArgumentException(
                "No active match found for " + homeTeam + " vs " + awayTeam
            );
        }
        matches.remove(key);
    }

    public void updateScore(String homeTeam, String awayTeam, int homeScore, int awayScore) {
        String key = matchKey(homeTeam, awayTeam);
        Match existing = matches.get(key);
        if (existing == null) {
            throw new IllegalArgumentException(
                "No active match found for " + homeTeam + " vs " + awayTeam
            );
        }
        matches.put(key, existing.withScore(homeScore, awayScore));
    }

    public List<Match> getSummary() {
        return matches.values().stream()
            .sorted(
                Comparator.comparingInt(Match::totalScore).reversed()
                    .thenComparing(Comparator.comparing(Match::startedAt).reversed())
            )
            .toList();
    }

    private String matchKey(String homeTeam, String awayTeam) {
        if (homeTeam == null || awayTeam == null) {
            throw new IllegalArgumentException("Team names cannot be null");
        }
        return homeTeam.trim().toLowerCase() + "_vs_" + awayTeam.trim().toLowerCase();
    }
}
