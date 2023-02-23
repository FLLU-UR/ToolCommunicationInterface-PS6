package com.jbm.urcap.toolcommunicationinterface.impl;

import java.awt.EventQueue;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.xmlrpc.XmlRpcException;

import com.jbm.urcap.toolcommunicationinterface.tci.TCICommunicatorXMLRPC;
import com.jbm.urcap.toolcommunicationinterface.tci.TCIDaemonService;
import com.jbm.urcap.toolcommunicationinterface.tci.ToolIOController;
import com.jbm.urcap.toolcommunicationinterface.tci.UnknownResponseException;
import com.ur.urcap.api.contribution.DaemonContribution.State;
import com.ur.urcap.api.contribution.docker.ContainerStatus;
import com.ur.urcap.api.contribution.InstallationNodeContribution;
import com.ur.urcap.api.contribution.installation.InstallationAPIProvider;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.resource.ControllableResourceModel;
import com.ur.urcap.api.domain.resource.tooliointerface.CommunicationInterfaceConfig.BaudRate;
import com.ur.urcap.api.domain.resource.tooliointerface.CommunicationInterfaceConfig.Parity;
import com.ur.urcap.api.domain.resource.tooliointerface.CommunicationInterfaceConfig.StopBits;
import com.ur.urcap.api.domain.script.ScriptWriter;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputCallback;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputFactory;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardTextInput;


public class TCIInstallationNodeContribution implements InstallationNodeContribution {
	private final InstallationAPIProvider apiProvider;
	private final TCIInstallationNodeView view;
	private final DataModel model;
	private final TCIDaemonService tciDaemon;
	private final ToolIOController toolIOController;
	private final KeyboardInputFactory kbFactory;

	private static final String 	XMLRPC_VARIABLE 				= "tci";
	private static final String 	ENABLE_FUNCTIONALITY_KEY 		= "enable";
	private static final boolean 	ENABLE_FUNCTIONALITY_DEFAULT	= false;
	private static final String     INPUT_DEFAULT_VALUE             = "not set";
	private static final String     INPUT_KEY                       = "input_key";
	private static final Integer	BYTE_COUNT_KEY					= 20;
	private static final String		TIMEOUT_KEY						= "timeout_key";
	private static final int		TIMEOUT_KEY_DEFAULT				= 5;
	private static final String		BAUDRATE_KEY					= "baudrate_key";
	private static final BaudRate	BAUDRATE_TOOL_KEY				= BaudRate.BAUD_9600;
	private static final Integer	BAUDRATE_DEFAULT				= 9600;
	private static final String		PARITY_KEY						= "parity_key";
	private static final Parity		PARITY_TOOL_KEY					= Parity.NONE;
	private static final Integer	PARITY_DEFAULT					= 0; //none
	private static final String		STOPBITS_KEY					= "stopbits_key";
	private static final StopBits	STOPBITS_TOOL_KEY				= StopBits.ONE;
	private static final Integer	STOPBITS_DEFAULT				= 1; //one
	private static final String		BYTESIZE_KEY					= "bytesize_key";
	private static final Integer	BYTESIZE_DEFAULT				= 3; //8bit

	private TCICommunicatorXMLRPC tci;
	private Timer updateTimer;
	
	public TCIInstallationNodeContribution(InstallationAPIProvider apiProvider,
			TCIInstallationNodeView view, DataModel model,
			TCIDaemonService tciDaemonService) {
		this.apiProvider = apiProvider;
		this.view = view;
		this.model = model;
		this.tciDaemon = tciDaemonService;
		this.kbFactory = apiProvider.getUserInterfaceAPI().getUserInteraction().getKeyboardInputFactory();
		tci = new TCICommunicatorXMLRPC();		
		ControllableResourceModel resourceModel = apiProvider.getInstallationAPI().getControllableResourceModel();
		this.toolIOController = new ToolIOController(resourceModel, apiProvider.getSystemAPI().getCapabilityManager());
		resourceModel.requestControl(this.toolIOController);
		
		if(shouldRunDaemon()) {
			runDaemon();
		}
	}
	
