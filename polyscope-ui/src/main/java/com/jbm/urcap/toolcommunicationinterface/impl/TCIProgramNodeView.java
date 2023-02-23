package com.jbm.urcap.toolcommunicationinterface.impl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ur.urcap.api.contribution.ContributionProvider;
import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.program.swing.SwingProgramNodeView;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardTextInput;

public class TCIProgramNodeView implements SwingProgramNodeView<TCIProgramNodeContribution>{
	
	private Style style;
	
	public TCIProgramNodeView(ViewAPIProvider provider, Style style) {
		this.style = style;
	}
	
	private JPanel PANEL_MAIN = new JPanel();
	private JPanel PANEL_READ = new JPanel();
	private JPanel PANEL_READUNTIL = new JPanel();
	private JPanel PANEL_WRITE = new JPanel();
	private JPanel PANEL_OPEN = new JPanel();
	private JPanel PANEL_CLOSE = new JPanel();
	
	private JTabbedPane TABBED_PANE = new JTabbedPane();
		
	private JLabel LABEL_RADIOBUTTON_STATUS = new JLabel();
	private JLabel LABEL_CONTROL_OF_TOOLIO_STATUS = new JLabel();
	private JLabel LABEL_TCI_CONFIGURATION_STATUS = new JLabel();
	private JLabel LABEL_DAEMON_RUNNING_STATUS = new JLabel();
	private JLabel LABEL_PORT_STATUS = new JLabel();
	private JLabel LABEL_MESSAGE_RECEIVED = new JLabel();
	private JLabel LABEL_STATUS_DESCRIPTION = new JLabel();
	
	private JButton OPEN_BUTTON = new JButton();
	private JButton CLOSE_BUTTON = new JButton();
	private JButton READ_BUTTON = new JButton();
	private JButton READUNTIL_BUTTON = new JButton();
	private JButton WRITE_BUTTON = new JButton();
	
	private JRadioButton RB_FIRST = new JRadioButton();
	private JRadioButton RB_SECOND = new JRadioButton();
	private JRadioButton RB_THIRD = new JRadioButton();
	private JRadioButton RB_FOURTH = new JRadioButton();
	private JRadioButton RB_FIFTH = new JRadioButton();
	
	private JTextField INPUT_TEXT_FIELD = new JTextField();
	private JTextField TEST_TEXT_FIELD = new JTextField();
	
