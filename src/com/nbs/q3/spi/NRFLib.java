package com.nbs.q3.spi;


import com.nbs.q3.exception.SpiDeviceException;
import com.nbs.q3.spi.SPIFactory;

public enum NRFLib
{
	INSTANCE;
	private static final byte config_addr 	=0x00;
	private static final byte enaa_addr   	=0x01;
	private static final byte enrx_addr		=0x02;
	private static final byte swar_addr		=0x03;
	private static final byte retry_addr		=0x04;
	private static final byte rfchn_addr 	=0x05;
	private static final byte rfset_addr		=0x06;
	private static final byte status_addr	=0x07;
	private static final byte observe_addr	=0x08;
	private static final byte carrdet_addr	=0x09;
	private static final byte rxpip0_addr  	=0x0A;
	private static final byte rxpip1_addr  	=0x0B;
	private static final byte rxpip2_addr  	=0x0C;
	private static final byte rxpip3_addr  	=0x0D;
	private static final byte rxpip4_addr  	=0x0E;
	private static final byte rxpip5_addr  	=0x0F;
	private static final byte tx_addr		=0x10;
	private static final byte rxpyldp0_addr =0x11;
	private static final byte rxpyldp1_addr =0x12;
	private static final byte rxpyldp2_addr =0x13;
	private static final byte rxpyldp3_addr =0x14;
	private static final byte rxpyldp4_addr =0x15;
	private static final byte rxpyldp5_addr =0x16;
	private static final	byte fifo_status_addr =0x17;
	private static final byte dyn_pay_load_addr =0x1C;
	private static final byte feature_addr		=0x1D;
	
	private static final byte write_mask = 0x20;
	
	private static final byte read_rx_payld_cmd = 0x61;
	private static final byte write_tx_payld_cmd = (byte) 0xA0;
	private static final byte flush_tx_buffer_cmd =(byte) 0xE1;
	private static final byte fluxh_rx_buffer_cmd =(byte) 0xE2;	
	private static final byte reuse_last_payload_cmd = (byte)0xE3;
	private static final byte activate_cmd = 0x50;
	private static final byte read_rx_payload_width_cmd=0x60;
	private static final byte write_ack_payload_mask =(byte) 0xA8;
	private static final byte disable_auto_ack_cmd = (byte)0xB0;
	
	private static final byte NOP = (byte)0xFF;
	
	
	
	private int payload_data_size = 32; // this is just set as default, application needs to fix correct data size
	
	
	
	private synchronized byte writeToRegister(int[] cmd_data) throws SpiDeviceException
	{
		return SPIFactory.INSTANCE.writeRead(cmd_data)[0];//returns status register value
	}
	private synchronized byte readFromRegister(int[] cmd_data) throws SpiDeviceException
	{
		return SPIFactory.INSTANCE.writeRead(cmd_data)[1];//returns register value ignores status register value
	}
	private synchronized byte[] readdataFromRegister(int[] cmd_data) throws SpiDeviceException
	{
		return SPIFactory.INSTANCE.writeRead(cmd_data);
	}
	 
	public synchronized byte writeToConfig(byte data) throws SpiDeviceException
	{
		return writeToRegister(new int[]{(byte) (write_mask|config_addr),data});
	}
	public synchronized byte readConfigReg() throws SpiDeviceException
	{
		return readFromRegister(new int[]{(byte)config_addr,0x00}); 
	}
	//
	public synchronized byte readEnhancedShockBurstReg() throws SpiDeviceException
	{
		return readFromRegister(new int[]{(byte)enaa_addr,0x00});
	}
	public synchronized byte writeToEnchancedShockBurstReg(byte data) throws SpiDeviceException
	{
		return writeToRegister(new int[]{(byte) (write_mask|enaa_addr),data});
	}
	//
	public synchronized byte readEnabledAddressesReg() throws SpiDeviceException
	{
		return readFromRegister(new int[]{(byte)enrx_addr,0x00});
	}
	public synchronized byte writeToEnabledAddressReg(byte data) throws SpiDeviceException
	{
		return writeToRegister(new int[]{(byte) (write_mask|enrx_addr),data});
	}
	//
	public synchronized byte readSetupAddWidthReg() throws SpiDeviceException
	{
		return readFromRegister(new int[]{(byte)swar_addr,0x00});
	}
	public synchronized byte writeToSetupAddWidthReg(byte data) throws SpiDeviceException
	{
		return writeToRegister(new int[]{(byte) (write_mask|swar_addr),data});
	}
	//
	public synchronized byte readSetupRetryReg() throws SpiDeviceException
	{
		return readFromRegister(new int[]{(byte)retry_addr,0x00});
	}
	public synchronized byte writeToSetupRetryReg(byte data) throws SpiDeviceException
	{
		return writeToRegister(new int[]{(byte) (write_mask|retry_addr),data});
	}
	//
	
