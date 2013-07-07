package com.nbs.q3.spi;

import com.nbs.q3.exception.SpiDeviceException;

public class ConfigRegister extends NRFRegister
{
	private static ConfigRegister registerInstance = null;
	private ConfigRegister()
	{
		this.address=0x00;
		this.value = 0x08;
		
	}
	public static ConfigRegister getInstance()
	{
		if(registerInstance == null)
			 registerInstance = new ConfigRegister();
		return registerInstance;
	}
	public boolean powerUp() throws SpiDeviceException, InterruptedException
	{
		value=(byte) (value|0x02);
		writeToRegister(value);
		Thread.sleep(0,150000);
		return true;
	}
	public boolean powerDown() throws SpiDeviceException, InterruptedException
	{
		value=(byte) (value&0xFD);
		writeToRegister(value);
		Thread.sleep(0,150000);
		return true;
	}
	public boolean enableTransmitter() throws SpiDeviceException
	{	
		value = (byte)(value&0xFE);
		writeToRegister(value);
		return true;
	}
	public boolean enableReceiver() throws SpiDeviceException
	{
		value=(byte) (value|0x01);
		writeToRegister(value);
		return true;
	}
	/*
	 * 
	 * Other bit wise operator methods can be added here later
	 */
	
}
