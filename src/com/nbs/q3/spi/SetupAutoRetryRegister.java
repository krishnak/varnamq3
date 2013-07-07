package com.nbs.q3.spi;

public class SetupAutoRetryRegister extends NRFRegister
{
	private static SetupAutoRetryRegister registerInstance = null;
	private SetupAutoRetryRegister()
	{
		this.address=0x04;
		
	}
	public static SetupAutoRetryRegister getInstance()
	{
		if(registerInstance == null)
			 registerInstance = new SetupAutoRetryRegister();
		return registerInstance;
	}
}