	public void userSelectedFunctonalityEnabled(boolean enabled) {
		setFunctionalityEnabled(enabled);
		if(getFunctionalityEnabled()) {
			runDaemon();
			enabledFunctionalityViewUpdate();
		} else {
			stopDaemon();
			disabledFunctionalityViewUpdate();
		}
	}
	
	@Override
	public void openView() {
		view.setFunctionalityEnabledCheckbox(getFunctionalityEnabled());
		view.setSliderValue(getTimeout());
		view.setBaudrate(getBaudrate());
		view.setBytesize(getBytesize());
		view.setParity(getParity());
		view.setStopBits(getStopbits());
		
		
		// Will not change while in this view, so just update on openView
		if(isTCIcontrolled()) {
			view.setStatusLabel_ToolIOControl("Controlling Tool I/O", "ok");
		} else {
			view.setStatusLabel_ToolIOControl("Not controlling Tool I/O", "not_ok");
		}
		
		// Will not change while in this view, so just update on openView

		
		if(getFunctionalityEnabled()) {
			enabledFunctionalityViewUpdate();
		} else {
			disabledFunctionalityViewUpdate();
		}
	}

	@Override
	public void closeView() {
		stopTimer();
	}

	@Override
	public void generateScript(ScriptWriter writer) {
		writer.assign(XMLRPC_VARIABLE, "http://servicegateway/universal-robots/toolcommunicationinterface/daemon-py/xmlrpc/RPC2\")");		
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
						boolean isConfigured = false;
						if(isTCIconfigured()) {
							view.setStatusLabel_TCIConfiguration("Configuration is compatible", "ok");
						} else {
							view.setStatusLabel_TCIConfiguration("Configuration is not compatible", "not_ok");
						}
						if(isDaemonRunning()) {
							view.setStatusLabel_DaemonRunning("Daemon Service running", "ok");
							if(isOpen()) {
								view.setStatusLabel_PortOpen("Port is OPEN", "ok");
								if(!isConfigured) {
									configureSerial();
									isConfigured = true;
									System.out.println("test");
								}
							} else {
								view.setStatusLabel_PortOpen("Port is CLOSED", "not_ok");
								isConfigured = false;
							}
						} else {
							view.setStatusLabel_DaemonRunning("Daemon Service is not running", "not_ok");
						}	
					}
				});
			}
		}, 0, 5000);
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

	// DataModel getters and setters to handle whether the URCap's functionality is enabled
	private void setFunctionalityEnabled(boolean enabled) {
		model.set(ENABLE_FUNCTIONALITY_KEY, enabled);
	}
	public boolean getFunctionalityEnabled() {
		return model.get(ENABLE_FUNCTIONALITY_KEY, ENABLE_FUNCTIONALITY_DEFAULT);
	}
	public void setTimeout(int value) {
		model.set(TIMEOUT_KEY, value);
		view.setSliderText("Timeout in s: " + getTimeout());
	}
	public int getTimeout() {
		return model.get(TIMEOUT_KEY, TIMEOUT_KEY_DEFAULT);
	}
	private void setBaudrate(Integer value) {
		model.set(BAUDRATE_KEY, value);
	}

	public Integer getBaudrate() {
		return model.get(BAUDRATE_KEY, BAUDRATE_DEFAULT);
	}
	
	public BaudRate getToolBaudrate() {
		Integer baudrate = model.get(BAUDRATE_KEY, BAUDRATE_DEFAULT);
		BaudRate toolBaudrate = BaudRate.BAUD_9600;
		if (baudrate == 9600) {
			toolBaudrate = BaudRate.BAUD_9600;
		}else if(baudrate == 19200) {
			toolBaudrate = BaudRate.BAUD_19200;
		}else if(baudrate == 38400) {
			toolBaudrate = BaudRate.BAUD_38400;
		}else if(baudrate == 57600) {
			toolBaudrate = BaudRate.BAUD_57600;
		}else if(baudrate == 115200) {
			toolBaudrate = BaudRate.BAUD_115200;
		}
		System.out.print("getToolBaudrate:");
		System.out.print(toolBaudrate.toString());
		return toolBaudrate;
	}
	private void setParity(Integer value) {
		model.set(PARITY_KEY, value);
	}
	public Integer getParity() {
		return model.get(PARITY_KEY, PARITY_DEFAULT);
	}
	public Parity getToolParity() {
		Integer parity = model.get(PARITY_KEY, PARITY_DEFAULT);
		Parity toolParity = Parity.NONE;
		if (parity == 0) {
			toolParity = Parity.NONE;
		}else if(parity == 1) {
			toolParity = Parity.EVEN;
		}else if(parity == 2) {
			toolParity = Parity.ODD;
		}
		System.out.print("getToolBaudrate:");
		System.out.print(toolParity.toString());
		return toolParity;
	}
	private void setBytesize(Integer value) {
		model.set(BYTESIZE_KEY, value);
	}
	public Integer getBytesize() {
		return model.get(BYTESIZE_KEY, BYTESIZE_DEFAULT);
	}
	private void setStopbits(Integer value) {
		model.set(STOPBITS_KEY, value);
	}
	public Integer getStopbits() {
		return model.get(STOPBITS_KEY, STOPBITS_DEFAULT);
	}
	public StopBits getToolStopbits() {
		Integer stopBits = model.get(STOPBITS_KEY,STOPBITS_DEFAULT);
		StopBits toolStopBits = StopBits.ONE;
		if (stopBits == 0) {
			toolStopBits = StopBits.ONE;
		}else if(stopBits == 2){
			toolStopBits = StopBits.TWO;
		}
		System.out.print("getToolBaudrate:");
		System.out.print(toolStopBits.toString());
		return toolStopBits;
	}
	
	// Handlers to control daemon
	private boolean shouldRunDaemon() {
		return getFunctionalityEnabled();
	}
	public boolean isDaemonRunning() {
		return tciDaemon.getDaemon().getContainerStatus().equals(ContainerStatus.RUNNING);
	}
	private void runDaemon() {
		tciDaemon.getDaemon().start();
	}
	private void stopDaemon() {
		tciDaemon.getDaemon().stop();
	}
	
	public boolean isTCIconfigured() {
		return toolIOController.isTCIConfiguredCorrectly();
	}
	
	public boolean isTCIcontrolled() {
		return toolIOController.hasControlOverTCI();
	}
	
	// Accessing the Methods of the TCI-Daemon
	public boolean isOpen() {
		if (tci.isOpen()){
			return true;
			} else {	
			return false;
		}	
	}
	
	public void open() {
		tci.open();
	}
	
	public void close() {
		tci.close();
	}
	
	public String read() {
		String messageReceived = tci.read(BYTE_COUNT_KEY);
		view.setStatusLabel_MessageReceived(messageReceived);
		return messageReceived;
	}
	public String readuntil() {
		String messageReceived = tci.readUntil(BYTE_COUNT_KEY);
		view.setStatusLabel_MessageReceived(messageReceived);
		return messageReceived;
	}
	
	public void write(String data) {
		tci.write(data);
	}
	
	public void configureSerial() {
		tci.configure(getTimeout(), getBaudrate(), getParity(), getStopbits(), getBytesize());
	}
	
	//Methods for the View
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
				view.setINPUTtextField(model.get(INPUT_KEY, INPUT_DEFAULT_VALUE));
			}			
		};		
	}

	public void onOutputSelection(String name, Integer value, String selected) {
		if (name == "Baudrate") {
			setBaudrate(Integer.parseInt(selected));	
		}else if (name == "Stopbits") {
			setStopbits(value);
		}else if (name == "Parity") {
			setParity(value);
		}else if (name == "Bytesize"){
			setBytesize(value);
		}
		getToolBaudrate();
		if (toolIOController.hasControlOverTCI()){
			//toolIOController.setupTCI(getToolBaudrate(), getToolParity(), getToolStopbits());
			toolIOController.setupTCI(getToolBaudrate(), getToolParity(), getToolStopbits());
		}
		
	}
	//XMLRPC
	public String getXMLRPCVariable(){
		return XMLRPC_VARIABLE;
	}
	
	public TCICommunicatorXMLRPC getXmlRpcDaemonInterface() {
		return tci;
	}
}
	
	