	private String status;
	private Integer selection;
	
	
	public void buildUI(JPanel panel, ContributionProvider<TCIProgramNodeContribution> provider) {

		PANEL_MAIN.setLayout(new BoxLayout(PANEL_MAIN, BoxLayout.Y_AXIS));
		PANEL_MAIN.setPreferredSize(new Dimension(800,800));
		PANEL_READ.setLayout(new BoxLayout(PANEL_READ, BoxLayout.Y_AXIS));
		PANEL_READUNTIL.setLayout(new BoxLayout(PANEL_READUNTIL, BoxLayout.Y_AXIS));
		PANEL_WRITE.setLayout(new BoxLayout(PANEL_WRITE, BoxLayout.Y_AXIS));
		PANEL_OPEN.setLayout(new BoxLayout(PANEL_OPEN, BoxLayout.Y_AXIS));
		PANEL_CLOSE.setLayout(new BoxLayout(PANEL_CLOSE, BoxLayout.Y_AXIS));
		
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(PANEL_MAIN);

		
		PANEL_MAIN.add(createJTabbedPane(TABBED_PANE, PANEL_OPEN, PANEL_CLOSE, PANEL_READ, PANEL_READUNTIL, PANEL_WRITE,
				"Open", "Close", "Read", "ReadUntil", "Write", provider));
		PANEL_OPEN.add(createVerticalSpacer(15));
		PANEL_OPEN.add(createHeadLabel("<html>Upon program execution the port for serialcommunication gets OPENED</html>", 16.0f, Font.PLAIN));
		
		PANEL_CLOSE.add(createVerticalSpacer(15));
		PANEL_CLOSE.add(createHeadLabel("<html>Upon program execution the port for serialcommunication gets CLOSED</html>", 16.0f, Font.PLAIN));
		
		PANEL_READ.add(createVerticalSpacer(15));
		PANEL_READ.add(createHeadLabel("<html>Upon program execution the program tries to read a text message over the serialcommunication"
				+ " on an open port.</html>", 16.0f, Font.PLAIN));
		
		PANEL_READUNTIL.add(createVerticalSpacer(15));
		PANEL_READUNTIL.add(createHeadLabel("<html>Upon program execution the program tries to read a text message over the"
				+ " serialcommunication on an open port. The function reads until a given bytecount is reached or a given"
				+ " character sequence is received.</html>", 16.0f, Font.PLAIN));
		
		PANEL_WRITE.add(createVerticalSpacer(15));
		PANEL_WRITE.add(createHeadLabel("<html>Upon program execution the program tries to write a text message over te serialcommunication"
				+ " on an open port.</html>", 16.0f, Font.PLAIN));
		PANEL_WRITE.add(createVerticalSpacer(15));
		PANEL_WRITE.add(createHeadLabel("<html>Enter text to send here:</html>", 18.0f, Font.BOLD));
		PANEL_WRITE.add(createInputBox(INPUT_TEXT_FIELD, provider));
		PANEL_WRITE.add(createInputBox(TEST_TEXT_FIELD, provider));
		
		//Enabled Color
		INPUT_TEXT_FIELD.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		INPUT_TEXT_FIELD.setEnabled(true);
		//Disabled Color
		TEST_TEXT_FIELD.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
		TEST_TEXT_FIELD.setEnabled(false);
		
		TEST_TEXT_FIELD.setText("not set");
		
		PANEL_MAIN.add(Box.createVerticalGlue());
		PANEL_MAIN.add(new JSeparator());
		PANEL_MAIN.add(createVerticalSpacer(25));
		PANEL_MAIN.add(createHeadLabel("Status and Testfunctions", 24.0f, Font.BOLD));
		PANEL_MAIN.add(createVerticalSpacer(15));
		PANEL_MAIN.add(createStatusLabel("Tool I/O Control:", LABEL_CONTROL_OF_TOOLIO_STATUS));
		PANEL_MAIN.add(createStatusLabel("TCI Configuration:", LABEL_TCI_CONFIGURATION_STATUS));
		PANEL_MAIN.add(createStatusLabel("TCI Communication Service:", LABEL_DAEMON_RUNNING_STATUS));
		PANEL_MAIN.add(createStatusLabel("Port Status:", LABEL_PORT_STATUS));
		PANEL_MAIN.add(createStatusLabel("Message Received:", LABEL_MESSAGE_RECEIVED));
		PANEL_MAIN.add(createVerticalSpacer(25));
		PANEL_MAIN.add(createButtons(OPEN_BUTTON, CLOSE_BUTTON, READ_BUTTON,READUNTIL_BUTTON, WRITE_BUTTON, provider));
		PANEL_MAIN.add(createVerticalSpacer(25));

	}
	
