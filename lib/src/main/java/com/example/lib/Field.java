package com.example.lib;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class Field {
    public static final int SIZE = 10;
    public static final int[] SHIP_SIZES = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};
    public enum ShotResult { MISS, HIT, SUNK, ALREADY_SHOT }
    private final Cell[][] cells = new Cell[SIZE][SIZE];
    private final List<Ship> ships = new ArrayList<>();
    private final Random random = new Random();
    public Field() {
        clear();
    }
    public void clear() {
        ships.clear();
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                cells[r][c] = new Cell(r, c);
            }
        }
    }
    public boolean inBounds(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }
    public Cell getCell(int row, int col) {
        return cells[row][col];
    }
    public List<Ship> getShips() {
        return ships;
    }
    public void placeShipsRandomly() {
        while (true) {
            clear();
            boolean ok = true;
            for (int size : SHIP_SIZES) {
                if (!tryPlaceShip(size)) {
                    ok = false;
                    break;
                }
            }
            if (ok) return;
        }
    }
    private boolean tryPlaceShip(int size) {
        for (int attempt = 0; attempt < 200; attempt++) {
            boolean horizontal = random.nextBoolean();
            int row = random.nextInt(SIZE);
            int col = random.nextInt(SIZE);
            int endRow = row + (horizontal ? 0 : size - 1);
            int endCol = col + (horizontal ? size - 1 : 0);
            if (!inBounds(endRow, endCol)) continue;
            if (!canPlace(row, col, size, horizontal)) continue;
            Ship ship = new Ship(size);
            for (int i = 0; i < size; i++) {
                ship.addCell(cells[row + (horizontal ? 0 : i)][col + (horizontal ? i : 0)]);
            }
            ships.add(ship);
            return true;
        }
        return false;
    }
    private boolean canPlace(int row, int col, int size, boolean horizontal) {
        for (int i = 0; i < size; i++) {
            int r = row + (horizontal ? 0 : i);
            int c = col + (horizontal ? i : 0);
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    int nr = r + dr;
                    int nc = c + dc;
                    if (inBounds(nr, nc) && cells[nr][nc].getState() == Cell.State.SHIP) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    public ShotResult shoot(int row, int col) {
        Cell cell = cells[row][col];
        if (cell.isShot()) {
            return ShotResult.ALREADY_SHOT;
        }
        if (cell.getState() == Cell.State.EMPTY) {
            cell.setState(Cell.State.MISS);
            return ShotResult.MISS;
        }
        cell.setState(Cell.State.HIT);
        Ship ship = cell.getShip();
        ship.registerHit();
        if (ship.isSunk()) {
            markAround(ship);
            return ShotResult.SUNK;
        }
        return ShotResult.HIT;
    }
    private void markAround(Ship ship) {
        for (Cell cell : ship.getCells()) {
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    int nr = cell.getRow() + dr;
                    int nc = cell.getCol() + dc;
                    if (inBounds(nr, nc) && cells[nr][nc].getState() == Cell.State.EMPTY) {
                        cells[nr][nc].setState(Cell.State.MISS);
                    }
                }
            }
        }
    }
    public boolean allShipsSunk() {
        for (Ship ship : ships) {
            if (!ship.isSunk()) return false;
        }
        return true;
    }
    public String[] render(boolean hideShips) {
        String[] lines = new String[SIZE + 1];
        StringBuilder header = new StringBuilder(" ");
        header.append("  ");
        for (int c = 0; c < SIZE; c++) {
            header.append((char) ('A' + c)).append(' ');
        }
        lines[0] = header.toString();
        for (int r = 0; r < SIZE; r++) {
            StringBuilder sb = new StringBuilder(String.format("%2d ", r + 1));
            for (int c = 0; c < SIZE; c++) {
                sb.append(cells[r][c].symbol(hideShips)).append(' ');
            }
            lines[r + 1] = sb.toString();
        }
        return lines;
    }
}