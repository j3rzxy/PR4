package com.example.lib;

import java.util.Scanner;

public class MainClass {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            name = "Player";
        }

        Player human = Player.human(name, scanner);
        Player computer = Player.computer("Computer");

        Game game = new Game(human, computer);
        game.start();
    }
}