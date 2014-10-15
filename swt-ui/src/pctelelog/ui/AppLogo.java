package pctelelog.ui;

import java.io.InputStream;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * App Logo is a helper class to retrieve
 * the image logo for the application
 * 
 * @author Jeremy May
 *
 */
public class AppLogo {
	enum LogoType {
		LARGE_LOGO("logo.png"),
		TRAY_ICON("tray_pink.png");
		
		private String m_file = null;
		private LogoType(String file) {
			m_file = file;
		}
		
		public String getFileName() { return m_file; }
	}
	
	public static Image getLogo(LogoType type, Display display) {
		return getLogo(type, display, 0, 0);
	}
	
	public static Image getLogo(LogoType type, Display display, int width, int height) {
		ClassLoader loader = AppLogo.class.getClassLoader();
		InputStream stream = loader.getResourceAsStream(type.getFileName());
		Image img; 
		
		if(width > 0 && height > 0) {
			Image orig = new Image(display, stream);
			
			img = new Image(display, width, height);
			GC gc = new GC(img);
			gc.drawImage(orig, 0, 0, orig.getBounds().width, orig.getBounds().height, 0, 0, width, height);
			gc.dispose();
			orig.dispose();
		}
		else {
			img = new Image(display, stream);
		}
		
		return img;		
	}
}
