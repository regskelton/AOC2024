/*
    Based on https://topaz.github.io/paste/#XQAAAQADEgAAAAAAAAA4GEiZzRd1JAhcKpma7J4gVovyUcIKu5i6OoFt7QqpzQ18PH5qwr+9ukWeBJvYfs/M8nvPHMhxOjyhzHPvlo3o79D4ik/GIJLgQT+49BEXDz1SrFq7uXYQ+lkZnr3J6Ums8SgMtRM6pEAP2551oq7QDgrdqT/pgr1c+2tj8pv9CvrjkzZJeTv5JtwoShuFBuKQUjtw6fWz3ZnGnh6clY0LQScxkcYW8JNZS4Yac/sZUdJx996nlwN8fwlt3TXQg1j/BLF8Yb8VNZAp77eGN9vBQn8aTFcNkir5sNrhiazIjuaAcnQPjDQBf1pOGoUMcSGRNfxMdjbc4TexDUYVAjZW6nct6/+itKcEiH68LQ/VthZpurFa4Z2AJJPg6N7muRVCeTOgFCi+TzH730cB16c9eRV+uf92jy9AM3LqoS4FaFsE8ry5mzIEMgITr3Wm1m35cOmWgh3gSz4UmqQY+xkAfMJRynYB3fNZhDdAVEWFywMc0j9kemMLQ6N8nQ1a494ENToKo5biW/p76rpKpmuHOiBnhvP3GRBRebeuilC/V5A2d0PhHMIsHbXvTPgffLriXXiVVJsR0F0rR7N6AU9vNusN8JckbhZS9/3K67sQo2BFVME7FX7aEPlbDr4fePdjJ9Xek0bG63+UaZzqXLHlqrkPEzL/L7f+7xN8LSNd6wcX+x9/6OB+eRlm5RwKQBragjiKHe85Uz0DQURcfwymiUpuZD13RUrqXHvkFsAeHAskK572qbm6WmJmlSYgi4y/P4xXmJH51698/3O9asYMnDlgCddxXEyPZouZsuzKGJCJZ/MigLNW5bJLw5UoB4ThJ5+G1nQKheFhwyvDlsrkEyjyPfVfKPHMydacD/WFHEFmaoG8iRbJ5wATTe2FwieVRehdY4hx5vtl7TDwCA5XF4tjJWupxMdF8PEv18FA2bBD40MxdSYRf53bDJKAsuNDrZQGthkOSEycuePH0p3GF0xLlXby3zfrliBW7aOSGkm17AiXqbL3mTQ9bsbX/tXoKPIBEp5b0nJU9VJ26zobhtEfKWWopBFKB/Z5m6iLVfqMqgIwXadQS4hHHLLcASIra4UJQhvum0JRy4GC8pqKpRwQLwButwu/YmA22c/nCzqCtl6HsRNWcHOD5zXYV7xRlh+AECRdsOcMFeEaQmFY66lTBS649udUwJyjuCpuVkH14paQj8pHRjj0SV3lAfFScKGoKS/m9kzQ7ldennbupb4+SVWi3f1jbdS6m0R5lNwU4rjVHN+Fd0m7jDpjy1w55f7bVp6JBvbanKZeahE3ruRc9Fi54gb8nS917kfojeNzITE0eZ1715hl3Bl2C0QUKfRHMTDSgSi812UgoKnlSWUDkyiG3lB3eKsRlK346TxYm3zEPxkDyIh4OXDDyrmlzgo94IW4axKymlvTSXvfeJZxb4JLnWH/Tf/KTsjTOUwZEVYmRJpDy0Rh3VimNJKDn/GJruoamSSZJm6/YgjGy9W68tziVxAhcTo8VIqvimlQw2PFKe5LZen8P8Hld4t/8CQ4V27RKeM1j3OFwFg2e/2HXDMoeqSWWQtS/O1v93G16sV6wuLyoyNtpP06GFIQogRiwwKp3xS6VD+T5CnjpROveSfelVpXoMewi6Q1L4T1rTRqJ14qk8fXIzpybEc5tse9+3xqSudYYUfvCsV9p2V6VUaUUPBdVimRnf421k2WHyD3RtQFuUcq0lOmusJyHb2lbgQtnKIKAyQS+ykOQlDEdcXzoHg2gy9DZyPQecwthTA5O/vOaO8=
 */

