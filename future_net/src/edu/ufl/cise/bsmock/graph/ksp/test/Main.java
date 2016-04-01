package edu.ufl.cise.bsmock.graph.ksp.test;

import java.io.*;

/**
 * Created by tian on 16-3-31.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        File file = new File("/home/tian/topo.csv");
        File file2 = new File("/home/tian/topo2.csv");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file2));
        String s = "";
        while ((s=bufferedReader.readLine())!=null){
            String[] splits = s.split(",");
            bufferedWriter.write(splits[1]+" "+splits[2]+" "+splits[3]+"\n");
        }
        bufferedReader.close();
        bufferedWriter.close();
    }
}
