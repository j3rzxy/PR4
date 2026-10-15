package com.example.lib;

public class Cell {
    public enum State {EMPTY, SHIP, HIT, MISS}
    private final int row;
    private final int col;
    private State state = State.EMPTY;
    private Ship ship;
    public Cell(int row, int col)
    {
        this.row = row;
        this.col = col;
    }
    public int getRow() { return row;}
    public int getCol() { return col;}
    public State getState() { return state; }
    public void setState(State state) { this.state = state; }
    public Ship getShip() { return ship; }
    public void setShip(Ship ship) { this.ship = this.ship; }
    public boolean isShot() {
        return state == State.HIT || state == State.MISS;
    }
    public char symbol(boolean hideShips) {
        switch (state) {
            case SHIP:
                return hideShips ? '.' : '#';
            case HIT:
                return 'X';
            case MISS:
                return 'o';
            default:
                return '.';
        }
    }
    @Override
    public String toString() {
        return "" + (char) ('A' + col) + (row + 1);
    }
}