	public void setStatusLabel_RBStatus(String status) {
		LABEL_RADIOBUTTON_STATUS.setText(status);
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
	public void setINPUTtextField(String text) {
		this.INPUT_TEXT_FIELD.setText(text);		
	}
	public void setTabbedPane(Integer selection) {
		TABBED_PANE.setSelectedIndex(selection);
		
	}
	public void setRadioButton(Integer selection) {
		switch(selection) {
		case 1: // open
			RB_FIRST.setSelected(true);
			
			PANEL_READ.setVisible(false);
			PANEL_READUNTIL.setVisible(false);
			PANEL_WRITE.setVisible(false);
			PANEL_OPEN.setVisible(true);
			PANEL_CLOSE.setVisible(false);
			break;
		case 2: // close
			RB_SECOND.setSelected(true);
			
			PANEL_READ.setVisible(false);
			PANEL_READUNTIL.setVisible(false);
			PANEL_WRITE.setVisible(false);
			PANEL_OPEN.setVisible(false);
			PANEL_CLOSE.setVisible(true);
			break;
		case 3: // read
			RB_THIRD.setSelected(true);
			
			PANEL_READ.setVisible(true);
			PANEL_READUNTIL.setVisible(false);
			PANEL_WRITE.setVisible(false);
			PANEL_OPEN.setVisible(false);
			PANEL_CLOSE.setVisible(false);
			break;
		case 4: // read until
			RB_FOURTH.setSelected(true);
			
			PANEL_READ.setVisible(false);
			PANEL_READUNTIL.setVisible(true);
			PANEL_WRITE.setVisible(false);
			PANEL_OPEN.setVisible(false);
			PANEL_CLOSE.setVisible(false);
			break;
		case 5: // write
			RB_FIFTH.setSelected(true);
			
			PANEL_READ.setVisible(false);
			PANEL_READUNTIL.setVisible(false);
			PANEL_WRITE.setVisible(true);
			PANEL_OPEN.setVisible(false);
			PANEL_CLOSE.setVisible(false);
			break;
		}
	}
	
	private Box createJTabbedPane(final JTabbedPane tp, JPanel firstPanel, JPanel secondPanel, JPanel thirdPanel, JPanel fourthPanel, JPanel fifthPanel,
			String firstText, String secondText, String thirdText, String fourthText, String fifthText, final ContributionProvider<TCIProgramNodeContribution> provider) {
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.LEFT_ALIGNMENT);
		tp.setPreferredSize(new Dimension(400,400));
		
		tp.addTab(firstText, firstPanel);
		tp.addTab(secondText, secondPanel);
		tp.addTab(thirdText, thirdPanel);
		tp.addTab(fourthText, fourthPanel);
		tp.addTab(fifthText, fifthPanel);
		
		ChangeListener tpListener = new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				selection = tp.getSelectedIndex();
				status = tp.getTitleAt(selection);
				provider.get().onTPSelection(selection, status);
				
			}		
		};
		
		tp.addChangeListener(tpListener);
		
		box.add(tp);
		
		
		return box;
	}

	private Box createStatusLabel(String description, JLabel statusLabel) {
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		//statusLabel.setIcon(ICON_OK);
		
		JLabel descriptionLabel = new JLabel(description);
		
		Dimension labelSize = new Dimension(250, 20);
		
		descriptionLabel.setPreferredSize(labelSize);
		descriptionLabel.setMaximumSize(labelSize);

		box.add(descriptionLabel);
		box.add(statusLabel);
		
		return box;
	}
	
	private Box createHeadLabel(String text, Float size, Integer style) {
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JLabel headLabel = new JLabel(text);
		headLabel.setFont(headLabel.getFont().deriveFont(style, size));
		
		box.add(headLabel);
		return box;
	}
	
	private Box createDescriptionLabel(String text) {
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JLabel descriptionLabel = new JLabel(text);
		
		box.add(descriptionLabel);
		
		return box;
	}
	
	private Box createInputBox(final JTextField textField, final ContributionProvider<TCIProgramNodeContribution> provider) {
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.LEFT_ALIGNMENT);
		box.setMaximumSize(new Dimension(800, 50));
	
		
		textField.setFocusable(false);
		textField.setEnabled(true);
		textField.setEditable(true);
		textField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		textField.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					KeyboardTextInput keyboardInput = provider.get().getKeyboardForInput();
					keyboardInput.show(textField, provider.get().getKeyboardCallBack());
				}
		});
	
		box.add(textField);
		return box;
	}
		
	private Component createVerticalSpacer(int height) {
		return Box.createRigidArea(new Dimension(0, height));
	}
	private Component createHorizontalSpacer(int width) {
		return Box.createRigidArea(new Dimension(width, 0));
	}
	
	private Box createButtons(final JButton openbutton,final JButton closebutton,final JButton readbutton, final JButton readuntilbutton,
								final JButton writebutton, final ContributionProvider<TCIProgramNodeContribution> provider) {
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.LEFT_ALIGNMENT);
				
		openbutton.setPreferredSize(new Dimension(130, 30));
		openbutton.setMaximumSize(openbutton.getPreferredSize());
		openbutton.setText("Open");
		openbutton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				provider.get().open();
			}
		});
		
		closebutton.setPreferredSize(new Dimension(130, 30));
		closebutton.setMaximumSize(closebutton.getPreferredSize());
		closebutton.setText("Close");
		closebutton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				provider.get().close();
			}
		});
		
		readbutton.setPreferredSize(new Dimension(130, 30));
		readbutton.setMaximumSize(readbutton.getPreferredSize());
		readbutton.setText("Read");
		readbutton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				provider.get().read();
			}
		});
		
		readuntilbutton.setPreferredSize(new Dimension(130, 30));
		readuntilbutton.setMaximumSize(readbutton.getPreferredSize());
		readuntilbutton.setText("ReadUntil");
		readuntilbutton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				provider.get().readuntil();
			}
		});
		
		writebutton.setPreferredSize(new Dimension(130, 30));
		writebutton.setMaximumSize(readbutton.getPreferredSize());
		writebutton.setText("Write");
		writebutton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				provider.get().write(INPUT_TEXT_FIELD.getText());
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

	



	
}
