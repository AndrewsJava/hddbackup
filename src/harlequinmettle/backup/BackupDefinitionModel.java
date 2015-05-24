package harlequinmettle.backup;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;

public class BackupDefinitionModel implements Serializable {

	TreeMap<String, Boolean> origins = new TreeMap<String, Boolean>();
	TreeMap<String, Boolean> destinations = new TreeMap<String, Boolean>();

	TreeMap<String, Boolean> inclusions = new TreeMap<String, Boolean>();
	TreeMap<String, Boolean> exclusions = new TreeMap<String, Boolean>();

	String title = "untitled";
	int interval = 0;
	boolean autoRun = false;
	boolean trackHistory = false;
	boolean applyInclusions = false;
	boolean applyExclusions = false;
	int iterations = 60000;

	public BackupDefinitionModel() {
		inclusions.put(".java", true);
		// inclusions.put(".txt", true);
		// inclusions.put(".xml", true);
		// inclusions.put(".html", true);
		// inclusions.put(".csv", true);
		//
		// exclusions.put(".jar", true);
		// exclusions.put(".class", true);
	}

	public String[] getInclusionList() {
		return inclusions.keySet().toArray(new String[inclusions.size()]);
	}

	public String[] getExclusionList() {
		return exclusions.keySet().toArray(new String[exclusions.size()]);
	}

	ArrayList<String> ignoreFileExtensions = new ArrayList<String>();

	public void mapFiles() {
		System.out.println("limiting file backup to: " + inclusions);
		for (Entry<String, Boolean> moveFrom : origins.entrySet()) {
			for (Entry<String, Boolean> moveTo : destinations.entrySet()) {
				if (moveFrom.getValue() && moveTo.getValue()) {
					HistoryPreservingMirror fileCopier = new HistoryPreservingMirror(moveFrom.getKey(), moveTo.getKey());
					fileCopier.mirrorNodeFiles(inclusions);
				}
			}
		}

	}

	public void clearDestinationDirectory() {
		for (Entry<String, Boolean> moveTo : destinations.entrySet())
			try {
				FileUtils.deleteDirectory(new File(moveTo.getKey()));
			} catch (IOException e) {
				e.printStackTrace();
			}

	}
}
