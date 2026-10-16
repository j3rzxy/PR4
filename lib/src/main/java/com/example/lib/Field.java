package com.example.lib;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Field implements Serializable {

    public static final int SIZE = 10;
    private static final Random RANDOM = new Random();

    private final Cell[][] cells = new Cell[SIZE][SIZE];
    private final List<Ship> ships = new ArrayList<>();

    public Field() {
        clear();
    }

    public static List<Ship> createFleet() {
        List<Ship> fleet = new ArrayList<>();
        fleet.add(new Battleship());
        for (int i = 0; i < 2; i++) fleet.add(new Cruiser());
        for (int i = 0; i < 3; i++) fleet.add(new Destroyer());
        for (int i = 0; i < 4; i++) fleet.add(new Boat());
        return fleet;
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

    public void placeShip(Ship ship, int row, int col, boolean horizontal)
            throws InvalidPlacementException {
        int size = ship.getSize();
        int endRow = row + (horizontal ? 0 : size - 1);
        int endCol = col + (horizontal ? size - 1 : 0);

        if (!inBounds(row, col) || !inBounds(endRow, endCol)) {
            throw new InvalidPlacementException("The ship is moving beyond the boundaries of the field..");
        }

        for (int i = 0; i < size; i++) {
            int r = row + (horizontal ? 0 : i);
            int c = col + (horizontal ? i : 0);
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    int nr = r + dr;
                    int nc = c + dc;
                    if (inBounds(nr, nc) && cells[nr][nc].getState() == Cell.State.SHIP) {
                        throw new InvalidPlacementException(
                                "The ship touches another ship. (near cell " + cells[nr][nc] + ").");
                    }
                }
            }
        }

        for (int i = 0; i < size; i++) {
            ship.addCell(cells[row + (horizontal ? 0 : i)][col + (horizontal ? i : 0)]);
        }
        ships.add(ship);
    }

    public void placeShipsRandomly() {
        do {
            clear();
        } while (!tryRandomFleet());
    }

    private boolean tryRandomFleet() {
        for (Ship ship : createFleet()) {
            boolean placed = false;
            for (int attempt = 0; attempt < 200 && !placed; attempt++) {
                try {
                    placeShip(ship, RANDOM.nextInt(SIZE), RANDOM.nextInt(SIZE), RANDOM.nextBoolean());
                    placed = true;
                } catch (InvalidPlacementException e) {
                    // РїСЂРѕР±СѓРµРј РґСЂСѓРіСѓСЋ РїРѕР·РёС†РёСЋ
                }
            }
            if (!placed) return false;
        }
        return true;
    }

    public ShotResult shoot(int row, int col) throws RepeatedShotException {
        Cell cell = cells[row][col];

        if (cell.isShot()) {
            throw new RepeatedShotException(cell.toString());
        }
        if (cell.getState() == Cell.State.EMPTY) {
            cell.setState(Cell.State.MISS);
            return ShotResult.MISS;
        }

        cell.setState(Cell.State.HIT);
        Ship ship = cell.getShip();
        ship.registerHit();
        if (ship.isSunk()) {
            for (Cell c : ship.getCells()) {
                c.setState(Cell.State.SUNK);
            }
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
        StringBuilder header = new StringBuilder("   ");
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
