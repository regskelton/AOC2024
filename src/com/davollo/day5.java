package com.davollo;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

class FileRead5 {
    public static void main(String[] args) throws IOException {

        HashMap<Integer, List<Integer>> rules = new HashMap<Integer, List<Integer>>();
        List<List<Integer>> updates = new ArrayList<List<Integer>>();

        try (BufferedReader input = new BufferedReader(new FileReader("day5-input.txt"))) {
            boolean readingRules = true;
            while (input.ready()) {
                String line = input.readLine();
                if (line.equals("")) {
                    readingRules = false;
                } else {
                    if (readingRules) {
                        int[] pages = Arrays.stream(line.split("\\|")).mapToInt(Integer::parseInt).toArray();

                        List<Integer> rule = rules.get(pages[0]);

                        if (rule == null) {
                            rule = new ArrayList<Integer>();
                            rules.put(pages[0], rule);
                        }
                        rule.add(pages[1]);
                    } else {
                        List<Integer> update = Arrays.stream(line.split(",")).map(Integer::valueOf)
                                .collect(Collectors.toList());

                        updates.add(update);
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }

        boolean correct;
        int score = 0, part2Score = 0;

        for (List<Integer> list : updates) {
            correct = true;
            for (int i = 0; i < list.size(); i++) {
                if (rules.containsKey(list.get(i))) {
                    for (int j = 0; j < i; j++) {
                        if (rules.get(list.get(i)).contains(list.get(j))) {
                            correct = false;
                            list.add(j, list.get(i));
                            list.remove(i + 1);
                            i = 0;
                        }
                    }
                }
            }

            if (correct)
                score += list.get(list.size() / 2);
            else
                part2Score += list.get(list.size() / 2);
        }

        System.out.println("Part1= " + score + " , Part2= " + part2Score);
    }
}