package com.nbs.q3.events;

import java.util.EventObject;


public class AxisEvent extends EventObject
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Byte[] value ;
	public AxisEvent(Object source, Byte[] val)
	{
		super(source);
		
		this.value = val;
		// TODO Auto-generated constructor stub
	}
	public synchronized int getX()
	{
		int x =value[0].byteValue(); 
		if(Math.abs(x)<3)
			return 0;
		return x;
	}
	public synchronized int getY()
	{
		int y =value[1].byteValue(); 
		if(Math.abs(y)<3)
			return 0;
		return y;
	}
	public synchronized int getZ()
	{
		int z =value[2].byteValue(); 
		if(Math.abs(z)<3)
			return 0;
		return z;
		
	}
	
	public synchronized float getNewX()
	{
		
		return  (float) (value[0].intValue()/64.0) ;
	}
	public synchronized float getNewY()
	{
		
		return  (float) (value[1].intValue()/64.0) ;
	}
	public synchronized float getNewZ()
	{
		return  (float) (value[2].intValue()/64.0) ;
		
	}
}
