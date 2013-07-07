package com.nbs.q3.spi;

import java.io.FileDescriptor;
import java.io.IOException;

import com.nbs.q3.exception.SpiDeviceException;

public enum SPIFactory 
{
	INSTANCE;
	private static boolean connectionEstablished = false;

	private final static String device = "/dev/spidev0.0";
	private SPIMode mode = SPIMode.mode0;
	private  byte bits=0x08;
	private  int speed = 5000000;
	private  short delay = 500;
	private  FileDescriptor spiHandle ;
	private  native FileDescriptor SPIConnect(String device) throws IOException ;
	private native int SPIConfigure(FileDescriptor handle,byte mode,byte bits,int speed);
	private native void SPIDisconnect(FileDescriptor handle);
	private native byte[] SPIWriteRead(FileDescriptor handle,byte[]tx,int length,short delay,byte bits,int speed);

	
	public synchronized byte[]  writeRead(byte[] tx) throws SpiDeviceException
	{
		if(spiHandle==null)
			throw new SpiDeviceException("Device not ready, Have you called Connect?");
		return this.SPIWriteRead(spiHandle,tx,tx.length,delay,bits,speed);
	}
	public synchronized void setConfiguration(SPIMode mode,byte bits,int speed) throws SpiDeviceException
	{
		this.mode=mode;
		this.bits=bits;
		this.speed=speed;
		this.setConfiguration();
	}
	public synchronized void setConfiguration() throws SpiDeviceException
	{
		if(spiHandle==null)
			throw new SpiDeviceException("Device not ready, Have you called Connect?");
		this.SPIConfigure(spiHandle,mode.getSpiMode(),bits,speed);

	}
	public  void connectSPI() throws IOException
	{
		spiHandle=SPIConnect(device);
	}
	public synchronized void disconnectSPI()
	{
		SPIDisconnect(spiHandle);
		spiHandle = null;
	}
	public void chipEnable()
	{
		
	}
	public void chipDisable()
	{
		
	}
	static {
		System.loadLibrary("SPILibrary");
		}
}
