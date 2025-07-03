package student;

import game.EscapeState;
import game.ExplorationState;
import game.NodeStatus;
import java.util.*;

/*
Command to run program: gradle run -PchooseMain=main.TXTmain --args="-n 100"
Command to run tests: gradle test
Explorer class  uses Binary Search Tree (BST) to find the orb
Single Responsibility Principle: Handles exploration logic
*/

public class Explorer {
    /*
    Counter to track number of steps taken to find orb
    Maps each node ID to a set of its neighboring node IDs
    Tracks which nodes have been visited during exploration
    */
    private int stepCount = 0;
    private final Map<Long, Set<Long>> graph = new HashMap<>();
    private final Set<Long> visited = new HashSet<>();
    
    /*
    Explores the cavern to find the orb using a BST-based approach
    The BST maintains nodes ordered by their distance to target
    Navigates to selected node directly if adjacent, or via path if not
     */
    public void explore(ExplorationState state) {
        // Reset for new exploration
        reset();
        
        // BST to maintain nodes ordered by distance to target
        BinarySearchTree bst = new BinarySearchTree();
        
        // Track nodes that have been seen but not fully explored as a hashmap
        Map<Long, Integer> nodeDistances = new HashMap<>();
        
        // Initialize with starting position
        long start = state.getCurrentLocation();
        visited.add(start);
        
        /* This is the primary exploration loop that implements a breadth-first search algorithm
				which tries to expand the node that is closest to the target.
				(The 5 Most Powerful Pathfinding Algorithms - https://www.graphable.ai/)
				(Breadth First Search (BFS) Algorithm with EXAMPLE -
				https://www.guru99.com/breadth-first-search-bfs-graph-example.html)
				(Breadth First Search or BFS for a Graph - https://www.geeksforgeeks.org/breadth-first-search-or-bfs-for-a-graph/)
				(Refactoring and Design Patterns - https://refactoring.guru/)
				*/
        while (state.getDistanceToTarget() != 0) {
            long current = state.getCurrentLocation();
            
            // Update graph with neighbors
            updateGraph(state, current);
            
            // Add unvisited neighbors to BST
            for (NodeStatus neighbor : state.getNeighbours()) {
                if (!visited.contains(neighbor.nodeID())) {
                    // Add to BST ordered by distance to target
                    bst.insert(neighbor.nodeID(), neighbor.distanceToTarget());
                    nodeDistances.put(neighbor.nodeID(), neighbor.distanceToTarget());
                }
            }
            
            /* Get next node to explore from BST (closest to target) by using
						the BST to maintain the unvisited	nodes ordered by distance 

						*/
            Long nextNode = selectNextNode(state, current, bst, nodeDistances);
            
            if (nextNode != null) {
                // Navigate to the selected node
                if (isAdjacent(state, nextNode)) {
                    // Direct neighbor - move directly
                    state.moveTo(nextNode);
                    stepCount++;
                    visited.add(nextNode);
                    bst.remove(nextNode);
                } else {
                    // Not adjacent - find path and move
                    List<Long> path = findPath(current, nextNode);
                    if (path != null && path.size() > 1) {
                        // Move one step towards target
                        state.moveTo(path.get(1));
                        stepCount++;
                        visited.add(path.get(1));
                        
                        // Remove from BST if it was just visited
                        if (path.get(1).equals(nextNode)) {
                            bst.remove(nextNode);
                        }
                    }
                }
            } else {
                // Provides a backup strategy when the BST is empty, explore any unvisited neighbor
                NodeStatus fallback = findFallbackMove(state);
                if (fallback != null) {
                    state.moveTo(fallback.nodeID());
                    stepCount++;
                    visited.add(fallback.nodeID());
                    bst.remove(fallback.nodeID());
                }
            }
        }
        
        System.out.println("Steps taken to find the orb: " + stepCount);

    }
    
