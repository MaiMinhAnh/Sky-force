package com.skyforce;

import com.skyforce.client.Player;
import com.skyforce.packet.AddConnectionRequestPacket;
import com.skyforce.server.Room;

import static com.skyforce.common.Constants.HOST_PORT;

import java.util.InputMismatchException;
import java.util.Scanner;

public class App {
    public static void main( String[] args ) {
        boolean running = true;
        while (running) {
            printMenu();
            running = handleUserOption();
        }
    }

    private static boolean handleUserOption() {
        try {
            Scanner scanner = new Scanner(System.in);
            int opt = scanner.nextInt();
            switch (opt) {
                case 1: {
                    createNewGame();
                    break;
                }
                case 2: {
                    joinGame(false);
                    break;
                }
                case 3: {
                    System.out.println("Thank you for using!");
                    return false;
                }
                default:
                    System.out.println("PLEASE choose option from 1 -> 3!");
            }
            return true;
        } catch (InputMismatchException e) {
            System.out.println("PLEASE choose option from 1 -> 3!");
            return true;
        }
    }

    private static void joinGame(boolean isMaster) {
        Scanner scanner = new Scanner(System.in);
        String playerName = enterPlayerName(scanner);
        Player player = new Player("localhost", HOST_PORT);
        player.playerName = playerName;
        player.connect();

        try {
            AddConnectionRequestPacket addConnectionRequestPacket = new AddConnectionRequestPacket(playerName, isMaster);
            player.sendObject(addConnectionRequestPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (true) {
            String command = scanner.nextLine();
            if (command.equals("exit")) {
                player.close();
                break;
            } else if (command.equals("ready")) {
                player.notifyReadyState(true);
            } else if (command.equals("unready")) {
                player.notifyReadyState(false);
            }
        }
    }

    private static String enterPlayerName(Scanner scanner) {
        boolean isDone = false;
        String name = "";
        do {
            try {
                System.out.print("Enter your name: ");
                name = scanner.nextLine();
                if (name.length() > 16) {
                    System.out.println("Name length must be less than 16!");
                } else {
                    isDone = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Name is unacceptable!");
            }
        } while (!isDone);
        return name;
    }

    private static void createNewGame() {
        Room room = new Room(HOST_PORT);
        room.start();

        joinGame(true);
    }

    private static void printMenu() {
        System.out.print(new StringBuilder()
                .append("1. Create new game\n")
                .append("2. Join game\n")
                .append("3. Quit\n")
                .append("Your choice: ")
                .toString()
        );
    }
}
