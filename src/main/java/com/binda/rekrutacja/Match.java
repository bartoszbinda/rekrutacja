package com.binda.rekrutacja;

import java.time.Instant;
import java.util.Objects;

public final class Match {

    private final String homeTeam;
    private final String awayTeam;
    private final int homeScore;
    private final int awayScore;
    private final Instant startedAt;

    public Match(String homeTeam, String awayTeam) {
        validate(homeTeam, "Home team");
        validate(awayTeam, "Away team");
        this.homeTeam = homeTeam.trim();
        this.awayTeam = awayTeam.trim();
        this.homeScore = 0;
        this.awayScore = 0;
        this.startedAt = Instant.now();
    }

    private Match(String homeTeam, String awayTeam, int homeScore, int awayScore, Instant startedAt) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.startedAt = startedAt;
    }

    public Match withScore(int newHomeScore, int newAwayScore) {
        if (newHomeScore < 0 || newAwayScore < 0) {
            throw new IllegalArgumentException("Score cannot be negative");
        }
        return new Match(homeTeam, awayTeam, newHomeScore, newAwayScore, startedAt);
    }

    public int totalScore() {
        return homeScore + awayScore;
    }

    public String homeTeam()  { return homeTeam; }
    public String awayTeam()  { return awayTeam; }
    public int homeScore()    { return homeScore; }
    public int awayScore()    { return awayScore; }
    public Instant startedAt(){ return startedAt; }

    private static void validate(String team, String label) {
        if (team == null || team.isBlank()) {
            throw new IllegalArgumentException(label + " name cannot be blank");
        }
    }

    @Override
    public String toString() {
        return "%s %d - %s %d".formatted(homeTeam, homeScore, awayTeam, awayScore);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Match match = (Match) o;
        return homeScore == match.homeScore && awayScore == match.awayScore && Objects.equals(homeTeam, match.homeTeam) && Objects.equals(awayTeam, match.awayTeam) && Objects.equals(startedAt, match.startedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(homeTeam, awayTeam, homeScore, awayScore, startedAt);
    }
}
