package com.example.lib;

import java.io.Serializable;

/**
 * Клетка игрового поля.
 */
public class Cell implements Serializable {

    public enum State { EMPTY, SHIP, HIT, MISS, SUNK }

    private final int row;
    private final int col;
    private State state = State.EMPTY;
    private Ship ship;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() { return row; }

    public int getCol() { return col; }

    public State getState() { return state; }

    public void setState(State state) { this.state = state; }

    public Ship getShip() { return ship; }

    public void setShip(Ship ship) { this.ship = ship; }

    /** По этой клетке уже стреляли? */
    public boolean isShot() {
        return state == State.HIT || state == State.MISS || state == State.SUNK;
    }

    /** Символ для отрисовки; hideShips скрывает неподбитые корабли. */
    public char symbol(boolean hideShips) {
        switch (state) {
            case SHIP: return hideShips ? '.' : '#';
            case HIT:
            case SUNK: return 'X';
            case MISS: return 'o';
            default:   return '.';
        }
    }

    /** Координата вида "B5". */
    @Override
    public String toString() {
        return "" + (char) ('A' + col) + (row + 1);
    }
}
