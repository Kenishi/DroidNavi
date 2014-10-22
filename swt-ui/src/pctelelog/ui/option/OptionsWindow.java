package pctelelog.ui.option;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;

import pctelelog.ui.PreferenceKey;
import pctelelog.ui.PreferenceManager;
import pctelelog.ui.WindowWidget;
import pctelelog.ui.notify.EventHandler;

public class OptionsWindow implements WindowWidget, SelectionListener {
	private Display m_mainDisplay = null;
	private Shell m_windowShell = null;
	
	private final EventHandler m_eventHandler;
	private OptionEvent m_event = null;
	private OptionEffect m_effect = null;
	
	/*
	 * Set on close to show saved status.
	 *
	 * Needed so that window can tell if it needs
	 * to revert settings or not.  
	 */
	private boolean m_isSaved = false;
	
	public OptionsWindow(Display display, EventHandler handler) {
		if(display == null) { 
			throw new NullPointerException("Display cannot be null.");
		}
		if(handler == null) {
			throw new NullPointerException("Event Handler cannot be null.");
		}
		
		m_mainDisplay = display;
		m_windowShell = new Shell(getMainDisplay(), SWT.CLOSE | SWT.APPLICATION_MODAL);
		m_windowShell.setText("Options");
		
		m_eventHandler = handler;
		
		init();
		m_windowShell.pack();
	}
	
	public Shell getWindowShell() {
		return m_windowShell;
	}

	public Display getMainDisplay() {
		return m_mainDisplay;
	}
	
	public void open() {
		getWindowShell().open();
	}
	
	public void widgetSelected(SelectionEvent e) {
		Button selected = (Button) e.getSource();
		
		if(selected.getText().contains("OK")) {
			saveSettings();
			m_isSaved = true;
		}

		getWindowShell().dispose();
	}

	public void widgetDefaultSelected(SelectionEvent e) {}
	
	private void init() {
		initLayout();
		initTabFolder();
		initButtons();
		initDisposeListener();
	}
	
	private void initLayout() {
		GridLayout layout = new GridLayout(1, true);
		layout.marginBottom = 0;
		
		if(System.getProperty("os.name").toLowerCase().contains("mac")) {
			// Pull buttons up closer to frame
			layout.verticalSpacing = -5;
		}
		
		getWindowShell().setLayout(layout);
	}
	
	private void initTabFolder() {
		TabFolder tabFolder = new TabFolder(getWindowShell(), SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		
		m_event = new OptionEvent(tabFolder);
		m_effect = new OptionEffect(tabFolder, m_eventHandler);
	}
	
	private void initButtons() {
		Composite buttons = new Composite(getWindowShell(), SWT.NO_FOCUS);
		buttons.setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, true, false));
		
		GridLayout layout = new GridLayout(2,false);
		layout.marginHeight = 0;
		layout.verticalSpacing = 0;
		buttons.setLayout(layout);

		GridData gd = new GridData(SWT.RIGHT, SWT.NONE, false, false);
		gd.widthHint = 80;

		Button ok = new Button(buttons, SWT.PUSH);
		ok.setText("OK");
		ok.setToolTipText("Save the settings and close the window.");
		ok.setLayoutData(gd);
		ok.addSelectionListener(this);
		
		Button cancel = new Button(buttons, SWT.PUSH);
		cancel.setText("Cancel");
		cancel.setToolTipText("Cancel currently made settings and close the window.");
		cancel.setLayoutData(gd);
		cancel.addSelectionListener(this);
		getWindowShell().setDefaultButton(cancel);
	}
	
	private void initDisposeListener() {
		getWindowShell().addDisposeListener(new DisposeListener() {
			
			public void widgetDisposed(DisposeEvent e) {
				if(!m_isSaved) {
					m_effect.revertState();
				}
			}
		});
	}
	
	/**
	 * Save the option window settings to storage
	 * 
	 */
	private void saveSettings() {
		HashMap<PreferenceKey, Serializable> pref = new HashMap<PreferenceKey, Serializable>();
		
		pref.putAll(m_effect.getOptions());
		pref.putAll(m_event.getOptions());
		
		Set<PreferenceKey> keys = pref.keySet();
		PreferenceManager prefManager = PreferenceManager.getPreferenceManager();
		
		for(PreferenceKey key : keys) {
			prefManager.set(key, pref.get(key));
		}
	}
}