    /*
     Prioritizes adjacent nodes with minimum distance. If no adjacent node is the minimum, 
		 returns the overall minimum from the BST.
		 Single Responsibility Principle: Node selection logic.

     */
    private Long selectNextNode(ExplorationState state, long current, 
            BinarySearchTree bst, Map<Long, Integer> distances) {
        // First, try to get adjacent node with minimum distance
        for (NodeStatus neighbor : state.getNeighbours()) {
            if (!visited.contains(neighbor.nodeID()) && bst.contains(neighbor.nodeID())) {
                // Check if this is the minimum in BST
                Long minNode = bst.findMin();
                if (minNode != null && minNode.equals(neighbor.nodeID())) {
                    return minNode;
                }
            }
        }
        
        // If no adjacent minimum, get overall minimum from BST
        return bst.findMin();
    }
    
    // Helper method that checks if a node is directly reachable from the current position
    private boolean isAdjacent(ExplorationState state, long nodeId) {
        for (NodeStatus neighbor : state.getNeighbours()) {
            if (neighbor.nodeID() == nodeId) {
                return true;
            }
        }
        return false;
    }
    
    // Find fallback move when BST is empty
    private NodeStatus findFallbackMove(ExplorationState state) {
        NodeStatus best = null;
        
        // Find unvisited neighbor closest to target
        for (NodeStatus neighbor : state.getNeighbours()) {
            if (!visited.contains(neighbor.nodeID())) {
                if (best == null || neighbor.distanceToTarget() < best.distanceToTarget()) {
                    best = neighbor;
                }
            }
        }
        
        // If all visited, pick any neighbor closest to target
        if (best == null) {
            for (NodeStatus neighbor : state.getNeighbours()) {
                if (best == null || neighbor.distanceToTarget() < best.distanceToTarget()) {
                    best = neighbor;
                }
            }
        }
        
        return best;
    }
    
    // Helper method that clears all data structures for a new exploration
    private void reset() {
        stepCount = 0;
        graph.clear();
        visited.clear();
    }
    
    /*
    Checks if the current node already exists in the graph, 
        If not, creates a new entry with an empty set of neighbors
        Updates the graph structure with newly discovered neighbors.
				The graph is built dynamically as the explorer discovers new areas (https://en.wikipedia.org/wiki/Breadth-first_search)
    */
    private void updateGraph(ExplorationState state, long current) {
        if (!graph.containsKey(current)) {
            graph.put(current, new HashSet<>());
        }

    /*
    For each neighbor of the current node,
    adds the neighbor's ID to the current node's set of neighbors,
    creates an entry for the neighbor if it doesn't exist yet,
    adds the current node's ID to the neighbor's set.
    The method creates edges in both directions: current → neighbor and neighbor → current
    which allows bidirectional traversal.
    */
        for (NodeStatus neighbor : state.getNeighbours()) {
            long neighborId = neighbor.nodeID();
            graph.get(current).add(neighborId);
            
            if (!graph.containsKey(neighborId)) {
                graph.put(neighborId, new HashSet<>());
            }
            graph.get(neighborId).add(current);
        }
    }
    
    /*
    Find path using BFS to process nodes in the order they are discovered
     */
    private List<Long> findPath(long start, long end) {
        if (start == end) {
            return Arrays.asList(start);
        }
    /*
    Java interface that holds nodes to be explored
    parent: Tracks each node's predecessor for path reconstruction
    visited: Prevents revisiting nodes to avoid cycles
    */    
        Queue<Long> queue = new LinkedList<>();
        Map<Long, Long> parent = new HashMap<>();
        Set<Long> visitedInPath = new HashSet<>();
    // offer method adds element to the rear of the queue. 
        queue.offer(start);
        visitedInPath.add(start);
        
        while (!queue.isEmpty()) {
            // Removes element from front of queue.
            long current = queue.poll();
            
            if (current == end) {
                // Reconstruct path
                List<Long> path = new ArrayList<>();
                long node = end;
                while (node != start) {
                    path.add(0, node);
                    node = parent.get(node);
                }
                path.add(0, start);
                return path;
            }
            
            Set<Long> neighbors = graph.get(current);
            if (neighbors != null) {
                for (long neighbor : neighbors) {
                    if (!visitedInPath.contains(neighbor)) {
                        visitedInPath.add(neighbor);
                        parent.put(neighbor, current);
                        queue.offer(neighbor);
                    }
                }
            }
        }
        
        return null;
    }

/* Binary Search Tree (BST) for maintaining nodes ordered by distance
 * Single Responsibility: BST data structure operations
 */

class BinarySearchTree {
    private TreeNode root;
    
