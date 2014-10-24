package pctelelog.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import pctelelog.ui.AppLogo.LogoType;

public class AboutWindow implements WindowWidget, DisposeListener {

	private Display m_mainDisplay = null;
	private Shell m_windowShell = null;
	
	private Image m_logo = null;
	
	public AboutWindow(Display display) {
		if(display == null) { throw new NullPointerException("Display cannot be null."); }
		
		m_mainDisplay = display;
		m_windowShell = new Shell(getWindowShell(), SWT.CLOSE | SWT.APPLICATION_MODAL);
		
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

	public void widgetDisposed(DisposeEvent e) {
		m_logo.dispose();
	}

	private void init() {
		initLayout();
		initLogo();
		initText();
		initButton();
		initDisposeListener();
	}
	
	private void initLayout() {
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.fill = true;
		layout.marginLeft = 10;
		layout.marginRight = 10;
		getWindowShell().setLayout(layout);
	}
	
	private void initLogo() {
		m_logo = AppLogo.getLogo(LogoType.LARGE_LOGO, getMainDisplay());
		
		Label logo = new Label(getWindowShell(), SWT.CENTER);
		logo.setImage(m_logo);
	}
	
	private void initText() {
		String txt = 
			"Droid Navi " + DroidNavi.VERSION + "\n\n" +
			"Licensed under LGPL v2.0\n\n" +
			"Libraries in use:\n" +
			"Jackson JSON Processor 1.9.13\n" +
			"Standard Widget Toolkti (SWT) 4.4\n" +
			"QRGen 1.4\n" + 
			"Zxing Java Core 3.0\n" +
			"Log4j2 2.0 RC1";
		
		Label text = new Label(getWindowShell(), SWT.LEFT);
		text.setText(txt);
		
		Label space = new Label(getWindowShell(), SWT.NONE);
		RowData data = new RowData();
		data.height = 10;
		space.setLayoutData(data);
	}
	
	private void initButton() {
		Button ok = new Button(getWindowShell(), SWT.PUSH);
		ok.setText("OK");
		ok.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getWindowShell().dispose();
			}
		});
		
		getWindowShell().setDefaultButton(ok);
	}
	
	private void initDisposeListener() {
		getWindowShell().addDisposeListener(this);
	}
}
