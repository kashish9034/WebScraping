package com.kashish;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Scrape {
	private static HtmlPage page;
	private static final String username = "ingotupdates@gmail.com";
	private static final String password = "Steel@123#";
	private static final String xpath = "/html/body/div[1]/div[2]/div/div/div[1]/div[1]/article[1]/div/blockquote";
	
	public static void main(String[] args) {

		WebClient client = new WebClient();
		client.getOptions().setCssEnabled(false);
		client.getOptions().setJavaScriptEnabled(false);
		try {
			String searchUrl = "https://www.ingotrates.com/";
			page = client.getPage(searchUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
		client.close();
		HtmlElement element = (HtmlElement) page
				.getFirstByXPath(xpath);
		checkChange(element.asText());
	}

	private static void checkChange(String text) {
		
		try {
			byte[] input = new byte[1000];
			FileInputStream fis = new FileInputStream("persist.txt");
			fis.read(input);
			fis.close();
			String str = new String(input,"UTF-8");
			if (text.equalsIgnoreCase(str.trim())==false) {
				System.out.println("Rates changed");
				FileOutputStream fos = new FileOutputStream("persist.txt");
				fos.write(text.getBytes());
				fos.close();
				sendMail(text);
			}else
			{System.out.println("There are no changes observed");}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void sendMail(String text) {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("ingotupdates@gmail.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("kashish9034@gmail.com,sgindelhi@gmail.com"));
			message.setSubject("Ingot Price Change Alert!");
			message.setText(text);

			Transport.send(message);

			System.out.println("Mail sent at " + LocalDateTime.now());

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}

	}
}
