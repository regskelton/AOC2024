package com.davollo;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

class Day10 {
    private static int rows, cols;

    record TrailHead(int r, int c) {
    };

    public static void main(String[] args) throws IOException {

        int[][] trailMap = Utils.readDigitsGrid("..\\input\\day10-input.txt");
        int rows= trailMap.length;
        int cols= trailMap[0].length;

        int total = 0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (trailMap[row][col] == 0) {
                    Set<TrailHead> heads = new HashSet<TrailHead>();

                    int rating= countTrails(trailMap, row, col, 1, heads);

                    total += rating;
                }
            }
        }

        Utils.printDigitsGrid( trailMap);

        System.out.printf("Trail score= %d", total);
    }

    record direction(int r, int c){};
    private static final direction DIRECTION_N = new direction(-1,0);
    private static final direction DIRECTION_E = new direction(0,1);
    private static final direction DIRECTION_S = new direction(1,0);
    private static final direction DIRECTION_W = new direction(0,-1);
    private static final direction[] DIRS ={DIRECTION_N, DIRECTION_E, DIRECTION_S, DIRECTION_W};

    private static int countTrails(int[][] trailMap, int row, int col, int next, Set<Day10.TrailHead> heads) {
        //dir[][] dirs = { new dir(-1,0), new dir{ -1, 0 }, { 0, 1 }, { 1, 0 }, { 0, -1 } };
        int rating= 0;

        //int dirNow = 0;

        //System.out.printf("Searching for %d near %d,%d\n", next, row, c);

        if (next == 10) {
            heads.add(new TrailHead(row, col));

            rating= 1;
        } else {

            for (int dir = 0; dir < DIRS.length; dir++) {

                int nextR= row + DIRS[dir].r;
                int nextC= col + DIRS[dir].c;

                if (nextR < 0 || nextR == trailMap.length || nextC < 0 || nextC == trailMap[0].length) {
                    continue;
                }

                if (trailMap[nextR][nextC] == next) {
                    rating +=countTrails(trailMap, nextR, nextC, next+1, heads);
                }
            }
        }

        return rating;
    }
}