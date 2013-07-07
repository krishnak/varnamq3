package com.nbs.q3.spi;

public class RxPayLoadinDataPipes extends NRFRegister
{
	private static RxPayLoadinDataPipes registerInstance = null;
	private RxPayLoadinDataPipes(RxDataPipes p)
	{
		switch(p.getDataPipe())
		{
		case 0:
			this.address=0x11;
			break;
		case 1:
			this.address=0x12;
			break;
		case 2:
			this.address=0x13;
			break;
		case 3:
			this.address=0x14;
			break;
		case 4:
			this.address=0x15;
			break;
		case 5:
			this.address=0x16;
			break;
	
		}
		
	}
	public static RxPayLoadinDataPipes getInstance(RxDataPipes p)
	{
		if(registerInstance == null)
			 registerInstance = new RxPayLoadinDataPipes(p);
		return registerInstance;
	}
	
}
