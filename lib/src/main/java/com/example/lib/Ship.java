package com.example.lib;

import java.util.ArrayList;
import java.util.List;

public class Ship {
    private int size;
    private final List<Cell> cells = new ArrayList<>();
    private int hits;
    public Ship(int size){
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
    public int getSize(){ return size; }
    public List<Cell> getCells(){ return cells; }
}