package com.example.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Scanner;

/**
 * Игровой цикл. При попадании игрок ходит снова, при промахе ход переходит.
 * Партию можно сохранить командой save и продолжить позже.
 */
public class Game implements Serializable {

    public static final String SAVE_FILE = "savegame.dat";

    private final Player human;
    private final AIPlayer ai;
    private boolean humanTurn = true;
    private boolean over;

    public Game(Player human, AIPlayer ai) {
        this.human = human;
        this.ai = ai;
    }

    /** Расстановка кораблей обоими игроками. */
    public void setup() {
        human.placeShips();
        ai.placeShips();
    }

    public void play() {
        System.out.println("\n=== SEA BATTLE ===");
        System.out.println("# - ship, X - hit, o - miss\n");

        while (!over) {
            Player current = humanTurn ? human : ai;
            Player enemy = humanTurn ? ai : human;

            if (humanTurn) {
                printBoards(false);
            }

            int[] target = current.chooseTarget(enemy.getField(), this::saveGame);

            if (current.hasSurrendered()) {
                System.out.println("\n" + current.getName() + " gave up. Won "
                        + enemy.getName() + "!");
                over = true;
                break;
            }

            ShotResult result;
            try {
                result = enemy.getField().shoot(target[0], target[1]);
            } catch (RepeatedShotException e) {
                System.out.println(e.getMessage() + " Choose another cell.\n");
                continue;
            }

            current.recordShot(result);
            System.out.println(current.getName() + " shoot in "
                    + enemy.getField().getCell(target[0], target[1])
                    + ": " + result.getMessage() + "!\n");

            if (enemy.getField().allShipsSunk()) {
                over = true;
                System.out.println("Won " + current.getName() + "!\n");
                break;
            }
            if (result == ShotResult.MISS) {
                humanTurn = !humanTurn;
            }
        }

        printBoards(true);
        printStats();
        new File(SAVE_FILE).delete();
    }

    // ---------- Сохранение ----------

    public void saveGame() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            out.writeObject(this);
            System.out.println("Game saved in " + SAVE_FILE + ".");
        } catch (IOException e) {
            System.out.println("Cant save game: " + e.getMessage());
        }
    }

    public static Game load(Scanner scanner) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            Game game = (Game) in.readObject();
            game.human.setScanner(scanner);
            return game;
        }
    }

    public static boolean saveExists() {
        return new File(SAVE_FILE).exists();
    }

    // ---------- Вывод ----------

    /** Два поля рядом: ваше и поле противника. reveal = true показывает все корабли. */
    private void printBoards(boolean reveal) {
        String[] own = human.getField().render(false);
        String[] foe = ai.getField().render(!reveal);

        System.out.printf("%-26s%s%n", "      YOUR FIELD", "     ENEMY FIELD");
        for (int i = 0; i < own.length; i++) {
            System.out.printf("%-26s%s%n", own[i], foe[i]);
        }
        System.out.println();
    }

    private void printStats() {
        System.out.println("=== STATS ===");
        for (Player p : new Player[]{human, ai}) {
            System.out.printf("%s: shots %d, hits %d, accuracy %.1f%%%n",
                    p.getName(), p.getShots(), p.getHits(), p.getAccuracy());
        }
    }
}
