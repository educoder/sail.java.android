package org.encorelab.sail.android.xmpp;

import org.encorelab.sail.Event;
import org.encorelab.sail.android.EventListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;


public class XMPPThread extends HandlerThread {
	
	public static final String TAG = "XMPP"; 
	
	private int state;
	
	// the JID of the conference room (MUC) for sending and listening to events
	protected String conferenceJid;
	
	protected String username;
	protected String resource;
	
	protected XMPPConnection xmpp;
	
	private ConnectionConfiguration xmppConfig;

	
	public static class State {
		public static final int DISOCNNECTED = 0;
		public static final int CONNECTED = 1;
		public static final int LOGGED_IN = 2;
	}
	
	public XMPPThread() {
		super("XMPP");
	}
	
	@Override
	public void run() {
		Looper.prepare();
		try {
			connect();
		} catch (Exception e) {
			Log.e("XMPP", "Couldn't connect to XMPP server!", e);
		}
		Looper.loop();
	}
	
	public void configure(String host, int port) {
		Log.d(XMPPThread.TAG, "Configuring with host: "+host+", port: "+port+"...");
		if (state != State.DISOCNNECTED)
			throw new NullPointerException("XMPPService must be disconnected before it can be configured!");
		
		xmppConfig = new ConnectionConfiguration(host, port);
		xmppConfig.setSASLAuthenticationEnabled(true);
		xmppConfig.setSecurityMode(SecurityMode.disabled);
		
		Log.d(XMPPThread.TAG, "Configured.");
	}
	
	public void connect() throws XMPPException {
		Log.d(XMPPThread.TAG, "Connecting...");
		if (xmppConfig == null)
			throw new NullPointerException("XMPPService must be configured before it can connect!");
		
		if (xmpp == null) {			
			xmpp = new XMPPConnection(xmppConfig);
		}
		
		xmpp.connect();
		
		state = State.CONNECTED;
		Log.d(XMPPThread.TAG, "Connected.");
	}
	
	public void login(String username, String password, String resource) throws XMPPException {
		Log.d(XMPPThread.TAG, "Logging in as "+username+" with password: '"+password+"', resource: '"+resource+"' ...");
		if (!this.isConnected())
			throw new NullPointerException("XMPPService must be connected in before it can log in!");
		
		xmpp.login(username, password, resource);
		state = State.LOGGED_IN;
		
		this.username = username;
		this.resource = resource;
		
		Log.d(XMPPThread.TAG, "Logged in.");
	}
	
	public void joinConference(String conferenceJid, String resource) {
		Log.d(XMPPThread.TAG, "Joining conference "+conferenceJid+"/"+resource+"...");
		if (!this.isLoggedIn())
			throw new NullPointerException("XMPPService must be logged in before it can join a conference!");
		
		this.conferenceJid = conferenceJid;
		Presence p = new Presence(Presence.Type.available);
		p.setStatus("chat");
		p.setTo(this.conferenceJid+"/"+resource);
		xmpp.sendPacket(p);
		Log.d(XMPPThread.TAG, "Joined conference "+conferenceJid+"/"+resource+".");
	}
	
	// TODO: implement me
	/*public void leaveConference() {
		
	}*/
	
	public void disconnect() {
		xmpp.disconnect();
		state = State.DISOCNNECTED;
	}
	
	public boolean isConnected() {
		return state == State.CONNECTED;
	}
	
	public boolean isLoggedIn() {
		return state == State.LOGGED_IN;
	}
	
	public void sendEvent(Event ev) {
		if (!this.isLoggedIn())
			throw new NullPointerException("XMPPService must be logged in before it can send!");
		
		Message m = new Message(this.conferenceJid, Message.Type.groupchat);
		m.setBody(ev.toJson());
		xmpp.sendPacket(m);
	}
	
	public void addEventListener(EventListener listener) {
		addEventListener(listener, new PacketTypeFilter(Message.class));
	}
	
	public void addEventListener(EventListener listener, PacketFilter filter) {
		xmpp.addPacketListener(listener, filter);
	}

}
