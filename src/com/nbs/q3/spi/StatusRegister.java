package com.nbs.q3.spi;

import com.nbs.q3.exception.SpiDeviceException;

public class StatusRegister extends NRFRegister
{
	private static StatusRegister registerInstance = null;
	private StatusRegister()
	{
		this.address=0x07;
		
	}
	public static StatusRegister getInstance()
	{
		if(registerInstance == null)
			 registerInstance = new StatusRegister();
		return registerInstance;
	}
	public void clearMaxRTFlag() throws SpiDeviceException
	{
		// TODO Auto-generated method stub
		writeToRegister((byte)0x70);
	}
}
