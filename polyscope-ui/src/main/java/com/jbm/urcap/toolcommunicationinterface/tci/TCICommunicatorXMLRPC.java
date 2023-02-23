package com.jbm.urcap.toolcommunicationinterface.tci;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.AsyncCallback;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;


public class TCICommunicatorXMLRPC {

	private final XmlRpcClient client;
	
	public TCICommunicatorXMLRPC() {
		this.client = createXmlRpcClient();
	}
	
	private XmlRpcClient createXmlRpcClient() {
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		config.setEnabledForExtensions(true);
		try {
			config.setServerURL(new URL("http://servicegateway/universal-robots/toolcommunicationinterface/daemon-py/xmlrpc/RPC2"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		config.setConnectionTimeout(1000); //1s
		XmlRpcClient client = new XmlRpcClient();
		client.setConfig(config);
		return client;
	}
	
	public Boolean isReachable() {
		try {
			Object result = client.execute("ping", new ArrayList<String>());
			if(result instanceof String) {
				return ((String) result).equals("pong");
			}
		} catch (XmlRpcException e) {
			System.out.println("Caught XMLRPC exception");
		}
		return false;
	}
	
	public void isReachable(final XmlRpcCallback<Boolean> callback) {
		try {
			client.executeAsync("ping", new ArrayList<String>(), new AsyncCallback() {
				
				@Override
				public void handleResult(XmlRpcRequest arg0, Object arg1) {
					if(arg1 instanceof String) {
						callback.getResult(((String) arg1).equals("pong"));
					}
				}
				
				@Override
				public void handleError(XmlRpcRequest arg0, Throwable arg1) {
					System.out.println("XMLRPC Error in isReachable callback");
				}
			});
		} catch (XmlRpcException e) {
			System.out.println("Caught XMLRPC exception");
		}
	}
	
	public Boolean isOpen() {
		Boolean open = false;
		try {
			Object result = client.execute("isOpen", new ArrayList<String>());
			if(result instanceof Boolean) {
				open = (Boolean) result;
			}
		} catch (XmlRpcException e) {
			System.out.println("Caught XMLRPC exception");
			e.printStackTrace();
			System.out.println(e.toString()); //short Err msg. beacause Stacktrace is to long for display
		}
		return open;
	}
	
	public void isOpen(final XmlRpcCallback<Boolean> callback) {
		try {
			client.executeAsync("isOpen", new ArrayList<String>(), new AsyncCallback() {
				
				@Override
				public void handleResult(XmlRpcRequest arg0, Object arg1) {
					if(arg1 instanceof Boolean) {
						callback.getResult((Boolean) arg1);
					}
				}
				
				@Override
				public void handleError(XmlRpcRequest arg0, Throwable arg1) {
					System.out.println("XMLRPC Error in isOpen callback");
				}
			});
		} catch (XmlRpcException e) {
			System.out.println("Caught XMLRPC exception");
			e.printStackTrace();
		}
	}
	
	public void write(String data) {
		ArrayList<String> args = new ArrayList<String>();
		args.add(data);
		try {
			client.execute("write", args);
		} catch (XmlRpcException e) {
			System.out.println("Caught XMLRPC exception");
			e.printStackTrace();
			System.out.println(e.toString()); //short Err msg. beacause Stacktrace is to long for display
		}
	}
	
	public void write(String data, final XmlRpcCallback<String> callback) {
		ArrayList<String> args = new ArrayList<String>();
		args.add(data);
		
		try {
			client.executeAsync("write", args, new AsyncCallback() {
				
				@Override
				public void handleResult(XmlRpcRequest arg0, Object arg1) {
					if(arg1 instanceof String) {
						callback.getResult((String) arg1);
					}
				}
				
				@Override
				public void handleError(XmlRpcRequest arg0, Throwable arg1) {
					System.out.println("XMLRPC Error in read callback");
				}
			});
		} catch (XmlRpcException e) {
			System.out.println("Caught XMLRPC exception");
		}
	}
	public void configure(Integer timeout, Integer baudrate, Integer parity, Integer stopbits, Integer bytesize) {
		ArrayList<Integer> args = new ArrayList<Integer>();
		args.add(timeout);
		args.add(baudrate);
		args.add(parity);
		args.add(stopbits);
		args.add(bytesize);
		try {
			client.execute("configure", args);
		} catch (XmlRpcException e) {
			System.out.println("Caught XMLRPC exception");
			e.printStackTrace();
			System.out.println(e.toString()); //short Err msg. beacause Stacktrace is to long for display
		}
	};
	public void configure(Integer timeout, Integer baudrate, Integer parity, Integer stopbits, Integer bytesize, final XmlRpcCallback<String> callback) {
		ArrayList<Integer> args = new ArrayList<Integer>();
		args.add(timeout);
		args.add(baudrate);
		args.add(parity);
		args.add(stopbits);
		args.add(bytesize);
		
		try {
			client.executeAsync("configure", args, new AsyncCallback() {
				
				@Override
				public void handleResult(XmlRpcRequest arg0, Object arg1) {
					if(arg1 instanceof String) {
						callback.getResult((String) arg1);
					}
				}
				
				@Override
				public void handleError(XmlRpcRequest arg0, Throwable arg1) {
					System.out.println("XMLRPC Error in read callback");
				}
			});
		} catch (XmlRpcException e) {
			System.out.println("Caught XMLRPC exception");
		}
	}
	
	public String readUntil(int byteCount) {
		ArrayList<Integer> args = new ArrayList<Integer>();
		args.add(byteCount);
		String result = "";
		try {
			result += (String) client.execute("readUntil", args);
		} catch (XmlRpcException e) {
			System.out.println("Caught XMLRPC exception");
			e.printStackTrace();
			System.out.println(e.toString()); //short Err msg. beacause Stacktrace is to long for display
		}
		return result;
	}
	public void readUntil(int bytesToRead, final XmlRpcCallback<String> callback) {
		ArrayList<Integer> args = new ArrayList<Integer>();
		args.add(bytesToRead);
		
		try {
			client.executeAsync("readUntil", args, new AsyncCallback() {
				
				@Override
				public void handleResult(XmlRpcRequest arg0, Object arg1) {
					if(arg1 instanceof String) {
						callback.getResult((String) arg1);
					}
				}
				
				@Override
				public void handleError(XmlRpcRequest arg0, Throwable arg1) {
					System.out.println("XMLRPC Error in read callback");
				}
			});
		} catch (XmlRpcException e) {
			System.out.println("Caught XMLRPC exception");
		}
	}
	
	public String read(int bytesToRead) {
		ArrayList<Integer> args = new ArrayList<Integer>();
		args.add(bytesToRead);
		String result = "";
		try {
			result += (String) client.execute("read", args);
			System.out.println(result);
		} catch (XmlRpcException e) {
			System.out.println("Caught XMLRPC exception");
			e.printStackTrace();
			System.out.println(e.toString()); //short Err msg. beacause Stacktrace is to long for display
		}
		return result;
	}
	
	public void read(int bytesToRead, final XmlRpcCallback<String> callback) {
		ArrayList<Integer> args = new ArrayList<Integer>();
		args.add(bytesToRead);
		
		try {
			client.executeAsync("read", args, new AsyncCallback() {
				
				@Override
				public void handleResult(XmlRpcRequest arg0, Object arg1) {
					if(arg1 instanceof String) {
						callback.getResult((String) arg1);
					}
				}
				
				@Override
				public void handleError(XmlRpcRequest arg0, Throwable arg1) {
					System.out.println("XMLRPC Error in read callback");
				}
			});
		} catch (XmlRpcException e) {
			System.out.println("Caught XMLRPC exception");
		}
	}
	
	public Boolean open() {
		Boolean opened = false;
		try {
			Object result = client.execute("open", new ArrayList<String>());
			if(result instanceof Boolean) {
				opened = (Boolean) result;
			}
		} catch (XmlRpcException e) {
			System.out.println("Caught XMLRPC exception");
			e.printStackTrace();
			System.out.println(e.toString()); //short Err msg. beacause Stacktrace is to long for displa
			System.out.println("Caught XMLRPC exception on OPEN");
		}
		return opened;
	}
	
	public void open(final XmlRpcCallback<Boolean> callback) {
		try {
			client.executeAsync("open", new ArrayList<String>(), new AsyncCallback() {
				
				@Override
				public void handleResult(XmlRpcRequest arg0, Object arg1) {
					if(arg1 instanceof Boolean) {
						callback.getResult((Boolean) arg1);
					}
				}
				
				@Override
				public void handleError(XmlRpcRequest arg0, Throwable arg1) {
					System.out.println("XMLRPC Error in open callback");
				}
			});
		} catch (XmlRpcException e) {
			System.out.println("Caught XMLRPC");
			System.out.println(e.toString()); //short Err msg. beacause Stacktrace is to long for display
		}
	}
	
	public Boolean close() {
		Boolean closed = false;
		try {
			Object result = client.execute("close", new ArrayList<String>());
			if(result instanceof Boolean) {
				closed = (Boolean) result;
			}
		} catch (XmlRpcException e) {
			System.out.println("Caught XMLRPC exception");
			System.out.println(e.toString()); //short Err msg. beacause Stacktrace is to long for display
		}
		return closed;
	}
	
	public void close(final XmlRpcCallback<Boolean> callback) {
		try {
			client.executeAsync("close", new ArrayList<String>(), new AsyncCallback() {
				
				@Override
				public void handleResult(XmlRpcRequest arg0, Object arg1) {
					if(arg1 instanceof Boolean) {
						callback.getResult((Boolean) arg1);
					}
				}
				
				@Override
				public void handleError(XmlRpcRequest arg0, Throwable arg1) {
					System.out.println("XMLRPC Error in close callback");
				}
			});
		} catch (XmlRpcException e) {
			System.out.println("Caught XMLRPC exception");
		}
	}
}
