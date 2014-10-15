package pctelelog.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

import pctelelog.TeleLogServer;
import pctelelog.TeleLogServer.RESULT;

public class DroidNavi {
	public static final String VERSION = "1.2";
	
	private static final Logger logger = LogManager.getLogger(DroidNavi.class);
	
	private TeleLogServer m_server = null;
	private RESULT m_startStatus = null;
	
	private final Display m_display;
	
	public DroidNavi(boolean resetPref) {
		m_server = startServer();
		
		Display.setAppName("Droid Navi");
		Display.setAppVersion(VERSION);
		m_display = Display.getDefault();
		
		// Check for pref reset
		if(resetPref) {
			PreferenceManager.getPreferenceManager().clear();
		}
		
		MainWindow main = new MainWindow(m_server, m_display);
		switch(m_startStatus) {
			case MULTI_BIND_EXCEPTION:
			case TCP_BIND_EXCEPTION:
				showBindErrorMessage(main, m_startStatus);
				main.getWindowShell().dispose();
				break;
			case OTHER_EXCEPTION:
				showUnknownException(main, m_startStatus);
				main.getWindowShell().dispose();
				break;
			case SUCCESS:
				main.open();			
		}

		
		while(!main.getWindowShell().isDisposed()) {
			try {
				if(!m_display.readAndDispatch()) {
					m_display.sleep();
				}
			}  
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		if(!main.getWindowShell().isDisposed()) {
			main.getWindowShell().dispose();
		}
		m_display.dispose();
		
		// Only shutdown server if it was started
		if(m_server != null) {
			m_server.shutdown();
			m_server = null;
		}
	}
	
	public static void main(String[] args) {
		boolean resetPref = false;
		for(String arg : args) {
			if(arg.contains("-resetPref")) {
				resetPref = true;
			}
		}
		new DroidNavi(resetPref);
	}
	
	private TeleLogServer startServer() {
		logger.entry();
		
		TeleLogServer server = new TeleLogServer();
		RESULT startResult = server.start();
		if(startResult != RESULT.SUCCESS) {
			if(startResult == RESULT.MULTI_BIND_EXCEPTION || 
					startResult == RESULT.TCP_BIND_EXCEPTION) {
				server = null;
			}
		}
		
		m_startStatus = startResult;
		logger.exit(startResult);
		return server;
	}

	/**
	 * Show bind error message when either TCP or UDP bind
	 * fails.
	 * 
	 * @param main the main window for the application
	 * @param result the result from starting the server
	 */
	private void showBindErrorMessage(MainWindow main, RESULT result) {
		MessageBox msg = new MessageBox(main.getWindowShell(), SWT.ICON_ERROR | SWT.OK);
		if(result == RESULT.TCP_BIND_EXCEPTION) {
			msg.setMessage("Error binding to port. Another program is using port: " +
					TeleLogServer.TCP_LISTEN_PORT + ".\nClosing application.");
		}
		else {
			msg.setMessage("Error binding to port. Another program is using port: " +
					TeleLogServer.MULTI_LIST_PORT + ".\nClosing application.");
		}
		msg.setText("Error");
		msg.open();
	}
	
	/**
	 * Show error message for an unknown error
	 * 
	 * @param main the main window for the application
	 * @param result the result from starting the server
	 */
	private void showUnknownException(MainWindow main, RESULT result) {
		MessageBox msg = new MessageBox(main.getWindowShell(), SWT.ICON_ERROR | SWT.OK);
		msg.setMessage("Unknown exception occured during startup.\n\n" + result.getCause().getClass().getName() +
				"\n" + result.getCause().getMessage());
		msg.setText("Error");
		msg.open();
	}
}
