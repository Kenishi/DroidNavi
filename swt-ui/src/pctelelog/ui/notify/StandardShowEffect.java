package pctelelog.ui.notify;


public class StandardShowEffect extends EventWindowEffect {

	public StandardShowEffect(final EventWindow display) {
		super(display);
		
		m_type = EffectType.STANDARD;
	}

	@Override
	public void run() {
		getEventWindow().getWindowShell().open();
		
		getEventWindow().getMainDisplay().timerExec(SHOW_TIME, new Runnable() {
			
			public void run() {
				getEventWindow().closeAfterEffect();	
			}
		});
	}
}
