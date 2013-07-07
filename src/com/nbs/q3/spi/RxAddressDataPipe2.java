package com.nbs.q3.spi;

public class RxAddressDataPipe2 extends NRFRegister
{
	private static RxAddressDataPipe2 registerInstance = null;
	private RxAddressDataPipe2()
	{
		this.address=0x0C;
		
	}
	public static RxAddressDataPipe2 getInstance()
	{
		if(registerInstance == null)
			 registerInstance = new RxAddressDataPipe2();
		return registerInstance;
	}
}
