package pctelelog.ui.notify;

import org.eclipse.swt.widgets.Shell;


/**
 * Fade Effect
 * <p>
 * Event notification will fade in and out at the x and y
 * coordinates specified by the window shell when it is 
 * passed in.
 * <p>
 * The speed of the fade in can be changed prior to running
 * the effect, but cannot be changed after the effect has started.
 * 
 * @author Jeremy May
 *
 */
public class FadeEffect extends EventWindowEffect {
	
	public final int FADE_QUICK = 50;
	public final int FADE_SLOW = 10;
	
	public final int FADE_WAIT = 100; // Milliseconds between fade change
	
	private int m_speed = FADE_SLOW;
	private boolean isRunning = false;
	
	public FadeEffect(final EventWindow display) {
		super(display);
		m_type = EffectType.FADE;
	}
	
	public void setFadeSpeed(int speed) {
		if(isRunning) return;
		if(speed < 0 || speed > 100) { throw new IllegalArgumentException("Invalid speed: 0 < speed < 100"); }
		m_speed = speed;
	}

	@Override
	public void run() {
		isRunning = true;
		EventWindow window = getEventWindow();
		Shell windowShell = window.getWindowShell();
		
		windowShell.setAlpha(0);
		int alpha = 0;
		windowShell.open();
				
		// Fade in
		int time = 0;
		while(alpha < 255) {
			alpha = (alpha + m_speed) > 255 ? 255 : alpha + m_speed;
			time = time + FADE_WAIT;
			window.getMainDisplay().timerExec(time, new Fade(alpha, windowShell));
		}
		
		// Hold for SHOW_TIME 
		time = time + SHOW_TIME;
		
		// Fade out
		while(alpha > 0) {
			alpha = (alpha - m_speed) < 0 ? 0 : alpha - m_speed;
			time = time + FADE_WAIT;
			window.getMainDisplay().timerExec(time, new Fade(alpha, windowShell));
		}
		
		// Close the window
		time = time + 100;
		window.getMainDisplay().timerExec(time, new Runnable() {
			public void run() {
				getEventWindow().closeAfterEffect();
			}
		});

	}

	private class Fade implements Runnable {
		private final int m_alpha;
		private final Shell m_window; 
		
		public Fade(int alpha, Shell window) {
			m_alpha = alpha;
			m_window = window;
		}
		
		public void run() {
			m_window.setAlpha(m_alpha);
		}
		
	}
}
