package Def;

import java.util.Map;

public class IntegrityChecker {
	public static void compare(Map<String, String> oldSnap, Map<String, String> newSnap) {
		for (Map.Entry<String, String> entry : oldSnap.entrySet()) {
			
		    String filePath = entry.getKey();
		    String oldHash = entry.getValue();
		    
		    if(!newSnap.containsKey(filePath))
		    {
		    	AlertManager.alert("DELETED", filePath);
		    }
		    else
		    {
		    	String newHash = newSnap.get(filePath);
		    	if(!oldHash.equals(newHash))
		    	{
		    		AlertManager.alert("MODIFIED", filePath);
		    	}
		    }
		}
		for (Map.Entry<String, String> entry : newSnap.entrySet()) {
		    String filePath = entry.getKey();
		    
		    // check if filePath exists in oldSnap
		    if (!oldSnap.containsKey(filePath)) {
		    	AlertManager.alert("ADDED", filePath);
		    }
		}
	}
}
