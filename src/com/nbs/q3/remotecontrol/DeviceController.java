package com.nbs.q3.remotecontrol;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;

import com.nbs.q3.dataObjects.Constants;
import com.nbs.q3.events.RemoteAdapter;
import com.nbs.q3.events.TiltEventListener;

import com.nbs.q3.exception.SpiDeviceException;
import com.nbs.q3.gpio.OutPin;
import com.nbs.q3.server.ThreadMonitor;
import com.nbs.q3.spi.*;


public class DeviceController implements Runnable, Constants
{
	//private  final byte[] pipe0Address = { (byte) 0xC4,0x7B,0x4D,0x03,0x31};
	private  final byte[] TxAddress = new byte[5];//{ (byte) 0xC4,0x7B,0x4D,0x03,0x31};
	private  final byte[] rfKeepAlive = {0,0,0,0,1,0,0,0,0,0};
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
	
	private ThreadMonitor tm;

	private boolean linkEstablished = false;
	private int exceptionCounter = 0;
	
	//private RemoteSensorData remoteData = null;
	private final ArrayBlockingQueue<Byte> buttonQueue = new ArrayBlockingQueue<Byte>(BUFFER_SIZE);
	private final ArrayBlockingQueue<Byte[]> accQueue = new ArrayBlockingQueue<Byte[]>(BUFFER_SIZE);
	private  ArrayBlockingQueue<Byte[]> txDataQueue ;
	private Thread buttonProcessorThread;
	private Thread tiltProcessorThread;
	
