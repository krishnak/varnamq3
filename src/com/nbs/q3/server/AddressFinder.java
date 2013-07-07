package com.nbs.q3.server;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;

import com.nbs.q3.dataObjects.Constants;
import com.nbs.q3.events.RemoteAdapter;
import com.nbs.q3.events.TiltEventListener;
import com.nbs.q3.exception.SpiDeviceException;
import com.nbs.q3.gpio.OutPin;
import com.nbs.q3.remotecontrol.AxisEventProcessor;
import com.nbs.q3.remotecontrol.ButtonEventProcessor;
import com.nbs.q3.spi.CarrierDetectRegister;
import com.nbs.q3.spi.ConfigRegister;
import com.nbs.q3.spi.DynamicPayloadEnableRegister;
import com.nbs.q3.spi.EnableDataPipeRegister;
import com.nbs.q3.spi.EnhancedShockBurstRegister;
import com.nbs.q3.spi.FIFOStatusRegister;
import com.nbs.q3.spi.FeatureRegister;
import com.nbs.q3.spi.NRFCommand;
import com.nbs.q3.spi.RFChannelRegister;
import com.nbs.q3.spi.RFSetupRegister;
import com.nbs.q3.spi.RxAddressDataPipe0;
import com.nbs.q3.spi.RxDataPipes;
import com.nbs.q3.spi.RxPayLoadinDataPipes;
import com.nbs.q3.spi.SPIFactory;
import com.nbs.q3.spi.SetupAutoRetryRegister;
import com.nbs.q3.spi.StatusRegister;
import com.nbs.q3.spi.TxAddressRegister;

public class AddressFinder implements Constants
{
	private  final byte[] TxAddress = { (byte) 0xE3,(byte) 0xC3,(byte) 0xAA,(byte) 0xE5,0x53};
	//private  final byte[]  = { (byte) 0xC4,0x7B,0x4D,0x03,0x31};
	private  final byte[] rfKeepAlive = {(byte)0xEE,(byte)0xF8,0x21,(byte)0xE2,(byte)0xBF,(byte)0x88,0,0,0,0};
	//[0x61(0x00)0x00(0xEE)0x00(0xF8)0x00(0x21)0x00(0xE2)0x00(0xBF)0x00(0x88)0x00(0x00)0x00(0x00)0x00(0x00)0x00(0x00)] 
	private ConfigRegister configRegister = ConfigRegister.getInstance();
	private EnableDataPipeRegister dataPipeRegister = EnableDataPipeRegister.getInstance();
	private SetupAutoRetryRegister retryRegister = SetupAutoRetryRegister.getInstance();
	private RxPayLoadinDataPipes rxdataloadinPipe0 = RxPayLoadinDataPipes.getInstance(RxDataPipes.pipe0);
	private FeatureRegister featureRegister = FeatureRegister.getInstance();
	private DynamicPayloadEnableRegister dynamicRegister = DynamicPayloadEnableRegister.getInstance();
	private RxAddressDataPipe0 addressPipe0 = RxAddressDataPipe0.getInstance();
	private TxAddressRegister txaddressRegister = TxAddressRegister.getInstance();
	private StatusRegister statusRegister = StatusRegister.getInstance();
	private FIFOStatusRegister fifoRegister = FIFOStatusRegister.getInstance();
	
	private OutPin chipSelect = null; // instantiate it in Initialise SPI
	
	
	private boolean linkEstablished = false;
	private int exceptionCounter = 0;
	
	
	private  ArrayBlockingQueue<Byte[]> txDataQueue ;
	
	private byte[]remoteDataPackets;
	
