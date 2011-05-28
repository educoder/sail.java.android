package org.encorelab.sail.android.xmpp;

import android.content.Context;
import android.content.Intent;

public class XMPPServiceIntent extends Intent {
	private String host;
	private int port;
	
	public XMPPServiceIntent(Context context, String host) {
		super(context, XMPPService.class);
		this.putExtra("host", host);
	}
	
	public XMPPServiceIntent(Context context, String host, int port) {
		super(context, XMPPService.class);
		this.putExtra("host", host);
		this.putExtra("port", port);
	}
}
