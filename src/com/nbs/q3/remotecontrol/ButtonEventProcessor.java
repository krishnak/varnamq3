package com.nbs.q3.remotecontrol;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.nbs.q3.dataObjects.Constants;
import com.nbs.q3.events.PropertyChangeEvent;
import com.nbs.q3.events.RemoteAdapter;
import com.nbs.q3.events.RemoteButtons;
import com.nbs.q3.server.ThreadMonitor;

public class ButtonEventProcessor implements Runnable
{
	private ArrayBlockingQueue<Byte> sensorData;
	private ThreadMonitor monitor;
	private byte buttonCode = 0;
	Byte buttondata;
	private static RemoteButtons button1 = RemoteButtons.None;
	private static RemoteButtons button2 = RemoteButtons.None;
	private static RemoteButtons prevbutton1 = RemoteButtons.None;
	private static RemoteButtons prevbutton2 = RemoteButtons.None;

	private ArrayList eventQueue = new ArrayList();
	private RemoteAdapter remoteAdapter;
	private PropertyChangeEvent buttonEvent;

	public ButtonEventProcessor(ArrayBlockingQueue<Byte> data,
			RemoteAdapter ra, ThreadMonitor tm)
	{
		sensorData = data;
		monitor = tm;
		remoteAdapter = ra;

	}

	public ButtonEventProcessor(ArrayBlockingQueue<Byte> data,
			ThreadMonitor tm, ArrayList eventQueue)
	{
		sensorData = data;
		monitor = tm;
		this.eventQueue = eventQueue;
	}

	@Override
	public void run()
	{
		System.out.println("Button Processor Thread started");
		while (!monitor.getStopStatus())
		{
			try
			{
				buttondata = sensorData.poll(10L, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (buttondata != null)
			{
				buttonCode = buttondata.byteValue();
				if (!(buttonCode == Constants.DEFAULT_BUTTON_BYTE
						&& button1.equals(RemoteButtons.None) && button2
							.equals(RemoteButtons.None)))
				{
					// check whether previous button states. If previous button
					// state is NONE - then avoid processing this

					byte lowernibble = (byte) (buttonCode & (byte) 0x0F);
					byte uppernibble = (byte) (buttonCode & (byte) 0xF0);
					switch (lowernibble)
					{
					case (byte) 0x0E:
						button1 = RemoteButtons.DPadL;
						break;
					case (byte) 0x0D:
						button1 = RemoteButtons.DPadR;
						break;
					case (byte) 0x07:
						button1 = RemoteButtons.DPadU;
						break;
					case (byte) 0x0B:
						button1 = RemoteButtons.DPadD;
						break;
					default:
						button1 = RemoteButtons.None;

					}
					switch (uppernibble)
					{
					case (byte) 0x60:
						button2 = RemoteButtons.AButton;
						break;
					case (byte) 0x50:
						button2 = RemoteButtons.BButton;
						break;
					case (byte) 0x30:
						button2 = RemoteButtons.Menu;
						break;
					default:
						button2 = RemoteButtons.None;

					}
					if (!button1.equals(prevbutton1))
					{
						if (prevbutton1.equals(RemoteButtons.None))
						{
							buttonEvent = new PropertyChangeEvent(this,
									button1, true);
							// this is a button press
							if (remoteAdapter != null)
								remoteAdapter.buttonPressed(buttonEvent);
							else
								eventQueue.add(buttonEvent);
						}
						if (button1.equals(RemoteButtons.None))
						{
							buttonEvent = new PropertyChangeEvent(this,
									prevbutton1, false);
							// this is a button release
							if (remoteAdapter != null)
							{
								remoteAdapter.buttonReleased(buttonEvent);
								remoteAdapter.ActionPerformed(buttonEvent);
							} 
							else
								eventQueue.add(buttonEvent);

						}

						prevbutton1 = button1;
					} 
					else
					{
						if (!button1.equals(RemoteButtons.None))
						{
							buttonEvent = new PropertyChangeEvent(this,
									button1, true);
							eventQueue.add(buttonEvent);
						}

					}
					if (!button2.equals(prevbutton2))
					{
						if (prevbutton2.equals(RemoteButtons.None))
						{
							buttonEvent = new PropertyChangeEvent(this,
									button2, true);

							// this is a button press
							if (remoteAdapter != null)
								remoteAdapter.buttonPressed(buttonEvent);
							else
								eventQueue.add(buttonEvent);

						}
						if (button2.equals(RemoteButtons.None))
						{
							buttonEvent = new PropertyChangeEvent(this,
									prevbutton2, false);
							// this is a button press
							if (remoteAdapter != null)
							{
								remoteAdapter.buttonReleased(buttonEvent);
								remoteAdapter.ActionPerformed(buttonEvent);
							} else
								eventQueue.add(buttonEvent);
						}

						prevbutton2 = button2;
					} 
					else
					{
						if (!button2.equals(RemoteButtons.None))
						{
							buttonEvent = new PropertyChangeEvent(this,
									button2, true);
							eventQueue.add(buttonEvent);
						}

					}
				}
			}
			try
			{
				Thread.sleep(10L);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}

		}
		System.out.println("Button Processor Thread Stopping.....");

	}

}
