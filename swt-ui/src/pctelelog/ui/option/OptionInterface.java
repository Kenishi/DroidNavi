package pctelelog.ui.option;

import java.io.Serializable;
import java.util.HashMap;

import pctelelog.ui.PreferenceKey;

public interface OptionInterface {

	public HashMap<PreferenceKey, Serializable> getOptions();
}
