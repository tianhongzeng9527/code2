package com.filetool.main;

import com.filetool.util.FileUtil;
import com.filetool.util.LogUtil;
import com.routesearch.route.Route;

import java.util.*;

/**
 * 工具入口
 *
 * @author
 * @version v1.0
 * @since 2016-3-1
 */
public class Main {
    private static Map<Integer, Vector<Node>> graph = null;
    public static List<Integer> mustContainNodes = null;
    public static Map<String, String> NodeMappedEdge = new HashMap<String, String>();
    public static Map<Integer, ArrayList<Integer>> canReachNode = new HashMap<Integer, ArrayList<Integer>>();
    private static int begin, end;
    public static String resultStr;
    public static int[][] a = new int[600][600];

    private static void setCanReachNode() {
        for (Map.Entry<Integer, Vector<Node>> map : graph.entrySet()) {
            int key = map.getKey();
            ArrayList<Integer> list = new ArrayList<Integer>();
            for (Node node : map.getValue()) {
                list.add(node.getVal());
            }
            canReachNode.put(key, list);
        }
    }

    private static void setGraph(String graphContent) {
        String[] graphContents = graphContent.split("\n");
        for (String s : graphContents) {
            String[] splits = s.split(",");
            a[Integer.valueOf(splits[1])][Integer.valueOf(splits[2])] = Integer.valueOf(splits[3]);
            NodeMappedEdge.put(splits[1] + "|" + splits[2], splits[0]);
            if (graph.containsKey(Integer.valueOf(splits[1]))) {
                Vector<Node> nodes = graph.get(Integer.valueOf(splits[1]));
                insertToGraph(nodes, new Node(Integer.valueOf(splits[2]), Integer.valueOf(splits[3])));
                graph.put(Integer.valueOf(splits[1]), nodes);
            } else {
                Vector<Node> nodes = new Vector<Node>();
                nodes.add(new Node(Integer.valueOf(splits[2]), Integer.valueOf(splits[3])));
                graph.put(Integer.valueOf(splits[1]), nodes);
            }
        }
    }

    private static void setMustContainNodes(String conditionContent) {
        String[] splits = conditionContent.split("\n")[0].split(",");
        begin = Integer.valueOf(splits[0]);
        end = Integer.valueOf(splits[1]);
        String[] mustNodes = splits[2].split("\\|");
        for (String mustNode : mustNodes) {
            mustContainNodes.add(Integer.valueOf(mustNode));
        }
        mustContainNodes.add(0, end);
        System.out.println(mustContainNodes);
    }

    public static void main(String[] args) {
        mustContainNodes = new ArrayList<Integer>();
        graph = new HashMap<Integer, Vector<Node>>();
        if (args.length != 3) {
            System.err.println("please input args: graphFilePath, conditionFilePath, resultFilePath");
            return;
        }
        String graphFilePath = args[0];
        String conditionFilePath = args[1];
        String resultFilePath = args[2];
        LogUtil.printLog("Begin");
        String graphContent = FileUtil.read(graphFilePath, null);
        setGraph(graphContent);
        for(int i = 0; i < 20 ; i++){
            for(int j = 0; j < 20; j++){
                if(a[i][j] == 0)
                    a[i][j] = -1;
                System.out.print(a[i][j]+" ");
            }
            System.out.println();
        }
        setCanReachNode();
        String conditionContent = FileUtil.read(conditionFilePath, null);
        setMustContainNodes(conditionContent);
//        System.out.println(graph.size()+"  "+mustContainNodes.size());
//        DFS dfsAlgo = new DFS(graph, new Node(begin), new Node(end));
        DFSOptimization dfsAlgo = new DFSOptimization(graph, new Node(begin), new Node(end), 6);
        dfsAlgo.getAvailablePath();
//        dfsAlgo.printMinPath();
        System.out.println(dfsAlgo.sum);
        Stack<Node> stack = dfsAlgo.getMinPath();
        List<Integer> resultList = new ArrayList<Integer>();
//        String resultStr = "";
        for (Node node : stack) {
            resultList.add(node.getVal());
        }
        for (int i = 1; i < resultList.size() - 1; i++) {
            resultStr += NodeMappedEdge.get(resultList.get(i - 1) + "|" + resultList.get(i)) + "|";
        }
        if (resultList.size() >= 2)
            resultStr += NodeMappedEdge.get(resultList.get(resultList.size() - 2) + "|" + resultList.get(resultList.size() - 1));
        if (resultStr.length() < 1)
            resultStr = "NA";
        FileUtil.write(resultFilePath, resultStr, false);
    }

    private static int getSplitIndex(Vector<Node> vector) {
        for (int i = 0; i < vector.size(); i++) {
            if (!mustContainNodes.contains(vector.get(i).getVal())) {
                return i;
            }
        }
        return vector.size();
    }

    private static boolean handleSameNodeWithDifferentWeight(Vector<Node> vector, Node node) {
        for (Node node1 : vector) {
            if (node1.getVal() == node.getVal()) {
                if (node1.getWeight() < node.getWeight()) {
                    return false;
                } else {
                    vector.remove(node1);
                    return true;
                }
            }
        }
        return true;
    }

    private static void insertToGraph(Vector<Node> vector, Node node) {
//        if(handleSameNodeWithDifferentWeight(vector, node)){
        int splitIndex = getSplitIndex(vector);
        if (mustContainNodes.contains(node.getVal())) {
            boolean insert = false;
            for (int i = 0; i < splitIndex; i++) {
                if (node.getWeight() < vector.get(i).getWeight()) {
                    vector.add(i, node);
                    insert = true;
                    break;
                }
            }
            if (!insert) {
                vector.add(splitIndex, node);
            }

        } else {
            boolean insert = false;
            for (int i = splitIndex; i < vector.size(); i++) {
                if (node.getWeight() < vector.get(i).getWeight()) {
                    vector.add(i, node);
                    insert = true;
                    break;
                }
            }
            if (!insert)
                vector.add(node);
        }
//        }
    }
}