	public synchronized byte readRFChannelReg() throws SpiDeviceException
	{
		return readFromRegister(new int[]{(byte)rfchn_addr,0x00});
	}
	public synchronized byte writeToRFChannelReg(byte data) throws SpiDeviceException
	{
		return writeToRegister(new int[]{(byte) (write_mask|rfchn_addr),data});
	}
	//
	public synchronized byte readRFSetupReg() throws SpiDeviceException
	{
		return readFromRegister(new int[]{(byte)rfset_addr,0x00});
	}
	public synchronized byte writeToRFSetupReg(byte data) throws SpiDeviceException
	{
		return writeToRegister(new int[]{(byte) (write_mask|rfset_addr),data});
	}
	//
	public synchronized byte readStatusReg() throws SpiDeviceException
	{
		return readFromRegister(new int[]{(byte)status_addr,0x00});
	}
	public synchronized byte writeToStatusReg(byte data) throws SpiDeviceException
	{
		return writeToRegister(new int[]{(byte) (write_mask|status_addr),data});
	}
	//
	public synchronized byte readObserveReg() throws SpiDeviceException
	{
		return readFromRegister(new int[]{(byte)observe_addr,0x00});
	}
	public synchronized byte readCarrierDetectReg() throws SpiDeviceException
	{
		return readFromRegister(new int[]{(byte)carrdet_addr,0x00});
	}
	//

	public synchronized byte[] readDataPipe0AddressReg() throws SpiDeviceException
	{
		return readdataFromRegister(new int[]{(byte)rxpip0_addr,0x00,0x00,0x00,0x00,0x00});
	}
	public synchronized byte writeToDataPipe0AddressReg(int data[]) throws SpiDeviceException
	{
		
		return writeToRegister(appendWriteCommandToDataArray((byte)(write_mask|rxpip0_addr),data));	
	}

	//

	public synchronized byte[] readDataPipe1AddressReg() throws SpiDeviceException
	{
		return readdataFromRegister(new int[]{(byte)rxpip1_addr,0x00,0x00,0x00,0x00,0x00});
	}
	public synchronized byte writeToDataPipe1AddressReg(int[] data) throws SpiDeviceException
	{
	
		return writeToRegister(appendWriteCommandToDataArray((byte)(write_mask|rxpip1_addr),data));
	}

	//

	public synchronized byte readDataPipe2AddressReg() throws SpiDeviceException
	{
		return readFromRegister(new int[]{(byte)rxpip2_addr,0x00});
	}
	public synchronized byte writeToDataPipe2AddressReg(byte data) throws SpiDeviceException
	{
		return writeToRegister(new int[]{(byte) (write_mask|rxpip2_addr),data});
	}

	//

	public synchronized byte readDataPipe3AddressReg() throws SpiDeviceException
	{
		return readFromRegister(new int[]{(byte)rxpip3_addr,0x00});
	}
	public synchronized byte writeToDataPipe3AddressReg(byte data) throws SpiDeviceException
	{
		return writeToRegister(new int[]{(byte) (write_mask|rxpip3_addr),data}); 
	}

	//

	public synchronized byte readDataPipe4AddressReg() throws SpiDeviceException
	{
		return readFromRegister(new int[]{(byte)rxpip4_addr,0x00});
	}
	public synchronized byte writeToDataPipe4AddressReg(byte data) throws SpiDeviceException
	{
		return writeToRegister(new int[]{(byte) (write_mask|rxpip4_addr),data});
	}

	//

	public synchronized byte readDataPipe5AddressReg() throws SpiDeviceException
	{
		return readFromRegister(new int[]{(byte)rxpip5_addr,0x00});
	}
	public synchronized byte writeToDataPipe5AddressReg(byte data) throws SpiDeviceException
	{
		return writeToRegister(new int[]{(byte) (write_mask|rxpip5_addr),data});
	}

	
	
	
	public synchronized byte[] readTxPipeAddressReg() throws SpiDeviceException
	{
		return readdataFromRegister(new int[]{(byte)tx_addr,0x00,0x00,0x00,0x00,0x00});
	}
	public synchronized byte writeTxAddressReg(int[] data) throws SpiDeviceException
	{
		
		return writeToRegister(appendWriteCommandToDataArray((byte)(write_mask|tx_addr),data));
	}

