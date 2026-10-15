package com.example.lib;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import java.util.Scanner;
/**
 * Игрок: человек (ввод с консоли) или компьютер (случайные выстрелы + добивание).
 */
public class Player {
    private final String name;
    private final boolean human;
    private final Scanner scanner;
    private final Field field = new Field();
    // Для компьютера
    private final Random random = new Random();
    private final Deque<int[]> targets = new ArrayDeque<>();
    private Player(String name, boolean human, Scanner scanner) {
        this.name = name;
        this.human = human;
        this.scanner = scanner;
    }
    public static Player human(String name, Scanner scanner) {
        return new Player(name, true, scanner);
    }
    public static Player computer(String name) {
        return new Player(name, false, null);
    }
    public String getName() { return name; }
    public boolean isHuman() { return human; }
    public Field getField() { return field; }
    public boolean hasLost() {
        return field.allShipsSunk();
    }
    /** Выбор клетки для выстрела по полю противника. Возвращает {row, col}. */
    public int[] chooseTarget(Field enemy) {
        return human ? askHuman(enemy) : chooseComputer(enemy);
    }
    /** Сообщить игроку результат его выстрела (нужно компьютеру для добивания). */
    public void notifyResult(int row, int col, Field.ShotResult result, Field enemy) {
        if (human) return;
        if (result == Field.ShotResult.SUNK) {
            targets.clear();
        } else if (result == Field.ShotResult.HIT) {
            int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
            for (int[] d : dirs) {
                int nr = row + d[0];
                int nc = col + d[1];
                if (enemy.inBounds(nr, nc) && !enemy.getCell(nr, nc).isShot()) {
                    targets.push(new int[]{nr, nc});
                }
            }
        }
    }
    // ---------- Человек ----------
    private int[] askHuman(Field enemy) {
        while (true) {
            System.out.print(name + ", enter coordinates to shoot (for exmp, B5): ");
            String line = scanner.nextLine().trim().toUpperCase();
            int[] target = parse(line);
            if (target == null) {
                System.out.println("Incorrect enter. Letter A-J and number 1-10.");
                continue;
            }
            if (enemy.getCell(target[0], target[1]).isShot()) {
                System.out.println("You hitted this cell yet.");
                continue;
            }
            return target;
        }
    }
    private int[] parse(String s) {
        if (s.length() < 2 || s.length() > 3) return null;
        char letter = s.charAt(0);
        if (letter < 'A' || letter >= 'A' + Field.SIZE) return null;
        try {
            int number = Integer.parseInt(s.substring(1));
            if (number < 1 || number > Field.SIZE) return null;
            return new int[]{number - 1, letter - 'A'};
        } catch (NumberFormatException e) {
            return null;
        }
    }
    // ---------- Компьютер ----------
    private int[] chooseComputer(Field enemy) {
        // Сначала добиваем найденный корабль
        while (!targets.isEmpty()) {
            int[] t = targets.pop();
            if (!enemy.getCell(t[0], t[1]).isShot()) {
                return t;
            }
        }
        // Иначе стреляем случайно
        while (true) {
            int r = random.nextInt(Field.SIZE);
            int c = random.nextInt(Field.SIZE);
            if (!enemy.getCell(r, c).isShot()) {
                return new int[]{r, c};
            }
        }
    }
}
