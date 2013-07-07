package com.nbs.q3.spi;

public class RxAddressDataPipe1 extends NRFRegister
{
	private static RxAddressDataPipe1 registerInstance = null;
	private RxAddressDataPipe1()
	{
		this.address=0x0B;
		this.registerLength = 5;
		
	}
	public static RxAddressDataPipe1 getInstance()
	{
		if(registerInstance == null)
			 registerInstance = new RxAddressDataPipe1();
		return registerInstance;
	}
}
