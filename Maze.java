import java.util.*;
import java.util.Collections;
import java.util.Arrays;

public class Maze {

    private static final int right = 0;
    private static final int down = 1;
    private static final int left = 2;
    private static final int up = 3;
    private static Random randomGenerator;  // for random numbers

    public static int Size;

    public static int[] parent; //added for find
    public static int[] height; //added for unionByHeight

    public static class Point {  // a Point is a position in the maze

        public int x, y;

        // Constructor
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void copy(Point p) {
            this.x = p.x;
            this.y = p.y;
        }
    }

    public static class Edge {
        // an Edge links two neighboring Points:
        // For the grid graph, an edge can be represented by a point and a direction.
        Point point;
        int direction;    // one of right, down, left, up
        boolean used;     // for maze creation
        boolean deleted;  // for maze creation

        // Constructor
        public Edge(Point p, int d) {
            this.point = p;
            this.direction = d;
            this.used = false;
            this.deleted = false;
        }
    }

    public static int find(int x) {
        int r = x;
        while (parent[r] != -1) //find root
            r = parent[r];
        if (x != r) { //compress path//
            int k = parent[x];
            while (k != r) {
                parent[x] = r;
                x = k;
                k = parent[k];
            }
        }
        return r;
    }

    public static void unionByHeight(int i, int j) {
        int ri = height[i];
        int rj = height[j];
        if (ri < rj) parent[i] = j;
        else if(ri > rj) parent[j] = i;
        else {
            height[j]++;
            parent[j] = i;
        }
    }

    // A board is an SizexSize array whose values are Points
    public static Point[][] board;

    // A graph is simply a set of edges: graph[i][d] is the edge
    // where i is the index for a Point and d is the direction
    public static Edge[][] graph;
    public static int N;   // number of points in the graph

    public static void displayInitBoard() {
        System.out.println("\nInitial Configuration:");

        for (int i = 0; i < Size; ++i) {
            System.out.print("    -");
            for (int j = 0; j < Size; ++j) System.out.print("----");
            System.out.println();
            if (i == 0) System.out.print("Start");
            else System.out.print("    |");
            for (int j = 0; j < Size; ++j) {
                if (i == Size-1 && j == Size-1)
                    System.out.print("    End");
                else System.out.print("   |");
            }
            System.out.println();
        }
        System.out.print("    -");
        for (int j = 0; j < Size; ++j) System.out.print("----");
        System.out.println();
    }

    public static Edge[][] createMaze(Edge[][] graph) {
        while(find(0) != find(N - 1)) {
            System.out.println("NEW BOARD");
            int randomCell, randomDirection;
            Edge dummy = new Edge(new Point(0, 0), 0);
            do {
                randomCell = randomGenerator.nextInt(N);
                randomDirection = randomGenerator.nextInt(4);
            }while(graph[randomCell][randomDirection].used);
            System.out.println("a random edge picked...");
            Edge temp = graph[randomCell][randomDirection];
            int x, y = 0;
            x = randomCell;
            switch (randomDirection) {
                case 0: // right
                    if((x + 1) % Size != 0) y = x + 1;
                    break;
                case 1: //down
                    if(x < Size * (Size - 1)) y = x + Size;
                    break;
                case 2: //left
                    if(x % Size != 0) y = x - 1;
                    break;
                case 3: // up
                    if(x >= Size) y = x - Size;
                    break;
            }
            int u = find(x);
            int v = find(y);
            System.out.printf("x: %d, y: %d%n", x, y);
            System.out.printf("u: %d, v: %d%n", u, v);
            if(u != v) {
                unionByHeight(u, v);
                graph[randomCell][randomDirection].deleted = true;
                graph[randomCell][randomDirection].used = true;
            } else graph[randomCell][randomDirection].used = true;
            printMaze();
        }
        return graph;
    }

    public static void printMaze() {
        System.out.println("\nPrinting maze");
        /*first line*/
        System.out.print("    -");
        for (int j = 0; j < Size; ++j) System.out.print("----");
        /*to right and down*/
        for (int i = 0; i < Size; ++i) {
            /* right */
            System.out.println();
            if (i == 0) System.out.print("S");
            else System.out.print("    |");
            for (int j = 0; j < Size; ++j) {
                int currentCell = i * Size + j;
                if (i == Size-1 && j == Size-1)
                    System.out.print("E");
                else {
                    if(graph[currentCell][0].deleted) {
                        System.out.print("    ");
                    }
                    else {
                        System.out.print("   |");
                    }
                }
            }
            System.out.println();

            /* down */
            System.out.print("    -");
            for (int j = 0; j < Size; ++j) {
                int currentCell = i * Size + j;
                if(graph[currentCell][down].deleted) {
                    System.out.print("    ");
                } else {
                    System.out.print("----");
                }
            }
        }
        System.out.println();
    }




    public static void main(String[] args) {

        // Read in the Size of a maze
        Scanner scan = new Scanner(System.in);
        try {
            System.out.println("What's the size of your maze? ");
            Size = scan.nextInt();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        scan.close();


        // Create one dummy edge for all boundary edges.
        Edge dummy = new Edge(new Point(0, 0), 0);
        dummy.used = true;

        // Create board and graph.
        board = new Point[Size][Size];
        N = Size*Size;  // number of points
        graph = new Edge[N][4];
        parent = new int[N];
        height = new int[N];

        graph = new Edge[N][4];

        for (int i = 0; i < Size; ++i)
            for (int j = 0; j < Size; ++j) {
                Point p = new Point(i, j);
                int pindex = i*Size+j;   // Point(i, j)'s index is i*Size + j

                board[i][j] = p;

                graph[pindex][right] = (j < Size-1)? new Edge(p, right): dummy;
                graph[pindex][down] = (i < Size-1)? new Edge(p, down) : dummy;
                graph[pindex][left] = (j > 0)? graph[pindex-1][right] : dummy;
                graph[pindex][up] = (i > 0)? graph[pindex-Size][down] : dummy;

            }

        displayInitBoard();

        // Hint: To randomly pick an edge in the maze, you may
        // randomly pick a point first, then randomly pick
        // a direction to get the edge associated with the point.
        randomGenerator = new Random();
        int i = randomGenerator.nextInt(N);
        System.out.println("\nA random number between 0 and " + (N-1) + ": " + i);
        createMaze(graph);
    }
}
