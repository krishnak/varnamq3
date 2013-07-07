package com.nbs.q3.spi;

public class DynamicPayloadEnableRegister extends NRFRegister
{
	private static DynamicPayloadEnableRegister registerInstance = null;
	private DynamicPayloadEnableRegister()
	{
		this.address=0x1C;
		
	}
	public static DynamicPayloadEnableRegister getInstance()
	{
		if(registerInstance == null)
			 registerInstance = new DynamicPayloadEnableRegister();
		return registerInstance;
	}
}
