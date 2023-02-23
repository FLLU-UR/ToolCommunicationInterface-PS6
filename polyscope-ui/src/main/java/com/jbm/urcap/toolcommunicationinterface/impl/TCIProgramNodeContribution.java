package com.jbm.urcap.toolcommunicationinterface.impl;

import java.awt.EventQueue;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;

import com.ur.urcap.api.contribution.ProgramNodeContribution;
import com.ur.urcap.api.contribution.program.ProgramAPIProvider;
import com.ur.urcap.api.domain.ProgramAPI;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.script.ScriptWriter;
import com.ur.urcap.api.domain.undoredo.UndoRedoManager;
import com.ur.urcap.api.domain.undoredo.UndoableChanges;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputCallback;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputFactory;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardTextInput;


public class TCIProgramNodeContribution implements ProgramNodeContribution{
	private final ProgramAPI programAPI;
	private final UndoRedoManager undoRedoManager;
	private final KeyboardInputFactory kbFactory;

	private final TCIProgramNodeView view;
	private final DataModel model;
	
	private Timer updateTimer;
	private Style style;
	
	private static final String     INPUT_DEFAULT_VALUE             = "not set";
	private static final String     INPUT_KEY                       = "input_key";
	private static final String		RADIO_KEY						= "not set";
	private static final String		RADIO_STATUS_KEY				= "radio_status_key";
	private static final String 	RADIO_STATUS_DEFAULT_VALUE 		= "not set";
	private static final String		TABBED_PANE_STATUS_KEY			= "0";
	private static final String 	TABBED_PANE_STATUS_DEFAULT_VALUE= "Open";
	private static final String		TABBED_PANE_SELECTION_KEY		= "tabbed_pane_selection_key";
	private static final String		WRITE_KEY						= "not set";
	private static final Integer	TABBED_PANE_SELECTION_DEFAULT_VALUE	= 0;
	private static final Integer	BYTE_COUNT						= 20;


	public TCIProgramNodeContribution(ProgramAPIProvider apiProvider, TCIProgramNodeView view, DataModel model) {
		this.programAPI = apiProvider.getProgramAPI();
		this.undoRedoManager = apiProvider.getProgramAPI().getUndoRedoManager();
		this.kbFactory = apiProvider.getUserInterfaceAPI().getUserInteraction().getKeyboardInputFactory();

		this.view = view;
		this.model = model;
		
	}
	private String getINPUTtextField() {
		return model.get(INPUT_KEY, INPUT_DEFAULT_VALUE);
	}
	private String getRBStatus() {
		return model.get(RADIO_STATUS_KEY, RADIO_STATUS_DEFAULT_VALUE);
	}
	private String getTPStatus() {
		return model.get(TABBED_PANE_STATUS_KEY, TABBED_PANE_STATUS_DEFAULT_VALUE);
	}
	private Integer getTPSelection() {
		return model.get(TABBED_PANE_SELECTION_KEY, TABBED_PANE_SELECTION_DEFAULT_VALUE);
	}
	@Override
	public void openView() {
		// Will not change while in this view, so just update on openView
		if(getInstallation().isTCIcontrolled()) {
			view.setStatusLabel_ToolIOControl("Controlling Tool I/O", "ok");
		} else {
			view.setStatusLabel_ToolIOControl("Not controlling Tool I/O", "not_ok");
		}
		
		// Will not change while in this view, so just update on openView
		if(getInstallation().isTCIconfigured()) {
			view.setStatusLabel_TCIConfiguration("Configuration is compatible", "ok");
		} else {
			view.setStatusLabel_TCIConfiguration("Configuration is not compatible", "not_ok");
		}
		if(getInstallation().getFunctionalityEnabled()) {
			enabledFunctionalityViewUpdate();
		} else {
			disabledFunctionalityViewUpdate();
		}
		/* Outcommented for showcasing the data model update effect on the UI
		view.setINPUTtextField(getINPUTtextField());
		view.setRadioButton(model.get(RADIO_KEY, 0));
		view.setTabbedPane(getTPSelection());
		view.setStatusLabel_RBStatus(getRBStatus());
		*/
		getInstallation().configureSerial();
		
	}

