package pctelelog.ui.notify;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import pctelelog.ContactInfo.Name;
import pctelelog.ContactInfo.Photo;
import pctelelog.events.AbstractCallEvent;
import pctelelog.ui.PreferenceKey;
import pctelelog.ui.PreferenceManager;
import pctelelog.ui.WindowWidget;

public class EventWindow implements WindowWidget, DisposeListener {
	private static final int SMALL_WIDTH = 150;
	private static final int SMALL_HEIGHT = 50;
	private static final int LARGE_WIDTH = SMALL_WIDTH * 2;
	private static final int LARGE_HEIGHT = SMALL_HEIGHT * 3;
	
	/**
	 * Padding from screen edge
	 */
	private static final int PADDING = 50;
	
	/**
	 * Resources to be disposed of
	 * at shell disposal
	 */
	private ArrayList<Resource> m_resources = new ArrayList<Resource>();
	
	private final Display m_display;
	private final Shell m_windowShell;
	
	private boolean  m_isDetailed = false;
	private WindowLocation m_windowLocation = null;
	private boolean m_autoDispose = true;
	
	public EventWindow(final Display display, boolean isDetailed) {
		if(display == null) { throw new NullPointerException("Display cannot be null."); }
		m_display = display;
		m_windowShell = new Shell(m_display, SWT.NO_TRIM | SWT.TOOL | SWT.ON_TOP);
		m_isDetailed = isDetailed;
		
			
		// Register resource reclamation
		m_windowShell.addDisposeListener(this);
		
		// Add Mouse Listener for clicks
		getWindowShell().addMouseListener(new MouseListener() {
			public void mouseUp(MouseEvent e) {
				disableAutoClose();
			}
			
			public void mouseDown(MouseEvent e) {}
			
			public void mouseDoubleClick(MouseEvent e) {}
		});
	}
	
	/** Event Actions **/
	public static EventWindow connect(Display display) {
		EventWindow eventWindow = new EventWindow(display, false);
		
		//Set size and location
		WindowLocation loc = eventWindow.pollWindowLocation();
		eventWindow.setWindowSizeAndLocation(loc);
		
		// Create status label
		Label text = new Label(eventWindow.getWindowShell(), SWT.HORIZONTAL);
		text.setText("Phone Connected");
		return eventWindow;
	}
	
	public static EventWindow incoming(Display display, AbstractCallEvent event) {
		EventWindow eventWindow = new EventWindow(display , true);
		
		// Set size and location
		WindowLocation loc = eventWindow.pollWindowLocation();
		eventWindow.setWindowSizeAndLocation(loc);
		
		eventWindow.createDetailedLayout(event, eventWindow);
		
		return eventWindow;
	}
	
	public Shell getWindowShell() {
		return m_windowShell;
	}

	public Display getMainDisplay() {
		return m_display;
	}
	
	public WindowLocation getWindowLocation() {
		return m_windowLocation;
	}

	public void open() {
		getWindowShell().open();
	}
	
	public void disableAutoClose() {
		m_autoDispose = false;
	}
	
	public void closeAfterEffect() { 
		if(m_autoDispose) {
			getWindowShell().dispose();
		}
		else {
			createCloseLabel();
		}
	}
		
	public void registerResource(Resource rs) {
		if(rs == null) { throw new NullPointerException("Resource cannot be null."); }
		m_resources.add(rs);
	}

	public void widgetDisposed(DisposeEvent e) {
		for(Resource rs: m_resources) {
			if(!rs.isDisposed()) {
				rs.dispose();
			}
		}
	}
	
	/**
	 * Retrieve the Event Window Effect from
	 * preferences
	 * 
	 * @return the current Event Window Effect
	 */
	public EventWindowEffect getEffect() {
		@SuppressWarnings("unchecked")
		Class<? extends EventWindowEffect> effectClass = (Class<? extends EventWindowEffect>) PreferenceManager.getPreferenceManager()
				.get(PreferenceKey.EFFECT_TYPE, StandardShowEffect.class);
		
		EventWindowEffect out = null;
		try {
			out = effectClass.getConstructor(EventWindow.class).newInstance(this);
		} catch(Exception e) {
			e.printStackTrace();
		}

		return out;
	}
	
