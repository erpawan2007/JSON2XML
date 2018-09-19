package io.robomq.thingsconnect.json2xml;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;

import io.robomq.thingsconnect.quboid.amqp.AmqpClient;
import io.robomq.thingsconnect.quboid.amqp.Callback;
import io.robomq.thingsconnect.quboid.amqp.Consumer;
import io.robomq.thingsconnect.quboid.amqp.Publisher;
import io.robomq.thingsconnect.quboid.config.Config;
import io.robomq.thingsconnect.quboid.util.CallBackException;
import io.robomq.thingsconnect.quboid.util.ExceptionMessage;

/**
*
* @author Jayshree Kanse
* 
* 		  The purpose of this transformer is to transform the received JSON message to XML.
*
*/

public class JSON2XML implements Callback {
	
	public static final Logger logger = LoggerFactory.getLogger(JSON2XML.class);
	private static AmqpClient msgclient = null;
	private static Config config = null;
	private static Publisher publisher;
	private static final String encoding = "UTF-8";
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.robomq.thingsconnect.quboid.amqp.Callback#messageCallBack(io.robomq.
	 * thingsconnect.quboid.amqp.AmqpClient, com.rabbitmq.client.Envelope,
	 * com.rabbitmq.client.AMQP.BasicProperties, byte[]) The messageCallback
	 * function 1) Converts any incoming message from consumer; 2) Queries the
	 * database; 3) Triggers the Publishers; 4) Publishes 
	 */
	
	@Override
	public void messageCallBack(AmqpClient msgClient, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
			throws CallBackException {
		// 1. decode message.
		String strBody;
		try {
			strBody = new String(body, encoding);	
		} catch (UnsupportedEncodingException e) {
			logger.error("Error in encoding the received message");
			ExceptionMessage em = new ExceptionMessage();
			em.setErrorCode("UnsupportedEncodingException");
			em.setErrorDesc("The encoding is unsupported");
			throw new CallBackException(em);
		}
		
    		String message ;
    	    /* Program for XML converter. Takes json data and converts it into XML formatted data */
    		try {
    			JSONObject jsonObj = new JSONObject(strBody);
	        message = XML.toString(jsonObj);
	        logger.info("Converted JSON object"+message);
    		}catch(Exception e) {
    			logger.error("Message is not valid JSON");
    			ExceptionMessage em = new ExceptionMessage();
    			em.setErrorCode("JSONParsingException");
    			em.setErrorDesc("Message is not valid JSON");
    			throw new CallBackException(em);
    		}
		// publish message
    		if(properties.getHeaders()!= null) {
        		publishMessage(message, properties.getHeaders());
    		}else {
        		publishMessage(message);
    		}
	}
	
	/**
	 * This method is used to publish the message to the next queue with required header information
	 * @param message
	 * @param headers
	 * 
	 * @throws InterruptedException
	 * @throws CallBackException
	 * @throws IOException
	 */
	private void publishMessage(String message, Map<String, Object> headers) throws CallBackException {
		try {
			publisher = msgclient.createPublisher(config.getPublisher());
			logger.info("publish message");
			publisher.publish(message.getBytes(), headers);
		} catch (IOException e) {
			logger.error("Cannot create publisher for JSON2XML." + e.getMessage());
			ExceptionMessage em = new ExceptionMessage();
			em.setErrorCode("IOException");
			em.setErrorDesc("Please Check Quboid Config for Publisher.");
			throw new CallBackException(em);
		} catch (InterruptedException i) {
			// This will interupt the current thread.
			Thread.currentThread().interrupt();
			logger.error("error happened when publish the message." + i.getMessage());
			ExceptionMessage em = new ExceptionMessage();
			em.setErrorCode("InterruptedException");
			em.setErrorDesc("Error with the publisher being intrupted.");
			throw new CallBackException(em);
		} catch (TimeoutException t) {
			logger.error("The publisher timed out with exception:" + t.getMessage());
			ExceptionMessage em = new ExceptionMessage();
			em.setErrorCode("TimeoutException");
			em.setErrorDesc("Error with the publisher timing out.");
			throw new CallBackException(em);
		}
	}

	/**
	 * This method is used to publish the message to the next queue
	 * @throws InterruptedException
	 * @throws CallBackException
	 * @throws IOException
	 */
	private static void publishMessage(String xmlData) throws CallBackException {
		try {
			publisher = msgclient.createPublisher(config.getPublisher());
			logger.info("publish message");
			publisher.publish(xmlData.getBytes());
		} catch (IOException e) {
			logger.error("Cannot create publisher for JSON2XML." + e.getMessage());
			ExceptionMessage em = new ExceptionMessage();
			em.setErrorCode("IOException");
			em.setErrorDesc("Please Check Quboid Config for Publisher.");
			throw new CallBackException(em);
		} catch (InterruptedException i) {
			// This will interupt the current thread.
			Thread.currentThread().interrupt();
			logger.error("error happened when publish the message." + i.getMessage());
			ExceptionMessage em = new ExceptionMessage();
			em.setErrorCode("InterruptedException");
			em.setErrorDesc("Error with the publisher being intrupted.");
			throw new CallBackException(em);
		} catch (TimeoutException t) {
			logger.error("The publisher timed out with exception:" + t.getMessage());
			ExceptionMessage em = new ExceptionMessage();
			em.setErrorCode("TimeoutException");
			em.setErrorDesc("Error with the publisher timing out.");
			throw new CallBackException(em);
		}
	}
	
	/**
	 * This method is used to read the config for all the needed parameters
	 * 
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// read quboid config
		readConfig();
		// Start the consumer
		consumeMessage(); 
	}

	/**
	 * This method is used to read the quboidConfig for all the needed parameters
	 * @throws IOException
	 */
	private static void readConfig() throws Exception {
		// read the main Quboid config file
        String quboidConfig = System.getProperty("quboidConfig");   
        
        if(quboidConfig == null){
        		logger.error("No quboid/xsdMapping config path found in VM configuration, please make sure enter the configuration file path in VM argument.");
        }
        
		ObjectMapper mapper = new ObjectMapper();
		try {
			config = mapper.readValue(new File(quboidConfig), Config.class);
			logger.info("loaded quboid config");

		} catch (IOException e) {
			logger.error("Cannot read quboid or soap config file, detail:" + e.getMessage());
			System.exit(1);
		}
	}
	
	/**
	 * This method is used to consume a message from the scheduler to trigger
	 * the JSON2XML 1) Read Quboid Config 2) Create AMQP Connection 3)
	 * Create Consumer 4) Connect Consumer
	 * 
	 * @throws IOException
	 * @throws KeyManagementException
	 * @throws TimeoutException
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws CallBackException
	 */
	private static void consumeMessage() throws Exception {
		// Creating the Mapper for reading the quboid config
		ObjectMapper mapper = new ObjectMapper();
		String quboidConfig = System.getProperty("quboidConfig");
		// Handling the Config reading
		try {
			config = mapper.readValue(new File(quboidConfig), Config.class);
		} catch (IOException e1) {
			logger.error("Cannot read quboid config file, detail:" + e1.getMessage(), e1);
			System.exit(1);
		}

		// Create AMQP connnection
		try {
			msgclient = new AmqpClient(config.getApp(), config.getBasic());
		} catch (KeyManagementException | TimeoutException | NoSuchAlgorithmException | IOException e) {
			logger.error("Failed to create Connection. " + e.getMessage());
		} catch (Exception e) {
			logger.error("Some Execption in creating AMQP Connection. " + e.getMessage());

		}

		// Creates a Consumer and Start consuming from the Response queue.
		JSON2XML json2xml = new JSON2XML();
		Consumer consumer;
		try {
			consumer = msgclient.createConsumer(config.getConsumer(), json2xml, msgclient);
			consumer.startConsuming();
			logger.info("Start consuming...");
		} catch (IOException e1) {
			logger.error("Create consumer error. " + e1.getMessage());

		} catch (TimeoutException e2) {
			logger.error("Consuming timeout. " + e2.getMessage());

		} catch (Exception e3) {
			logger.error("Check console for general exception in creating Consumer. " + e3.getMessage());

		}
	}	
}
