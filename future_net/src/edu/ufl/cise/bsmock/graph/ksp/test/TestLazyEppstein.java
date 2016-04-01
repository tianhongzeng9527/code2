package edu.ufl.cise.bsmock.graph.ksp.test;

import edu.ufl.cise.bsmock.graph.Graph;
import edu.ufl.cise.bsmock.graph.ksp.LazyEppstein;
import edu.ufl.cise.bsmock.graph.util.Path;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Test of the lazy version of Eppstein's algorithm for computing the K shortest paths between two nodes in a graph.
 * <p/>
 * Copyright (C) 2015  Brandon Smock (dr.brandon.smock@gmail.com, GitHub: bsmock)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * Created by Brandon Smock on October 7, 2015.
 * Last updated by Brandon Smock on December 24, 2015.
 */
public class TestLazyEppstein {

    public static List<Integer> list = new ArrayList<Integer>();

    public static void main(String args[]) throws FileNotFoundException {
        /* Uncomment any of these example tests */
        String graphFilename, sourceNode, targetNode;
        int K;
        String s = "3|5|7|11|13|17";
        String[] split = s.trim().split("\\|");
        for (String s2 : split) {
            list.add(Integer.valueOf(s2));
        }
        /* Example 1 */
        //graphFilename = "edu/ufl/cise/bsmock/graph/ksp/test/tiny_graph_01.txt";
        //sourceNode = "1";
        //targetNode = "10";
        //K = 10;

        /* Example 2 */
        //graphFilename = "edu/ufl/cise/bsmock/graph/ksp/test/tiny_graph_02.txt";
        //sourceNode = "1";
        //targetNode = "9";
        //K = 100;

        /* Example 3 */
        graphFilename = "edu/ufl/cise/bsmock/graph/ksp/test/topo2.csv";
        sourceNode = "2";
        targetNode = "19";
//        graphFilename = "edu/ufl/cise/bsmock/graph/ksp/test/small_road_network_01.txt";
//        sourceNode = "5524";
//        targetNode = "7239";
        K = 10000;

        usageExample1(graphFilename, sourceNode, targetNode, K);
    }

    public static void usageExample1(String graphFilename, String source, String target, int k) {
        /* Read graph from file */
        System.out.print("Reading data from file... ");
        Graph graph = new Graph(graphFilename);
        System.out.println("complete.");

        /* Compute the K shortest paths and record the completion time */
        System.out.print("Computing the " + k + " shortest paths from [" + source + "] to [" + target + "] ");
        System.out.print("using the lazy version of Eppstein's algorithm... ");
        List<Path> ksp;
        long timeStart = System.currentTimeMillis();
        LazyEppstein lazyEppsteinAlgorithm = new LazyEppstein();
        ksp = lazyEppsteinAlgorithm.ksp(graph, source, target, k);
        long timeFinish = System.currentTimeMillis();
        System.out.println("complete.");

        System.out.println("Operation took " + (timeFinish - timeStart) / 1000.0 + " seconds.");

        /* Output the K shortest paths */
        System.out.println("k) cost: [path]");
        int n = 0;
        for (Path p : ksp) {
            if (p.containAllNodes(TestLazyEppstein.list))
                System.out.println(++n + ") " + p);
        }
    }
}