	/**
	 * Is the event window a detailed event
	 * 
	 * @return true if it is, false if it isn't
	 */
	private boolean isDetailed() {
		return m_isDetailed;
	}

	/**
	 * Retrieve Event Window display location from
	 * preferences
	 * 
	 * @return the enum location
	 */
	private WindowLocation pollWindowLocation() {
		return (WindowLocation) PreferenceManager.getPreferenceManager()
				.get(PreferenceKey.WINDOW_LOCATION, WindowLocation.BOTTOM_RIGHT);
	}
	
	/**
	 * Get a rectangle that specifies where the event
	 * window should be displayed.
	 * 
	 * @param loc the window location area
	 * @param edgePadding the padding between the windo and the screen edge
	 * @param isDetails is the event window a detailed event (Incoming/Missed call)
	 * @return a rectangle with the size and location set
	 */
	private Rectangle getShellSizeAndLocation(WindowLocation loc, int edgePadding, boolean isDetails) {
 
		if(loc == null) { throw new NullPointerException("Location cannot be null."); }
		
		Rectangle monitorRegion = getMainDisplay().getPrimaryMonitor().getClientArea();
		int monitorWidth = monitorRegion.width;
		int monitorHeight = monitorRegion.height;
		
		int width = isDetails ? LARGE_WIDTH : SMALL_WIDTH;
		int height = isDetails ? LARGE_HEIGHT: SMALL_HEIGHT; 
		int x =  edgePadding;
		int y =  edgePadding;
		
		
		switch(loc) {
		case BOTTOM_LEFT:
			y = monitorHeight - (y + height);
			break;
		case BOTTOM_RIGHT:
			x = monitorWidth - (x + width);
			y = monitorHeight - (y + height);
			break;
		case TOP_LEFT:
			break;
		case TOP_RIGHT:
			x = monitorWidth - (x + width);
			break;
		}
		
		return new Rectangle(x, y, width, height); 
	}
	
	/**
	 * Set the current Event Window's location
	 * to the Window Location region
	 * 
	 * @param location the region the window should be displayed at
	 */
	private void setWindowSizeAndLocation(WindowLocation location) {
		Rectangle loc = getShellSizeAndLocation(location, PADDING , isDetailed());
		getWindowShell().setSize(loc.width, loc.height);
		getWindowShell().setLocation(loc.x, loc.y);
		
		m_windowLocation = location;
	}
	
	/**
	 * Event Window Control item creation methods
	 */
	
	private void createDetailedLayout(AbstractCallEvent event, EventWindow window) {
		// Setup layout
		GridLayout layout = new GridLayout(3, true);
		getWindowShell().setLayout(layout);
		
		createNotifyLabel(event, window);
		createPhotoLabel(event, window);
		createNameLabel(event, window);
		createNumberLabel(event, window);
		createEmailLabel(event, window);
		createDeviceLabel(event,window);
		registerShellAutoDisposeAbort();
	}
	
