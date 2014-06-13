package harlequinmettle.backup;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

//import utilities.Feedback;

public class ShaCheckMirror extends Mirror{
 
   
	public   void mirrorNodeFiles() {
		mirrorNodeFiles(originsFilePath);
	}

	private   void mirrorNodeFiles(String path) {

		File root = new File(path);
		File[] list = root.listFiles();

		if (list == null)
			return;

		for (File original : list) {
			if (original.isDirectory()) {
				mirrorNodeFiles(original.getAbsolutePath());
			} else {
				File copied = new File(original.getAbsolutePath().replaceAll(
						originsFilePath, destinationsFilePath));

				if (isChanged(original, copied)) {

					if (doesBackupFileAlreadyExist_JustMoveIt(original)) {

						// otherwise copy it and make a backup
					} else {
						try {
							FileUtils.copyFile(original, copied);
							Thread.yield();
						} catch (IOException e) {
							e.printStackTrace();
						}
//						Feedback.filesMoved++;
//						Feedback.totalMem += original.length();
//						Feedback.compareTime("COPPIED: "
//								+ original.getAbsolutePath());

					}
				}

			}
		}
	}

	// checks hashmap of files already on backup hdd for
	private   boolean doesBackupFileAlreadyExist_JustMoveIt(File original) {

		for (Entry<String, File> ent : COPPIES_re.entrySet()) {
			File starting = ent.getValue();
			if (isSameFile(original, starting)) {
				System.out.println("original backup file: "
						+ ent.getValue().getAbsolutePath());
				File ending = new File(original.getAbsolutePath().replaceAll(
						originsFilePath, destinationsFilePath));
				File upOne = ending.getParentFile();
				upOne.mkdirs();
				boolean isMoved = ent.getValue().renameTo(ending);
				System.out.println("backup file moved to: "
						+ ending.getAbsolutePath());
//				Feedback.compareTime("MOVED_FILE: "
//						+ original.getAbsolutePath());
				Thread.yield();
				return true;
			}
		}
		return false;
	}

	// compare files; determine whether to move it somewhere else
	private   boolean isSameFile(File original, File value) {
		boolean areSame = false;
		boolean sameName = original.getName().equals(value.getName());
		boolean sameSize = original.length() == value.length();
		if (original.length() == 0) {
			return sameName;
		}
		boolean sameLastModifiedTime = original.lastModified() == value
				.lastModified();
		if (sameSize) {
			String shaog = SHAsum(original);
			String sharem = SHAsum(value);
			boolean sameSha = shaog.equals(sharem);
			if (sameSha) {
				System.out.println(original.getAbsolutePath().replaceAll(
						originsFilePath, "")
						+ "        :::        "
						+ value.getAbsolutePath().replaceAll(destinationsFilePath, ""));
				System.out.println(shaog + "           ...               "
						+ sharem);
			}
			areSame = sameSha;
		}
		return (areSame);
	}

	// copy/paste get a sha sum for a file
	public static String SHAsum(File original) {
		try {
			byte[] fileContent = FileUtils.readFileToByteArray(original);

			MessageDigest md = MessageDigest.getInstance("SHA-1");
			return byteArray2Hex(md.digest(fileContent));
		} catch (Exception e) {
			e.printStackTrace();
			return "there was an error computing sha1";
		}

	}

	// copy/paste - makes shasum readable
	private static String byteArray2Hex(final byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		return formatter.toString();
	}

	// start of recursive pass, with NODE on backupHDD
	public   void storeFileReference() {
		storeFileReference(destinationsFilePath);
	}
 
 

}
