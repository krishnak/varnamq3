package com.nbs.q3.server;

public class ThreadMonitor
{
	private boolean stopThread = false;
	public ThreadMonitor()
	{
		
	}
	public synchronized void stopThreads()
	{
		stopThread = true;
	}
	public synchronized boolean getStopStatus()
	{
		return stopThread;
	}
}