	private void createNotifyLabel(AbstractCallEvent event, EventWindow window) {
		Label typeLabel = new Label(window.getWindowShell(), SWT.CENTER);
		typeLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER | SWT.TOP, true, true, 3, 1));
		
		
		typeLabel.setFont(new Font(window.getMainDisplay(),"Arial",12, SWT.BOLD));
		
		switch(event.getEventType()) {
			case INCOMING_CALL:
				typeLabel.setText("INCOMING CALL");
				break;
			case MISSED_CALL:
				typeLabel.setText("MISSED CALL");
				break;
			default:
				typeLabel.setText("UNKNOWN EVENT: " + event.getEventType().toString());
				break;
		}
	}
	
	private boolean createPhotoLabel(AbstractCallEvent event, EventWindow window) {
		Photo photo = event.getContactInfo().getPhoto();
		
		Image img = null;
		if(photo == null) {
			ClassLoader loader = EventHandler.class.getClassLoader();
			InputStream dataStream = loader.getResourceAsStream("nophoto.png");
			try {
				img = (dataStream == null) ? null : new Image(window.getMainDisplay(), dataStream);
			} catch(SWTException e) {
				img = null;
			}
		}
		else {
			ByteArrayInputStream data = new ByteArrayInputStream(photo.getDecodedData());
			img = new Image(window.getMainDisplay(), data);
		}
				
		Label imgLabel = new Label(window.getWindowShell(), SWT.CENTER);		
		if(img == null) {
			imgLabel.setText("No Photo");
			imgLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 3));
		}
		else {
			// Resize for column
			int colWidth = window.getWindowShell().getBounds().width / 3;
			int colHeight = (int)(window.getWindowShell().getBounds().height * .75);
			img = scaleImage(img, colWidth, colHeight);
			
			window.registerResource(img);
			imgLabel.setImage(img);
			imgLabel.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, true, 1, 3));
		}
		return true;
	}
	
	private void createNameLabel(AbstractCallEvent event, EventWindow window) {
		Label nameLabel = new Label(window.getWindowShell(), SWT.CENTER);
		Name name_data = event.getContactInfo().getName();
		
		String name = name_data.getDisplayName().equals("") ? 
				name_data.getFirst() + " " + name_data.getLast() : name_data.getDisplayName();
		
		nameLabel.setText(name);
		nameLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 2, 1));
	}
	
	private void createNumberLabel(AbstractCallEvent event, EventWindow window) {
		Label numberLabel = new Label(window.getWindowShell(), SWT.CENTER);
		
		numberLabel.setText(event.getContactInfo().getNumber().getNumber());
		numberLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 2, 1));
	}
	
	private void createEmailLabel(AbstractCallEvent event, EventWindow window) {
		Label emailLabel = new Label(window.getWindowShell(), SWT.LEFT);
		
		emailLabel.setText(event.getContactInfo().getEmail().getEmail());
		emailLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
	}
	
	private void createDeviceLabel(AbstractCallEvent event, EventWindow window) {
		Label deviceLabel = new Label(window.getWindowShell(), SWT.LEFT);
		
		deviceLabel.setText(event.getDevice().getName());
		deviceLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
	}
	
	private void createCloseLabel() {
		int labelSize = 25;
		
		Button closeLabel = new Button(getWindowShell(), SWT.PUSH);
		
		closeLabel.setText("close");
		closeLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 3, 1));
		closeLabel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getWindowShell().close();
			}
		});
		Rectangle bounds = getWindowShell().getBounds();
		
		getWindowShell().setBounds(bounds.x, bounds.y-labelSize, bounds.width, bounds.height+labelSize);
	}
	
	/**
	 * Scale an image to a width and height
	 * <p>
	 * Old image will be disposed of and a new image returned.
	 * 
	 * @param image image to be scaled
	 * @param width the width of the new image
	 * @param height the height of the new image
	 * @return a new image scaled
	 */
	private Image scaleImage(Image image, int width, int height) {
		Image scaled = new Image(getMainDisplay(), width, height);
		GC gc = new GC(scaled);
		gc.setAntialias(SWT.ON);
		gc.setInterpolation(SWT.HIGH);
		gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, 0, 0, width, height);
		gc.dispose();
		image.dispose();
		return scaled;
	}
	
	/**
	 * Register all of the shell's children controls
	 * for mouse events so that on click they will trigger
	 * the auto dispose to abort
	 */
	private void registerShellAutoDisposeAbort() {
		Control[] controls = getWindowShell().getChildren();
		
		for(Control control : controls) {
			control.addMouseListener(new MouseListener() {
				public void mouseUp(MouseEvent e) {
					disableAutoClose();
				}
				
				public void mouseDown(MouseEvent e) {}
				
				public void mouseDoubleClick(MouseEvent e) {}
			});
		}
	}
}
