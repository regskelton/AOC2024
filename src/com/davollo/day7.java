package com.davollo;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class Day7 {
    public static void main(String[] args) throws IOException {

        List<List<Long>> seqs = new ArrayList<List<Long>>();

        try (BufferedReader input = new BufferedReader(new FileReader("day7-input.txt"))) {
            while (input.ready()) {
                String s = input.readLine();

                //System.out.printf("Read[%s]\n", s);

                List<Long> seq = Arrays.stream(s.split("[: ]+")).map(Long::valueOf).collect(Collectors.toList());

                seqs.add(seq);
            }
        } catch (Exception e) {
            throw e;
        }

        long sumTestValues = 0;

        for (List<Long> seq : seqs) {
            long expected = seq.get(0);

            List<Long> possibleResults = new ArrayList<Long>();
            possibleResults.add(seq.get(1));
            
            getPossibleResults(seq, 2, possibleResults);

            if (possibleResults.contains(expected)) {
                //System.out.printf("%d can be made from " + seq + "\n" + possibleResults + "\n", expected);

                sumTestValues += expected;
            }
            else {
                //System.out.printf("%d can't be made from " + seq + "\n" + possibleResults + "\n", expected);
            }
        }

        System.out.printf("Result= %d", sumTestValues);
    }

    private static List<Long> getPossibleResults(List<Long> seq, int j, List<Long> possibleResults) {

        List<Long> newResults= new ArrayList<Long>();

        for (long possibleResult : possibleResults) {
            newResults.add(possibleResult + seq.get(j));
            newResults.add(possibleResult * seq.get(j));

            String cat= "" + possibleResult + seq.get(j);

            newResults.add( Long.parseLong(cat));
        }

        possibleResults.clear();
        possibleResults.addAll( newResults);

        if( j < seq.size() - 1){
            getPossibleResults(seq, j+1, possibleResults);
        }

        return possibleResults;
    }
}
