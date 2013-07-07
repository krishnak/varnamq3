package com.nbs.q3.server;



import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.concurrent.ArrayBlockingQueue;

import com.nbs.q3.events.*;
import com.nbs.q3.remotecontrol.DeviceController;

public class VarnamServer 
{
	private static final Byte[] vibrate = {0,2,0,0,1,0,0,0,0,0};
	private static final Byte[] sound = {1,0,0,0,1,0,0,0,0,0};
	
	public static int last_state = 0;
	public static int counter = 0;
	public static float x;
	public static float y;
	public static float z;
	public static void main(String args[]) 
	{
		 ArrayBlockingQueue<Byte[]> txData = new ArrayBlockingQueue<Byte[]>(1);
		 

		final ThreadMonitor tm = new ThreadMonitor();
		
		int c ;

		try
		{
		
			DeviceController dc = new DeviceController(tm,new RemoteAdapter(){

				@Override
				public void ActionPerformed(PropertyChangeEvent e)
				{
					
				}

				@Override
				public void buttonPressed(PropertyChangeEvent e)
				{
					System.out.println("Button Pressed : "+e.getButton().name());
					
				}

				@Override
				public void buttonReleased(PropertyChangeEvent e)
				{
					System.out.println("Button Released : "+e.getButton().name());
					
				}
				
			},new TiltEventListener()
			{

				@Override
				public void AxisChanged(AxisEvent e)
				{
					/*
					
					
					
					if(rest_x-x>3 || rest_y-y>3|| rest_z-z>3 || rest_x-x<3 || rest_y-y<3|| rest_z-z<3 )
					System.out.println(" Angle X : "+x);
					System.out.println(" Angle Y : "+y);
					System.out.println(" Angle Z : "+z);
					*/
					   // Y    X    Z
					/*-121,124,-91  remote horizontal on surface with dpad up
					
					   -100,122,-122 rotate forward
					    100,122,-122 rotate backward
					    -129 to 120 ,121,110 facing down 
					    -121,91,-119 DPad side up vertical
					    -131,-109,-124 DPAD down
					*/
					DecimalFormat df = new DecimalFormat("#.#");
					if(counter<25)
					{
						x += e.getNewX();
						y += e.getNewY();
						z += e.getNewZ();
						counter ++;
						return;
					}
					counter = 0;
					x = x/25;
					y = y/25;
					z = z/25;
				//	System.out.println(df.format(x)+":"+df.format(y)+":"+df.format(z));
					if(Math.abs(x)<=1.6 && Math.abs(z)>1.6)	
					{
					//	System.out.println("Inside 1");
						if(x>0 && last_state!=3)
						{
							System.out.println("Remote rolled to left");
							last_state = 3;
						}
						else if(x<0 && last_state !=2)
						{
							last_state = 2;
							System.out.println("Remote rolled to right");
						}
					}
					if(Math.abs(y)<=1.75 && Math.abs(z)>1.6)
					{
					//	System.out.println("Last state "+last_state);
						if(y>0 && last_state !=4)
						{
							last_state = 4;
							System.out.println("Remote Pitched up");
						}
						else if(y<0 && last_state !=5)
						{
							last_state =5;
							System.out.println("Remote pitched down");
						}
					}
					if(Math.abs(z)<=1.6 && Math.abs(x)>1.7 && Math.abs(y)>1.7)
					{
					//	System.out.println("Inside 2");
						if(z>0 && last_state!=6)
						{
							last_state=6;
							System.out.println("Remote Horizontal facing down");
						}
						else if(z<0 && last_state!=1)
						{
							last_state = 1;
							System.out.println("Remote facing up");	
						}
						
					}
				}
				
			},txData);
			Thread t =new Thread(dc);
			t.start();
		    Runtime.getRuntime().addShutdownHook(new Thread()
		    {
		    	public void run()
		    	{
		    		tm.stopThreads();
		    	}
		    });

			while(((char)(c=System.in.read()))!='q')
			{
				//System.out.println("Keyboard input "+c);
				int i=0;
				if(c=='v')
				{
					while(i<10000)
					{
						txData.offer(vibrate);
						i++;
					}
						
				}
				if(c=='s')
				{
					txData.clear();
					txData.offer(sound);
				}
				
				//next step is to pass this value to the remote
				Thread.sleep(2);
			}
			tm.stopThreads();
			System.out.println("Server thread exiting....");
		}
		catch(FileNotFoundException any)
		{
			System.out.println("Unable to find Remote's address file, please run address finder first");
		}
		catch(Exception any)
		{
			any.printStackTrace();
		}
	}
}