	@Override
	public void closeView() {
		stopTimer();
		
	}

	@Override
	public String getTitle() {
		
		return "TCI: "+getTPStatus();
	}

	@Override
	public boolean isDefined() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void generateScript(ScriptWriter writer) {
		switch(getTPSelection()) {
		case 0: //Open
			writer.appendLine(getInstallation().getXMLRPCVariable() + ".open()");
			break;
		case 1: //Close	
			writer.appendLine(getInstallation().getXMLRPCVariable() + ".close()");
			break;
		case 2: //Read		
			writer.assign("read", getInstallation().getXMLRPCVariable() + ".read("+BYTE_COUNT+")");			
			writer.appendLine("popup(read, title=\"Received Message\", warning=False, error=False)");
			break;
		case 3: //Read Until		
			writer.assign("read", getInstallation().getXMLRPCVariable() + ".readUntil("+BYTE_COUNT+")");
			writer.appendLine("popup(read, title=\"Received Message\", warning=False, error=False)");
			break;
		case 4: //Write	
			writer.appendLine(getInstallation().getXMLRPCVariable() + ".write(\""+model.get(INPUT_KEY, INPUT_DEFAULT_VALUE)+"\")");
			break;
		}	
	}
	// Dynamic UI control
	private void enabledFunctionalityViewUpdate() {
		updateTimer = new Timer(true);
		updateTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						if(getInstallation().isDaemonRunning()) {
							view.setStatusLabel_DaemonRunning("Daemon Service running", "ok");
							if(getInstallation().isOpen()) {
								view.setStatusLabel_PortOpen("Port is OPEN", "ok");
							} else {
								view.setStatusLabel_PortOpen("Port is CLOSED", "not_ok");
							}
						} else {
							view.setStatusLabel_DaemonRunning("Daemon Service is not running", "not_ok");
						}
						

					}
				});
			}
		}, 0, 500);
	}
	
	private void disabledFunctionalityViewUpdate() {
		view.setStatusLabel_DaemonRunning("Functionality is disabled", "not_ok");
		view.setStatusLabel_PortOpen("Functionality is disabled / Port is CLOSED", "not_ok");
		stopTimer();
	}
	
	private void stopTimer() {
		if(updateTimer!=null) {
			updateTimer.cancel();
		}
	}	

	public void open() {
		getInstallation().open();
	}
	
	public void close() {
		getInstallation().close();
	}
	
	public void read() {
		String messageReceived = getInstallation().read();
		view.setStatusLabel_MessageReceived(messageReceived);
	}
	public void readuntil() {
		String messageReceived = getInstallation().readuntil();
		view.setStatusLabel_MessageReceived(messageReceived);
	}
	
	public void write(String data) {
		getInstallation().write(data);
	}
	
	public void onTPSelection(final Integer selection, final String status) {
		undoRedoManager.recordChanges(new UndoableChanges() {
			
			@Override
			public void executeChanges() {
				model.set(TABBED_PANE_SELECTION_KEY, selection);
				model.set(TABBED_PANE_STATUS_KEY, status);
				view.setTabbedPane(selection);
				
			}
		});
		
	}
	
	public KeyboardTextInput getKeyboardForInput() {
		KeyboardTextInput keyboard = kbFactory.createStringKeyboardInput();
		return keyboard;
	}
	
	public KeyboardInputCallback<String> getKeyboardCallBack() {
		return new KeyboardInputCallback<String>() {

			@Override
			public void onOk(String value) {
				final String inputText = value;
				model.set(INPUT_KEY, inputText);
				view.setINPUTtextField(getINPUTtextField());
			}			
		};		
	}
	
	private TCIInstallationNodeContribution getInstallation() {
		return programAPI.getInstallationNode(TCIInstallationNodeContribution.class);
	}
	
	

}
