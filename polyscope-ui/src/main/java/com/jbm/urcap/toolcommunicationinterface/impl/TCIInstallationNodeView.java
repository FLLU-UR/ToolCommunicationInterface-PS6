package com.jbm.urcap.toolcommunicationinterface.impl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ur.urcap.api.contribution.ContributionProvider;
import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeView;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardTextInput;

public class TCIInstallationNodeView implements SwingInstallationNodeView<TCIInstallationNodeContribution>{



	private Style style;

	public TCIInstallationNodeView(ViewAPIProvider apiProvider, Style style) {
		this.style = style;
	}
	
	private JCheckBox CHECKBOX_FUNCTIONALITY_ENABLED = new JCheckBox();
	
	private JLabel LABEL_CONTROL_OF_TOOLIO_STATUS = new JLabel();
	private JLabel LABEL_TCI_CONFIGURATION_STATUS = new JLabel();
	private JLabel LABEL_DAEMON_RUNNING_STATUS = new JLabel();
	private JLabel LABEL_PORT_STATUS = new JLabel();
	private JLabel LABEL_MESSAGE_RECEIVED = new JLabel();
	private JLabel LABEL_TIMEOUT = new JLabel();
	
	private JButton OPEN_BUTTON = new JButton();
	private JButton CLOSE_BUTTON = new JButton();
	private JButton READ_BUTTON = new JButton();
	private JButton READUNTIL_BUTTON = new JButton();
	private JButton WRITE_BUTTON = new JButton();
	
	private JTextField INPUT_TEXT_FIELD = new JTextField();
	
	private JSlider TIMEOUT_SLIDER = new JSlider();
	
	private JComboBox<String> BAUDRATE_BOX = new JComboBox<String>();
	private String[] BAUDRATE_LIST = {"9600", "19200", "38400", "57600", "115200"};	
	private JComboBox<String> PARITY_BOX = new JComboBox<String>();
	private String[] PARITY_LIST = {"NONE", "EVEN", "ODD", "MARK", "SPACE"};
	private JComboBox<String> BYTESIZE_BOX = new JComboBox<String>();
	private String[] BYTESIZE_LIST = {"FIVE", "SIX", "SEVEN", "EIGHT"};	
	private JComboBox<String> STOPBITS_BOX = new JComboBox<String>();
	private String[] STOPBITS_LIST = {"ONE", "ONE POINT FIVE", "TWO"};	
	

	
	@Override
	public void buildUI(JPanel panel, TCIInstallationNodeContribution contribution) {
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		panel.add(createDescriptionLabel("This URCap assumes control over the Tool I/O, and enables communication over the Tool Communication Interface."));
		panel.add(createDescriptionLabel("Decide whether the functionality of this URCap should be enabled:"));
		panel.add(createFunctionalityEnabledBox("Enable TCI control", contribution));
		panel.add(createVerticalSpacer(25));
		panel.add(createSlider(LABEL_TIMEOUT, TIMEOUT_SLIDER, 1, 10, contribution));
		panel.add(createVerticalSpacer(25));
		panel.add(createComboBox("Baudrate", BAUDRATE_BOX, BAUDRATE_LIST, contribution));
		panel.add(createComboBox("Bytesize", BYTESIZE_BOX, BYTESIZE_LIST, contribution));
		panel.add(createComboBox("Parity", PARITY_BOX, PARITY_LIST, contribution));		
		panel.add(createComboBox("Stopbits", STOPBITS_BOX, STOPBITS_LIST, contribution));
		panel.add(createVerticalSpacer(25));
		panel.add(createStatusLabel("Tool I/O Control:", LABEL_CONTROL_OF_TOOLIO_STATUS));
		panel.add(createStatusLabel("TCI Configuration:", LABEL_TCI_CONFIGURATION_STATUS));
		panel.add(createStatusLabel("TCI Communication Service:", LABEL_DAEMON_RUNNING_STATUS));
		panel.add(createStatusLabel("Port Status:", LABEL_PORT_STATUS));
		panel.add(createVerticalSpacer(25));
		panel.add(createButtons(OPEN_BUTTON, CLOSE_BUTTON, READ_BUTTON,READUNTIL_BUTTON, WRITE_BUTTON, contribution));
		panel.add(createVerticalSpacer(25));
		panel.add(createStatusLabel("Message Received:", LABEL_MESSAGE_RECEIVED));
		panel.add(createVerticalSpacer(25));
		panel.add(createDescriptionLabel("Enter text to write here:"));
		panel.add(createInputBox(INPUT_TEXT_FIELD, contribution));
	}
	
