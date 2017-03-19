/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import index.Indexer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * @author AsterAether
 */
public class Main {


    public static void main(String[] args) {
        List<String> excluded = new ArrayList<>(Arrays.asList(".java", ".jar", ".scmp"));
        Map<String, String> mappings = new HashMap<>();
        Map<String, String> prefix = new HashMap<>();
        String fileName = "Assets.java";

        switch (args.length) {
            case 4:
                for (String s : args[3].split(" ")) {
                    if (s.isEmpty() || !s.contains("=")) continue;
                    String[] split = s.split("=");
                    prefix.put(split[0], split[1]);
                }
            case 3:
                for (String s : args[2].split(" ")) {
                    if (s.isEmpty() || !s.contains("=")) continue;
                    String[] split = s.split("=");
                    mappings.put(split[0], split[1]);
                }
            case 2:
                for (String s : args[1].split(" ")) {
                    if (s.isEmpty()) continue;
                    excluded.add(s);
                }
            case 1:
                fileName = args[0];
                break;
        }
        Indexer indexer = new Indexer(excluded, mappings, prefix, true);
        try {
            indexer.index(new File(System.getProperty("user.dir")), new File(System.getProperty("user.dir") + File.separator + fileName));
        } catch (IOException e) {
            System.out.println("Error writing: " +e.getMessage());
        }
    }
}
