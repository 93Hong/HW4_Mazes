package com.example.hong.mazes;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.widget.Toast;

/**
 * Created by hong on 2016-05-25.
 */
// Special subclass of View that offers a dedicated drawing surface within the View hierarchy
public class DrawingSurface extends SurfaceView implements SurfaceHolder.Callback {
	Canvas cacheCanvas;
	Bitmap backBuffer;
	int width, height, clientHeight;
	int mWidth, mHeight;
	Paint paint;
	Context context;
	SurfaceHolder mHolder;
	int maze[][];


	private Bitmap wall, exit, human;

	public DrawingSurface(Context context) {
		super(context);
		this.context = context;
		init();
	}
	public DrawingSurface(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	private void init() {
		this.maze = SolveMaze.maze;
		mWidth = maze.length; mHeight = maze[0].length;

		mHolder = getHolder();
		mHolder.addCallback(this);

		Resources res = getResources();
		wall = BitmapFactory.decodeResource(res, R.drawable.wall);
		exit = BitmapFactory.decodeResource(res, R.drawable.exit);
		human = BitmapFactory.decodeResource(res, R.drawable.human);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

	public void surfaceCreated(SurfaceHolder holder) {
		width = getWidth(); // initialize window width and height
		height = getHeight();

		cacheCanvas = new Canvas();
		backBuffer = Bitmap.createBitmap( width, height, Bitmap.Config.ARGB_8888); //
		cacheCanvas.setBitmap(backBuffer);
		cacheCanvas.drawColor(Color.WHITE);

		paint = new Paint();
		paint.setColor(Color.BLUE);
		paint.setStrokeWidth(10);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeJoin(Paint.Join.ROUND);

		draw();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {}

	int lastX, lastY, currX, currY, tX = 0, tY = 0, beX = 0, beY = 0;
	boolean isDeleting = false, isOk;
	@Override

	// touch event (phone screen touched)
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		int action = event.getAction();

		switch(action & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				lastX = (int) event.getX();
				lastY = (int) event.getY();

				// check touched point whether human point or not
				if ((beX == (Math.round(event.getX()) / 50)) && (beY == (Math.round(event.getY()) / 50)))
					isOk = true;
				else
					isOk = false;
				break;
			case MotionEvent.ACTION_MOVE:
				if (isDeleting) break;
				currX = (int) event.getX();
				currY = (int) event.getY();
				tX = Math.round(currX) / 50;
				tY = Math.round(currY) / 50;
				if (tX >= maze.length)
					tX--;
				if (tY >= maze[0].length)
					tY--;

				if (isOk) //  if clicked point is not human point, don't draw line
					cacheCanvas.drawLine(lastX, lastY, currX, currY, paint); // draw a line
				lastX = currX;
				lastY = currY;

				// if wall touched, delete line and initialize human point to start point
				if (maze[tX][tY] == 2)
					isDeleting = true;
				break;
			case MotionEvent.ACTION_UP:
				if (isOk) { // if not wall touched, change human point to arrived point
					beX = Math.round(event.getX()) / 50;
					beY = Math.round(event.getY()) / 50;
					cacheCanvas.drawColor(Color.WHITE); // clear line
					// draw human
					cacheCanvas.drawBitmap(human, new Rect(0, 0, 356, 600),
							new Rect(50 * beX, 50 * beY, 50 * (beX + 1), 50 * (beY + 1)), null);
				}
				if(isDeleting) {
					cacheCanvas.drawColor(Color.WHITE);
					beX = 0; beY = 0;
					isDeleting = false;
					cacheCanvas.drawBitmap(exit, new Rect(0, 0, 356, 600),
										new Rect(0, 0, 50, 50), null);
				}
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				cacheCanvas.drawColor(Color.WHITE);
				beX = 0; beY = 0;
				isDeleting = true;
				break;
			case MotionEvent.ACTION_POINTER_UP:
				break;
		}
		draw(); // Draw buffer at SurfaceView
		return true;
	}
	protected void draw() {
		if (clientHeight == 0) { // initialize clientHeight
			clientHeight = getClientHeight();
			height = clientHeight;
			// create bit map
			backBuffer = Bitmap.createBitmap( width, height, Bitmap.Config.ARGB_8888);
			cacheCanvas.setBitmap(backBuffer); // initialize canvas
			cacheCanvas.drawColor(Color.WHITE);
		}
		Canvas canvas = null;

		// draw maze (1 = road, 2 = wall)
		for (int i = 0; i < maze.length; i++) {
			for (int j = 0; j < maze[0].length; j++) {
				// start and exit point to red rectangle
				if ((i == 0 & j == 0) | i == maze.length-1 & j == maze[0].length-1)
					cacheCanvas.drawBitmap(exit, new Rect(0, 0, 200, 200),
							new Rect(50 * i, 50 * j, 50 * (i + 1), 50 * (j + 1)), null);
				// wall to black rectangle
				if (maze[i][j] == 2) {
					cacheCanvas.drawBitmap(wall, new Rect(0, 0, 200, 200),
							new Rect(50 * i, 50 * j, 50 * (i + 1), 50 * (j + 1)), null);
				}
			}
		}
		// set human image to maze
		cacheCanvas.drawBitmap(human, new Rect(0, 0, 356, 600),
				new Rect(50 * beX, 50 * beY, 50 * (beX + 1), 50 * (beY + 1)), null);
		if (beY == mHeight-1 && beX == mWidth-1) {
			Toast.makeText(context, "Maze clear", Toast.LENGTH_SHORT).show();
		}

		try{
			// lock canvas
			canvas = mHolder.lockCanvas(null);
			// back buffer image to screen buffer
			canvas.drawBitmap(backBuffer, 0,0, paint);
		}catch(Exception ex){ // exception handling
			ex.printStackTrace();
		}finally{
			// unlock canvas
			if(mHolder!=null) mHolder.unlockCanvasAndPost(canvas);
		}
	}

	// get client window height. except statusBar height and titleBar height
	private int getClientHeight() {
		Rect rect= new Rect();
		Window window = ((Activity)context).getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(rect);
		int statusBarHeight= rect.top;
		int contentViewTop= window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
		int titleBarHeight= contentViewTop - statusBarHeight;
		return ((Activity)context).getWindowManager().getDefaultDisplay().
				getHeight() - statusBarHeight - titleBarHeight;
	}
} // class DrawingSurface