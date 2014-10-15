package pctelelog.ui.notify;

import java.io.Serializable;

public enum EffectType implements Serializable {
	STANDARD(StandardShowEffect.class),
	FADE(FadeEffect.class),
	SLIDE(SlideInEffect.class);
	
	private Class<? extends EventWindowEffect> m_class = null;
	
	private EffectType(Class<? extends EventWindowEffect> clazz) {
		m_class = clazz;
	}
	
	public Class<? extends EventWindowEffect> getEffectClass() {
		return m_class;
	}
	
	public static EffectType getTypeFromClass(Class<? extends EventWindowEffect> clazz) {
		if(clazz == null) { throw new NullPointerException("Class cannot be null"); }
		
		if(clazz == StandardShowEffect.class) {
			return STANDARD;
		}
		else if(clazz == FadeEffect.class) {
			return FADE;
		}
		else if(clazz == SlideInEffect.class) {
			return SLIDE;
		}
		else {
			throw new RuntimeException("Unexpected class encountered.");
		}
	}
}
