package com.example.lib;

public class RepeatedShotException extends Exception {
    public RepeatedShotException(String cellName) {
        super("В клетку " + cellName + " уже стреляли.");
    }
}
