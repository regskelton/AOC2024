package com.davollo.aoc2024;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    public record direction( int r, int c){};
    static final direction DIRECTION_N = new direction(-1,0);
    static final direction DIRECTION_E = new direction(0,1);
    static final direction DIRECTION_S = new direction(1,0);
    static final direction DIRECTION_W = new direction(0,-1);
    public static final direction[] DIRS ={DIRECTION_N, DIRECTION_E, DIRECTION_S, DIRECTION_W};

    static List<Integer> readInts(String fileName) throws IOException {

        List<Integer> seqs=null;

        try (BufferedReader input = new BufferedReader(new FileReader(fileName))) {
            while (input.ready()) {
                String s = input.readLine();

                //System.out.printf("Read[%s]\n", s);

                seqs = Arrays.stream(s.split("[: ]+")).map(Integer::valueOf).collect(Collectors.toList());
            }
        } catch (Exception e) {
            throw e;
        }

        return seqs;
    }

    public static List<Long> readLongs(String fileName) throws IOException {

        List<Long> seqs=null;

        try (BufferedReader input = new BufferedReader(new FileReader(fileName))) {
            while (input.ready()) {
                String s = input.readLine();

                //System.out.printf("Read[%s]\n", s);

                seqs = Arrays.stream(s.split("[: ]+")).map(Long::valueOf).collect(Collectors.toList());
            }
        } catch (Exception e) {
            throw e;
        }

        return seqs;
    }

    public static int[][] readDigitsGrid(String fileName) throws IOException {
        int[][] grid = null;
        int rows = 0, cols = 0;

        try (BufferedReader input = new BufferedReader(new FileReader(fileName))) {
            int row = 0;
            while (input.ready()) {
                String line = input.readLine();

                if (grid == null) {
                    rows = line.length();
                    cols = line.length();

                    grid = new int[rows][cols];
                }

                for (int col = 0; col < cols; col++) {
                    grid[row][col] = line.charAt(col) - '0';
                }

                row++;
            }
        } catch (Exception e) {
            throw e;
        }
        return grid;
    }

    public static char[][] readCharGrid(String fileName) throws IOException {
        char[][] grid = null;
        int rows = 0, cols = 0;

        try (BufferedReader input = new BufferedReader(new FileReader(fileName))) {
            int row = 0;
            while (input.ready()) {
                String line = input.readLine();

                if (grid == null) {
                    rows = line.length();
                    cols = line.length();

                    grid = new char[rows][cols];
                }

                for (int col = 0; col < cols; col++) {
                    grid[row][col] = line.charAt(col);
                }

                row++;
            }
        } catch (Exception e) {
            throw e;
        }
        return grid;
    }

    public static void printDigitsGrid(int[][] grid) {
        System.out.printf("  ");
        for (int c = 0; c < grid[0].length; c++) {
            System.out.printf("%d", c % 10);
        }

        for (int r = 0; r < grid.length; r++) {
            System.out.printf("\n%d ", r % 10);

            for (int c = 0; c < grid[0].length; c++) {
                System.out.printf("%d", grid[r][c]);
            }
        }

        System.out.println();
    }

    public static void printCharGrid(char[][] grid) {
        System.out.print("  ");
        for (int c = 0; c < grid[0].length; c++) {
            System.out.printf("%d", c % 10);
        }

        for (int r = 0; r < grid.length; r++) {
            System.out.printf("\n%d ", r % 10);

            for (int c = 0; c < grid[0].length; c++) {
                System.out.printf("%c", grid[r][c]);
            }
        }

        System.out.println();
    }

}