package com.davollo.aoc2024.day12;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

public class Day12 {
    public static void main(String[] args) throws IOException {
        new Puzzle().solve();
    }
}

class Puzzle {

    private final Grid grid;

    Puzzle() throws IOException {
        try (var input = Objects.requireNonNull(getClass().getResourceAsStream("day12-input.txt"))) {
            grid = Grid.from(new BufferedReader(new InputStreamReader(input)).lines());
        }
    }

    void solve() {
        var regions = grid.regions();
        System.out.println(regions.stream().mapToInt(Region::price).sum());
        System.out.println(regions.stream().mapToLong(Region::betterPrice).sum());
    }
}

record Coordinate(int x, int y) implements Comparable<Coordinate> {
    Collection<Coordinate> neighbours() {
        return Set.of(
                new Coordinate(x + 1, y),
                new Coordinate(x, y + 1),
                new Coordinate(x - 1, y),
                new Coordinate(x, y - 1)
        );
    }

    Collection<Coordinate> edges() {
        return Set.of(
                new Coordinate(x, y),
                new Coordinate(x, y + 1),
                new Coordinate(x + 1, y),
                new Coordinate(x + 1, y + 1)
        );
    }

    boolean isNeighbor(Coordinate other) {
        return x == other.x || y == other.y;
    }

    @Override
    public int compareTo(Coordinate o) {
        return y == o.y ? Integer.compare(x, o.x) : Integer.compare(y, o.y);
    }
}

record Region(Map<Coordinate, Integer> plots) {
    int area() {
        return plots.size();
    }

    int perimeter() {
        return plots.values().stream().mapToInt(i -> i).sum();
    }

    int price() {
        return area() * perimeter();
    }

    long edges() {
        Map<Coordinate, SortedSet<Coordinate>> edges = new HashMap<>();
        for (Coordinate coordinate : plots.keySet()) {
            for (Coordinate edge : coordinate.edges()) {
                edges.computeIfAbsent(edge, c -> new TreeSet<>());
                edges.get(edge).add(coordinate);
            }

        }
        long hidden = 2 * edges.values().stream().filter(s -> s.size() == 2 && !s.first().isNeighbor(s.last())).count();
        return edges.values().stream().filter(s -> (s.size() & 1) == 1).count() + hidden;
    }

    long betterPrice() {
        return area() * edges();
    }
}

record Grid(Map<Coordinate, Character> plots) {
    static Grid from(Stream<String> lines) {
        Map<Coordinate, Character> plots = new HashMap<>();
        int y = 0;
        for (String line : lines.toList()) {
            int x = 0;
            for (char c : line.toCharArray()) {
                plots.put(new Coordinate(x, y), c);
                x++;
            }
            y++;
        }
        return new Grid(plots);
    }

    Region region(Coordinate coordinate, Set<Coordinate> visited) {
        var thisPlant = plots.get(coordinate);
        Map<Coordinate, Integer> bits = new HashMap<>();
        Deque<Coordinate> queue = new ArrayDeque<>();
        queue.addLast(coordinate);
        while (!queue.isEmpty()) {
            if (queue.size() > plots.size()) {
                throw new IllegalStateException();
            }
            Coordinate c = queue.removeFirst();
            visited.add(c);
            int fences = 4;
            for (Coordinate neighbour : c.neighbours()) {
                var otherPlant = plots.get(neighbour);
                if (Objects.equals(otherPlant, thisPlant)) {
                    --fences;
                    if (!visited.contains(neighbour)) {
                        if (!queue.contains(neighbour)) {
                            queue.addLast(neighbour);
                        }
                    }
                }
            }
            bits.put(c, fences);
        }
        return new Region(bits);
    }

    List<Region> regions() {
        List<Region> regions = new ArrayList<>();
        Set<Coordinate> visited = new HashSet<>();
        for (Coordinate coordinate : plots.keySet()) {
            if (!visited.contains(coordinate)) {
                regions.add(region(coordinate, visited));
            }
        }
        return regions;
    }
}
