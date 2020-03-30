package EPFSimulator;

import org.jpos.iso.*;
import org.jpos.iso.channel.ASCIIChannel;
import org.jpos.iso.packager.GenericPackager;
import org.jpos.iso.packager.ISO87APackager;
import org.jpos.util.LogSource;
import org.jpos.util.Logger;
import org.jpos.util.SimpleLogListener;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;  
import java.time.Instant;
import java.time.ZoneId;


public class EPFTestServer2 implements ISORequestListener {

	public static void main(String[] args) throws ISOException, NullPointerException {
		// TODO Auto-generated method stub
		Logger logger = new Logger();
        logger.addListener(new SimpleLogListener(System.out));
        GenericPackager packager = new GenericPackager("src/main/resources/epfisobase.xml");

        Properties prop = new Properties();
        InputStream inputStream = null;
        
        try {
        inputStream = new FileInputStream("src/main/resources/application.properties");
        // load a properties file
        prop.load(inputStream);
        // get the property value 
        String hostname = prop.getProperty("jpos.channel.host");
        int channelport = Integer.parseInt(prop.getProperty("jpos.channel.port"));
        int serverport = Integer.parseInt(prop.getProperty("jpos.server.port"));
      
        ServerChannel sChannel = new ASCIIChannel(hostname,channelport,packager);
        ((LogSource)sChannel).setLogger(logger,"server-channel-logger");
        ISOServer isoServer = new ISOServer(serverport,sChannel,null);
        isoServer.setLogger(logger,"server-logger");
        //Assign ISOListener Process
        isoServer.addISORequestListener(new EPFTestServer2());
       
        new Thread(isoServer).start();
        
        } catch (IOException e) {
            e.printStackTrace();
		} finally {
	        if (inputStream != null) {
	            try {
	                inputStream.close();
	            } catch(IOException e) {
	                e.printStackTrace();
	            }
	        }
		}

    }

    @Override
    public boolean process(ISOSource isoSource, ISOMsg isoMsg) {

    	try {
			System.out.println("Received: \n" +ISOUtil.hexdump(isoMsg.pack()));
		} catch (ISOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
        ISOMsg m = (ISOMsg) isoMsg.clone();
        try {
        	//Get timeStamp in zone "GMT+08:00"
        	ZonedDateTime currentDateTime = Instant.now().atZone(ZoneId.of("GMT+08:00"));
        	//Build formatter
        	DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;        	 
        	//Format LocalDateTime
        	String timeStamp = currentDateTime.format(formatter); 
        			
            m.setMTI("0210");
            //m.set(2,"12345678");
            m.set(11,isoMsg.getString(11));
            m.set(7,isoMsg.getString(7));
            m.set(12,timeStamp);
            ISOMsg field71 = new ISOMsg(71);
            ISOMsg field126 = new ISOMsg(126);
            
            String idNo = isoMsg.getString("71.3").trim();
            
            switch(idNo) {
            case "750601-01-1234":
              // Sara
	            field71.set(1,"SARAH LIAN");
	            field71.set(2,"23456789");
	            field71.set(3,idNo);
	            field71.set(4,"NA");
	            field71.set(5,"19950512");
	            field71.set(6,"20110730");
	            field71.set(7,"Active");
	            m.set(field71);
	            
	            field126.set(1,"50000.00");
	            field126.set(2,"30000.50");
	            field126.set(3,"20000.50");
	            m.set(field126);
	            
	            break;
            case "650830-01-1357":
              // Adam
	            field71.set(1,"ADAM SMITH");
	            field71.set(2,"13579135");
	            field71.set(3,idNo);
	            field71.set(4,"NA");
	            field71.set(5,"19960723");
	            field71.set(6,"20130225");
	            field71.set(7,"Active");
	            m.set(field71);
	            
	            field126.set(1,"99000.00");
	            field126.set(2,"50000.50");
	            field126.set(3,"49000.50");
	            m.set(field126);

	            break;
            default:
              // Others
	            field71.set(1,"LEE AH BENG");
	            field71.set(2,"12345678");
	            field71.set(3,idNo);
	            field71.set(4,"NA");
	            field71.set(5,"19900307");
	            field71.set(6,"20100510");
	            field71.set(7,"Active");
	            m.set(field71);
	            
	            field126.set(1,"100000.50");
	            field126.set(2,"60000.25");
	            field126.set(3,"40000.25");
	            m.set(field126);
            }
            
            isoSource.send(m);
            System.out.println("Replied:\n" +ISOUtil.hexdump(m.pack()));
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (ISOException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }
	

}
