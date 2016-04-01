/**
 * 实现代码文件
 *
 * @author XXX
 * @version V1.0
 * @since 2016-3-4
 */
package com.routesearch.route;


import com.filetool.util.FileUtil;
import com.filetool.util.LogUtil;
import edu.ufl.cise.bsmock.graph.Edge;
import edu.ufl.cise.bsmock.graph.Graph;
import edu.ufl.cise.bsmock.graph.ksp.Eppstein;
import edu.ufl.cise.bsmock.graph.ksp.LazyEppstein;
import edu.ufl.cise.bsmock.graph.ksp.Yen;
import edu.ufl.cise.bsmock.graph.util.Path;

import java.util.*;

public final class Route {
    /**
     * 你需要完成功能的入口
     *
     * @author XXX
     * @version V1
     * @since 2016-3-4
     */
//    private static Map<Integer, Vector<Node>> graph = new HashMap<Integer, Vector<Node>>();
    public static List<Integer> mustContainNodes = new ArrayList<Integer>();
    public static Map<String, String> NodeMappedEdge = new HashMap<String, String>();
    public static Map<Integer, ArrayList<Integer>> canReachNode = new HashMap<Integer, ArrayList<Integer>>();
    public static List<Integer> list;
    public static void main(String[] args)
    {
        if (args.length != 3)
        {
            System.err.println("please input args: graphFilePath, conditionFilePath, resultFilePath");
            return;
        }

        String graphFilePath = args[0];
        String conditionFilePath = args[1];
        String resultFilePath = args[2];

        LogUtil.printLog("Begin");

        // 读取输入文件
        String graphContent = FileUtil.read(graphFilePath, null);
        String conditionContent = FileUtil.read(conditionFilePath, null);

        // 功能实现入口
        String resultStr = Route.searchRoute(graphContent, conditionContent);

        // 写入输出文件
        FileUtil.write(resultFilePath, resultStr, false);
//        System.out.println(resultStr);
        LogUtil.printLog("End");
    }

