package com.example.lib;

public class Game {
    private final Player player1;
    private final Player player2;
    private Player current;
    private Player other;
    public Game(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.current = player1;
        this.other = player2;
    }
    public void start() {
        player1.getField().placeShipsRandomly();
        player2.getField().placeShipsRandomly();
        System.out.println("=== Sea Battle ===");
        System.out.println("# - your ship, X - hit, o - miss\n");
        while (true) {
            if (current.isHuman()) {
                printBoards(current, other);
            }
            int[] target = current.chooseTarget(other.getField());
            int row = target[0];
            int col = target[1];
            Field.ShotResult result = other.getField().shoot(row, col);
            current.notifyResult(row, col, result, other.getField());
            printResult(current, other.getField().getCell(row, col), result);
            if (other.hasLost()) {
                if (current.isHuman()) printBoards(current, other);
                System.out.println("\nWon player: " + current.getName() + "!");
                return;
            }
            if (result == Field.ShotResult.MISS) {
                switchPlayer();
            }
        }
    }
    private void switchPlayer() {
        Player tmp = current;
        current = other;
        other = tmp;
    }
    private void printResult(Player shooter, Cell cell, Field.ShotResult result) {
        String text;
        switch (result) {
            case MISS: text = "miss"; break;
            case HIT: text = "hit!"; break;
            case SUNK: text = "ship is down!"; break;
            default: text = "hitted yet"; break;
        }
        System.out.println(shooter.getName() + " shoot in  " + cell + ": " + text + "\n");
    }
    /** Печатает два поля рядом: своё и поле противника (корабли скрыты). */
    private void printBoards(Player me, Player enemy) {
        String[] own = me.getField().render(false);
        String[] foe = enemy.getField().render(true);
        System.out.printf("%-26s%s%n", " YOUR FIELD", " ENEMY FIELD");
        for (int i = 0; i < own.length; i++) {
            System.out.printf("%-26s%s%n", own[i], foe[i]);
        }
        System.out.println();
    }
}