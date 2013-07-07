package com.nbs.q3.spi;


import com.nbs.q3.exception.SpiDeviceException;

abstract  class NRFRegister
{
	protected byte address;
	protected byte	 value; 
	protected static final byte write_mask = 0x20;
	protected int registerLength = 1;
	
	public  synchronized boolean writeToRegister(byte data) throws SpiDeviceException
	{
	//	System.out.println(this.getClass().getName()+" "+data);
		byte dataarr[] = new byte[]{(byte) (write_mask|address),data};
		//System.out.println(this.getClass().getName()+"Array length  "+dataarr.length);
		//System.out.println(this.getClass().getName()+"Contents  "+dataarr[0]+","+dataarr[1]);
		SPIFactory.INSTANCE.writeRead(dataarr);
		value  =data;
		return true;
	}
	public  synchronized int readFromRegister() throws SpiDeviceException
	{
		return value = SPIFactory.INSTANCE.writeRead(new byte[]{(byte) address,0})[1];
	}
	
	protected synchronized byte[] readFromRegister(int bytes) throws SpiDeviceException
	{
		byte data[] = new byte[bytes+1];
		data[0]=(byte) address;
		return SPIFactory.INSTANCE.writeRead(data);
	}
	public synchronized boolean writeToRegister(byte[] data) throws SpiDeviceException
	{
		SPIFactory.INSTANCE.writeRead(appendWriteCommandToDataArray((byte) (write_mask|address), data));
		return true;
	}
	public  byte[] appendWriteCommandToDataArray(byte command,byte[] data)
	{
		int length = data.length;
		byte newdata[]=new byte[length+1];
		newdata[0]=command;
		System.arraycopy(data, 0, newdata, 1, length);
		//System.out.println("*****");
		//System.out.println(newdata[1]);
		//System.out.println("*****");
		return newdata;
	}
	
	public int getRegisterLength()
	{
		return registerLength;
	}
	

}
