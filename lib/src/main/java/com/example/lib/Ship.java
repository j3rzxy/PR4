package com.example.lib;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Абстрактный корабль: название, размер, клетки и число попаданий.
 */
public abstract class Ship implements Serializable {

    private final String name;
    private final int size;
    private final List<Cell> cells = new ArrayList<>();
    private int hits;

    protected Ship(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public void addCell(Cell cell) {
        cells.add(cell);
        cell.setShip(this);
        cell.setState(Cell.State.SHIP);
    }

    public void registerHit() {
        hits++;
    }

    public boolean isSunk() {
        return hits >= size;
    }

    public String getName() { return name; }

    public int getSize() { return size; }

    public List<Cell> getCells() { return cells; }
}