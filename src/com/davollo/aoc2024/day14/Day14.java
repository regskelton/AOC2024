package com.davollo.aoc2024.day14;

import java.io.*;
import java.util.*;

public class Day14 {
    public static void main(String[] args) throws IOException {
        new Puzzle().solve();
    }
}

class Puzzle {

    record Guard(int px, int py, int vx, int vy) {
    }

    List<Guard> guards = new ArrayList<>();
    int maxX = 101, maxY = 103;

    Puzzle() throws IOException {

        Scanner s = new Scanner(Objects.requireNonNull(getClass().getResourceAsStream("input.txt")));

        s.findAll("p=(-?\\d+),(-?\\d+) v=(-?\\d+),(-?\\d+)").forEach(result -> {
            int px = Integer.parseInt(result.group(1));
            int py = Integer.parseInt(result.group(2));
            int vx = Integer.parseInt(result.group(3));
            int vy = Integer.parseInt(result.group(4));

            Guard g = new Guard(px, py, vx, vy);
            guards.add(g);

            System.out.printf("New guard(%s)\n", g);
        });
    }

    int[][] grid = new int[maxX][maxY];

    void tick() {
        List<Guard> newGuards = new ArrayList<>();

        for (int x = 0; x < maxX; x++) {
            for (int y = 0; y < maxY; y++) {

                grid[x][y] = 0;

            }
        }

        for (Guard g : guards) {
            int px, py;

            px = g.px + g.vx;
            py = g.py + g.vy;

            Guard ng = new Guard((px % maxX + maxX) % maxX, (py % maxY + maxY) % maxY, g.vx, g.vy);

            newGuards.add(ng);

            grid[ng.px][ng.py]++;
        }

        guards = newGuards;
    }

    void display() {
        for (int x = 0; x < maxX; x++) {
            for (int y = 0; y < maxY; y++) {
                if (grid[x][y] > 0) {
                    System.out.printf("%d", grid[x][y] % 10);
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }

    int treeCandidate() {
        int robots = 0;

        for (int y = 0; y < maxY; y++) {
            robots += grid[maxX - 1][y];
            robots += grid[0][y];
        }
        for (int x = 0; x < maxX; x++) {
            robots += grid[x][0];
            robots += grid[x][maxY-1];
        }

        return robots;
    }

    record quads(int q1, int q2, int q3, int q4){
        int score() {
            return q1 * q2 * q3 * q4;
        }
    }

    quads score() {
        int q1 = 0, q2 = 0, q3 = 0, q4 = 0;

        for (Guard g : guards) {
            if (g.py < maxY / 2) {
                if (g.px < maxX / 2) {
                    q1++;
                } else {
                    if (g.px > maxX / 2) {
                        q2++;
                    }
                }
            } else {
                if (g.py > maxY / 2) {
                    if (g.px < maxX / 2) {
                        q4++;
                    } else {
                        if (g.px > maxX / 2) {
                            q3++;
                        }
                    }
                }
            }
        }

//        System.out.println("q1 = " + q1);
//        System.out.println("q2 = " + q2);
//        System.out.println("q3 = " + q3);
//        System.out.println("q4 = " + q4);

        return new quads(q1, q2, q3, q4);
    }

    void solve() throws IOException {

        int fac=0;

        for (int t = 0; t < 10000000; t++) {
            tick();

            //quads q= score();
            int r= treeCandidate();

            if( r > fac) {
                System.out.printf("%d ticks, %d score\n", t, r);

                fac= r;

                display();
            }
            //display();

            //char tmp = (char) System.in.read();
        }

        for (Guard g : guards) {
//            System.out.printf("%s\n", g);
        }

        int safetyFactor = score().score();

//        System.out.println("safetyFactor = " + safetyFactor);


//        var regions = grid.regions();
//        System.out.println(regions.stream().mapToInt(Region::price).sum());
//        System.out.println(regions.stream().mapToLong(Region::betterPrice).sum());
    }
}

//    class Day11 {
//        private static int rows, cols;
//
//        record Counted(long head, long count) {
//        }

//        private static Map<com.davollo.aoc2024.day11.Day11.Counted, Long> children= new HashMap<com.davollo.aoc2024.day11.Day11.Counted, Long>();
//
//        private static long countChildren( int blinks, long head) {
//            long count = 0;
//
//            if (blinks == 0) {
//                // System.out.printf("<%d>\n", head);
//
//                return 1;
//            }
//
//            com.davollo.aoc2024.day11.Day11.Counted c = new com.davollo.aoc2024.day11.Day11.Counted(head, blinks);
//
//            if (children.containsKey(c)) {
//                count = children.get(c);
//            } else
//            {
//                //System.out.printf("%d has ...unknown kids\n", head);
//
//                if (head == 0) {
//                    count = countChildren(blinks - 1, 1);
//                } else {
//                    String asString = "" + head;
//
//                    int digits = asString.length();
//
//                    if (digits % 2 == 0) {
//                        //System.out.printf("Splitting [%s] at %d\n", asString, digits/2);
//
//                        count = + countChildren(blinks - 1, Long.parseLong(asString.substring(0, digits / 2)));
//                        count += countChildren(blinks - 1, Long.parseLong(asString.substring(digits / 2)));
//                    } else {
//                        count = countChildren(blinks - 1, head * 2024);
//                    }
//                }
//
//                //System.out.printf("%d with %d blinks has %d stones\n", head, blinks, count);
//                children.put(c, count);
//            }
//
//            return count;
//        }

