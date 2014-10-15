package pctelelog.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

public class TrayIcon {

	private final Display m_mainDisplay;
	private final MainWindow m_mainWindow;
	private final TrayItem m_item;
	private final Image m_logo;
	
	private Menu m_trayMenu = null;
	
	public TrayIcon(Display display, MainWindow window) {
		if(display == null) { throw new NullPointerException("Display cannot be null."); }
		if(window == null) { throw new NullPointerException("Main window cannot be null."); }
		
		m_mainDisplay = display;
		m_mainWindow = window;
		
		// Create tray item
		Tray tray = display.getSystemTray();
		
		m_item = new TrayItem(tray, SWT.NONE);
		m_item.setText("Droid Navi");
		
		// Set Image
		m_logo = AppLogo.getLogo(AppLogo.LogoType.TRAY_ICON, display);
		m_item.setImage(m_logo);
		
		init(m_item);
	}
	
	private void init(TrayItem item) {
		initContextMenu();
		initListeners();
	}
	
	/**
	 * Right Click Menu for the Icon
	 */
	private void initContextMenu() {
		m_trayMenu = new Menu(m_mainWindow.getWindowShell(), SWT.POP_UP);
		
		// Show
		MenuItem menu_show = new MenuItem(m_trayMenu, SWT.PUSH);
		menu_show.setText("Show");
		menu_show.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				m_mainWindow.getWindowShell().setVisible(true);
				closeTray();
			}
		});
		
		// Options
		MenuItem menu_opt = new MenuItem(m_trayMenu, SWT.PUSH);
		menu_opt.setText("Options");
		menu_opt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				m_mainWindow.showOptions();
			}
		});
		
		// Exit
		MenuItem menu_exit = new MenuItem(m_trayMenu, SWT.PUSH);
		menu_exit.setText("Exit");
		menu_exit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				/*
				 * By adding the dispose routine as an asyn task,
				 * this increases the likelihood that Tray will dispose
				 * first following by the shell and the app.  
				 */
				m_mainDisplay.asyncExec(new Runnable() {
					public void run() {
						m_mainWindow.getWindowShell().dispose();
					}
				});
				closeTray();
			}
		});
	}
	
	/**
	 * Set up Menu listener and dbl-click show
	 * 
	 */
	private void initListeners() {
		// Menu Listener
		m_item.addMenuDetectListener(new MenuDetectListener() {
			public void menuDetected(MenuDetectEvent e) {
				m_trayMenu.setVisible(true);
			}
		});
		
		// Setup Double-click show
		if(System.getProperty("os.name").toLowerCase().startsWith("win")) {
			m_item.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if((e.stateMask & SWT.MouseDoubleClick) != 0) {
						m_mainWindow.getWindowShell().setVisible(true);
						closeTray();
					}
				}
			});
		}
	}
	
	/**
	 * Close the tray icon out and release
	 * all resources
	 * 
	 */
	private void closeTray() {
		m_logo.dispose();
		m_item.dispose();
		m_trayMenu.dispose();
	}
}