	public static void main(String argsv[])
	{
		AddressFinder adf;
		try
		{
			adf = new AddressFinder();
			adf.addressScaner();

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SpiDeviceException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*
	 *  Allows this class to be instantiated from any GUI class and pass the Listeners, the listeners will be updated with events 
	 *  as and when it happens
	 *  Data populated to txData will be transmitted to the remote
	 */
	public AddressFinder() throws IOException, SpiDeviceException, InterruptedException 
	{
		InitialiseSPI();
		InitialiseRFTransmitter();
	}

	private void InitialiseRFTransmitter() throws SpiDeviceException , InterruptedException
	{
		
		//System.out.println("Status of Enable Data pipe register "+String.format("%02X ",dataPipeRegister.readFromRegister()));
		
		configRegister.writeToRegister((byte) 0x0C); //Set CRC encoding to 2 bytes
	//	System.out.println("Status of Config register "+String.format("%02X ",configRegister.readFromRegister()));

		configRegister.powerUp();
	//	System.out.println("Status of Config register "+String.format("%02X ",configRegister.readFromRegister()));

		dataPipeRegister.writeToRegister((byte)0x03); //Enable data pipe 0 and 1
	//	System.out.println("Status of Enable Data pipe register "+String.format("%02X ",dataPipeRegister.readFromRegister()));
		retryRegister.writeToRegister((byte)0x1F); //wait 500uS and 15 retransmits
		rxdataloadinPipe0.writeToRegister((byte)0x0A); //10 byte data in pipe 0
		activateCommand();
		featureRegister.writeToRegister((byte)0x06);// Enable dynamic payload length and enable payload with ack
		dynamicRegister.writeToRegister((byte)0x03); // Enable dynamic payload on pipe 0 and 1
		addressPipe0.writeToRegister(TxAddress);
	//	System.out.println("Status of Address PIPE0 register "+String.format("%02X ",addressPipe0.readFromRegister()));

		txaddressRegister.writeToRegister(TxAddress);
 	}
	private void activateCommand() throws SpiDeviceException
	{
		
		//  this SPI Factory is abstract its concrete class will use JNI calls when using the RaspberryPi
		// it will use the IOIO calls SPIWriteRead when the code runs on Android
		SPIFactory.INSTANCE.writeRead(new byte[]{(byte) NRFCommand.Activate.getCommand(),(byte)0x73});
	}
	private void InitialiseSPI() throws IOException, SpiDeviceException
	{
		SPIFactory.INSTANCE.connectSPI();
		SPIFactory.INSTANCE.setConfiguration();
		// Outpin again has multiple implementation based on whether running on Android or Pi
		chipSelect = new OutPin(0); //header 11 on Pi (Marked GPIO17) used for Chip Enable (Note: This is not CSN for SPI)
	}
	//Actual Listener
	
	public void addressScaner()
	{
		// write default payload to device
		System.out.println("Synchronisation in progress... waiting for remote to broadcast...");

		try
		{
		
		//	System.out.println("Status of Config register "+String.format("%02X ",configRegister.readFromRegister()));
			//tm is a threadMonitor  from the calling application to shutdown the threads when UI exits.
			while(!linkEstablished)
			{
					
				int status = statusRegister.readFromRegister();
				
			//	System.out.println("Status reg value: "+String.format("%02X ",status));
				//check whether RF link is up or down i.e whether the remote responds
				if(hasMaxRetransmitReached(status))
				{
					//reset this bit
					statusRegister.clearMaxRTFlag();
				}
				
						chipSelect.setValue(false);
						SPIFactory.INSTANCE.writeRead(new byte[]{(byte) NRFCommand.FlushTx.getCommand()});
						writePayLoad(rfKeepAlive);
						chipSelect.setValue(true);
						try
						{
							Thread.sleep(0,15000);
						} catch (InterruptedException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						chipSelect.setValue(false);

					
			
				if(hasAcknowledgementArrived(status))
				{
					

				
					//data has arrived
					//we know the data is 10 bytes long. 
					
					// Data is read from NRF register
					// 10 bytes long -this array is of size 10
					 remoteDataPackets = readDataFromFIFO(NUM_DATA_PACKETS);
					
					
				
					linkEstablished = true;
					for(int k=1;k<NUM_DATA_PACKETS;k++)
					{
						
							if(remoteDataPackets[k]==0)
								linkEstablished &= false;
							
					}
				}
				
				
				try
				{
					Thread.sleep(10);
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			System.out.print("Received remote control address : ");
			FileOutputStream fw = new FileOutputStream("q3remoteadd.cfg");
			for(int k=1;k<=5;k++)
			{
				System.out.print(String.format("%2X", remoteDataPackets[k])+",");
				
			}
			System.out.println();
			fw.write(remoteDataPackets, 1, 5);
			fw.flush();
			fw.close();
			byte[] readfromfile = new byte[5];
			FileInputStream fr = new FileInputStream("q3remoteadd.cfg");
			fr.read(readfromfile);
			fr.close();
			System.out.println("address from file : "+Arrays.toString(readfromfile));
			SPIFactory.INSTANCE.writeRead(new byte[]{(byte) NRFCommand.FlushRx.getCommand()});

		} 
		catch (SpiDeviceException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			System.out.println("Shutting down....");
			try
			{
				shutDown();
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SPIFactory.INSTANCE.disconnectSPI();
			
		}
	}
	private void shutDown() throws InterruptedException
	{
		chipSelect.setValue(false);
		chipSelect.close();
		try
		{
			// move this stuff into a function
			configRegister.powerDown();
			SPIFactory.INSTANCE.writeRead(new byte[]{(byte) NRFCommand.FlushTx.getCommand()});
			statusRegister.clearMaxRTFlag();
			activateCommand();
		//	System.out.println("Status of Config register "+String.format("%02X ",configRegister.readFromRegister()));
			
		//	System.out.println("Status of Enable Data pipe register "+String.format("%02X ",dataPipeRegister.readFromRegister()));
//			System.out.println("Status of Retry register "+String.format("%02X ",retryRegister.readFromRegister()));
//			System.out.println("Status of Rx dataload width pipe0 register "+String.format("%02X ",rxdataloadinPipe0.readFromRegister()));
//			System.out.println("Status of Feature register "+String.format("%02X ",featureRegister.readFromRegister()));
			
			
			
			
	//		System.out.println("Status of Address PIPE0 register "+String.format("%02X ",addressPipe0.readFromRegister()));

	
			
		} catch (SpiDeviceException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private byte[] readDataFromFIFO(int length) throws SpiDeviceException
	{
		byte[] data = new byte[length+1];
		data[0] = (byte) NRFCommand.ReadRxPayload.getCommand();
		return SPIFactory.INSTANCE.writeRead(data);
		
	}
	private boolean hasAcknowledgementArrived(int status)
	{
		
		if(status==(byte)0x2E||status==(byte)0x60||status==(byte)0x70||status==(byte)0x00||status==(byte)0x7E||status==(byte)0x7F||status==(byte)0x30)
			return true;
		
		return	false;
	}

	
	private void writePayLoad(byte data[]) throws SpiDeviceException
	{
		//use any of the instantiated registers to access this method to append command to payload data
		SPIFactory.INSTANCE.writeRead(configRegister.appendWriteCommandToDataArray((byte) NRFCommand.WriteTxPayload.getCommand(), data));
		// send a reuse command if it is just keep alive data
		/*
		if(Arrays.equals(rfKeepAlive,data))
		{
			SPIFactory.INSTANCE.writeRead(new byte[]{(byte) NRFCommand.ReusePayLoad.getCommand()});
		}*/	
		
	}
	
	private boolean hasMaxRetransmitReached(int status)
	{
		
		if(status==(byte)0x1E ||status == (byte)0x10||status == (byte)0x1F)
			return true;
		
		
		return	false;
	}
	

}
