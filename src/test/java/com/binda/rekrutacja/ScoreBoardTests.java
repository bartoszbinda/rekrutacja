package com.binda.rekrutacja;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ScoreBoardTests {

    private ScoreBoard board;

    @BeforeEach
    public void setUp() {
        board = new ScoreBoard();
    }


    @Test
    public void startGame_initialScoreIsZeroZero() {
        board.startGame("Mexico", "Canada");
        List<Match> summary = board.getSummary();
        assertThat(summary.size()).as("Expected 1 match").isEqualTo(1);
        assertThat(summary.get(0).homeScore()).as("Home score should be 0").isEqualTo(0);
        assertThat(summary.get(0).awayScore()).as("Away score should be 0").isEqualTo(0);
    }

    @Test
    public void startGame_capturesTeamNames() {
        board.startGame("Mexico", "Canada");
        Match m = board.getSummary().get(0);
        assertEquals("Mexico", m.homeTeam());
        assertEquals("Canada", m.awayTeam());
    }

    @Test
    public void startGame_trimsTeamNames() {
        board.startGame("  Mexico  ", "  Canada  ");
        Match m = board.getSummary().get(0);
        assertEquals("Mexico", m.homeTeam());
        assertEquals("Canada", m.awayTeam());
    }

    @Test
    public void startGame_throwsOnDuplicate() {
        board.startGame("Mexico", "Canada");
        assertThrows(IllegalStateException.class, () -> board.startGame("Mexico", "Canada"));
    }

    @Test
    public void startGame_throwsOnBlankHomeName() {
        assertThrows(IllegalArgumentException.class, () -> board.startGame("  ", "Canada"));
    }

    @Test
    public void startGame_throwsOnNullAwayName() {
        assertThrows(IllegalArgumentException.class, () -> board.startGame("Mexico", null));
    }

    @Test
    public void finishGame_removesMatchFromBoard() {
        board.startGame("Mexico", "Canada");
        board.finishGame("Mexico", "Canada");
        assertThat(board.getSummary()).as("Board should be empty after finish").isEmpty();
    }

    @Test
    public void finishGame_throwsWhenMatchNotFound() {
        assertThrows(IllegalArgumentException.class, () -> board.finishGame("Mexico", "Canada"));
    }

    @Test
    public void finishGame_doesNotAffectOtherMatches() {
        board.startGame("Mexico", "Canada");
        board.startGame("Spain",  "Brazil");
        board.finishGame("Mexico", "Canada");
        List<Match> summary = board.getSummary();
        assertThat(summary).as("Should have 1 match remaining").hasSize(1);
        assertEquals("Spain", summary.get(0).homeTeam());
    }


    @Test
    public void updateScore_updatesExistingMatch() {
        board.startGame("Mexico", "Canada");
        board.updateScore("Mexico", "Canada", 0, 5);
        Match m = board.getSummary().get(0);
        assertThat(m.homeScore()).as("Home score should be 0").isEqualTo(0);
        assertThat(m.awayScore()).as("Away score should be 5").isEqualTo(5);
    }

    @Test
    public void updateScore_throwsWhenMatchNotFound() {
        assertThrows(IllegalArgumentException.class, () -> board.updateScore("Mexico", "Canada", 1, 0));
    }

    @Test
    public void updateScore_throwsOnNegativeScore() {
        board.startGame("Mexico", "Canada");
        assertThrows(IllegalArgumentException.class, () -> board.updateScore("Mexico", "Canada", -1, 0));
    }

    @Test
    public void updateScore_allowsMultipleUpdates() {
        board.startGame("Mexico", "Canada");
        board.updateScore("Mexico", "Canada", 1, 0);
        board.updateScore("Mexico", "Canada", 2, 1);
        Match m = board.getSummary().get(0);
        assertThat(m.homeScore()).as("Home should be 2").isEqualTo(2);
        assertThat(m.awayScore()).as("Away should be 1").isEqualTo(1);
    }


    @Test
    public void getSummary_emptyWhenNoMatches() {
        assertThat(board.getSummary()).as("Should be empty").isEmpty();
    }

    @Test
    public void getSummary_ordersByTotalScoreDescending() {
        board.startGame("Germany", "France");
        board.startGame("Spain",   "Brazil");
        board.updateScore("Germany", "France", 2, 2);
        board.updateScore("Spain",   "Brazil", 10, 2);
        List<Match> summary = board.getSummary();
        assertEquals("Spain",   summary.get(0).homeTeam());
        assertEquals("Germany", summary.get(1).homeTeam());
    }

    @Test
    public void getSummary_tiesBrokenByMostRecentlyStarted() throws InterruptedException {
        board.startGame("Germany", "France");
        Thread.sleep(5);
        board.startGame("Spain", "Brazil");
        board.updateScore("Germany", "France", 2, 2);
        board.updateScore("Spain",   "Brazil",  3, 1);
        List<Match> summary = board.getSummary();
        assertEquals("Spain",   summary.get(0).homeTeam());
        assertEquals("Germany", summary.get(1).homeTeam());
    }

    @Test
    public void getSummary_fullRequirementsExample() throws InterruptedException {
        board.startGame("Mexico",    "Canada");    Thread.sleep(5);
        board.startGame("Spain",     "Brazil");    Thread.sleep(5);
        board.startGame("Germany",   "France");    Thread.sleep(5);
        board.startGame("Uruguay",   "Italy");     Thread.sleep(5);
        board.startGame("Argentina", "Australia");

        board.updateScore("Mexico",    "Canada",    0,  5);
        board.updateScore("Spain",     "Brazil",    10, 2);
        board.updateScore("Germany",   "France",    2,  2);
        board.updateScore("Uruguay",   "Italy",     6,  6);
        board.updateScore("Argentina", "Australia", 3,  1);

        List<Match> summary = board.getSummary();

        assertEquals("Uruguay 6 - Italy 6",       summary.get(0).toString());
        assertEquals("Spain 10 - Brazil 2",       summary.get(1).toString());
        assertEquals("Mexico 0 - Canada 5",       summary.get(2).toString());
        assertEquals("Argentina 3 - Australia 1", summary.get(3).toString());
        assertEquals("Germany 2 - France 2",      summary.get(4).toString());
    }

    @Test
    public void getSummary_returnsUnmodifiableList() {
        board.startGame("Mexico", "Canada");
        List<Match> summary = board.getSummary();
        assertThrows(UnsupportedOperationException.class, () -> summary.add(new Match("X", "Y")));
    }


}
