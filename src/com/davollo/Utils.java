package com.davollo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Utils {
    static int[][] readDigitsGrid(String fileName) throws IOException {
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

    static void printDigitsGrid(int[][] grid) {
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
}
