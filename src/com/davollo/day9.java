package com.davollo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

class Day9 {
    private static int rows, cols;

    public static void main(String[] args) throws IOException {

        String diskmap = "";

        try (BufferedReader input = new BufferedReader(new FileReader("day9-input.txt"))) {
            while (input.ready()) {
                diskmap = input.readLine();
            }
        } catch (Exception e) {
            throw e;
        }

        List<Integer> expanded = new ArrayList<Integer>();

        int fileId = 0;
        for (int i = 0; i < diskmap.length(); i += 2) {
            for (int f = 0; f < diskmap.charAt(i) - '0'; f++) {
                expanded.add(fileId);
            }

            fileId++;

            if (i + 1 < diskmap.length()) {
                for (int s = 0; s < diskmap.charAt(i + 1) - '0'; s++) {
                    expanded.add(-1);
                }
            }
        }

        int[] exp = expanded.stream().mapToInt(Integer::intValue).toArray();

        int length = exp.length;
        int other = length - 1;

        int maxLen = 60;

        //printFirstAndLast(exp, maxLen);

        for (int i = length - 1; i >= 0; i--) {
            //System.out.printf("Searching from %d  ", i);
            // skip to start of next occupied field, from right
            while (exp[i] == -1) {
                i--;
            }

            fileId = exp[i];

            //System.out.printf("File %d found at %d  ", fileId, i);

            int fileIdLength = 1;

            while ((i-fileIdLength >= 0) && exp[i - fileIdLength] == fileId) {
                fileIdLength++;
            }

            //System.out.printf("Length %d\n", fileIdLength);

            boolean moved= false;

            for (int j = 0; j < length && j < i-fileIdLength; j++) {

                // skip to start of next blank field, from left
                while (exp[j] != -1) {
                    j++;
                }

                int blanks = 1;

                while (((j+blanks) < length) && (exp[j + blanks] == -1) ) {
                    blanks++;
                }

                if (i>j && blanks >= fileIdLength) {
                   // System.out.printf("Move %d/%d, %d digits to %d\n", i, fileId, fileIdLength, j);

                    for (int k = 0; k < fileIdLength; k++) {
                        exp[j + k] = fileId;
                        exp[i - k] = -1;
                    }
                    //i++;

                    moved= true;

                    //printFirstAndLast(exp, maxLen);

                    break;
                }
            }

            if (!moved)
            {
                i= i - fileIdLength +1;
            }
        }

        long checksum = 0;

        for (int i = 0; i < length; i++) {
            if( exp[i]!= -1) {
                checksum += exp[i] * i;
            }
        }

        System.out.printf("Checksum=%d", checksum);
    }

    private static void printFirstAndLast(int[] ints, int len) {

        String sep = "";

        boolean redact= false;

        if( len < ints.length){
            redact= true;
            sep= "---";
        }

        len = Integer.min(len, ints.length);

        System.out.print("[");
        for (int i = 0; i < len - sep.length(); i++) {
            System.out.printf("%d", i % 10);
        }
        System.out.printf("%s", sep);
        for (int i = 0; i < len - sep.length(); i++) {
            System.out.printf("%d", (ints.length - i) % 10);
        }
        System.out.printf("]\n[");

        for (int i = 0; i < len; i++) {
            System.out.printf("%c", (ints[i] == -1 ? '.' : ints[i] + '0'));
        }

        System.out.printf("%s", sep);
        for (int i = len + sep.length(); i < ints.length; i++) {
            System.out.printf("%c", (ints[i] == -1 ? '.' : ints[i] + '0'));
        }

        System.out.printf("]\n");
    }

}