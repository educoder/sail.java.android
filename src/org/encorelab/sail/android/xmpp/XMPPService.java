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

import android.R;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

// FIXME: reconcile this with org.encorelab.sail.XMPPClient
//        (probably just instantiate an XMPPClient as a member
//         variable inside XMPPService and delegate to XMPPClient)
public class XMPPService extends Service {
	
	public static final String TAG = "XMPPService";
	
	public static final int DEFAULT_XMPP_PORT = 5222;
	
	private final IBinder localBinder = new LocalBinder();
	
	private XMPPThread xmppThread;
	
	
	@Override
	public void onCreate() {
		Log.d(XMPPService.TAG, "Creating XMPPService...");
		super.onCreate();
		
		xmppThread = new XMPPThread();
		xmppThread.run();
		
		Log.i(TAG, "XMPPService created.");
	}
	
	

	@Override
	public IBinder onBind(Intent intent) {
		if (intent.hasExtra("host"))
			xmppThread.configure(intent.getStringExtra("host"), intent.getIntExtra("port", 5222));
		
		XMPPConnection.DEBUG_ENABLED = true;
		
		if (!xmppThread.isConnected()) {
			try {
				xmppThread.connect();
			} catch (XMPPException e) {
				Log.e(TAG, e.getMessage());
			}
		}
		
		return localBinder;
	}
	
	@Override
	// FIXME: do we need this if we're only binding? docs say that this is
	//        only used for services that are manually started
	public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.        
        return START_STICKY;
    }
	
	public class LocalBinder extends Binder {
		public XMPPService getService() {
			return XMPPService.this;
		}
	}
}
