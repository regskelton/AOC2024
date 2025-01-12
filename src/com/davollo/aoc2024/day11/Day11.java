package com.davollo.aoc2024.day11;

import com.davollo.aoc2024.Utils;

import java.io.*;
import java.util.*;

class Day11 {
    private static int rows, cols;

    record Counted ( long head, long count){}

    private static Map<Counted, Long> children= new HashMap<Counted, Long>();

    private static long countChildren( int blinks, long head) {
        long count = 0;

        if (blinks == 0) {
           // System.out.printf("<%d>\n", head);

            return 1;
        }

        Counted c = new Counted(head, blinks);

        if (children.containsKey(c)) {
            count = children.get(c);
        } else
        {
            //System.out.printf("%d has ...unknown kids\n", head);

            if (head == 0) {
                count = countChildren(blinks - 1, 1);
            } else {
                String asString = "" + head;

                int digits = asString.length();

                if (digits % 2 == 0) {
                    //System.out.printf("Splitting [%s] at %d\n", asString, digits/2);

                    count = + countChildren(blinks - 1, Long.parseLong(asString.substring(0, digits / 2)));
                    count += countChildren(blinks - 1, Long.parseLong(asString.substring(digits / 2)));
                } else {
                    count = countChildren(blinks - 1, head * 2024);
                }
            }

            //System.out.printf("%d with %d blinks has %d stones\n", head, blinks, count);
            children.put(c, count);
        }

        return count;
    }

    public static void main(String[] args) throws IOException {

        List<Long> stones = Utils.readLongs("day11-input.txt");

        long total=0;
            for (int i = 0; i < stones.size(); i++) {
                long s = stones.get(i);

                long count= countChildren( 75, s);

                total+= count;

                //System.out.println("min = " + Collections.min(children.values()));

                System.out.printf("Stones[%d]= %d %d \n", s, count, total);
         }

        //System.out.printf("Stones= %d", stones.size());
    }

//    public static long blink(String inputFilename, String outputFilename) {
//
//        long count=0;
//
//        FileInputStream fin = null;
//        try {
//            fin = new FileInputStream(inputFilename);
//            FileOutputStream fout = new FileOutputStream(outputFilename);
//
//            boolean EOF = false;
//            while (!EOF) {
//                int i = 0;
//                String s = "";
//
//                while ((i = fin.read()) != ' ') {
//                    if (i == -1) {
//                        EOF = true;
//                        break;
//                    }
//
//                    s = s + (char) i;
//                }
//
//                if( s.length()==0) {
//                    break;
//                }
//
//                //System.out.printf("Processing [%s]\n", s);
//
//                Long inp = Long.parseLong(s);
//
//
//                if (inp == 0) {
//                    fout.write("1 ".getBytes());
//                } else {
//                    int digits = s.length();
//
//                    if (digits % 2 == 0) {
//                        //System.out.printf("Splitting [%s] at %d\n", asString, digits/2);
//
//                        fout.write((""+Long.parseLong(s.substring(0, digits / 2))).getBytes());
//                        fout.write(" ".getBytes());
//                        fout.write((""+Long.parseLong(s.substring(digits / 2))).getBytes());
//                        fout.write(" ".getBytes());
//
//                        count++; //extra stone
//                    } else {
//                        fout.write(("" + inp * 2024L + " ").getBytes());
//                    }
//                }
//
//                count++;
//            }
//
//            fout.close();
//
//            //System.out.println("Done reading and writing!!");
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        return count;
//    }


//    List<Long> stones = Utils.readLongs("..\\input\\day11-input.txt");
//
//        for (int b = 0; b < 75; b++) {
//        List<Long> prime = new ArrayList<Long>();
//
//        for (int i = 0; i < stones.size(); i++) {
//            long s = stones.get(i);
//
//            if (s == 0) {
//                prime.add(1L);
//            } else {
//                String asString = "" + s;
//
//                int digits = asString.length();
//
//                if (digits % 2 == 0) {
//                    //System.out.printf("Splitting [%s] at %d\n", asString, digits/2);
//
//                    prime.add(Long.parseLong(asString.substring(0, digits / 2)));
//                    prime.add(Long.parseLong(asString.substring(digits / 2)));
//                } else {
//                    prime.add(s * 2024);
//                }
//
//            }
//        }
//
//        stones = prime;
//
//        System.out.printf("Stones %d = %d\n", b, stones.size());
//    }


}