package com.nbs.q3.spi;

public class SetupAddressWidthRegister extends NRFRegister
{
	private static SetupAddressWidthRegister registerInstance = null;
	private SetupAddressWidthRegister()
	{
		this.address=0x03;
		
	}
	public static SetupAddressWidthRegister getInstance()
	{
		if(registerInstance == null)
			 registerInstance = new SetupAddressWidthRegister();
		return registerInstance;
	}
}
