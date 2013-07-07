package com.nbs.q3.remotecontrol;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.nbs.q3.dataObjects.Constants;
import com.nbs.q3.events.AxisEvent;
import com.nbs.q3.events.TiltEventListener;
import com.nbs.q3.server.ThreadMonitor;

public class AxisEventProcessor implements Runnable
{
	private ArrayBlockingQueue<Byte[]> sensorData;
	private TiltEventListener tiltListener;
	private ThreadMonitor monitor;
	private boolean filter;
	private int windowsize = 1;
	private Byte[] axisData;
	
	/*private int currentX = 0;
	private int currentY = 0;
	private int currentZ = 0;
	
	private int previousX=0;
	private int previousY=0;
	private int previousZ=0;
	
	private int deltaX = 0;
	private int deltaY = 0;
	private int deltaZ = 0;
	
	private int maxValueX =0;
	private int maxValueY = 0;
	private int maxValueZ = 0;
	
	private int minValueX =0;
	private int minValueY = 0;
	private int minValueZ = 0;*/
	
	
	public AxisEventProcessor(ArrayBlockingQueue<Byte[]> rd,TiltEventListener tilt,ThreadMonitor tm,boolean filterData,int size)
	{
		this.sensorData = rd;
		this.tiltListener = tilt;
		this.monitor = tm;
		this.filter = filterData;
		if(this.filter)
			this.windowsize = size;
	}
	@Override
	public void run()
	{
		System.out.println("Axix Processor Thread Starting.....");

		int counter = 0;
		while(!monitor.getStopStatus())
		{
			try
			{
				axisData = sensorData.poll(10L,TimeUnit.MILLISECONDS);
				tiltListener.AxisChanged(new AxisEvent(this,axisData));
/*				if(axisData.length>0)
				{
					if(axisData[0]!=0)
					{
						currentX=(byte)(axisData[0]+(byte)0x80);
						currentY=(byte)(axisData[1]-(byte)0x78);
						currentZ=(byte)(axisData[2]+(byte)0x80);
						System.out.println("X: "+axisData[0]+"  Y:  "+axisData[1]+" Z :"+axisData[2]);
						System.out.println("cX: "+currentX+"  cY:  "+currentY+" cZ :"+currentZ);
						if(maxValueX==0&&minValueX==0)
						{
							//first time
							maxValueX=currentX;
							minValueX=currentX;
						}
						else
						{	
							if(currentX>maxValueX)
							{
								maxValueX = currentX;
							}
							if(currentX<minValueX)
							{
							
								minValueX = currentX;
							}
							
						}
						if(maxValueY==0&&minValueY==0)
						{
							maxValueY=currentY;
							minValueY=currentY;
						}
						else
						{
							if(currentY>maxValueY)
							{
								maxValueY = currentY;
							}
							if(currentY<minValueY)
							{
								minValueY = currentY;
							}
						}
						if(maxValueZ==0&&minValueZ==0)
						{
							maxValueZ=currentZ;
							minValueZ=currentZ;
						}
						else
						{
							if(currentZ>maxValueZ)
							{
								maxValueZ = currentZ;
							}
							
							if(currentZ<minValueZ)
							{
								minValueZ = currentZ;
							}
						}
						counter++;
					}
					if(counter<windowsize)
					{
					}
					else
					{
						counter =0;
						currentX/=windowsize;
						currentY/=windowsize;
						currentZ/=windowsize;
						if(filter)
						{
							if(Math.abs(currentX)<=2)
							{
								currentX=0;							
							}
							if(Math.abs(currentY)<=2)
							{
								currentY=0;							
							}
							if(Math.abs(currentZ)<=2)
							{
								currentZ=0;							
							}
							
						}
						deltaX= currentX-previousX;
						deltaY= currentY-previousY;
						deltaZ= currentZ-previousZ;
						
						if(deltaX>1)
						{
							//System.out.println("Motion detected in + Y axis");
							tiltListener.AxisChanged(new AxisEvent(this,Constants.DIRECTION_NORTH,currentX));
						}
						if(deltaX<-1)
						{
							//System.out.println("Motion detected in - Y axis");
							tiltListener.AxisChanged(new AxisEvent(this,Constants.DIRECTION_SOUTH,currentX));
						}
						if(deltaY>1)
						{
							//System.out.println("Motion detected in  -X axis");
							tiltListener.AxisChanged(new AxisEvent(this,Constants.DIRECTION_WEST,currentY));

						}
						if(deltaY<-1)
						{
							//System.out.println("Motion detected in +X axis");
							tiltListener.AxisChanged(new AxisEvent(this,Constants.DIRECTION_EAST,currentY));

						}
						if(deltaZ>1)
						{
							
						}
						if(deltaZ<1)
						{
							
						}
						
						previousX = currentX;
						previousY = currentY;
						previousZ = currentZ;
					}
					
				}*/
				Thread.sleep(10L);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
			catch(NullPointerException e)
			{
				
			}
			
		}
/*		System.out.println("Maximum value of X : "+maxValueX);
		System.out.println("Maximum value of Y : "+maxValueY);
		System.out.println("Maximum value of Z : "+maxValueZ);
		
		System.out.println("minimum value of X : "+minValueX);
		System.out.println("minimum value of Y : "+minValueY);
		System.out.println("minimum value of Z : "+minValueZ);*/

		System.out.println("Axis Processor Thread Stopping.....");

	}

}