	//set UI-Elements
	public void setFunctionalityEnabledCheckbox(boolean enabled) {
		CHECKBOX_FUNCTIONALITY_ENABLED.setSelected(enabled);
	}
	
	public void setStatusLabel_ToolIOControl(String status, String icon) {
		LABEL_CONTROL_OF_TOOLIO_STATUS.setText(status);	
		if (icon=="ok") {
			LABEL_CONTROL_OF_TOOLIO_STATUS.setIcon(new ImageIcon(style.getImage("ok",15, 15)));
		} else {
			LABEL_CONTROL_OF_TOOLIO_STATUS.setIcon(new ImageIcon(style.getImage("nok",15, 15)));
		}
	}
	
	public void setStatusLabel_TCIConfiguration(String status, String icon) {
		LABEL_TCI_CONFIGURATION_STATUS.setText(status);
		if (icon=="ok") {
			LABEL_TCI_CONFIGURATION_STATUS.setIcon(new ImageIcon(style.getImage("ok",15, 15)));
		} else {
			LABEL_TCI_CONFIGURATION_STATUS.setIcon(new ImageIcon(style.getImage("nok",15, 15)));	
		}
	}
	
	public void setStatusLabel_DaemonRunning(String status, String icon) {
		LABEL_DAEMON_RUNNING_STATUS.setText(status);
		if (icon=="ok") {
			LABEL_DAEMON_RUNNING_STATUS.setIcon(new ImageIcon(style.getImage("ok",15, 15)));
		} else {
			LABEL_DAEMON_RUNNING_STATUS.setIcon(new ImageIcon(style.getImage("nok",15, 15)));	
		}
		
	}
	public void setStatusLabel_PortOpen(String status, String icon) {
		LABEL_PORT_STATUS.setText(status);
		if (icon=="ok") {
			LABEL_PORT_STATUS.setIcon(new ImageIcon(style.getImage("ok",15, 15)));
		} else {
			LABEL_PORT_STATUS.setIcon(new ImageIcon(style.getImage("nok",15, 15)));
		}
	}
	public void setStatusLabel_MessageReceived(String status) {
		LABEL_MESSAGE_RECEIVED.setText(status);
	}
	
	public void setSliderText(String text) {
		LABEL_TIMEOUT.setText(text);
	}
	public void setSliderValue(int sliderValue) {
		this.TIMEOUT_SLIDER.setValue(sliderValue);
	}
	
	public void setBaudrate(Integer selection) {
		BAUDRATE_BOX.setSelectedItem(Integer.toString(selection));
	}
	public void setBytesize(Integer index) {
		BYTESIZE_BOX.setSelectedIndex(index);
	}
	public void setParity(Integer index) {
		PARITY_BOX.setSelectedIndex(index);
	}
	public void setStopBits(Integer index) {
		STOPBITS_BOX.setSelectedIndex(index);
	}
	public void setINPUTtextField(String text) {
		this.INPUT_TEXT_FIELD.setText(text);		
	}
	
