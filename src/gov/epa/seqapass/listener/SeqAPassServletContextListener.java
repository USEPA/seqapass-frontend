package gov.epa.seqapass.listener;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class SeqAPassServletContextListener implements ServletContextListener {
	
	private static Properties properties;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("ServletContextListener stopped");

	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		ServletContext ctx = servletContextEvent.getServletContext();
		String cfgfile = ctx.getInitParameter("config_file");
		
		
		properties = new Properties();
		try {
			properties.load(getClass().getResourceAsStream(cfgfile));
		} catch (IOException e1) {
			System.out.println("Properties file failed to load");
			e1.printStackTrace();
		}
		
		
	}
	
	public static Properties getProperties(){
		return properties;
	}
	
	public static String getHost(){
		return properties.getProperty("host");
	}
	
	public static String getPort(){
		return properties.getProperty("port");
	}	
	
	public static String getProtocol(){
		return properties.getProperty("protocol");
	}
	
	public static String getFrontHost(){
		return properties.getProperty("fronthost");
	}
	
	public static String getFEUser(){
		return properties.getProperty("FEuser");
	}
	
	public static String getFEPass(){
		return properties.getProperty("FEpass");
	}
	

}
