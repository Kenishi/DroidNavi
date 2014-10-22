package pctelelog.ui;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import pctelelog.TeleLogServer;
import pctelelog.ui.AppLogo.LogoType;
import pctelelog.ui.notify.EventHandler;
import pctelelog.ui.option.OptionsWindow;

public class MainWindow implements WindowWidget, DisposeListener {
	private TeleLogServer m_server = null;
	
	private Display m_mainDisplay = null;
	private Shell m_windowShell = null;
	
	private EventHandler m_eventHandler;
	
	/**
	 * Main constructor
	 * @param server a running DroidNavi server, may be null if startup encountered errors
	 * @param display
	 */
	public MainWindow(TeleLogServer server, Display display) {
		if(display == null) { 
			throw new NullPointerException("Display cannot be null");
		}
		
		m_mainDisplay = display;
		m_windowShell = new Shell(getMainDisplay());
		m_server = server;
		m_eventHandler = new EventHandler(getMainDisplay());
		
		/*
		 *  Abort the rest of init if server failed to start
		 *  we will be showing a MessageBox and then exiting
		 */
		if(server == null) {
			return; 
		}
		else {
			init();
		}
	}
	
	public Shell getWindowShell() {
		return m_windowShell;
	}

	public Display getMainDisplay() {
		return m_mainDisplay;
	}
	
	/** 
	 * Open/Show the window
	 */
	public void open() {
		getWindowShell().open();
	}
	
	public void widgetDisposed(DisposeEvent e) {
		// Save current x, y
		Point loc = getWindowShell().getLocation();
		Integer x = Integer.valueOf(loc.x);
		Integer y = Integer.valueOf(loc.y);
		PreferenceManager.getPreferenceManager().set(PreferenceKey.WINDOW_X, x);
		PreferenceManager.getPreferenceManager().set(PreferenceKey.WINDOW_Y, y);
		
		getWindowShell().getImage().dispose();
	}
	
	/**
	 * Show the options window.
	 * <p>
	 * Method that allows other classes to show the
	 * options dialog, such as the Tray Icon.
	 */
	protected void showOptions() {
		new OptionsWindow(getMainDisplay(), m_eventHandler).open();
	}
	
	/**
	 * Window initialization 
	 */
	private void init() {
		initLocation();
		initIcon();
		initMenu();
		initUI();
		initEventHandler();
		initTrayIconListeners();
		
		getWindowShell().addDisposeListener(this);
	}
	
	/**
	 * Set Main Window location
	 */
	private void initLocation() {
		Integer x = (Integer) PreferenceManager.getPreferenceManager()
				.get(PreferenceKey.WINDOW_X, Integer.valueOf(300));
		Integer y = (Integer) PreferenceManager.getPreferenceManager()
				.get(PreferenceKey.WINDOW_Y, Integer.valueOf(300));
		getWindowShell().setLocation(x,y);
	}
	
	/**
	 * Set the App Icon
	 */
	private void initIcon() {
		Image icon = AppLogo.getLogo(LogoType.LARGE_LOGO, getMainDisplay());
		getWindowShell().setImage(icon);
	}
	
	/**
	 * Setup the menus
	 */
	private void initMenu() {
		Shell windowShell = getWindowShell();
		
		// Initialize menu bar based on OS
		initMenuBar();
		
		// Menu bar
		Menu menuBar = getWindowShell().getMenuBar();
		
		if(menuBar == null) {
			getWindowShell().dispose();
		}
		
		// File
		MenuItem menuBar_File = new MenuItem(menuBar, SWT.CASCADE);
		menuBar_File.setText("&File");
		
		Menu fileMenu = new Menu(windowShell, SWT.DROP_DOWN);
		menuBar_File.setMenu(fileMenu);
		
		MenuItem optionsItem = new MenuItem(fileMenu, SWT.PUSH);
		optionsItem.setText("&Options");
		optionsItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				new OptionsWindow(getMainDisplay(), m_eventHandler).open();
			}
		});
		
		MenuItem exitItem = new MenuItem(fileMenu, SWT.PUSH);
		exitItem.setText("Exit");
		exitItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				getMainDisplay().dispose();
			}
		});
		
		// Help
		MenuItem menuBar_Help = new MenuItem(menuBar, SWT.CASCADE);
		menuBar_Help.setText("&Help");
		
		Menu aboutMenu = new Menu(windowShell, SWT.DROP_DOWN);
		menuBar_Help.setMenu(aboutMenu);
		
		MenuItem aboutItem = new MenuItem(aboutMenu, SWT.PUSH);
		aboutItem.setText("&About");
		aboutItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				new AboutWindow(getMainDisplay()).open();
			}
		});
	}
	
	/**
	 * Setup the UI
	 */
	private void initUI() {
		// Set Window Size
		getWindowShell().setSize(225, 300);
		getWindowShell().setText("Droid Navi");
		
		// Set up layout
		GridLayout gridLayout = new GridLayout(1,false);
		gridLayout.marginLeft = 5;
		gridLayout.marginRight = 5;
		getWindowShell().setLayout(gridLayout);
		
		// Set up connected box
		ConnectionBox connectionBox = new ConnectionBox(getWindowShell());
		getServer().addEventListener(connectionBox);
		
		// Set up pairing button
		Button pairing = new Button(getWindowShell(), SWT.PUSH);
		pairing.setText("Pairing");
		pairing.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		pairing.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				new PairingWindow(getMainDisplay()).open();
			}
		});
		
	}
	
	/**
	 * Setup the event handler to receive events
	 */
	private void initEventHandler() {
		getServer().addEventListener(m_eventHandler);
	}
	
	/**
	 * Sets up the listeners needed for showing tray
	 * icon when shell is minimized.
	 */
	private void initTrayIconListeners() {
		final MainWindow mw = this;
		getWindowShell().addShellListener(new ShellAdapter() {
			@Override
			public void shellIconified(ShellEvent e) {
				getWindowShell().setVisible(false);
				new TrayIcon(getMainDisplay(), mw);
			}
		});
	}
	
	private void initMenuBar() {	
		if(System.getProperty("os.name").toLowerCase().contains("mac")) {
			// Do nothing, OS supplies menu bar
		}
		else {
			Menu menu = new Menu(getWindowShell(), SWT.BAR);
			getWindowShell().setMenuBar(menu);
		}
	}
	
	/**
	 * Get the current TeleLogServer instance
	 * @return a telelog server instance
	 */
	private TeleLogServer getServer() {
		return m_server;
	}
}
