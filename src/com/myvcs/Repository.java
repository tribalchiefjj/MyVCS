package com.myvcs;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;




public class Repository {

    // Initialize the repository (creating .myvcs folder)
    public static void initRepository(String directoryPath) {
        File repoDir = new File(directoryPath, ".myvcs");
        if (repoDir.exists()) {
            System.out.println("Repository already initialized.");
        } else {
            repoDir.mkdirs(); // Create the .myvcs directory
            new File(repoDir, "objects").mkdir(); // Create objects directory inside .myvcs
            try {
                new File(repoDir, "index").createNewFile(); // Create index file to track staged files
                new File(repoDir, "HEAD").createNewFile(); // Create HEAD file to track latest commit
                System.out.println("Repository initialized.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Add a file to the staging area (index)
    public static void addFile(String directoryPath, String fileName) {
        File file = new File(directoryPath, fileName);
        if (!file.exists()) {
            System.out.println("File does not exist: " + fileName);
            return;
        }

        // Compute the SHA-1 hash of the file content
        String sha1Hash = Utils.computeSHA1(file);
        if (sha1Hash == null) {
            System.out.println("Failed to compute SHA-1 for file: " + fileName);
            return;
        }

        // Create the .myvcs/objects directory if it doesn't exist
        File objectsDir = new File(directoryPath, ".myvcs/objects");
        if (!objectsDir.exists()) {
            objectsDir.mkdirs();  // Create the directory if it doesn't exist
        }

        // Copy the file to .myvcs/objects with its SHA-1 hash as its name
        File objectFile = new File(objectsDir, sha1Hash);
        try {
            Files.copy(file.toPath(), objectFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File staged: " + fileName);  // Confirmation message
        } catch (IOException e) {
            System.out.println("Failed to copy file to objects: " + e.getMessage());
            e.printStackTrace();
        }

        // Add the file to the index file (index contains the file name and its SHA-1 hash)
        File indexFile = new File(directoryPath, ".myvcs/index");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(indexFile, true))) {
            writer.write(fileName + " " + sha1Hash + "\n");
        } catch (IOException e) {
            System.out.println("Failed to update index: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    public static void commitChanges(String directoryPath, String commitMessage) {
        File objectsDir = new File(directoryPath, ".myvcs/objects");
        File indexFile = new File(directoryPath, ".myvcs/index");

        // Create the commit directory if it doesn't exist
        File commitsDir = new File(directoryPath, ".myvcs/commits");
        if (!commitsDir.exists()) {
            commitsDir.mkdirs();
        }

        // Create the commit object
        String commitID = Utils.computeSHA1(commitMessage.getBytes());

        File commitFile = new File(commitsDir, commitID);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(commitFile));
            writer.write("Commit message: " + commitMessage + "\n");
            writer.write("Files:\n");

            // Read the index file and write the staged files to the commit
            BufferedReader reader = new BufferedReader(new FileReader(indexFile));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                String fileName = parts[0];
                String sha1Hash = parts[1];

                // Write the file name and hash in the commit file
                writer.write(fileName + " " + sha1Hash + "\n");

                // Copy the file from objects to the commit folder
                File objectFile = new File(objectsDir, sha1Hash);
                if (objectFile.exists()) {
                    Files.copy(objectFile.toPath(), new File(commitFile.getParent(), sha1Hash).toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
            reader.close();
            writer.close();
            System.out.println("Commit successful: " + commitMessage);  // Confirmation message
        } catch (IOException e) {
            System.out.println("Failed to save commit: " + e.getMessage());
            e.printStackTrace();
        }
    }


// Method to view the commit history
public static void viewHistory(String directoryPath) {
    // Path to the commits directory
    File commitsDir = new File(directoryPath, ".myvcs/commits");
    
    // Check if the commits directory exists
    if (commitsDir.exists()) {
        // List all files (commit files are named by their SHA-1 hashes)
        String[] commits = commitsDir.list();
        
        // If there are commits
        if (commits != null && commits.length > 0) {
            System.out.println("Commit history:");
            // Loop through all the commit files (these are the commit IDs, which are SHA-1 hashes)
            for (String commit : commits) {
                System.out.println(commit);  // Print the commit ID (SHA-1 hash)
            }
        } else {
            System.out.println("No commits yet.");
        }
    } else {
        System.out.println("No commits directory found.");
    }
}
}




    
   