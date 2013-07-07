package com.nbs.q3.spi;

public class EnableDataPipeRegister extends NRFRegister
{
	private static EnableDataPipeRegister registerInstance = null;
	private EnableDataPipeRegister()
	{
		this.address=0x02;
		
	}
	public static EnableDataPipeRegister getInstance()
	{
		if(registerInstance == null)
			 registerInstance = new EnableDataPipeRegister();
		return registerInstance;
	}
}
