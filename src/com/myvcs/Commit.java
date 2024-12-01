package com.myvcs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Commit {
    private String message;
    private List<String> files;

    public Commit(String message) {
        this.message = message;
        this.files = new ArrayList<>();
    }

    public void addFile(String fileName) {
        files.add(fileName);
    }

    public void saveCommit(String directoryPath) {
        File commitDir = new File(directoryPath, ".myvcs/commits");
        if (!commitDir.exists()) {
            commitDir.mkdirs();
        }

        String commitHash = Utils.computeSHA1(new File(directoryPath, ".myvcs/objects/" + files.get(0))); // Simplified for now
        File commitFile = new File(commitDir, commitHash);
        try (FileWriter writer = new FileWriter(commitFile)) {
            writer.write("Commit Message: " + message + "\n");
            writer.write("Files: \n");
            for (String file : files) {
                writer.write(file + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Update HEAD file to point to the latest commit
        File headFile = new File(directoryPath, ".myvcs/HEAD");
        try (FileWriter writer = new FileWriter(headFile)) {
            writer.write(commitHash);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
