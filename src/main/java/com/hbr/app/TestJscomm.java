package com.hbr.app;

import java.io.InputStream;
import java.util.Arrays;

import com.fazecast.jSerialComm.SerialPort;

public class TestJscomm {
  
  static public void main(String[] args){
    System.out.println("in test jscommm...connect and read");

    SerialPort[] portArray = SerialPort.getCommPorts();
    System.out.println("in test jscommm number of serial port " + portArray.length);

    for (int i = 0; i < portArray.length; i++){
      System.out.println("Found  serial port " + portArray[i].toString());
      System.out.println("serial port " + portArray[i].getSystemPortName()   + " :::: " + portArray[i].getDescriptivePortName());
      
          }
    SerialPort comPort = portArray[0];
    comPort.openPort();
    if(comPort.isOpen()) {
      System.out.println("Port is open for "+ comPort.toString());
    }
    else{
      System.out.println("Port is NOTTT open for "+ comPort.toString());
    }
    comPort.setBaudRate(115200);
    comPort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 1000, 0);
    
    System.out.println("setting serial port setting 9600/8bits/stopBit1/noparity");
    comPort.setComPortParameters(9600,8,comPort.ONE_STOP_BIT,comPort.NO_PARITY);
    System.out.println("using  serial port " + comPort);
    comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 1000, 0);
    
    System.out.println("Port is ready to read data");
    String initStr = "*11\r-SHLAF\r~~~O\r-SHLAF\r";
    byte [] barray = initStr.getBytes();
    int result = comPort.writeBytes(barray, initStr.length());
    if(result == -1) {
      System.out.println("Failed to write the data " + initStr);
    }
    else {
      System.out.println("Wrote data of length " + result + " Actual data---> " + initStr);
    }
    int address = 1;
    try {
       while (true)
       {
         byte [] command = { (byte)0x55, (byte)0xAA, (byte)address, (byte) 0x00, (byte) 0x21,(byte)( (byte)0x20 + address) };
         
         System.out.println("Send command data of length " + command.length + " Actual data---> " + Arrays.toString(command));
         result = comPort.writeBytes(command, command.length);
         
         if(result == -1) {
           System.out.println("Failed to write the command " + command);
         }
         else {
           System.out.println("Wrote command data of length " + result + " Actual data---> " + Arrays.toString(command));
         }
        
          byte[] readBuffer = new byte[32];
          int numRead = comPort.readBytes(readBuffer, readBuffer.length);
          System.out.println("Read " + numRead + " bytes. ==>" + Arrays.toString(readBuffer));
          Byte h = new Byte(readBuffer[7]);
          int humid_high = h.intValue();
          h = new Byte(readBuffer[8]);
          int humid_low = h.intValue();
          System.out.println("Humid high: " + humid_high + "  Humid low: " + humid_low);

          
          int humidity = humid_high;
          humidity = (humidity * 256) | humid_low;
          humidity   = humidity / 10;
          
          Byte t = new Byte(readBuffer[9]);
          int temp_high = h.intValue();
          t = new Byte(readBuffer[10]);
          int temp_low = h.intValue();
          
          System.out.println("Temp high: " + temp_high + "  temp low: " + temp_low);

          
          int temperature = temp_high;
          temperature   = (temperature * 256) | temp_low;
          temperature   = (temperature/10)*(9/5)+32;
          System.out.println("Temperature:"+temperature+ " Humidity:" +humidity);
          
          
          Thread.sleep(5000);
          
         
       }
    } catch (Exception e) {
      System.out.println("exception " + e.getMessage());
      }
    System.out.println("exiting");
    comPort.closePort();
  }
}
