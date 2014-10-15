package pctelelog.ui.option;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import pctelelog.ContactInfo;
import pctelelog.ContactInfo.Email;
import pctelelog.ContactInfo.Name;
import pctelelog.PhoneNumber;
import pctelelog.events.IncomingCallEvent;
import pctelelog.ui.PreferenceKey;
import pctelelog.ui.PreferenceManager;
import pctelelog.ui.notify.EffectType;
import pctelelog.ui.notify.EventHandler;
import pctelelog.ui.notify.EventWindowEffect;
import pctelelog.ui.notify.FadeEffect;
import pctelelog.ui.notify.SlideInEffect;
import pctelelog.ui.notify.StandardShowEffect;
import pctelelog.ui.notify.WindowLocation;

public class OptionEffect implements OptionInterface {

	/** Window Related Variables **/
	private final TabFolder m_parentFolder;
	private final TabItem m_tabItem;
	private final EventHandler m_handler;
	private Text m_txtShowTime;
	
	/** State related variables **/
	private final WindowLocation m_oldLocation;
	private final EffectType m_oldEffect;
	
	private WindowLocation m_locationSelected = WindowLocation.BOTTOM_RIGHT;
	private EffectType m_effectSelected = EffectType.STANDARD;
	

	
	public OptionEffect(TabFolder folder, EventHandler handler) {
		if(folder == null) { throw new NullPointerException("Tab Folder cannot be null."); }
		if(handler == null) { throw new NullPointerException("Event Handler cannot be null."); }
		
		m_parentFolder = folder;
		m_tabItem = new TabItem(m_parentFolder, SWT.NONE);
		m_tabItem.setText("Notify Effects");
		m_handler = handler;
		
		m_oldLocation = (WindowLocation) PreferenceManager.getPreferenceManager()
				.get(PreferenceKey.WINDOW_LOCATION, WindowLocation.BOTTOM_RIGHT);
		
		@SuppressWarnings("unchecked")
		Class<? extends EventWindowEffect> clazz = (Class<? extends EventWindowEffect>) PreferenceManager.getPreferenceManager()
				.get(PreferenceKey.EFFECT_TYPE, StandardShowEffect.class);
		m_oldEffect = EffectType.getTypeFromClass(clazz);
		
		init();
	}
	
	/**
	 * Get the option state for this panel
	 * 
	 */
	public HashMap<PreferenceKey, Serializable> getOptions() {
		HashMap<PreferenceKey, Serializable> options = new HashMap<PreferenceKey, Serializable>();
		
		options.put(PreferenceKey.WINDOW_LOCATION, m_locationSelected);
		options.put(PreferenceKey.EFFECT_TYPE, m_effectSelected.getEffectClass());
		
		options.put(PreferenceKey.SHOW_TIME, m_txtShowTime.getText());
		
		return options;
	}
	
	/**
	 * Causes the settings to revert to previous settings
	 * from when the option window was opened.
	 * 
	 */
	public void revertState() {
		PreferenceManager.getPreferenceManager().set(PreferenceKey.EFFECT_TYPE, m_oldEffect.getEffectClass());
		PreferenceManager.getPreferenceManager().set(PreferenceKey.WINDOW_LOCATION, m_oldLocation);		
	}
	
	
	private TabFolder getTabFolder() {
		return m_parentFolder;
	}
	
	private TabItem getTabItem() {
		return m_tabItem;
	}
	
	private void init() {
		Composite mainGroup = initMainGroup(getTabFolder());
		initShowTimeOption(mainGroup);
		initEffectGroup(mainGroup);
		initPositionGroup(mainGroup);
	}
	
	private Composite initMainGroup(TabFolder folder) {
		// Set Tab Item Group
		Composite mainGroup = new Composite(m_parentFolder, SWT.NO_FOCUS);
		mainGroup.setToolTipText("Set how the notification window shows");
		
		// Set Tab Group Layout
		GridLayout tabLayout = new GridLayout(2, true);
		mainGroup.setLayout(tabLayout);
		
		getTabItem().setControl(mainGroup);
		
		return mainGroup;
	}
	
	private void initShowTimeOption(Composite group) {
		Label txtLabel = new Label(group, SWT.LEFT);
		txtLabel.setText("Show Time (ms): ");
		txtLabel.setToolTipText("Set how long notifications are shown for");
		txtLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		
		m_txtShowTime = new Text(group, SWT.SINGLE);
		String defTxt = (String)PreferenceManager.getPreferenceManager().get(PreferenceKey.SHOW_TIME, "4000");
		m_txtShowTime.setText(defTxt);
		m_txtShowTime.setToolTipText("Set how long notifications are shown for");
		m_txtShowTime.setLayoutData(new GridData(40,17));
		
		// Setup Verify
		m_txtShowTime.addVerifyListener(new VerifyListener() {
			
			public void verifyText(VerifyEvent e) {
				if(e.character == SWT.BS || e.character == SWT.DEL) {
					e.doit = true;
					return;
				}
				try {
					Integer.valueOf(e.text);
				} catch(NumberFormatException excep) {
					e.doit = false;
					return;
				}
				e.doit = true;
			}
		});
		
	}
	
