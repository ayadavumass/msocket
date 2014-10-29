/**
 * Mobility First - Global Name Resolution Service (GNS)
 * Copyright (C) 2013 University of Massachusetts - Emmanuel Cecchet.
 * Contact: cecchet@cs.umass.edu
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 *
 * Initial developer(s): Emmanuel Cecchet.
 * Contributor(s): ______________________.
 */
package edu.umass.cs.msocket;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 * 
 * Singleton keep alive thread class, and also keeps timer time.
 * 
 * @author <a href="mailto:cecchet@cs.umass.edu">Emmanuel Cecchet</a>
 * @version 1.0
 */
public class KeepAliveStaticThread implements Runnable
{
	//Timer ticks after 1000ms
	private static final int								TIMER_TICK			= 1000;
	
	private static KeepAliveStaticThread                    keepAliveObj        = null;
	private static boolean 										runstatus		= true;
	private static HashMap<Long, StoreInfo>					registeredMSockets	= null;
	private static Timer									localTimer			= null;
	
	private static long                     				localClock			= 0;
	
	/**
	 * MSockets register for keep alives
	 * 
	 * @param cInfo
	 */
	public synchronized static void registerForKeepAlive(ConnectionInfo cinfo)
	{
		createSingleton();
		TimerTaskClass timertask = new TimerTaskClass(cinfo.getMSocket());
		StoreInfo storeinfo = new StoreInfo(cinfo, timertask);
		registeredMSockets.put(cinfo.getFlowID(), storeinfo);
	}
	
	public synchronized static void unregisterForKeepAlive(ConnectionInfo cinfo)
	{
		createSingleton();
		registeredMSockets.remove(cinfo.getFlowID());
	}
	
	public synchronized static long getLocalClock()
	{
		createSingleton();
		return localClock;
	}
	
	public static void stopKeepAlive()
	{
		runstatus = false;
	}
	 
	/**
	 * private constructor
	 */
	private KeepAliveStaticThread()
	{
		registeredMSockets = new HashMap<Long, StoreInfo>();
		localTimer = new Timer();
		startLocalTimer();
	}
	
	private void startLocalTimer()
	{
		localTimer.scheduleAtFixedRate(new TimerTask()
	    {
	      @Override
	      public void run()
	      { 
	        localClock++;
	      }
	    }, TIMER_TICK, TIMER_TICK);
	}
	
	 /**
	  * Checks if the singleton object is created or not, 
	  * if not it creates the object and then the object is returned.
	  * 
	  * @return the singleton object
	  */
	 private static void createSingleton()
	 {
		 if (keepAliveObj == null)
		 {
			 keepAliveObj = new KeepAliveStaticThread();
			 new Thread(keepAliveObj).start();
		 }
	 }
	 

	@Override
	public void run() 
	{
		while(runstatus)
		{
			Vector<StoreInfo> infoAll = new Vector<StoreInfo>();
			infoAll.addAll(registeredMSockets.values());
			
			for(int i=0; i< infoAll.size(); i++)
			{
				StoreInfo info = infoAll.get(i);
				TimerTaskClass timertask = info.getTimerTask();
				// does the timer task procedure
				timertask.run();
			}
			
			try
			{
				Thread.sleep(MSocket.KEEP_ALIVE_FREQ * 1000);
		    }
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		localTimer.cancel();
		
		// free up the state
		registeredMSockets.clear();
		keepAliveObj = null;
		System.out.println("Keep alive static thread exits");
	}
	
	private static class StoreInfo
	{
		private final ConnectionInfo cinfo;
		private final TimerTaskClass timertask;
		
		public StoreInfo(ConnectionInfo cinfo, TimerTaskClass timertask)
		{
			this.cinfo = cinfo;
			this.timertask = timertask;
		}
		
		public ConnectionInfo getConnectionInfo()
		{
			return cinfo;
		}
		
		public TimerTaskClass getTimerTask()
		{
			return timertask;
		}
	}
}