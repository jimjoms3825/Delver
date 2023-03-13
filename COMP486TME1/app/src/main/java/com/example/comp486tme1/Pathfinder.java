package com.example.comp486tme1;

import java.util.ArrayList;
/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: August 24th, 2022

    Class: Pathfinder
    Description: A class which allows for convenient universal calls to A* pathfinding methods.

 */
public class Pathfinder {

    //Node for A* pathfinding.

    private static class Node {
        Vector2 position;
        public ArrayList<Node> path;
        public ArrayList<Node> connectedNodes; // Neighbors.
        float cost = 0; // Actual cost.
        float fScore = 0; // Heuristic cost.

        /*
        Inputs: Vector2 representing the position of the node.

        Outputs: Creates a Node at position passed.

        Called by: Pathfinder.createNodeList()

        Calls: None.
        */
        public Node(Vector2 pos){
            position = pos;
            path = new ArrayList<Node>();
            connectedNodes = new ArrayList<Node>();
            cost = 0;
            fScore = 0;
            path.add(this);
        }

        /*
        Inputs: None.

        Outputs: Returns the path of nodes as an arrayList filled with Vector2s

        Called by: PathFinder.getPath()

        Calls: none.
        */
        public ArrayList<Vector2> getPathAsVectors(){
            //Scale the path nodes.
            ArrayList<Vector2> vectors = new ArrayList<Vector2>();
            for(Node vec: path){
                vec.position.scale(GameView.level.scaledTileWidth);
                vectors.add(vec.position);
            }
            return vectors;
        }

        /*
        Inputs: ArrayList of Nodes representing the new best path to reach this node.

        Outputs: None. Resets this path as passed path with this on the end.

        Called by: Pathfinder.getPath()

        Calls: None
        */
        public void setPath(ArrayList<Node> newPath){
            path.clear();
            for(Node n: newPath){
                path.add(n);
            }
            path.add(this);
        }
    }

    /*
    Inputs: A vector2 for the starting position, A vector2 for the target end position, and an int representing
    how many nodes the algorithm may search.

    Outputs: Returns the best path from start to end as an arraylist of vector 2s.

    Called by: Demon.think(), Imp.think()

    Calls: createNodeList(), getClosestNode(), getNodeAtPosition(), getSmallestNode() Node.getPathAsVectors(), Node.setPath()
    */
    public static ArrayList<Vector2> getPath(Vector2 start, Vector2 end, int searchDepth){
        if(GameView.level == null || Player.instance == null) { return new ArrayList<Vector2>(); }
        Vector2 startingPos = start.clone();
        Vector2 endPos = end.clone();
        //convert to tile coords.
        startingPos.divide(GameView.level.scaledTileWidth);
        endPos.divide(GameView.level.scaledTileWidth);

        ArrayList<Node> nodes = createNodeList(new Vector2((int)(startingPos.x), (int)(startingPos.y)), searchDepth);
        ArrayList<Node> openList = new ArrayList<Node>();
        ArrayList<Node> closedList = new ArrayList<Node>();

        openList.add(getNodeAtPosition(startingPos, nodes));
        Node bestNode = openList.get(0);
        Node targetNode = getNodeAtPosition(endPos, nodes);
        //Backs out if no target node can be found.
        if(targetNode == null) {
            return null;
        }
        //Assigns best node if null
        if(bestNode == null){
            bestNode = getClosestNode(endPos, nodes);
            //If the best node still cant be found back out of algorithm.
            if(bestNode == null){
                return  null;
            }
        }
        //Assigns the fScore of the first node (has no cost, is the start).
        bestNode.fScore = Vector2.getDistance(bestNode.position, targetNode.position);

        //Traverse whole open list.
        while(!openList.isEmpty()){
            bestNode = getSmallestNode(openList);
            if(bestNode == targetNode){ // Target reached.
                return bestNode.getPathAsVectors();
            }
            if(bestNode == null){ // If for some reason the best node is not valid, dont continue.
                return null;
            }
            //Update all connected nodes.
            for(Node connected: bestNode.connectedNodes){
                if(openList.contains(connected)){ continue; }
                //Using a cartesian distance heuristic for computational simplicity.
                float distanceToTarget = Vector2.getDistance(connected.position, targetNode.position);
                //Update node on closed list if it exists there.
                if(closedList.contains(connected)){
                    //Only if cost is lower (Better path found).
                    if(bestNode.cost + 1 >= connected.cost) { continue; }
                    else{
                        closedList.remove(connected);
                        openList.add(connected);
                        connected.cost = bestNode.cost + 1;
                        connected.fScore = connected.cost + distanceToTarget;
                        connected.setPath(bestNode.path);
                    }
                }
                //add unexplored node to open list.
                else{
                    openList.add(connected);
                    connected.cost = bestNode.cost + 1;
                    connected.fScore = connected.cost + distanceToTarget;
                    connected.setPath(bestNode.path);
                }
            }

            closedList.add(bestNode);
            openList.remove(bestNode);
        }
        //If no path is found, then give the smallest fscore node in the closed list which contains all nodes now..
        return getSmallestNode(closedList).getPathAsVectors();
    }

