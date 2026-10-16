package com.example.lib;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== SEA BATTLE ===");
        System.out.println("1 - new game");
        if (Game.saveExists()) {
            System.out.println("2 - continue saved game");
        }
        System.out.println("0 - exit");
        System.out.print("Your choice: ");

        if (!scanner.hasNextLine()) return;
        String choice = scanner.nextLine().trim();

        if (choice.equals("1")) {
            System.out.print("Enter your name: ");
            String name = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
            if (name.isEmpty()) name = "Player";

            Game game = new Game(new Player(name, scanner), new AIPlayer("Computer"));
            game.setup();
            game.play();
        } else if (choice.equals("2") && Game.saveExists()) {
            try {
                Game game = Game.load(scanner);
                System.out.println("Game loaded.");
                game.play();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Cant load game: " + e.getMessage());
            }
        }
    }
}
