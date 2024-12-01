package com.myvcs;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to MyVCS!");

        // Loop to allow multiple commands to be entered
        while (true) {
            System.out.print("Enter command: ");
            String command = scanner.nextLine();

            if (command.equals("exit")) {
                break;  // Exit the loop if 'exit' is entered
            }

            if (command.equals("init")) {
                Repository.initRepository(System.getProperty("user.dir"));
            } else if (command.startsWith("add ")) {
                String fileName = command.substring(4).trim();
                System.out.println("Adding file: " + fileName);
                Repository.addFile(System.getProperty("user.dir"), fileName);
            } else if (command.startsWith("commit ")) {
                String commitMessage = command.substring(7).trim();
                Repository.commitChanges(System.getProperty("user.dir"), commitMessage);
            } else if (command.equals("history")) {
                // Call the viewHistory method to display the commit history
                Repository.viewHistory(System.getProperty("user.dir"));
            } else {
                System.out.println("Unknown command: " + command);
            }
        }

        scanner.close();  // Close the scanner after exiting the loop
    }
}
