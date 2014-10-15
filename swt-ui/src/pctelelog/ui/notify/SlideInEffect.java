package pctelelog.ui.notify;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;


public class SlideInEffect extends EventWindowEffect {
	private static final int BOUNCE = 5;
	private static final int MOVE_INTERVAL = 5; // Milliseconds
	private static final int SPEED = BOUNCE * 2;
	
	private final WindowLocation m_loc;
	
	public SlideInEffect(final EventWindow display) {
		super(display);
		
		m_type = EffectType.SLIDE;
		m_loc = display.getWindowLocation();
		if(m_loc == null) { throw new NullPointerException("Location cannot be null"); }
	}

	@Override
	public void run() {
		final Shell window = getEventWindow().getWindowShell();
		
		Rectangle windowLoc = getEventWindow().getWindowShell().getBounds();
		Rectangle desktopArea = getEventWindow().getMainDisplay().getBounds();
		
		Point restingPoint = new Point(windowLoc.x, windowLoc.y);
		
		// Move based on side
		int time = 0;
		if(m_loc == WindowLocation.BOTTOM_LEFT || m_loc == WindowLocation.TOP_LEFT) {
			time = moveLeft(window, desktopArea, restingPoint);
		}
		else {
			time = moveRight(window, desktopArea, restingPoint);
		}
		
		// Hold for SHOW_TIME
		time += SHOW_TIME;
		getEventWindow().getMainDisplay().timerExec(time, new Runnable() {
			public void run() {
				getEventWindow().closeAfterEffect();
			}
		});
		
	}
	
	/**
	 * Perform the slide when the location is on the left
	 * 
	 */
	private int moveLeft(Shell window, Rectangle desktopArea, Point restingPoint) {
		// Set window outside screen
		int startX = desktopArea.x - window.getSize().x - 1;
		window.setLocation(startX, restingPoint.y);
		
		// Start Slide
		window.open();
		window.update();
		
		int time = 0;
		
		// Move to resting
		for(int i=startX; i <= restingPoint.x; i+=SPEED, time=+MOVE_INTERVAL) {
			getEventWindow().getMainDisplay().timerExec(time, new Move(window, i, restingPoint.y));
		}
		
		// Bounce
		boolean moveLeft = false;
		for(int i=BOUNCE; i >= 0; i--) {
			// Move to side
			int newX = restingPoint.x;
			for(int j=0; j <= i; j++, time+=MOVE_INTERVAL) {
				newX = moveLeft ? newX-SPEED : newX+SPEED;
				getEventWindow().getMainDisplay().timerExec(time, new Move(window, newX, restingPoint.y));
			}
			// Move back to center
			for(; newX != restingPoint.x; time+=MOVE_INTERVAL) {
				newX = moveLeft ? newX+SPEED : newX-SPEED;
				getEventWindow().getMainDisplay().timerExec(time, new Move(window, newX, restingPoint.y));
			}
			moveLeft = !moveLeft;
		}
		return time;
	}
	
	/**
	 * Perform the slid when the location is on the right
	 * 
	 * @return current time interval
	 */
	private int moveRight(Shell window, Rectangle desktopArea, Point restingPoint) {
		// Set window outside screen
		window.setLocation(desktopArea.width+1, restingPoint.y);
		
		// Start Slide
		window.open();
		window.update();
		
		int time = 0;
		
		// Move to resting
		for(int i=desktopArea.width+1; i >= restingPoint.x; i-=SPEED, time=+MOVE_INTERVAL) {
			getEventWindow().getMainDisplay().timerExec(time, new Move(window, i, restingPoint.y));
		}
		
		// Bounce
		boolean moveLeft = true;
		for(int i=BOUNCE; i >= 0; i--) {
			// Move to side
			int newX = restingPoint.x;
			for(int j=0; j <= i; j++, time+=MOVE_INTERVAL) {
				newX = moveLeft ? newX-SPEED : newX+SPEED;
				getEventWindow().getMainDisplay().timerExec(time, new Move(window, newX, restingPoint.y));
			}
			// Move back to center
			for(; newX != restingPoint.x; time+=MOVE_INTERVAL) {
				newX = moveLeft ? newX+SPEED : newX-SPEED;
				getEventWindow().getMainDisplay().timerExec(time, new Move(window, newX, restingPoint.y));
			}
			moveLeft = !moveLeft;
		}
	
		return time;
	}
	
	private class Move implements Runnable {

		private final int m_x;
		private final int m_y;
		private final Shell m_window;
		
		public Move(final Shell window, final int x, final int y) {
			m_window = window;
			m_x = x;
			m_y = y;
		}
		
		public void run() {
			m_window.setLocation(m_x, m_y);
			m_window.redraw();
		}
		
	}
}
