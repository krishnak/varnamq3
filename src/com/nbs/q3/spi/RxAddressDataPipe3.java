package com.nbs.q3.spi;

public class RxAddressDataPipe3 extends NRFRegister
{
	private static RxAddressDataPipe3 registerInstance = null;
	private RxAddressDataPipe3()
	{
		this.address=0x0D;
		
	}
	public static RxAddressDataPipe3 getInstance()
	{
		if(registerInstance == null)
			 registerInstance = new RxAddressDataPipe3();
		return registerInstance;
	}
}
