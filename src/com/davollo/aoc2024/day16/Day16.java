package com.davollo.aoc2024.day16;

import com.davollo.aoc2024.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day16 {
    public static void main(String[] args) throws IOException {
        new Puzzle().solve();
    }
}

class Puzzle {
    char[][] grid;
    int rows, cols;
    int startR;
    int startC;

    Puzzle() throws IOException {

        grid = Utils.readCharGrid("..\\input\\ex2");

//        route (29,10028) = E NNNN EE NN EEEEEE NN EE NN WW NN EEEE
//        route (37,7036)  = E NNNN EE NN EEEEEEEE SSSSSS EE NNNNNNNNNNNN

        rows = grid.length;
        cols = grid[0].length;

        //Utils.printCharGrid(grid);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 'S') {
                    startR = r;
                    startC = c;
                }
            }
        }
    }

    record Coord(int r, int c) {
    }

    Map<Coord, Integer> visited = new HashMap<Coord, Integer>();

    List<String> validRoutes = new ArrayList<>();

    int scoreRoute(String route) {
        int score = route.length() - 1;

        for (int i = 1; i < route.length(); i++) {
            if (route.charAt(i) != route.charAt(i - 1)) {
                score += 1000;
            }
        }

        return score;
    }

    int recurse=0;

    void findRoute(int r, int c, char dir, String route) {
        recurse++;

        Coord coord = new Coord(r, c);

        route = route + dir;

        int score = scoreRoute(route);

        int prevScore = score + 100000;

        //System.out.printf("%d Here (%d,%d) for %d", recurse, r, c, score);

        if (visited.containsKey(coord)) {
            prevScore = visited.get(coord);

            if( score < prevScore) {
                System.out.printf("%d %d,%d for %d -> %d\n", recurse, r, c, prevScore, score);
                //System.out.printf(" beats %d\n", prevScore);
            } else {
               // System.out.println();

                return;
            }
        } else {
           // System.out.println();
        }

        if (!visited.containsKey(coord) || score < prevScore) {
            visited.put(coord, score);

            if (grid[r][c] == 'E') {
                validRoutes.add(route);
            } else {


                if (grid[r][c] != '#') {
                    //System.out.printf("Paths from %d,%d\n", r, c);

                    findRoute(r - 1, c, 'N', route);
                    findRoute(r, c + 1, 'E', route);
                    findRoute(r + 1, c, 'S', route);
                    findRoute(r, c - 1, 'W', route);
                }
            }
        }
    }

    void solve() throws IOException {
        findRoute(startR, startC, 'E', "");

        System.out.println("validRoutes = " + validRoutes.size());

        int minScore = 100000000;

        for (String route : validRoutes) {
            int score = scoreRoute(route);
            System.out.printf("route (%d,%d) = %s\n", route.length(), score, route);

            minScore = (Math.min(score, minScore));
        }

        System.out.println("minScore = " + minScore);
    }
}