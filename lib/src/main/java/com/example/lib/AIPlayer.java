package com.example.lib;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Бот. Для каждого хода строит карту вероятностей: перебирает все возможные
 * положения оставшихся кораблей и считает, через какие клетки они проходят.
 * Если есть раненый корабль, учитываются только положения, проходящие через
 * раненые клетки (добивание), причём чем больше попаданий покрыто, тем выше вес.
 */
public class AIPlayer extends Player {

    private static final Random RANDOM = new Random();

    public AIPlayer(String name) {
        super(name, null);
    }

    @Override
    public boolean isHuman() { return false; }

    @Override
    public void placeShips() {
        getField().placeShipsRandomly();
    }

    @Override
    public int[] chooseTarget(Field enemy, Runnable saveAction) {
        int[][] map = probabilityMap(enemy);

        int best = -1;
        List<int[]> candidates = new ArrayList<>();
        for (int r = 0; r < Field.SIZE; r++) {
            for (int c = 0; c < Field.SIZE; c++) {
                if (enemy.getCell(r, c).isShot()) continue;
                if (map[r][c] > best) {
                    best = map[r][c];
                    candidates.clear();
                    candidates.add(new int[]{r, c});
                } else if (map[r][c] == best) {
                    candidates.add(new int[]{r, c});
                }
            }
        }
        return candidates.get(RANDOM.nextInt(candidates.size()));
    }

    /** Карта вероятностей: чем больше число, тем вероятнее, что в клетке корабль. */
    public int[][] probabilityMap(Field enemy) {
        boolean hasWounded = false;
        for (int r = 0; r < Field.SIZE && !hasWounded; r++) {
            for (int c = 0; c < Field.SIZE; c++) {
                if (enemy.getCell(r, c).getState() == Cell.State.HIT) {
                    hasWounded = true;
                    break;
                }
            }
        }

        int[][] map = buildMap(enemy, hasWounded);
        if (hasWounded && isEmpty(map)) {
            map = buildMap(enemy, false);
        }
        return map;
    }

    private int[][] buildMap(Field enemy, boolean targetMode) {
        int[][] map = new int[Field.SIZE][Field.SIZE];

        // Размеры неубитых кораблей известны игроку из сообщений «убил».
        for (Ship ship : enemy.getShips()) {
            if (ship.isSunk()) continue;
            int size = ship.getSize();

            for (int r = 0; r < Field.SIZE; r++) {
                for (int c = 0; c < Field.SIZE; c++) {
                    for (int o = 0; o < 2; o++) {
                        boolean horizontal = (o == 0);
                        if (!enemy.inBounds(r + (horizontal ? 0 : size - 1),
                                c + (horizontal ? size - 1 : 0))) {
                            continue;
                        }

                        int woundedCovered = 0;
                        boolean possible = true;
                        for (int i = 0; i < size; i++) {
                            Cell.State s = enemy.getCell(r + (horizontal ? 0 : i),
                                    c + (horizontal ? i : 0)).getState();
                            if (s == Cell.State.MISS || s == Cell.State.SUNK) {
                                possible = false;
                                break;
                            }
                            if (s == Cell.State.HIT) woundedCovered++;
                        }
                        if (!possible) continue;
                        if (targetMode && woundedCovered == 0) continue;

                        int weight = targetMode ? 1 + 10 * woundedCovered : 1;
                        for (int i = 0; i < size; i++) {
                            int rr = r + (horizontal ? 0 : i);
                            int cc = c + (horizontal ? i : 0);
                            if (!enemy.getCell(rr, cc).isShot()) {
                                map[rr][cc] += weight;
                            }
                        }
                    }
                }
            }
        }
        return map;
    }

    private boolean isEmpty(int[][] map) {
        for (int[] row : map) {
            for (int v : row) {
                if (v > 0) return false;
            }
        }
        return true;
    }
}