	private void initEffectGroup(Composite group) {
		Group effectGroup = new Group(group, SWT.BORDER);
		effectGroup.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		effectGroup.setText("Type");
		effectGroup.setToolTipText("Set the type of effect");
 		
		// Effect Group Layout
		RowLayout effectLayout = new RowLayout(SWT.VERTICAL);
		effectGroup.setLayout(effectLayout);
		
		EffectSelected selectionListener = new EffectSelected();
		
		// Default Effect
		Button standard = new Button(effectGroup, SWT.RADIO);
		standard.setText("Standard");
		standard.setToolTipText("A simple show and then disappear effect");
		standard.setData(EffectType.STANDARD);
		standard.addSelectionListener(selectionListener);
		
		// Fade
		Button fade = new Button(effectGroup, SWT.RADIO);
		fade.setText("Fade");
		fade.setToolTipText("A fade in and out effect");
		fade.setData(EffectType.FADE);
		fade.addSelectionListener(selectionListener);
		
		// Slide In
		Button slide = new Button(effectGroup, SWT.RADIO);
		slide.setText("Slide");
		slide.setToolTipText("A slide in effect");
		slide.setData(EffectType.SLIDE);
		slide.addSelectionListener(selectionListener);
		
		// Set the current effect being used, from Pref
		@SuppressWarnings("unchecked")
		Class<? extends EventWindowEffect> effect = (Class) PreferenceManager.getPreferenceManager()
				.get(PreferenceKey.EFFECT_TYPE, StandardShowEffect.class);
		if(effect == StandardShowEffect.class) { standard.setSelection(true); }
		else if(effect == FadeEffect.class) { fade.setSelection(true); }
		else if(effect == SlideInEffect.class) { slide.setSelection(true); }
		
		// Set current selected effect
		m_effectSelected = EffectType.getTypeFromClass(effect);
	}
	
	private void initPositionGroup(Composite group) {
		Group positionGroup = new Group(group, SWT.BORDER);
		positionGroup.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		positionGroup.setText("Position");
		positionGroup.setToolTipText("Set the location of the notification window");
		
		// Position Group Layout
		RowLayout positionLayout = new RowLayout(SWT.VERTICAL);
		positionGroup.setLayout(positionLayout);
		
		PositionSelected selectionListener = new PositionSelected();
		
		// Top Left
		Button topLeft = new Button(positionGroup, SWT.RADIO);
		topLeft.setText("Top Left");
		topLeft.setData(WindowLocation.TOP_LEFT);
		topLeft.addSelectionListener(selectionListener);
		
		// Top Right
		Button topRight = new Button(positionGroup, SWT.RADIO);
		topRight.setText("Top Right");
		topRight.setData(WindowLocation.TOP_RIGHT);
		topRight.addSelectionListener(selectionListener);
		
		// Bottom Left
		Button botLeft = new Button(positionGroup, SWT.RADIO);
		botLeft.setText("Bottom Left");
		botLeft.setData(WindowLocation.BOTTOM_LEFT);
		botLeft.addSelectionListener(selectionListener);
		
		// Bottom Right
		Button botRight = new Button(positionGroup, SWT.RADIO);
		botRight.setText("Bottom Right");
		botRight.setData(WindowLocation.BOTTOM_RIGHT);
		botRight.addSelectionListener(selectionListener);
		
		// Set the current position, from Pref
		WindowLocation loc = (WindowLocation) PreferenceManager.getPreferenceManager()
				.get(PreferenceKey.WINDOW_LOCATION, WindowLocation.BOTTOM_RIGHT);
		switch(loc) {
			case BOTTOM_LEFT:
				botLeft.setSelection(true);
				break;
			case BOTTOM_RIGHT:
				botRight.setSelection(true);
				break;
			case TOP_LEFT:
				topLeft.setSelection(true);
				break;
			case TOP_RIGHT:
				topRight.setSelection(true);
				break;
		}
		
		// Set currently selected position
		m_locationSelected = loc;
	}
	
	/**
	 * Set the current selected state in effect window
	 * 
	 */
	private void setCurrentState() {
		PreferenceManager.getPreferenceManager().set(PreferenceKey.EFFECT_TYPE, m_effectSelected.getEffectClass());
		PreferenceManager.getPreferenceManager().set(PreferenceKey.WINDOW_LOCATION, m_locationSelected);
	}
	
	private void sendTestEvent() {
		ContactInfo testInfo = new ContactInfo(new Name("Effect Test", "Effect", "Test"), new PhoneNumber("000-000-0000"), new Email("effect_test@effect_test"));
		IncomingCallEvent testEvent = new IncomingCallEvent(new Date(), testInfo);
		m_handler.onEvent(testEvent);
	}
	
	class PositionSelected implements SelectionListener {

		public void widgetSelected(SelectionEvent e) {
			Button selected = (Button) e.getSource();
			if(selected.getSelection()) {
				WindowLocation location = (WindowLocation) selected.getData();
				m_locationSelected = location;
				
				setCurrentState();
				sendTestEvent();
			}
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			System.out.println("Yes");
		}
	}
	
	class EffectSelected implements SelectionListener {

		public void widgetSelected(SelectionEvent e) {
			Button selected = (Button) e.getSource();
			if(selected.getSelection()) {
				EffectType effect = (EffectType) selected.getData();
				m_effectSelected = effect;
				
				setCurrentState();
				sendTestEvent();
			}
		}

		public void widgetDefaultSelected(SelectionEvent e) {}
		
	}
}
