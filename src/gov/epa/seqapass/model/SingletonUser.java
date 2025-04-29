package gov.epa.seqapass.model;

import javax.inject.Singleton;

import org.jasypt.util.text.BasicTextEncryptor;

import gov.epa.seqapass.listener.SeqAPassServletContextListener;

@Singleton
//@Startup
public class SingletonUser {
	
	BasicTextEncryptor textEncryptor;
	String encryptPass;
	
	
	public SingletonUser(){
		textEncryptor = new BasicTextEncryptor();
		encryptPass = "PASSHERE";
		textEncryptor.setPasswordCharArray(encryptPass.toCharArray());
	}
	
	public String getUsername(){
		return textEncryptor.decrypt(SeqAPassServletContextListener.getFEUser());
	}
	
	public String getPassword(){
		return textEncryptor.decrypt(SeqAPassServletContextListener.getFEPass());
	}
	
	public BasicTextEncryptor getTextEncryptor(){
		return textEncryptor;
	}
	
	public String getEncryptPass(){
		return encryptPass;
	}
	
}