    /*
     * Node class for BST
     */
    private static class TreeNode {
        long nodeId;
        int distance;
        TreeNode left;
        TreeNode right;
        
        TreeNode(long nodeId, int distance) {
            this.nodeId = nodeId;
            this.distance = distance;
        }
    }
    
    /*
    Inserts nodes ordered by distance
    Nodes with same distance are ordered by nodeId
     */
    public void insert(long nodeId, int distance) {
        root = insertRec(root, nodeId, distance);
    }
    
    private TreeNode insertRec(TreeNode root, long nodeId, int distance) {
        if (root == null) {
            return new TreeNode(nodeId, distance);
        }
        
        // Compare by distance first, then by nodeId
        if (distance < root.distance || 
            (distance == root.distance && nodeId < root.nodeId)) {
            root.left = insertRec(root.left, nodeId, distance);
        } else if (distance > root.distance || 
                   (distance == root.distance && nodeId > root.nodeId)) {
            root.right = insertRec(root.right, nodeId, distance);
        }
        
        return root;
    }
    
    /*
    Returns the node with minimum distance (leftmost node)
     */
    public Long findMin() {
        if (root == null) return null;
        
        TreeNode current = root;
        while (current.left != null) {
            current = current.left;
        }
        return current.nodeId;
    }
    
    /*
     * Remove a node from BST
     */
    public void remove(long nodeId) {
        root = removeRec(root, nodeId);
    }
    
    private TreeNode removeRec(TreeNode root, long nodeId) {
        if (root == null) return null;
        
        if (nodeId == root.nodeId) {
            // Node found - remove it
            if (root.left == null) return root.right;
            if (root.right == null) return root.left;
            
            // Node has two children - replace with inorder successor
            TreeNode minRight = findMinNode(root.right);
            root.nodeId = minRight.nodeId;
            root.distance = minRight.distance;
            root.right = removeRec(root.right, minRight.nodeId);
        } else {
            // Search in subtrees
            root.left = removeRec(root.left, nodeId);
            root.right = removeRec(root.right, nodeId);
        }
        
        return root;
    }
    
    private TreeNode findMinNode(TreeNode node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }
    
    /*
     * Check if BST contains a node, searches the entire tree for a node
     */
    public boolean contains(long nodeId) {
        return containsRec(root, nodeId);
    }
    
    private boolean containsRec(TreeNode root, long nodeId) {
        if (root == null) return false;
        if (root.nodeId == nodeId) return true;
        return containsRec(root.left, nodeId) || containsRec(root.right, nodeId);
    }
    
    // Get all nodes in order (for debugging)
    public List<Long> inorderTraversal() {
        List<Long> result = new ArrayList<>();
        inorderRec(root, result);
        return result;
    }
    
    private void inorderRec(TreeNode root, List<Long> result) {
        if (root != null) {
            inorderRec(root.left, result);
            result.add(root.nodeId);
            inorderRec(root.right, result);
        }
    }
}
    
    /**
     * Escape from the cavern before the ceiling collapses, trying to collect as much
     * gold as possible along the way. Your solution must ALWAYS escape before time runs
     * out, and this should be prioritized above collecting gold.
     * <p>
     * You now have access to the entire underlying graph, which can be accessed through EscapeState.
     * getCurrentNode() and getExit() will return you Node objects of interest, and getVertices()
     * will return a collection of all nodes on the graph.
     * <p>
     * Note that time is measured entirely in the number of steps taken, and for each step
     * the time remaining is decremented by the weight of the edge taken. You can use
     * getTimeRemaining() to get the time still remaining, pickUpGold() to pick up any gold
     * on your current tile (this will fail if no such gold exists), and moveTo() to move
     * to a destination node adjacent to your current node.
     * <p>
     * You must return from this function while standing at the exit. Failing to do so before time
     * runs out or returning from the wrong location will be considered a failed run.
     * <p>
     * You will always have enough time to escape using the shortest path from the starting
     * position to the exit, although this will not collect much gold.
     *
     * @param state the information available at the current state
     */
    public void escape(EscapeState state) {
        // TODO: Escape from the cavern before time runs out. This is not required for individual assignment
    }
}

