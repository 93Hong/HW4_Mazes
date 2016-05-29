/*
* 2016.5.25 Hong Gi Wook
* Assignment #4 –Mazes
*
* – N x M SIZE maze (14 * 24)
*	 • The information is stored as a matrix (implemented via a 2-D array)
* – The maze is represented as image blocks (wall or empty block)
* – Start (Start) and Finishing (Exit) point should be clearly identified by special images
*
* Design
* – There is a user icon that indicates the current position of player
* – User touches the path from the current position to the destination of player; to finally
	get the exit point to finish the game.
* - If Human icon clicked, Maze progress
* - If not, nothing happened
* - In progress wall clicked, initialized
* - If reach exit point, Toast message and finish activity.
*
* */

package com.example.hong.mazes;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by hong on 2016-05-25.
 */
public class MainActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SolveMaze s = new SolveMaze(14, 24); // make 14 * 24 maze
		do {
			s.solveMaze();
		} while (!SolveMaze.inMaze); // get randomly generated maze

		setContentView(new DrawingSurface(this)); // start surfaceView
	}
}