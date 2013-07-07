package com.nbs.q3.events;

import java.util.EventObject;

public class PropertyChangeEvent extends EventObject
{
	private RemoteButtons buttonSource;
	private boolean isPressed = false;
	


	/**
	 * The event
	 */
	private static final long serialVersionUID = 1L;

	public  PropertyChangeEvent(Object source,RemoteButtons button,boolean pressed)
	{
		super(source);
		this.buttonSource = button;
		this.isPressed = pressed;
		
	}
	public RemoteButtons getButton()
	{
		return this.buttonSource;
	}
	public boolean isPressed()
	{
		return isPressed;
	}
	

}
