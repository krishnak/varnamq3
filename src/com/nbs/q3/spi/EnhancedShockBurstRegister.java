package com.nbs.q3.spi;

public class EnhancedShockBurstRegister extends NRFRegister
{
	private static EnhancedShockBurstRegister registerInstance = null;
	private EnhancedShockBurstRegister()
	{
		this.address=0x01;
		
	}
	public static EnhancedShockBurstRegister getInstance()
	{
		if(registerInstance == null)
			 registerInstance = new EnhancedShockBurstRegister();
		return registerInstance;
	}
}
