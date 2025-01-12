package com.davollo;

import com.davollo.aoc2024.Utils;

import java.io.IOException;

class Day12p1 {
    public static void main(String[] args) throws IOException {

        char[][] garden = Utils.readCharGrid("..\\input\\day12-example-input.txt");

        boolean[][] visited = new boolean[garden.length][garden[0].length];

        int total = 0;

        for (int r = 0; r < garden.length; r++) {
            for (int c = 0; c < garden[0].length; c++) {
                char p = garden[r][c];

                Cost cost = getCost(garden, visited, r, c, p);

                if (cost.area > 0) {
                    System.out.printf("%c plants, cost = %s\n", p, cost.toString());
                }

                total += cost.area * cost.fences;
            }
        }

        System.out.println("total = " + total);
    }

    record Cost(int area, int fences, int corners) {
    }


    private static boolean safeCheck(char[][] grid, int r, int c, char match, boolean offGridResult) {


        if (r >= 0 && r < grid.length && c >= 0 && c < grid[0].length) {

            if (grid[r][c] == match) return true;
            else return false;
        }

        return offGridResult;
    }

    private static Cost getCost(char[][] garden, boolean[][] visited, int r, int c, char p) {
        Cost totalCost = new Cost(0, 0, 0);

        if (!visited[r][c] && garden[r][c] == p) {
            visited[r][c] = true;

            boolean[] fences = getFences(garden, r, c, p);

            int fenceCount = 0; //BoolStream.of(fences).sum();
            for (boolean fence : fences) if (fence) fenceCount++;

            int corners = 0;

            boolean[] n = new boolean[8];

            boolean na = safeCheck(garden, r, c - 1, p, false);
            boolean nb = safeCheck(garden, r - 1, c - 1, p, false);
            boolean nc = safeCheck(garden, r - 1, c, p, false);
            boolean nd = safeCheck(garden, r - 1, c + 1, p, false);
            boolean ne = safeCheck(garden, r, c + 1, p, false);
            boolean nf = safeCheck(garden, r + 1, c + 1, p, false);
            boolean ng = safeCheck(garden, r + 1, c, p, false);
            boolean nh = safeCheck(garden, r + 1, c - 1, p, false);

//            //top left
//            if (
//                    (!na && nb && !nc) ||
//                    (!na && nb && !nc) ||
//                    (!na && nb && !nc) ||
//            )
//
//            )


            switch (fenceCount) {
                case 0:
                    int inners = 0;

                    //check for inner corner here
                    if (!safeCheck(garden, r + 1, c + 1, p, false)) inners++;
                    if (!safeCheck(garden, r + 1, c - 1, p, false)) inners++;
                    if (!safeCheck(garden, r - 1, c + 1, p, false)) inners++;
                    if (!safeCheck(garden, r - 1, c - 1, p, false)) inners++;

                    System.out.printf("inners(%d,%d) = %d\n", r, c, inners);

                    corners += inners;

                    break;
                case 1:
                    break;
                case 2:
                    if (!(fences[0] && fences[2]) || (fences[1] && fences[3])) {
                        corners++;
                    }
                    break;
                case 3:
                    corners += 2;
                    break;
            }

            totalCost = new Cost(1, fenceCount, corners);

            if (r > 0) {
                Cost cost = getCost(garden, visited, r - 1, c, p);

                totalCost = new Cost(totalCost.area + cost.area, totalCost.fences + cost.fences, totalCost.corners + cost.corners);
            }

            if (c > 0) {
                Cost cost = getCost(garden, visited, r, c - 1, p);

                totalCost = new Cost(totalCost.area + cost.area, totalCost.fences + cost.fences, totalCost.corners + cost.corners);
            }

            if (r < garden.length - 1) {
                Cost cost = getCost(garden, visited, r + 1, c, p);

                totalCost = new Cost(totalCost.area + cost.area, totalCost.fences + cost.fences, totalCost.corners + cost.corners);
            }

            if (c < garden[0].length - 1) {
                Cost cost = getCost(garden, visited, r, c + 1, p);

                totalCost = new Cost(totalCost.area + cost.area, totalCost.fences + cost.fences, totalCost.corners + cost.corners);
            }
        }

        return totalCost;
    }

    private static boolean[] getFences(char[][] garden, int r, int c, char p) {
        boolean[] fence = new boolean[]{true, true, true, true};

        if (r > 0) {
            if (garden[r - 1][c] == p) {
                fence[0] = false;
            }
        }

        if (c < garden[0].length - 1) {
            if (garden[r][c + 1] == p) {
                fence[1] = false;
            }
        }

        if (r < garden.length - 1) {
            if (garden[r + 1][c] == p) {
                fence[2] = false;
            }
        }
        if (c > 0) {
            if (garden[r][c - 1] == p) {
                fence[3] = false;
            }
        }
        return fence;
    }
}