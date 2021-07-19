import edu.princeton.cs.algs4.Stack;

public class PictureSP {
    private final double[][] graph;
    private final boolean[][] marked;
    private final Stack<Integer> postorderX;
    private final Stack<Integer> postorderY;
    private final double[][] distTo;
    private final int[][] vertexTo;
    private final int[] seam;

    public PictureSP(double[][] graph) {
        this.graph = graph;
        marked = new boolean[graph.length][graph[0].length];
        postorderX = new Stack<>();
        postorderY = new Stack<>();
        distTo = new double[graph.length][graph[0].length];
        for (int x = 0; x < graph.length; x++) {
            for (int y = 0; y < graph[0].length; y++) {
                if (y == 0) {
                    distTo[x][y] = 1000;
                } else {
                    distTo[x][y] = Double.POSITIVE_INFINITY;
                }
            }
        }
        vertexTo = new int[graph.length][graph[0].length];

        for (int i = graph.length - 1; i >= 0; i--) {
            dfs(i, 0);
        }

        for (int postOrderCol : postorderX) {
            int postOrderRow = postorderY.pop();
            if (postOrderRow != graph[0].length - 1 && postOrderCol == 0) {
                relax(postOrderCol, postOrderCol, postOrderRow + 1);
                relax(postOrderCol, postOrderCol + 1, postOrderRow + 1);
            } else if (postOrderRow != graph[0].length - 1 && postOrderCol != 0 && postOrderCol != graph.length - 1) {
                relax(postOrderCol, postOrderCol - 1, postOrderRow + 1);
                relax(postOrderCol, postOrderCol, postOrderRow + 1);
                relax(postOrderCol, postOrderCol + 1, postOrderRow + 1);
            } else if (postOrderRow != graph[0].length - 1 && postOrderCol == graph.length - 1) {
                relax(postOrderCol, postOrderCol - 1, postOrderRow + 1);
                relax(postOrderCol, postOrderCol, postOrderRow + 1);
            }
        }

        double min = distTo[0][graph[0].length - 1];
        int minInd = 0;
        for (int i = 0; i < graph.length; i++) {
            if (distTo[i][graph[0].length - 1] < min) {
                min = distTo[i][graph[0].length - 1];
                minInd = i;
            }
        }

        seam = new int[graph[0].length];
        for (int i = graph[0].length - 1; i >= 0; i--) {
            seam[i] = minInd;
            minInd = vertexTo[minInd][i];
        }
    }

    private void dfs(int col, int row) {
        marked[col][row] = true;
        if (row != graph[0].length - 1 && col == 0) {
            if (!marked[col][row + 1]) {
                dfs(col, row + 1);
            }
            if (!marked[col + 1][row + 1]) {
                dfs(col + 1, row + 1);
            }
        } else if (row != graph[0].length - 1 && col != 0 && col != graph.length - 1) {
            if (!marked[col - 1][row + 1]) {
                dfs(col - 1, row + 1);
            }
            if (!marked[col][row + 1]) {
                dfs(col, row + 1);
            }
            if (!marked[col + 1][row + 1]) {
                dfs(col + 1, row + 1);
            }
        } else if (row != graph[0].length - 1 && col == graph.length - 1) {
            if (!marked[col - 1][row + 1]) {
                dfs(col - 1, row + 1);
            }
            if (!marked[col][row + 1]) {
                dfs(col, row + 1);
            }
        }
        postorderX.push(col);
        postorderY.push(row);
    }

    private void relax(int colFrom, int col, int row) {
        if (distTo[col][row] > distTo[colFrom][row - 1] + graph[col][row]) {
            distTo[col][row] = distTo[colFrom][row - 1] + graph[col][row];
            vertexTo[col][row] = colFrom;
        }
    }

    public int[] getSeam() {
        return seam;
    }
}