	private boolean startThreads = true;
	private byte[]remoteDataPackets;
	/*
	 *  Allows this class to be instantiated from any GUI class and pass the Listeners, the listeners will be updated with events 
	 *  as and when it happens
	 *  Data populated to txData will be transmitted to the remote
	 */
	public DeviceController(ThreadMonitor tm,RemoteAdapter rm,TiltEventListener tlt,ArrayBlockingQueue<Byte[]>txData) throws IOException, SpiDeviceException, InterruptedException 
	{
		InitialiseSPI();
		InitialiseRFTransmitter();
		this.txDataQueue = txData;
		this.tm = tm;
		
		
		this.buttonProcessorThread = new Thread( new ButtonEventProcessor(buttonQueue,rm,tm));
		this.tiltProcessorThread = new Thread(new AxisEventProcessor(accQueue,tlt,tm,true,DEFAULT_WINDOW_SIZE));
	}
	public DeviceController(ThreadMonitor tm, ArrayList eventQueue, ArrayBlockingQueue<Byte[]>txData) throws IOException, SpiDeviceException, InterruptedException
	{
		InitialiseSPI();
		InitialiseRFTransmitter();
		this.txDataQueue = txData;
		this.tm = tm;
		this.buttonProcessorThread = new Thread( new ButtonEventProcessor(buttonQueue,tm,eventQueue));

	}
	private void InitialiseRFTransmitter() throws SpiDeviceException , InterruptedException
	{
		
		System.out.println("Status of Enable Data pipe register "+String.format("%02X ",dataPipeRegister.readFromRegister()));
		
		configRegister.writeToRegister((byte) 0x0C); //Set CRC encoding to 2 bytes
		System.out.println("Status of Config register "+String.format("%02X ",configRegister.readFromRegister()));

		configRegister.powerUp();
		System.out.println("Status of Config register "+String.format("%02X ",configRegister.readFromRegister()));

		dataPipeRegister.writeToRegister((byte)0x03); //Enable data pipe 0 and 1
		System.out.println("Status of Enable Data pipe register "+String.format("%02X ",dataPipeRegister.readFromRegister()));
		retryRegister.writeToRegister((byte)0x1F); //wait 500uS and 15 retransmits
		rxdataloadinPipe0.writeToRegister((byte)0x0A); //10 byte data in pipe 0
		activateCommand();
		featureRegister.writeToRegister((byte)0x06);// Enable dynamic payload length and enable payload with ack
		dynamicRegister.writeToRegister((byte)0x03); // Enable dynamic payload on pipe 0 and 1
		addressPipe0.writeToRegister(TxAddress);
		System.out.println("Status of Address PIPE0 register "+String.format("%02X ",addressPipe0.readFromRegister()));

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
		FileInputStream fr = new FileInputStream("q3remoteadd.cfg");
		fr.read(TxAddress);
		fr.close();
		SPIFactory.INSTANCE.connectSPI();
		SPIFactory.INSTANCE.setConfiguration();
		// Outpin again has multiple implementation based on whether running on Android or Pi
		chipSelect = new OutPin(0); //header 11 on Pi (Marked GPIO17) used for Chip Enable (Note: This is not CSN for SPI)
	}
	//Actual Listener
	@Override
	public void run()
	{
		// start two threads one for button event processing and another for axis data processing
		// pass the RemoteSensorData object to it
		// write default payload to device
		try
		{
			//move these two lines in to a function
			writePayLoad(rfKeepAlive);
			chipSelect.setValue(true);
			System.out.println("Status of Config register "+String.format("%02X ",configRegister.readFromRegister()));
			//tm is a threadMonitor  from the calling application to shutdown the threads when UI exits.
			while(!tm.getStopStatus())
			{
					
				int status = statusRegister.readFromRegister();
				
			//	System.out.println("Status reg value: "+String.format("%02X ",status));
				//check whether RF link is up or down i.e whether the remote responds
				if(hasMaxRetransmitReached(status))
				{
				//	System.out.println("MaxRT Flag raised");
					//reset this bit
					statusRegister.clearMaxRTFlag();
					if(linkEstablished && (exceptionCounter>4))
					{

						System.out.println("Link dropped -2");
						//previously link established
						chipSelect.setValue(false);
						SPIFactory.INSTANCE.writeRead(new byte[]{(byte) NRFCommand.FlushTx.getCommand()});
						writePayLoad(rfKeepAlive);
						chipSelect.setValue(true);
						exceptionCounter=0;

					}
					if(linkEstablished)
					{
						exceptionCounter++;
					}
					
				}
				else if(hasAcknowledgementArrived(status))
				{
					if(startThreads)
					{
						// start the threads to handle data received
						
						buttonProcessorThread.start();
						if(tiltProcessorThread!=null)
							tiltProcessorThread.start();
						startThreads =false;
					}

					//reset NOACK counter here
					linkEstablished =true;
					exceptionCounter = 0;
					statusRegister.clearMaxRTFlag();
					//data has arrived
					//we know the data is 10 bytes long. 
					
					// Data is read from NRF register
					// 10 bytes long -this array is of size 10
					 remoteDataPackets = readDataFromFIFO(NUM_DATA_PACKETS);
					
					
					statusRegister.clearMaxRTFlag();
					
					if(buttonQueue.size()==BUFFER_SIZE)
					{
						buttonQueue.remove();
					}
					// last byte in response message is the button data
					// add it to a Queue - Button processor thread will create an event if a button 
					//has been pressed or released
					buttonQueue.add(new Byte(remoteDataPackets[NUM_DATA_PACKETS]));
					if(tiltProcessorThread!=null)
					{
						if(accQueue.size()==BUFFER_SIZE)
						{
							accQueue.remove();
						}

						// Accelerator data arrives in 1,2,3 positions
						// they are added to a different queue so another thread creates an event
						if(remoteDataPackets[1]!=0)		
				//		accQueue.add(new Byte[]{(byte) (remoteDataPackets[1]+(byte)(0x80)),(byte)( remoteDataPackets[2]-(byte)0x78) ,   (byte)(remoteDataPackets[3]+0x72)});
							accQueue.add(new Byte[]{(byte) remoteDataPackets[1],(byte) remoteDataPackets[2] ,   (byte)remoteDataPackets[3],(byte)remoteDataPackets[4],(byte)remoteDataPackets[5],(byte)remoteDataPackets[6]});
					}
					
					int reuse = fifoRegister.readFromRegister();
				//	System.out.println("FIFO reg value: "+String.format("%02X ",(byte)reuse));
					//check whether there is a new transmit data is available to send or
					// or should we send default keep alive packet
					if(!(((byte)reuse&0x40)==0x40))
					{
				//		System.out.println("Calling write payload");
						if(txDataQueue.isEmpty())
							writePayLoad(rfKeepAlive);
						else
						{
							Byte[] dataFromuser = txDataQueue.poll();
							
							byte []sendData = new byte[dataFromuser.length];
							for(int i=0;i<sendData.length;i++)
							{
								sendData[i]= dataFromuser[i].byteValue();
							}
							writePayLoad(sendData);
							
						}
					}
				}
				else
				{
					//keep a counter here if it reaches 4 - then power down chip - reinitialise 
				//	System.out.println("Neither Ack received nor Max rt occured Status reg value: "+String.format("%02X ",status));

					if(linkEstablished && (exceptionCounter>4))
					{
						buttonQueue.clear();
						accQueue.clear();
						System.out.println("Link dropped");
						//previously link established
						chipSelect.setValue(false);
						SPIFactory.INSTANCE.writeRead(new byte[]{(byte) NRFCommand.FlushTx.getCommand()});
						writePayLoad(rfKeepAlive);
						chipSelect.setValue(true);
						exceptionCounter = 0;
					}
					if(linkEstablished)
					{
						exceptionCounter++;
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
			
			
		} 
		catch (SpiDeviceException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			System.out.println("Shutdown command received....");
			try
			{
				shutDown();
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SPIFactory.INSTANCE.disconnectSPI();
			System.out.println("Device Controller thread exiting....");
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
			System.out.println("Status of Config register "+String.format("%02X ",configRegister.readFromRegister()));
			
			System.out.println("Status of Enable Data pipe register "+String.format("%02X ",dataPipeRegister.readFromRegister()));
			System.out.println("Status of Retry register "+String.format("%02X ",retryRegister.readFromRegister()));
			System.out.println("Status of Rx dataload width pipe0 register "+String.format("%02X ",rxdataloadinPipe0.readFromRegister()));
			System.out.println("Status of Feature register "+String.format("%02X ",featureRegister.readFromRegister()));
			
			
			
			
			System.out.println("Status of Address PIPE0 register "+String.format("%02X ",addressPipe0.readFromRegister()));

	
			
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
		if(Arrays.equals(rfKeepAlive,data))
		{
			SPIFactory.INSTANCE.writeRead(new byte[]{(byte) NRFCommand.ReusePayLoad.getCommand()});
		}
		
	}
	
	private boolean hasMaxRetransmitReached(int status)
	{
		
		if(status==(byte)0x1E ||status == (byte)0x10||status == (byte)0x1F)
			return true;
		
		
		return	false;
	}
	
}
