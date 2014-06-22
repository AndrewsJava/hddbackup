package harlequinmettle.backup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;

//import utilities.Feedback;

public class Mirror {
	int count = 0;
	String originsFilePath;
	String destinationsFilePath;
	private ArrayList<String> fileTypeFilter;
	// all files on original hdd mapped by path->file
	// public static final HashMap<String, File> COPPIES_og = new
	// HashMap<String, File>();
	// all files on backup hdd mapped by path->file
	public static final HashMap<String, File> COPPIES_re = new HashMap<String, File>();

	void setNodes(String from, String to) {
		originsFilePath = from;
		destinationsFilePath = to;
	}

	public Mirror() {
		originsFilePath = "/home/andrew/Desktop/nd/";
		destinationsFilePath = "/home/andrew/Desktop/ndd/";
	}

	public Mirror(String from, String to) {
		originsFilePath = from;
		destinationsFilePath = to;
	}

	public void clearOutEmptyFiles() {
		clearOutEmptyFiles(destinationsFilePath);
		// clearOutEmptyFiles(ORIGIN);
	}

	private void clearOutEmptyFiles(String backup2) {

		File root = new File(backup2);
		File[] list = root.listFiles();

		if (list == null) {
			// File empty = new File(backup2);
			// empty.delete();
			return;
		}
		root.delete();
		for (File original : list) {
			if (original.isDirectory()) {
				clearOutEmptyFiles(original.getAbsolutePath());
				// only succeeds if directory is empty
				original.delete();

			}
		}

	}

	public void mirrorNodeFiles() {
		mirrorNodeFiles(originsFilePath);
	}

	public void mirrorNodeFiles(TreeMap<String, Boolean> includeOnly) {
		fileTypeFilter = new ArrayList<String>();
		for (Entry<String, Boolean> ent : includeOnly.entrySet()) {
			if (ent.getValue())
				fileTypeFilter.add(ent.getKey());
		}
		mirrorNodeFiles(originsFilePath);
	}

	private void mirrorNodeFiles(String path) {

		File root = new File(path);
		File[] list = root.listFiles();

		if (list == null)
			return;

		for (File original : list) {
			if (original.isDirectory()) {
				mirrorNodeFiles(original.getAbsolutePath());
			} else {
				boolean shouldBackup = false;
				if (fileTypeFilter != null) {
					String fileName = original.getName();

					for (String fileEnding : fileTypeFilter) {
						if (fileName.endsWith(fileEnding)) {
							shouldBackup = true;
							break;
						}
					}
				}
				if (shouldBackup) {
					File copied = new File(original.getAbsolutePath()
							.replaceAll(originsFilePath, destinationsFilePath));

					if (isChanged(original, copied)) {
						try {
							FileUtils.copyFile(original, copied);
							Thread.yield();
						} catch (IOException e) {
							e.printStackTrace();
						}

					}

				}
			}
		}
	}

	// start of recursive pass, with NODE on backupHDD
	public void storeFileReference() {
		storeFileReference(destinationsFilePath);
	}

	// stores a reference to all files recursively
	protected void storeFileReference(String rootPath) {
		File root = new File(rootPath);
		File[] list = root.listFiles();

		if (list == null) {
			return;
		}

		for (File f : list) {
			if (f.isDirectory()) {
				storeFileReference(f.getAbsolutePath());
			} else {
				COPPIES_re.put(f.getAbsolutePath(), f);
			}
		}
	}

	// checks if there is no backup OR modifiedDate is different
	protected boolean isChanged(File og, File bkup) {
		boolean hasChanged = (!bkup.exists() || og.lastModified() > bkup
				.lastModified()+1000);
		if (hasChanged) {
			System.out.println("files changed so far: " + count++);
			System.out.println("        " + og);
			// System.out.println("        " +bkup);
			// System.out.println("        " );
		}
		return hasChanged;
	}

}
