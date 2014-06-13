package harlequinmettle.backup;

import harlequinmettle.utils.filetools.ChooseFilePrompter;
import harlequinmettle.utils.guitools.HorizontalJPanel;
import harlequinmettle.utils.guitools.JLabelFactory;
import harlequinmettle.utils.guitools.JScrollPanelledPane;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class BackupDefinitionView extends JFrame {
	private BackupDefinitionModel dataForView = new BackupDefinitionModel();
	public static final Integer[] SECONDS = { 20, 30, 60, 90, 60 * 2, 60 * 5,
			60 * 10, 60 * 30, 60 * 60 };
	protected static final int PATH_TYPE_ORIGIN = 11111111;
	protected static final int PATH_TYPE_DESTINATION = 22222222;
	protected static final int INCLUSION_TYPE = 1313131;
	protected static final int EXCLUSION_TYPE = 2424242;
	JComboBox<Integer> interval = new JComboBox<Integer>(SECONDS);
	JFrame hackApp;

	public BackupDefinitionView(JFrame app, BackupDefinitionModel dataForView) {
		hackApp = app;
		this.dataForView = dataForView;
		init();
	}

	public BackupDefinitionView(JFrame app) {
		hackApp = app;
		init();
	}

	public void closeFrame() {
		this.dispose();
	}

	public void init() {

		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(900, 500);
		this.setVisible(true);
		setUpView();
	}

	private void setUpView() {
		this.add(makeModelView());
	}

	private JScrollPanelledPane makeModelView() {
		JScrollPanelledPane definitionDetails = new JScrollPanelledPane();

		definitionDetails.addComp(makeTitleDefinitionPanel());
		definitionDetails.addComp(makeRunTimesPanel());
		definitionDetails.addComp(JLabelFactory
				.doBluishJLabel("move files FROM list"));
		for (Entry<String, Boolean> ent : dataForView.origins.entrySet()) {
			definitionDetails.addComp(makePathPanel(ent.getKey(),
					ent.getValue(), PATH_TYPE_ORIGIN));
		}

		JButton addOrigin = new JButton("add new origin");
		addOrigin.addActionListener(makeAddPathListener(dataForView.origins));
		definitionDetails.addComp(addOrigin);

		definitionDetails.addComp(JLabelFactory
				.doBluishJLabel("move files TO list"));
		for (Entry<String, Boolean> ent : dataForView.destinations.entrySet()) {
			definitionDetails.addComp(makePathPanel(ent.getKey(),
					ent.getValue(), PATH_TYPE_DESTINATION));
		}
		JButton addDest = new JButton("add new destination");
		addDest.addActionListener(makeAddPathListener(dataForView.destinations));
		definitionDetails.addComp(addDest);

		definitionDetails.addComp(makeInclusionExclusionPanel("include: ",
				dataForView, INCLUSION_TYPE));
		definitionDetails.addComp(makeInclusionExclusionPanel("exclude: ",
				dataForView, EXCLUSION_TYPE));

		return definitionDetails;
	}

	private JComponent makeInclusionExclusionPanel(String title,
			BackupDefinitionModel incexc, int INC_EXC_TYPE) {

		HorizontalJPanel incExcPanel = new HorizontalJPanel();
		JCheckBox apply = new JCheckBox(title);
		JTextArea fileLimits = new JTextArea();

		if (INC_EXC_TYPE == INCLUSION_TYPE) {
			apply.setSelected(incexc.applyInclusions);
			for (Entry<String, Boolean> ent : incexc.inclusions.entrySet()) {
				if (ent.getValue())
					fileLimits.append(ent.getKey() + " ");
			}
		} else if (INC_EXC_TYPE == EXCLUSION_TYPE) {
			apply.setSelected(incexc.applyExclusions);
			fileLimits.setEnabled(false);
			for (Entry<String, Boolean> ent : incexc.exclusions.entrySet()) {
				if (ent.getValue())
					fileLimits.append(ent.getKey() + " ");
			}
		}
		apply.addItemListener(makeIncludeExcludeItemListener(fileLimits,
				incexc, INC_EXC_TYPE));
		incExcPanel.add(apply);
		incExcPanel.add(fileLimits);
		return incExcPanel;
	}

	private ItemListener makeIncludeExcludeItemListener(
			final JTextArea fileLimits, final BackupDefinitionModel incexc,
			final int INC_EXC_TYPE) {

		return new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				JCheckBox uspPath = (JCheckBox) (arg0.getSource());
				boolean applyFileLimit = uspPath.isSelected();

				String[] fileChecks = fileLimits.getText().split(" ");
				if (INC_EXC_TYPE == INCLUSION_TYPE) {
					incexc.applyInclusions = applyFileLimit;
					for (String lim : fileChecks) {
						if (lim.trim().length() > 1)
							incexc.inclusions.put(lim.trim(), applyFileLimit);
					}
				} else if (INC_EXC_TYPE == EXCLUSION_TYPE) {

					incexc.applyExclusions = applyFileLimit;
					for (String lim : fileChecks) {
						if (lim.trim().length() > 1)
							incexc.exclusions.put(lim.trim(), applyFileLimit);
					}
				}
				BackupUtility.saveObjects();
			}

		};
	}

	private JComponent makeTitleDefinitionPanel() {
		HorizontalJPanel titleDef = new HorizontalJPanel();
		JTextField titleField = new JTextField(dataForView.title);
		titleDef.add(titleField);
		JButton saveDefinition = new JButton("save");
		titleDef.add(saveDefinition);
		saveDefinition
				.addActionListener(makeSaveDefinitionListener(titleField));
		return titleDef;
	}

	private ActionListener makeSaveDefinitionListener(
			final JTextField titleField) {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				dataForView.title = titleField.getText();
				BackupUtility.backupModels.put(titleField.getText(),
						dataForView);
				BackupUtility.saveObjects();

				hackApp.dispose();
				closeFrame();
				new BackupUtility();
			}

		};
	}

	private ActionListener makeAddPathListener(
			final TreeMap<String, Boolean> fileList) {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String newFile = ChooseFilePrompter.filePathChooser();
				if (newFile == null)
					return;
				fileList.put(newFile, true);
				BackupUtility.saveObjects();
				new BackupDefinitionView(hackApp, dataForView);
				closeFrame();

			}

		};
	}

	private HorizontalJPanel makePathPanel(String key, Boolean value,
			int originOrDestination) {
		HorizontalJPanel pathPanel = new HorizontalJPanel();
		JCheckBox use = new JCheckBox(key);
		use.setOpaque(true);
		use.setBackground(new Color(150, 250, 250));
		use.setSelected(value);
		use.addItemListener(makePathUseItemListener(key, originOrDestination));
		pathPanel.add(use);
		return pathPanel;
	}

	private ItemListener makePathUseItemListener(final String key,
			final int originOrDestination) {

		return new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				JCheckBox uspPath = (JCheckBox) (arg0.getSource());
				boolean isUsePath = uspPath.isSelected();
				if (originOrDestination == PATH_TYPE_ORIGIN) {
					dataForView.origins.put(key, isUsePath);
				} else if (originOrDestination == PATH_TYPE_DESTINATION) {
					dataForView.destinations.put(key, isUsePath);
				}
				BackupUtility.saveObjects();
			}

		};

	}

	private HorizontalJPanel makeRunTimesPanel() {
		HorizontalJPanel runTimesDef = new HorizontalJPanel();
		JCheckBox autoRun = new JCheckBox(
				"run automatically with interval (sec)");
		autoRun.setSelected(dataForView.autoRun);
		autoRun.addItemListener(makeAutoRunItemListener());
		runTimesDef.add(autoRun);

		Integer[] SECONDS = { 0, 5, 10, 20, 30, 60, 90, 60 * 2, 60 * 5,
				60 * 10, 60 * 30, 60 * 60 };
		JComboBox<Integer> interval = new JComboBox<Integer>(SECONDS);
		interval.setSelectedItem(dataForView.interval);
		interval.addItemListener(makeIntervalChoiceItemListener());
		runTimesDef.add(interval);

		JButton runNow = new JButton("run once now");
		runNow.addActionListener(makeDoBackupNowListener());
		runTimesDef.add(runNow);

		return runTimesDef;
	}

	private ItemListener makeIntervalChoiceItemListener() {
		return new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					Object item = event.getItem();
					dataForView.interval = (Integer) item;
					System.out.println("choice : 	" + item);
					BackupUtility.saveObjects();
				}
			}

		};
	}

	private ItemListener makeAutoRunItemListener() {
		return new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				JCheckBox autoRun = (JCheckBox) (arg0.getSource());
				boolean doAutoRun = autoRun.isSelected();
				dataForView.autoRun = doAutoRun;
				BackupUtility.saveObjects();
			}

		};
	}

	private ActionListener makeDoBackupNowListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new Thread(new BackupUtilityThread()).start();
			}

		};
	}

}
