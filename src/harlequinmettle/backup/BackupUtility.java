package harlequinmettle.backup;

import harlequinmettle.utils.filetools.ChooseFilePrompter;
import harlequinmettle.utils.filetools.SerializationTool;
import harlequinmettle.utils.guitools.JScrollPanelledPane;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class BackupUtility {
	static final String serializedModel = ".model_object_saved";
	static TreeMap<String, BackupDefinitionModel> backupModels = new TreeMap<String, BackupDefinitionModel>();
	static TreeMap<String, BackupUtilityThread> backupThreads = new TreeMap<String, BackupUtilityThread>();
	JFrame app;

	public static void main(String[] args) {
		BackupUtility bu = new BackupUtility();
	}

	public BackupUtility() {
		init();
	}

	public void init() {
		restoreObjectIfPossible();
		JFrame applicationFrame = new JFrame();
		app = applicationFrame;
		applicationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		applicationFrame.setSize(1200, 200);
		applicationFrame.setVisible(true);
		JTabbedPane jtp = new JTabbedPane();
		applicationFrame.add(jtp);
		jtp.add("backups", makeBackupThreadList());
		// jtp.add("backup", generateBackupPannel(1));
		// jtp.add("history", generateBackupPannel(2));
	}

	private Component makeBackupThreadList() {

		JScrollPanelledPane list = new JScrollPanelledPane();
		JButton addNewBackupDef = new JButton("add new definition");
		addNewBackupDef.addActionListener(makeDefinitionLauchListener(null));
		list.addComp(addNewBackupDef);
		for (Entry<String, BackupDefinitionModel> model : backupModels
				.entrySet()) {
			JButton modelLaunch = new JButton(model.getKey());
			modelLaunch.addActionListener(makeDefinitionLauchListener(model
					.getValue()));
			list.addComp(modelLaunch);
			if (backupThreads.containsKey(model.getKey()))
				backupThreads.get(model.getKey()).quit = true;

			BackupUtilityThread backerUp = new BackupUtilityThread(
					(model.getValue()));
			if (model.getValue().autoRun)
				backerUp.start();

			backupThreads.put(model.getKey(), backerUp);

		}
		return list;
	}

	private ActionListener makeDefinitionLauchListener(
			final BackupDefinitionModel data) {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				app.dispose();
				new BackupUtility();

				if (data == null)
					new BackupDefinitionView(app);
				else
					new BackupDefinitionView(app, data);

			}
		};
	}

	private ActionListener makeStartListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO: startbackup
			}

		};
	}

	private void restoreObjectIfPossible() {

		if (new File(serializedModel).exists())
			backupModels = SerializationTool.deserialize(
					backupModels.getClass(), serializedModel);

	}

	static void saveObjects() {
		SerializationTool.serialize(backupModels, serializedModel);
	}
}
