package com.hbr.app;

import gnu.io.CommPort;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Properties;



public class TwoWaySerialComm
{ 
  
   private static Thread inputTh;
   private static Thread outputTh;
    public TwoWaySerialComm()
    {
        super();
    }
    
    

    void connect ( String portName ) throws Exception
    {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if ( portIdentifier.isCurrentlyOwned() )
        {
            System.out.println("Error: Port is currently in use");
        }
        else
        {
           System.out.println("Port is currently free.....");
            CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);

            if ( commPort instanceof SerialPort)
            {
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(115200,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                System.out.println("Getting the input and output streams...");
                InputStream in = serialPort.getInputStream();
                OutputStream out = serialPort.getOutputStream();
                System.out.println("Starting the readers and writers...");
                inputTh = new Thread(new SerialReader(in));
                outputTh = new Thread(new SerialWriter(out));
                //(new Thread(new SerialReader(in))).start();
                //(new Thread(new SerialWriter(out))).start();
                inputTh.start();
                outputTh.start();
                System.out.println("Done Starting the readers and writers...");
            }
            else
            {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }
    }

    
    public static class SerialReader implements Runnable
    {
        InputStream in;

        public SerialReader ( InputStream in )
        {
            this.in = in;
            System.out.println("Serial reader initialized.");
        }

        public void run ()
        {
            byte[] buffer = new byte[1024];
            int len = -1;
            try
            {
               System.out.println("Serial reader starting to read data....");
              
                while ( ( len = this.in.read(buffer)) > -1 )
                {
                    System.out.print(new String(buffer,0,len));
                }
                System.out.println("Serial reader Done reading the data");
            }
            catch ( IOException e )
            {
              System.out.print("caught an exception " + e.getMessage());
                //e.printStackTrace();
            }
        }
    }

    public static class SerialWriter implements Runnable
    {
        OutputStream out;

        public SerialWriter ( OutputStream out )
        {
            this.out = out;
        }

        public void run ()
        {
            try
            {
                int c = 0;
                while ( ( c = System.in.read()) > -1 )
                {
                    this.out.write(c);
                }
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }
    }

    public static void mainNotUsed ( String[] args )
    {
        System.out.println("Starting sensor application");
        try
        {
          Properties systemProperties = System.getProperties();
          Enumeration enuProp = systemProperties.propertyNames();
          System.out.println("Current System properties: ");
          while (enuProp.hasMoreElements()) {
              String propertyName = (String) enuProp.nextElement();
              String propertyValue = systemProperties.getProperty(propertyName);
              System.out.println(propertyName + ": " + propertyValue);
          }
          
          System.out.println("Test loading the RXTXSerial library with libname: rxtxSerial");
          
          System.loadLibrary("rxtxSerial");
          System.out.println("Done RXTXSerial library with libname: rxtxSerial");
          System.out.println("Connecting to /dev/ttyS1");
          (new TwoWaySerialComm()).connect("/dev/ttyS1");
          /*
          System.out.println("Initializing jssc serial util");
          //SerialTestJssc testSerialPort = new SerialTestJssc();
          //testSerialPort.InitAndRead();
          System.out.println("Initializing test serial");
          TestSerial tserial = new TestSerial();
          System.out.println("Search for ports");
          tserial.searchForPorts();
          System.out.println("Connecting to: " + System.getenv("HOST_DEV1"));
          tserial.connect(System.getenv("HOST_DEV1"));
          if (tserial.getConnected() == true)
          {
              if (tserial.initIOStream() == true)
              {
                  tserial.initListener();
              }
          }
          System.out.println("looping indefinately");
          while(true) {
            
          }
          */
        }
        catch ( Exception e )
        {
          System.out.println("failed due to  " + e.getMessage());
            e.printStackTrace();
        }
   
        System.out.println("Exiting the application");
    }
}
