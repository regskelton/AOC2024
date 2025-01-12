package com.davollo;

import java.io.*;
import java.util.HashSet;
import java.util.Set;



record GuardVelocity (int r, int c, int dir){};

class FileRead {
    private static final char GUARD = '^';

    public static void main(String[] args) throws IOException {

        char board[][] = null; // = new char[ROWS][COLS];
        int gR = 0, gC = 0; // row col of guard

        try (BufferedReader input = new BufferedReader(new FileReader("day6-input.txt"))) {
            int r = 0;

            while (input.ready()) {
                String s = input.readLine();

                if (board == null)
                    board = new char[s.length()][s.length()];

                for (int c = 0; c < s.length(); c++) {
                    board[r][c] = s.charAt(c);

                    if (board[r][c] == GUARD) {
                        gR = r;
                        gC = c;
                    }

                }
                r++;
            }
        } catch (Exception e) {
            throw e;
        }

        printBoard(board, gR, gC);

//        int count = countUniques(board, gR, gC);

        int obstacles=0;
        for (int r = 0; r < board.length; r++) {
  //          System.out.printf("\n%d ", r % 10);

            for (int c = 0; c < board[0].length; c++) {
    //            System.out.printf("%c", board[r][c]);
                if( board[r][c]=='.') {
                    board[r][c]='#';
                    if( hasLoop(board, gR, gC)) {
                        obstacles++;
                    }
                    board[r][c]='.';
                }

            }
        }


        System.out.printf("Count = %d", obstacles);
    }

   

    private static boolean hasLoop(char[][] board, int gR, int gC) {
        int dirs[][] = { { -1, 0 }, { 0, 1 }, { 1, 0 }, { 0, -1 } };
        int dir = 0;

        Set<GuardVelocity> gvs= new HashSet<GuardVelocity>();

        int nextR;
        int nextC;
        while (true) {
            nextR = gR + dirs[dir][0];
            nextC = gC + dirs[dir][1];

            if (nextR < 0 || nextR == board.length || nextC < 0 || nextC == board[0].length) {
                break;
            }

            if (board[nextR][nextC] == '#') {
                dir = (dir + 1) % dirs.length;

                continue;
            } else {
                gR = nextR;
                gC = nextC;

                GuardVelocity gv= new GuardVelocity( nextR, nextC, dir);

                if( gvs.contains(gv)) { return true;}

                gvs.add( gv);

            if(gvs.size() > board.length * board[0].length) {
               throw new IllegalArgumentException("Too many velocities" + gv + ":" + gvs) ;
            }
            
            }

           // printBoard(board, gR, gC);
        }
        return false;
    }



    private static int countUniques(char[][] board, int gR, int gC) {
        int dirs[][] = { { -1, 0 }, { 0, 1 }, { 1, 0 }, { 0, -1 } };
        int dir = 0;

        int count= 1;

        int nextR;
        int nextC;
        while (true) {
            nextR = gR + dirs[dir][0];
            nextC = gC + dirs[dir][1];

            if (nextR < 0 || nextR == board.length || nextC < 0 || nextC == board[0].length) {
                break;
            }

            if (board[nextR][nextC] == '#') {
                dir = (dir + 1) % dirs.length;

                continue;
            } else {
                if (board[nextR][nextC] == '.') {
                    count++;
                }

                board[nextR][nextC] = 'X';

                gR = nextR;
                gC = nextC;
            }

           // printBoard(board, gR, gC);
        }
        return count;
    }

    private static void printBoard(char[][] board, int gR, int gC) {

        System.out.printf("Guard at (%d,%d)\n  ", gR, gC);
        for (int c = 0; c < board[0].length; c++) {
            System.out.printf("%d", c % 10);
        }

        for (int r = 0; r < board.length; r++) {
            System.out.printf("\n%d ", r % 10);

            for (int c = 0; c < board[0].length; c++) {
                System.out.printf("%c", board[r][c]);
            }
        }

        System.out.println();
    }
}