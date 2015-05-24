package harlequinmettle.backup;

import harlequinmettle.utils.filetools.SerializationTool;
import harlequinmettle.utils.guitools.JScrollPanelledPane;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

public class BackupUtility {
	static final String serializedModel = ".backup_utility_thread_model_object";
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
		applicationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		applicationFrame.setSize(1200, 200);
		applicationFrame.setVisible(true);
		JTabbedPane jtp = new JTabbedPane();
		applicationFrame.add(jtp);
		jtp.add("backups", makeBackupThreadList(applicationFrame));
		// jtp.add("backup", generateBackupPannel(1));
		// jtp.add("history", generateBackupPannel(2));
	}

	private Component makeBackupThreadList(JFrame applicationFrame) {

		JScrollPanelledPane list = new JScrollPanelledPane();

		JCheckBox killOnClose = new JCheckBox(
				"Stop Threads When Window Is Closed");
		//killOnClose.setSelected(true);
		killOnClose.addItemListener(makeKillItemListener(applicationFrame));

		list.addComp(killOnClose);
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

	private ItemListener makeKillItemListener(final JFrame applicationFrame) {

		return new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				JCheckBox killOnClose = (JCheckBox) (arg0.getSource());
				boolean killOrNot = killOnClose.isSelected();
				if (killOrNot) {
					for (BackupUtilityThread backup : backupThreads.values()) {
						backup.quit = true;
					}
					applicationFrame
							.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				} else {
					applicationFrame
							.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				}
			}
		};

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
			backupModels = new SerializationTool().deserialize(
					backupModels.getClass(), serializedModel);

	}

	static void saveObjects() {
		new SerializationTool().serialize(backupModels, serializedModel);
	}
}
