package com.filetool.main;

import com.routesearch.route.Route;

import java.util.Map;
import java.util.Stack;
import java.util.Vector;

public class DFSOptimization {
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
//            if (i >= 3) {
//                break;
//            }
//            i++;
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
                System.out.print(currentDepth + "---"+Main.mustContainNodes+"-----");
                printPath(nodeStack);
                if (childNode.getVal() == endNode.getVal()) {
                    printPath(nodeStack);
                    reachEndNode();
//                    if (minPathLength > 0)
//                        return;
                } else if (currentLength >= 560) {
                    backToParent();
                } else if (Main.mustContainNodes.contains(childNode.getVal())) {
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
