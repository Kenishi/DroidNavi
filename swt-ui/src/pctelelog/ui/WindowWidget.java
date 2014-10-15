package pctelelog.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public interface WindowWidget {
	public Shell getWindowShell();
	public Display getMainDisplay();
	public void open();
}
