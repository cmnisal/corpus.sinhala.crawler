package corpus.sinhala.crawler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.util.StAXUtils;

import javanet.staxutils.IndentingXMLStreamWriter;

public class XMLFileWriter {
	private int documentCounter;
	private String filePrefix;
	private int fileCounter;
	private String baseFolder;
	private String filePath;
	private String path;

	private OMFactory factory;
	private OMElement root;
	private QName rootName;
	private QName linkName;
	private QName topicName;
	private QName authorName;
	private QName contentName;
	private QName postName;
	private QName dateName;
	private QName yearName;
	private QName monthName;
	private QName dayName;
	private QName categoryName;

	ArrayList<OMElement> docs;
	
	Constructor<?> cons;
	
	public XMLFileWriter(String parserClass) throws IOException {
		init();
		createFolder();
		factory = OMAbstractFactory.getOMFactory();
		root = factory.createOMElement(rootName);


	}

		

	public XMLFileWriter() throws IOException {
		init();
		//createFolder();
		factory = OMAbstractFactory.getOMFactory();
		root = factory.createOMElement(rootName);
	}

	private void init() {
		documentCounter = 0;
		filePrefix = "L";
		fileCounter = 0;
		rootName = new QName("root");
		linkName = new QName("link");
		topicName = new QName("topic");
		authorName = new QName("author");
		contentName = new QName("content");
		postName = new QName("post");
		dateName = new QName("date");
		yearName = new QName("year");
		monthName = new QName("month");
		dayName = new QName("day");
		categoryName = new QName("category");

		docs = new ArrayList<>();
		
	}

	

	public void addDocument(String page, String url) throws IOException,
			XMLStreamException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		System.out.println(url);
		WikipediaParser parser = new WikipediaParser(page, url);
		OMElement doc = factory.createOMElement(postName);
		
		OMElement category = factory.createOMElement(categoryName);
		category.setText("ACADEMIC");
		doc.addChild(category);

		OMElement date = factory.createOMElement(dateName);
		OMElement year = factory.createOMElement(yearName);
		year.setText(parser.getYear());
		date.addChild(year);

		OMElement month = factory.createOMElement(monthName);
		month.setText("");
		date.addChild(month);

		OMElement day = factory.createOMElement(dayName);
		day.setText("");
		date.addChild(day);

		doc.addChild(date);

		OMElement link = factory.createOMElement(linkName);
		link.setText(parser.getUrl());
		doc.addChild(link);

		OMElement topic = factory.createOMElement(topicName);
		topic.setText(parser.getTitle());
		doc.addChild(topic);

		OMElement author = factory.createOMElement(authorName);
		try {
			author.setText("");
		} catch (Exception e) {
			author.setText("");
		}

		doc.addChild(author);

		OMElement content = factory.createOMElement(contentName);
		content.setText(parser.getContent());
		doc.addChild(content);
		
		docs.add(doc);
	}

	
	public void writeToFile(String fileName) throws IOException, XMLStreamException {
		path = fileName + ".xml";
		OutputStream out = new FileOutputStream(path);
		XMLStreamWriter writer = StAXUtils.createXMLStreamWriter(out);
		writer = new IndentingXMLStreamWriter(writer);
		root.serialize(writer);
		writer.flush();
		fileCounter++;
	}
	
	public void writeToFileTemp() throws IOException, XMLStreamException {
		path = filePath + "/" + filePrefix + String.format("%04d", fileCounter)
				+ ".xml";
		OutputStream out = new FileOutputStream(path);
		XMLStreamWriter writer = StAXUtils.createXMLStreamWriter(out);
		writer = new IndentingXMLStreamWriter(writer);
		root.serialize(writer);
		writer.flush();
	}

	private void createFolder() throws IOException {
		File dirBase = new File(baseFolder);
		dirBase.mkdir();
		File dirXml = new File(filePath);
		dirXml.mkdir();

	}

	//@Override
	public void update(String message) {
		//String message = (String)arg;
		
		for (int i = 0; i < docs.size(); i++) {

			root.addChild(docs.get(i));

			documentCounter++;
			System.out
					.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
							+ documentCounter);
//			if (documentCounter % maxDocumentCounter == 0) {
//				try {
//					writeToFile();
//				} catch (IOException | XMLStreamException e) {
//				}
//				documentCounter = 0;
//				root = factory.createOMElement(rootName);
//			}
		}
		
//		if(documentCounter>0){
//			try {
//				writeToFileTemp();
//			} catch (IOException | XMLStreamException e) {
//			}
//		}
		try {
			writeToFile(message);
		} catch (IOException | XMLStreamException e) {
		}
		root = factory.createOMElement(rootName);
		
		docs.clear();

	}

	 public static void main(String[] args) throws IOException {
//	 XMLFileWriter a = new XMLFileWriter("/home/maduranga/bb/");
	 }
}
