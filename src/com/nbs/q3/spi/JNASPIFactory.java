package com.nbs.q3.spi;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.List;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Platform;
import com.sun.jna.Structure;

import com.nbs.q3.exception.SpiDeviceException;

public enum JNASPIFactory
{
	//TODO: Code incomplete left it as it proved hard to find documentation for JNA
	INSTANCE;
	private static boolean connectionEstablished = false;

	private final static String device = "/dev/spidev0.0";
	private final static int SPI_IOC_WR_MODE = 1073834753;
	private final static int SPI_IOC_WR_BITS_PER_WORD = 1073834755;
	private final static int SPI_IOC_WR_MAX_SPEED_HZ = 1074031364;
	private final static int SPI_WRT_RD_CMD=1075866368;
	private SPIMode mode = SPIMode.mode0;
	private  byte bits=0x08;
	private  int speed = 5000000;
	private  short delay = 500;
	private  int spiHandle =0;
	//private  native FileDescriptor SPIConnect(String device) throws IOException ;
	//private native int SPIConfigure(FileDescriptor handle,byte mode,byte bits,int speed);
	//private native void SPIDisconnect(FileDescriptor handle);
	private native byte[] SPIWriteRead(FileDescriptor handle,byte[]tx,int length,short delay,byte bits,int speed);
    
	private static CLibrary clib = (CLibrary)Native.loadLibrary("c", CLibrary.class);
	static final int O_RDWR = 0x02;
	
	private interface CLibrary extends Library {
        
        public int ioctl(int fd, int cmd, int arg);
        public int ioctl(int fd,int cmd, SPIMessage sm);
        public int open(String path, int flags);
        public int close(int fd);
        public class spi_ioc_transfer extends Structure
        {
        	public NativeLong tx_buf;
        	public NativeLong rx_buf;
        	public int len;
        	public int delay_usecs;
        	public short speed_hz;
        	public byte bits_per_word ;
        	
			@Override
			protected List getFieldOrder()
			{
				// TODO Auto-generated method stub
				return null;
			}
        	
        	
        }
	}

	
	
	public synchronized byte[]  writeRead(byte[] tx) throws SpiDeviceException
	{
		if(spiHandle<=0)
			throw new SpiDeviceException("Device not ready, Have you called Connect?");
	//	clib.ioctl(spiHandle, SPI_WRT_RD_CMD, arg);
		//return this.SPIWriteRead(spiHandle,tx,tx.length,delay,bits,speed);
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
		if(spiHandle<=0)
			throw new SpiDeviceException("Device not ready, Have you called Connect?");
		//this.SPIConfigure(spiHandle,mode.getSpiMode(),bits,speed);
	//	clib.ioctl(spiHandle, mode.getSpiMode(), arg);
		if(clib.ioctl(spiHandle,SPI_IOC_WR_MODE,mode.getSpiMode())<0)
		{
			throw new SpiDeviceException("Device not ready, Have you called Connect?");
		}
		
	    if(clib.ioctl(spiHandle, SPI_IOC_WR_BITS_PER_WORD, bits)<0)
	    {
			throw new SpiDeviceException("Device not ready, Have you called Connect?");	    	
	    }
	    if(clib.ioctl(spiHandle, SPI_IOC_WR_MAX_SPEED_HZ, speed)<0)
	    {
			throw new SpiDeviceException("Device not ready, Have you called Connect?");	    	
	    }
	    

	}
	public  void connectSPI() throws IOException
	{
		//spiHandle=SPIConnect(device);
		spiHandle = clib.open(device, O_RDWR);
		//change it to a standard java NIO
	}
	public synchronized void disconnectSPI()
	{
		clib.close(spiHandle);
		spiHandle = 0;
	}
	public void chipEnable()
	{
		
	}
	public void chipDisable()
	{
		
	}

}
