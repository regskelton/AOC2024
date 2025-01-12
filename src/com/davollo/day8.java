package com.davollo;

import java.io.*;

class Day8 {
    private static int rows, cols;

    public static void main(String[] args) throws IOException {

        char antennae[][] = null;
        boolean antinodes[][] = null;

        try (BufferedReader input = new BufferedReader(new FileReader("day8-input.txt"))) {
            int row = 0;

            while (input.ready()) {
                String s = input.readLine();

                if (antennae == null) {
                    cols = s.length();
                    rows = s.length();

                    antennae = new char[rows][rows];
                    antinodes = new boolean[rows][rows];
                }

                for (int col = 0; col < s.length(); col++) {
                    char charAt = s.charAt(col);

                    if (charAt == '#')
                        charAt = '.';

                    antennae[row][col] = charAt;
                }
                row++;
            }
        } catch (Exception e) {
            throw e;
        }

        //printBoard(rows, cols, antennae, antinodes);

        // for every cell on the board...
        for (int r1 = 0; r1 < rows; r1++) {
            for (int c1 = 0; c1 < cols; c1++) {

                char freq = antennae[r1][c1];

                if (freq != '.') {

                    // ... search for all frequency matches to that cell
                    for (int r2 = 0; r2 < rows; r2++) {
                        for (int c2 = 0; c2 < cols; c2++) {

                            if (!(r1 == r2 && c1 == c2)) {
                                if (antennae[r2][c2] == freq) {
                                    //System.out.printf("Matching %c at (%d,%d) & (%dd, %d) : ", freq, r1, c1, r2, c2);
                                    
                                    int rn, cn;

                                    for( int n=0; n < 60; n++) {
                                        rn = (n+1) * r1 - (n * r2);
                                        cn = (n+1) * c1 - (n * c2);
    
                                        addAntinode(antinodes, rn, cn);
    
                                        rn = (n+1) * r2 - (n*r1);
                                        cn = (n+1) * c2 - (n*c1);
    
                                        addAntinode(antinodes, rn, cn);
    
                                    }
                                }
                            }

                        }
                    }
                }

            }
        }

        int count = 0;

        for (int row = 0; row < rows; row++) {

            for (int col = 0; col < cols; col++) {
                if (antinodes[row][col]) {
                    count++;
                }
            }
        }

        System.out.printf("Count = %d", count);
    }

    private static void addAntinode(boolean[][] antinodes, int rn, int cn) {
        if (!(rn < 0 || rn >= rows || cn < 0 || cn >= cols)) {
            //System.out.printf("A(%d,%d) ok\n", rn, cn);

            antinodes[rn][cn] = true;
        } else {
            //System.out.printf("A(%d,%d) bad\n", rn, cn);
        }
    }

    private static void printBoard(int rows, int cols, char[][] antennae, boolean antinodes[][]) {

        for (int col = 0; col < cols; col++) {
            System.out.printf("%d", col % 10);
        }

        for (int row = 0; row < rows; row++) {
            System.out.printf("\n%d ", row % 10);

            for (int col = 0; col < cols; col++) {
                System.out.printf("%c", antennae[row][col]);
            }
        }

        System.out.println();
    }
}