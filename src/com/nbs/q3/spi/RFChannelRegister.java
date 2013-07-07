package com.nbs.q3.spi;

public class RFChannelRegister extends NRFRegister
{
	private static RFChannelRegister registerInstance = null;
	private RFChannelRegister()
	{
		this.address=0x05;
		
	}
	public static RFChannelRegister getInstance()
	{
		if(registerInstance == null)
			 registerInstance = new RFChannelRegister();
		return registerInstance;
	}
}
