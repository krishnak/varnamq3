package com.nbs.q3.spi;

public class RFSetupRegister extends NRFRegister
{
	private static RFSetupRegister registerInstance = null;
	private RFSetupRegister()
	{
		this.address=0x06;
		
	}
	public static RFSetupRegister getInstance()
	{
		if(registerInstance == null)
			 registerInstance = new RFSetupRegister();
		return registerInstance;
	}
}
