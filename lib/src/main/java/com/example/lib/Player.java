package com.example.lib;

import java.io.Serializable;
import java.util.Scanner;

/**
 * Игрок-человек: ручная/случайная расстановка, ввод выстрелов, статистика.
 * От него наследуется AIPlayer.
 */
public class Player implements Serializable {

    private final String name;
    private final Field field = new Field();
    private transient Scanner scanner;
    private int shots;
    private int hits;
    private boolean surrendered;

    public Player(String name, Scanner scanner) {
        this.name = name;
        this.scanner = scanner;
    }

    public String getName() { return name; }

    public Field getField() { return field; }

    public void setScanner(Scanner scanner) { this.scanner = scanner; }

    public boolean isHuman() { return true; }

    public boolean hasSurrendered() { return surrendered; }

    // ---------- Расстановка ----------

    public void placeShips() {
        System.out.println("\nShip place: 1 - manual, 2 - random");
        while (true) {
            System.out.print("Your choice: ");
            String line = readLine();
            if (line == null || line.trim().equals("2")) {
                field.placeShipsRandomly();
                return;
            }
            if (line.trim().equals("1")) {
                placeManually();
                return;
            }
        }
    }

    private void placeManually() {
        System.out.println("Format: B5 H (H or Г - right, V or В - down). R - place all random.");
        for (Ship ship : Field.createFleet()) {
            while (true) {
                printOwnField();
                System.out.printf("%s (%d c.): ", ship.getName(), ship.getSize());
                String line = readLine();
                if (line == null || line.trim().equalsIgnoreCase("R")) {
                    field.placeShipsRandomly();
                    return;
                }

                String[] parts = line.trim().toUpperCase().split("\\s+");
                int[] pos = parseCoordinate(parts[0]);
                if (pos == null) {
                    System.out.println("Wrong coordinate.");
                    continue;
                }
                boolean horizontal = true;
                if (parts.length > 1) {
                    String d = parts[1];
                    if (d.equals("V") || d.equals("В")) {
                        horizontal = false;
                    } else if (!d.equals("H") && !d.equals("Г")) {
                        System.out.println("Side: H/Г (right) or V/В (down).");
                        continue;
                    }
                }
                try {
                    field.placeShip(ship, pos[0], pos[1], horizontal);
                    break;
                } catch (InvalidPlacementException e) {
                    System.out.println("Cant place like this: " + e.getMessage());
                }
            }
        }
        printOwnField();
    }

    private void printOwnField() {
        System.out.println();
        for (String line : field.render(false)) {
            System.out.println(line);
        }
        System.out.println();
    }

    // ---------- Выстрелы ----------

    /**
     * Выбор цели. Возвращает {row, col} или null, если игрок сдался.
     * Команды: save - сохранить партию, surrender - сдаться.
     */
    public int[] chooseTarget(Field enemy, Runnable saveAction) {
        while (true) {
            System.out.print(name + ", shot (for exmp B5; save - сохранить, surrender - сдаться): ");
            String line = readLine();
            if (line == null) {
                surrendered = true;
                return null;
            }
            line = line.trim().toUpperCase();

            if (line.equals("SAVE") || line.equals("СОХРАНИТЬ")) {
                saveAction.run();
                continue;
            }
            if (line.equals("SURRENDER") || line.equals("СДАТЬСЯ")) {
                surrendered = true;
                return null;
            }

            int[] target = parseCoordinate(line);
            if (target == null) {
                System.out.println("Wrong input. Letter A-J and number 1-10.");
                continue;
            }
            return target;
        }
    }

    // ---------- Статистика ----------

    public void recordShot(ShotResult result) {
        shots++;
        if (result != ShotResult.MISS) hits++;
    }

    public int getShots() { return shots; }

    public int getHits() { return hits; }

    public double getAccuracy() {
        return shots == 0 ? 0 : 100.0 * hits / shots;
    }

    // ---------- Вспомогательное ----------

    /** "B5" -> {4, 1}; null при неверном формате. */
    public static int[] parseCoordinate(String s) {
        if (s == null || s.length() < 2 || s.length() > 3) return null;
        char letter = Character.toUpperCase(s.charAt(0));
        if (letter < 'A' || letter >= 'A' + Field.SIZE) return null;
        try {
            int number = Integer.parseInt(s.substring(1));
            if (number < 1 || number > Field.SIZE) return null;
            return new int[]{number - 1, letter - 'A'};
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String readLine() {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }
}