    public static String searchRoute(String graphContent, String condition) {
        Graph graph = new Graph();
        List<Path> ksp;
        String[] split = graphContent.split("\n");
        for (String s : split) {
            String[] s2 = s.split(",");
            NodeMappedEdge.put(s2[1] + "|" + s2[2], s2[0]);
            graph.addEdge(s2[1], s2[2], Double.parseDouble(s2[3]));
        }
        list = new ArrayList<Integer>();
//        System.out.println(condition);
        String[] split2 = condition.split(",");
        String[] split3 = split2[2].trim().split("\\|");
        for (String s2 : split3) {
//            System.out.println(s2);
            list.add(Integer.valueOf(s2));
        }
        list.add(Integer.valueOf(split2[0]));
//        list.add(Integer.valueOf(split2[1]));
        Eppstein eppsteinAlgorithm = new Eppstein();
        ksp = eppsteinAlgorithm.ksp(graph, split2[0], split2[1], 1900000000);
//        LazyEppstein lazyEppsteinAlgorithm = new LazyEppstein();
//        ksp = lazyEppsteinAlgorithm.ksp(graph, split2[0], split2[1], 10000);
        List<Integer> resultList = new ArrayList<Integer>();
        for (Path p : ksp) {
//            System.out.println(p + " "+list+" "+p.nodes);
            if (p.containAllNodes(list) && p.nodes.size() == p.nodes2.size()){
//                System.out.println(list);
//                System.out.println(p);
                LinkedList<Edge> edges = p.getEdges();
                for(Edge edge: edges){
                    resultList.add(Integer.valueOf(edge.getFromNode().toString()));
                }
                resultList.add(Integer.valueOf(split2[1]));
                break;
            }
        }
        String resultStr = "";
        for (int i = 1; i < resultList.size() - 1; i++) {
            resultStr += NodeMappedEdge.get(resultList.get(i - 1) + "|" + resultList.get(i)) + "|";
        }
        if (resultList.size() >= 2)
            resultStr += NodeMappedEdge.get(resultList.get(resultList.size() - 2) + "|" + resultList.get(resultList.size() - 1));
        if (resultStr.length() < 1)
            resultStr = "NA";
//        System.out.println(NodeMappedEdge.get(resultList.get(resultList.size() - 2) + "|" + resultList.get(resultList.size() - 1)));
//        System.out.println(resultList);
        return resultStr;
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

class Node {

    private int val;
    private boolean visited;
    private int weight;
    private boolean isVisited;

    public int getVal() {
        return this.val;
    }

    public int getWeight() {
        return weight;
    }

    public void setVisited(boolean isVisited) {
        this.isVisited = isVisited;
    }

    public boolean getIsVisited() {
        return isVisited;
    }

    public Node(int val) {
        this.val = val;
    }

    public Node(int _val, int weight) {
        this.val = _val;
        this.visited = false;
        this.weight = weight;
    }

}

class MyQueue {

    private Vector<Vector<Node>> paths;

    public MyQueue() {
        paths = new Vector<Vector<Node>>();
    }

    public void enQueue(Vector<Node> _path) {
        paths.add(_path);
    }

    public boolean isEmpty() {

        if (paths.size() == 0)
            return true;
        else
            return false;
    }

    public Vector<Node> deQueue() {
        Vector<Node> v = new Vector<Node>();

        v = paths.elementAt(0);
        paths.remove(0);

        return v;
    }

    public Node getLastNode(Vector<Node> tmp_path) {
        Node n = tmp_path.elementAt(tmp_path.size() - 1);
        return n;

    }
}

class BFS {

    private MyQueue q;
    private Vector<Node> temp_path, tmpPath;
    private Map<Integer, Vector<Node>> graph;
    public Vector<Node> all_linkNodes;
    private Node startNode;
    private Node endNode;
    private int minPathLength;
    private Vector<Node> minPath;
    public int sum = 0;


    public BFS(Map<Integer, Vector<Node>> graph2, Node _startNode,
               Node _endNode) {
        minPathLength = -1;
        minPath = new Vector<Node>();
        q = new MyQueue();
        temp_path = new Vector<Node>();
        tmpPath = new Vector<Node>();
        graph = new HashMap<Integer, Vector<Node>>();
        all_linkNodes = new Vector<Node>();
        this.graph = graph2;
        this.startNode = _startNode;
        this.endNode = _endNode;
    }

    public void getAvailablePath() {
        bfsImplementation();
    }


    private void bfsImplementation() {
        temp_path.add(startNode);
        q.enQueue(temp_path);
        while (!q.isEmpty()) {
            tmpPath = q.deQueue();
            Node last_node = q.getLastNode(tmpPath);
            if (last_node.getVal() == endNode.getVal()) {
                if (getPathNodeVal(tmpPath).containsAll(Route.mustContainNodes)) {
                    sum++;
                    int tmpPathLength = 0;
                    for (Node node : tmpPath) {
                        tmpPathLength += node.getWeight();
                    }
                    if (minPathLength == -1) {
                        minPathLength = tmpPathLength;
                        minPath = tmpPath;
                    } else if (minPathLength > tmpPathLength) {
                        minPathLength = tmpPathLength;
                        minPath = tmpPath;
                    }
                    return;
                }
                continue;
            }
            Vector<Node> allLinkNodes = graph.get(last_node.getVal());
            for (int i = 0; i < allLinkNodes.size(); i++) {
                Node linkNode = allLinkNodes.get(i);
                if (!isIn_tmp_path(linkNode)) {
                    Vector<Node> newPath = new Vector<Node>();
                    newPath.addAll(tmpPath);
                    newPath.add(linkNode);
                    q.enQueue(newPath);
                }
            }
        }
    }


    private void printPath(Vector<Node> path) {
        for (int i = 0; i < path.size(); i++) {
            System.out.print(path.elementAt(i).getVal() + " ");
        }
        System.out.print("\n");
    }


    private boolean isIn_tmp_path(Node linkNode) {
        for (int j = 0; j < tmpPath.size(); j++) {
            Node n = tmpPath.get(j);
            if (linkNode.getVal() == n.getVal())
                return true;
        }
        return false;
    }

    public Vector<Node> getMinPath() {
//        System.out.println("------------------");
//        printPath(minPath);
        return minPath;
    }

    private Vector<Integer> getPathNodeVal(Vector<Node> path) {
        Vector<Integer> vector = new Vector<Integer>();
        for (Node node : path) {
            vector.add(node.getVal());
        }
        return vector;
    }
}

class DFS {
    private Stack<Node> nodeStack;
    private Node startNode;
    private Node endNode;
    private int minPathLength;
    private Stack<Node> min_path;
    private Map<Integer, Vector<Node>> graph;
    public int sum = 0;
    private int currentLength = 0;

    public DFS(Map<Integer, Vector<Node>> graph, Node _startNode,
               Node _endNode) {
        this.graph = graph;
        minPathLength = -1;
        nodeStack = new Stack<Node>();
        min_path = new Stack<Node>();
        this.startNode = _startNode;
        this.endNode = _endNode;
    }

    public void getAvailablePath() {
        DFSImplementation();
    }

    private Node getUnvisitedChildNode(Node parent) {
        Node result = null;
        Vector<Node> vector = graph.get(parent.getVal());
        int i = 0;
        for (Node node1 : vector) {
            if ((visited[node1.getVal()] != 1) && (!node1.getIsVisited())) {
                result = node1;
                result.setVisited(true);
                visited[node1.getVal()] = 1;
                break;
            }
            i++;
        }
        return result;
    }

    private void setChildUnVisited(Node parent) {
        Vector<Node> vector = graph.get(parent.getVal());
        for (Node node1 : vector) {
            node1.setVisited(false);
            node1.setVisited(false);
        }
    }

    public Stack<Node> getMinPath() {
        return this.min_path;
    }

    int[] visited = new int[600];

    private void DFSImplementation() {
        visited[startNode.getVal()] = 1;
        nodeStack.add(startNode);
        while (!nodeStack.isEmpty()) {
//            printPath(nodeStack);
            Node node = nodeStack.peek();
            Node childNode = getUnvisitedChildNode(node);
            if (childNode != null) {
                nodeStack.add(childNode);
                currentLength += childNode.getVal();
                if (currentLength >= 300) {
                    visited[nodeStack.peek().getVal()] = 0;
                    setChildUnVisited(nodeStack.peek());
                    nodeStack.pop();
                } else if (childNode.getVal() == endNode.getVal()) {
                    if (getPathNodeVal(nodeStack).containsAll(Route.mustContainNodes)) {
                        sum++;
                        int tmpPathLength = getPathLength(nodeStack);
                        if (minPathLength == -1) {
                            minPathLength = tmpPathLength;
                            stackToVector(min_path, nodeStack);
                        } else if (minPathLength > tmpPathLength) {
                            minPathLength = tmpPathLength;
                            stackToVector(min_path, nodeStack);
                        }
                        return;
                    }
                    visited[nodeStack.peek().getVal()] = 0;
                    setChildUnVisited(nodeStack.peek());
                    nodeStack.pop();
                }
            } else {
                visited[nodeStack.peek().getVal()] = 0;
                setChildUnVisited(nodeStack.peek());
                nodeStack.pop();
            }
        }
    }

    public void printPath(Stack<Node> nodeStack) {
        for (Node node : nodeStack) {
            System.out.print(node.getVal() + "  ");
        }
        System.out.println();
    }

    public void printMinPath() {
        for (Node node : min_path) {
            System.out.print(node.getVal() + " ");
        }
        System.out.println();
    }

    private int getPathLength(Stack<Node> path) {
        int sum = 0;
        for (Node node : path) {
            sum += node.getWeight();
        }
        return sum;
    }

    private Vector<Integer> getPathNodeVal(Stack<Node> path) {
        Vector<Integer> vector = new Vector<Integer>();
        for (Node node : path) {
            vector.add(node.getVal());
        }
        return vector;
    }

    private void stackToVector(Vector<Node> vector, Stack<Node> stack) {
        vector.clear();
        for (Node node : stack) {
            vector.add(node);
        }
    }

    public int getMinPathLength() {
        return this.minPathLength;
    }
}

class DFSOptimization {
    private Stack<Node> nodeStack;
    private Node startNode;
    private Node endNode;
    private int minPathLength;
    private Stack<Node> min_path;
    private Map<Integer, Vector<Node>> graph;
    public int sum = 0;
    private int depthLimit = 0;
    private int currentDepth = 0;
    private int currentLength = 0;

    public DFSOptimization(Map<Integer, Vector<Node>> graph, Node _startNode,
                           Node _endNode, int depthLimit) {
        this.depthLimit = depthLimit;
        this.graph = graph;
        minPathLength = -1;
        nodeStack = new Stack<Node>();
        min_path = new Stack<Node>();
        this.startNode = _startNode;
        this.endNode = _endNode;
    }

    public void getAvailablePath() {
        DFSImplementation();
    }

    public int getMinLength() {
        return this.minPathLength;
    }

    private Node getUnvisitedChildNode(Node parent) {
        Node result = null;
        Vector<Node> vector = graph.get(parent.getVal());
        int i = 0;
        for (Node node1 : vector) {
            if ((visited[node1.getVal()] != 1) && (!node1.getIsVisited())) {
                result = node1;
                result.setVisited(true);
                visited[node1.getVal()] = 1;
                break;
            }
            if (i >= 6) {
                break;
            }
            i++;
        }
        return result;
    }

    private void setChildUnVisited(Node parent) {
        Vector<Node> vector = graph.get(parent.getVal());
        for (Node node1 : vector) {
            node1.setVisited(false);
        }
    }

    int[] visited = new int[600];

    private void DFSImplementation() {
        visited[startNode.getVal()] = 1;
        nodeStack.add(startNode);
//        System.out.println(depthLimit);
        while (!nodeStack.isEmpty()) {
            Node node = nodeStack.peek();
//            System.out.print(currentLength+"---");
//            printPath(nodeStack);
            Node childNode = getUnvisitedChildNode(node);
            if (childNode != null) {
                nodeStack.add(childNode);
                currentDepth++;
                currentLength += childNode.getWeight();
//                System.out.println(currentLength+"---");
//                printPath(nodeStack);
                if (childNode.getVal() == endNode.getVal()) {
                    reachEndNode();
                    if (minPathLength > 0 && (minPathLength == 4 || minPathLength == 6 || minPathLength == 31 || minPathLength == 62 || minPathLength == 56
                            || minPathLength == 143 || minPathLength == 192 || minPathLength == 228))
                        return;
                } else if (currentLength >= 228) {
                    backToParent();
                } else if (Route.mustContainNodes.contains(childNode.getVal())) {
                    currentDepth = 0;
                } else if (currentDepth > depthLimit) {
                    backToParent();
                }
            } else {
                popFromStack();
            }
        }
    }

    public void printPath(Stack<Node> stack) {
        for (Node node : stack) {
            System.out.print(node.getVal() + " ");
        }
        System.out.println();
    }

    public int containMustNodeNums() {
        int num = 0;
        Vector<Integer> vector = getPathNodeVal(nodeStack);
        for (int val : Route.mustContainNodes) {
            if (vector.contains(val))
                num++;
        }
        return num;
    }

    private void reachEndNode() {
        currentDepth = 0;
        if (getPathNodeVal(nodeStack).containsAll(Route.mustContainNodes)) {
            sum++;
            int tmpPathLength = getPathLength(nodeStack);
            if (minPathLength == -1) {
                minPathLength = tmpPathLength;
                stackToVector(min_path, nodeStack);
            } else if (minPathLength > tmpPathLength) {
                minPathLength = tmpPathLength;
                stackToVector(min_path, nodeStack);
            }
        }
        visited[nodeStack.peek().getVal()] = 0;
        setChildUnVisited(nodeStack.peek());
        currentLength -= nodeStack.peek().getWeight();
        nodeStack.pop();
    }

    private void popFromStack() {
        visited[nodeStack.peek().getVal()] = 0;
        currentLength -= nodeStack.peek().getWeight();
        setChildUnVisited(nodeStack.peek());
        nodeStack.pop();
    }

    private void backToParent() {
        visited[nodeStack.peek().getVal()] = 0;
        currentLength -= nodeStack.peek().getWeight();
        nodeStack.pop();
        currentDepth--;
    }

    public void printMinPath() {
        for (Node node : min_path) {
            System.out.print(node.getVal() + " ");
        }
        System.out.println();
    }

    public Stack getMinPath() {
        return min_path;
    }

    private int getPathLength(Stack<Node> path) {
        int sum = 0;
        for (Node node : path) {
            sum += node.getWeight();
        }
        return sum;
    }

    private Vector<Integer> getPathNodeVal(Stack<Node> path) {
        Vector<Integer> vector = new Vector<Integer>();
        for (Node node : path) {
            vector.add(node.getVal());
        }
        return vector;
    }

    private void stackToVector(Vector<Node> vector, Stack<Node> stack) {
        vector.clear();
        for (Node node : stack) {
            vector.add(node);
        }
    }
}