    /*
    Inputs: An arraylist of nodes.

    Outputs: Returns the node with the smallest fScore in passed list.

    Called by: getPath()

    Calls: None.
    */
    private static Node getSmallestNode(ArrayList<Node> list){
        Node smallest = null;
        for(Node node : list){
            if(node == null) { continue; }
            if(smallest == null){
                smallest = node;
            }
            if(node.fScore < smallest.fScore){
                smallest = node;
            }
        }
        return smallest;
    }

    /*
    Inputs: An int for the position to start searching from, and an int for how many nodes to create in any direction.

    Outputs: Creates the node list, accounting for discontinuous tiles (<4 neighbors) from level data.

    Called by: getPath()

    Calls: none.
    */
    private static ArrayList<Node> createNodeList(Vector2 startingPos, int searchDepth){
        int[][] walkable = GameView.level.walkable;
        Node[][] nodesArr = new Node[searchDepth * 2][searchDepth * 2];
        ArrayList<Node> nodes = new ArrayList<Node>();
        //Limit nodes to passed parameter for performance.
        for(int i = -searchDepth; i < searchDepth; i++){
            if(startingPos.x + i <= 0 || startingPos.x + i > walkable.length - 1){ continue; } // oob
            for(int j = -searchDepth; j < searchDepth; j++){
                if(startingPos.y + j <= 0 || startingPos.y + j > walkable.length - 1) { continue; } // oob
                if(walkable[(int)startingPos.x + i][(int)startingPos.y + j] == 1){
                    Node newNode = new Node(new Vector2((int)startingPos.x + i, (int)startingPos.y + j));
                    nodes.add(newNode);
                    nodesArr[searchDepth + i][searchDepth + j] = newNode;
                }
            }
        }

        //Connect nodes.
        for(int i = 0; i < nodesArr.length - 1; i++){
            for(int j = 0; j < nodesArr.length - 1; j++){
                if(nodesArr[i][j] == null){ continue; }
                if(nodesArr[i + 1][j] != null){
                    nodesArr[i][j].connectedNodes.add(nodesArr[i + 1][j]);
                    nodesArr[i + 1][j].connectedNodes.add(nodesArr[i ][j]);
                }
                if(nodesArr[i][j + 1] != null){
                    nodesArr[i][j].connectedNodes.add(nodesArr[i][j + 1]);
                    nodesArr[i][j + 1].connectedNodes.add(nodesArr[i ][j]);
                }
            }
        }

        return nodes;
    }


        /*
    Inputs: Vector2 Position, ArrayList of nodes to be searched.

    Outputs: Returns node in list passed position.

    Called by: getPath()

    Calls: None.
    */
    private static Node getNodeAtPosition(Vector2 position, ArrayList<Node> nodes){
        for(Node node: nodes){
            if((int)position.x == node.position.x && (int)position.y == node.position.y){
                return node;
            }
        }
        return null;
    }


        /*
    Inputs: Vector2 Position, ArrayList of nodes to be searched.

    Outputs: returns closest node in list to passed position.

    Called by: getPath()

    Calls: None.
    */
    private static Node getClosestNode(Vector2 position, ArrayList<Node> list){
        Node closest = list.get(0);
        float closestDistance = Vector2.getDistance(closest.position, position);
        for(Node n: list){
            float thisDistance = Vector2.getDistance(closest.position, position);
            if(thisDistance < closestDistance){
                closestDistance = thisDistance;
                closest = n;
            }
        }
        return closest;
    }

}