	//Create UI-Elements
	private Box createFunctionalityEnabledBox(String text, final TCIInstallationNodeContribution contribution) {
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		CHECKBOX_FUNCTIONALITY_ENABLED.setText(text);
		CHECKBOX_FUNCTIONALITY_ENABLED.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				contribution.userSelectedFunctonalityEnabled(CHECKBOX_FUNCTIONALITY_ENABLED.isSelected());
			}
		});
		
		box.add(CHECKBOX_FUNCTIONALITY_ENABLED);
		
		return box;
	}
		
	private Box createStatusLabel(String description, JLabel statusLabel) {
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		
		
		JLabel descriptionLabel = new JLabel(description);
		
		Dimension labelSize = new Dimension(250, 20);
		
		descriptionLabel.setPreferredSize(labelSize);
		descriptionLabel.setMaximumSize(labelSize);

		box.add(descriptionLabel);
		box.add(statusLabel);
		
		return box;
	}
	
	private Box createDescriptionLabel(String text) {
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JLabel descriptionLabel = new JLabel(text);
		
		box.add(descriptionLabel);
		
		return box;
	}
	
	private Box createComboBox(String name, final JComboBox<String> comboBox, String[] list, final TCIInstallationNodeContribution contribution) {
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.LEFT_ALIGNMENT);
		box.setPreferredSize(new Dimension(700, 50));
		comboBox.setPreferredSize(new Dimension(104, 30));
		comboBox.setMaximumSize(comboBox.getPreferredSize());	
		
		JLabel label = new JLabel();
		Dimension labelSize = new Dimension(100, 20);
		
		label.setPreferredSize(labelSize);
		label.setMaximumSize(labelSize);
		label.setText(name);
		
		
		comboBox.removeAllItems();
		comboBox.setModel(new DefaultComboBoxModel<String>(list));
		comboBox.setName(name);
		
		comboBox.addItemListener(new ItemListener() {
		
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					contribution.onOutputSelection(comboBox.getName(), comboBox.getSelectedIndex(), (String) comboBox.getSelectedItem());
					contribution.configureSerial();
					
				}
			};
		});
		
		box.add(label);
		box.add(comboBox);
		return box;
	}
		
	private Box createInputBox(final JTextField textField, final TCIInstallationNodeContribution contribution) {
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.LEFT_ALIGNMENT);
		box.setMaximumSize(new Dimension(800, 50));
	
		
		textField.setFocusable(false);
		textField.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					KeyboardTextInput keyboardInput = contribution.getKeyboardForInput();
					keyboardInput.show(textField, contribution.getKeyboardCallBack());
				}
		});
	
		box.add(textField);
		return box;
	}
	
	private Box createSlider(final JLabel label, final JSlider slider, int min, int max, final TCIInstallationNodeContribution contribution) {
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.LEFT_ALIGNMENT);
				
		label.setPreferredSize(new Dimension(300, 30));
		label.setMaximumSize(label.getPreferredSize());
		
		slider.setMinimum(min);
		slider.setMaximum(max);
		slider.setPreferredSize(new Dimension(300, 30));
		slider.setMaximumSize(slider.getPreferredSize());
		
		slider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				int value = slider.getValue();
				contribution.setTimeout(value);
				contribution.configureSerial();
				
			}
		});
		box.add(slider);
		box.add(label);
		
		return box;
	}
	

	
	private Box createButtons(final JButton openbutton,final JButton closebutton,final JButton readbutton, final JButton readuntilbutton,
								final JButton writebutton, final TCIInstallationNodeContribution contribution) {
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.LEFT_ALIGNMENT);
				
		openbutton.setPreferredSize(new Dimension(130, 30));
		openbutton.setMaximumSize(openbutton.getPreferredSize());
		openbutton.setText("Open");
		openbutton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				contribution.open();
			}
		});
		
		closebutton.setPreferredSize(new Dimension(130, 30));
		closebutton.setMaximumSize(closebutton.getPreferredSize());
		closebutton.setText("Close");
		closebutton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				contribution.close();
			}
		});
		
		readbutton.setPreferredSize(new Dimension(130, 30));
		readbutton.setMaximumSize(readbutton.getPreferredSize());
		readbutton.setText("Read");
		readbutton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				contribution.read();
			}
		});
		
		readuntilbutton.setPreferredSize(new Dimension(130, 30));
		readuntilbutton.setMaximumSize(readbutton.getPreferredSize());
		readuntilbutton.setText("ReadUntil");
		readuntilbutton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				contribution.readuntil();
			}
		});
		
		writebutton.setPreferredSize(new Dimension(130, 30));
		writebutton.setMaximumSize(readbutton.getPreferredSize());
		writebutton.setText("Write");
		writebutton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				contribution.write(INPUT_TEXT_FIELD.getText());
			}
		});
		
		box.add(openbutton);
		box.add(createHorizontalSpacer(10));
		box.add(closebutton);
		box.add(createHorizontalSpacer(10));
		box.add(readbutton);
		box.add(createHorizontalSpacer(10));
		box.add(readuntilbutton);
		box.add(createHorizontalSpacer(10));
		box.add(writebutton);
		
		return box;
		
	}
	private Component createVerticalSpacer(int height) {
		return Box.createRigidArea(new Dimension(0, height));
	}
	private Component createHorizontalSpacer(int width) {
		return Box.createRigidArea(new Dimension(width, 0));
	}
	
}
