package util;

import eis.iilang.Percept;

public class ArtifactUtil {
	
	public static String perceive(Percept percept)
	{
		return "perceive" + StringUtil.capitalize(percept.getName());
	}

}