	public synchronized byte readDataPipe0PayloadSize() throws SpiDeviceException
	{
		return readFromRegister(new int[]{(byte)rxpyldp0_addr,0x00});
	}
	public synchronized byte readDataPipe1PayloadSize() throws SpiDeviceException
	{
		return readFromRegister(new int[]{(byte)rxpyldp1_addr,0x00});
	}
	public synchronized byte readDataPipe2PayloadSize() throws SpiDeviceException
	{
		return readFromRegister(new int[]{(byte)rxpyldp2_addr,0x00});
	}
	public synchronized byte readDataPipe3PayloadSize() throws SpiDeviceException
	{
		return readFromRegister(new int[]{(byte)rxpyldp3_addr,0x00});
	}
	public synchronized byte readDataPipe4PayloadSize() throws SpiDeviceException
	{
		return readFromRegister(new int[]{(byte)rxpyldp4_addr,0x00});
	}
	
	public synchronized byte readDataPipe5PayloadSize() throws SpiDeviceException
	{
		return readFromRegister(new int[]{(byte)rxpyldp5_addr,0x00});
	}
	public synchronized byte writeDataPipe0PayloadSize(byte data) throws SpiDeviceException
	{
		return writeToRegister(new int[]{(byte) (write_mask|rxpyldp0_addr),data});
	}
	public synchronized byte writeDataPipe1PayloadSize(byte data) throws SpiDeviceException
	{
		return writeToRegister(new int[]{(byte) (write_mask|rxpyldp1_addr),data});
	}
	public synchronized byte writeDataPipe2PayloadSize(byte data) throws SpiDeviceException
	{
		return writeToRegister(new int[]{(byte) (write_mask|rxpyldp2_addr),data});
	}
	public synchronized byte writeDataPipe3PayloadSize(byte data) throws SpiDeviceException
	{
		return writeToRegister(new int[]{(byte) (write_mask|rxpyldp3_addr),data});
	}
	public synchronized byte writeDataPipe4PayloadSize(byte data) throws SpiDeviceException
	{
		return writeToRegister(new int[]{(byte) (write_mask|rxpyldp4_addr),data});
	}
	public synchronized byte writeDataPipe5PayloadSize(byte data) throws SpiDeviceException
	{
		return writeToRegister(new int[]{(byte) (write_mask|rxpyldp5_addr),data});
	}
	public synchronized byte readFIFOStatus() throws SpiDeviceException
	{
		return readFromRegister(new int[]{(byte)fifo_status_addr,0x00});
	}
	public synchronized byte writeFIFOStatus(byte data) throws SpiDeviceException
	{
		return writeToRegister(new int[]{(byte)(write_mask|fifo_status_addr),data});
	}
	
	public byte readDynamicPayloadStatus() throws SpiDeviceException
	{
		return readFromRegister(new int[]{(byte)dyn_pay_load_addr,0x00});
	}
	public byte enableDynamicPayloadLength(byte data) throws SpiDeviceException
	{
		return writeToRegister(new int[]{(byte)(write_mask|dyn_pay_load_addr),data});
	}
	public byte readFeatureReg() throws SpiDeviceException
	{
		return readFromRegister(new int[]{(byte)feature_addr,0x00});
	}
	public byte writetoFeatureReg(byte data) throws SpiDeviceException
	{
		return writeToRegister(new int[]{(byte)(write_mask|feature_addr),data});
	}
	
	public byte[] readRxPayLoad(int loadcount) throws SpiDeviceException
	{
		int[] data = new int[loadcount+1];
		data[0]= read_rx_payld_cmd;
		return readdataFromRegister(data);
	}
	
	public byte writeTxPayLoad(int[] data) throws SpiDeviceException
	{
		return writeToRegister(appendWriteCommandToDataArray(write_tx_payld_cmd,data));
	}
	private int[] appendWriteCommandToDataArray(byte command,int[] data)
	{
		int length = data.length;
		int newdata[]=new int[length+1];
		newdata[0]=command;
		System.arraycopy(data, 0, newdata, 1, length);
		return newdata;

	}
}
