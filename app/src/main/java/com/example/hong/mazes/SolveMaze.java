package com.example.hong.mazes;

/**
 * Created by hong on 2016-05-25.
 */
public class SolveMaze {
	int width, height;
	static int[][] maze; // The maze
	boolean[][] wasHere;
	boolean[][] correctPath; // The solution to the maze
	int startX, startY; // Starting X and Y values of maze
	int endX, endY;     // Ending X and Y values of maze
	public static boolean inMaze;

	public SolveMaze(int width, int height) {
		this.width = width;
		this.height = height;

		maze = new int[width][height];
		wasHere = new boolean[width][height];
		correctPath = new boolean[width][height];
		startX = 0; // start
		startY = 0;
		endX = this.width - 1; // exit
		endY = this.height - 1;
	}

	public void solveMaze() {
		generateMaze(); // Create Maze w * h (1 = path, 2 = wall)
		for (int row = 0; row < maze.length; row++)
			// Sets boolean Arrays to default values
			for (int col = 0; col < maze[row].length; col++) {
				wasHere[row][col] = false;
				correctPath[row][col] = false;
			}
		inMaze = recursiveSolve(startX, startY);
		// Will leave you with a boolean array (correctPath)
		// with the path indicated by true values.
		// If b is false, there is no solution to the maze
	}

	public void generateMaze() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				// randomly get double (0 to 99)
				double d = Math.random() * 100;
				if (d < 50) maze[i][j] = 1; // 50% road
				else maze[i][j] = 2; // 50% wall
			}
		}
		maze[0][0] = 1; // start
		maze[width - 1][height - 1] = 1; // exit
	}

	public boolean recursiveSolve(int x, int y) {
		if (x == endX && y == endY) return true; // If you reached the end
		if (maze[x][y] == 2 || wasHere[x][y]) return false;
		// If you are on a wall or already were here
		wasHere[x][y] = true;
		if (x != 0) // Checks if not on left edge
			if (recursiveSolve(x - 1, y)) { // Recalls method one to the left
				correctPath[x][y] = true; // Sets that path value to true;
				return true;
			}
		if (x != width - 1) // Checks if not on right edge
			if (recursiveSolve(x + 1, y)) { // Recalls method one to the right
				correctPath[x][y] = true;
				return true;
			}
		if (y != 0)  // Checks if not on top edge
			if (recursiveSolve(x, y - 1)) { // Recalls method one up
				correctPath[x][y] = true;
				return true;
			}
		if (y != height - 1) // Checks if not on bottom edge
			if (recursiveSolve(x, y + 1)) { // Recalls method one down
				correctPath[x][y] = true;
				return true;
			}
		return false;
	}
}