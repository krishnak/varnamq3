package com.nbs.q3.spi;

public class FIFOStatusRegister extends NRFRegister
{
	private static FIFOStatusRegister registerInstance = null;
	private FIFOStatusRegister()
	{
		this.address=0x17;
		
	}
	public static FIFOStatusRegister getInstance()
	{
		if(registerInstance == null)
			 registerInstance = new FIFOStatusRegister();
		return registerInstance;
	}
}
