package com.nbs.q3.spi;

public class TxAddressRegister extends NRFRegister
{
	private static TxAddressRegister registerInstance = null;
	private TxAddressRegister()
	{
		this.address=0x10;
		this.registerLength = 5;
		
	}
	public static TxAddressRegister getInstance()
	{
		if(registerInstance == null)
			 registerInstance = new TxAddressRegister();
		return registerInstance;
	}
}
