package corpus.sinhala.crawler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.util.StAXUtils;

//import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter;

import corpus.sinhala.crawler.parser.AlokaUParser;
import corpus.sinhala.crawler.parser.BudusaranaParser;
import corpus.sinhala.crawler.parser.DinaminaParser;
import corpus.sinhala.crawler.parser.DivainaParser;
import corpus.sinhala.crawler.parser.LankadeepaParser;
import corpus.sinhala.crawler.parser.NamaskaraParser;
import corpus.sinhala.crawler.parser.Parser;
import corpus.sinhala.crawler.parser.SarasawiyaParser;
import corpus.sinhala.crawler.parser.SiluminaParser;
import corpus.sinhala.crawler.url.generator.AlokoudapadiGenerator;
import javanet.staxutils.IndentingXMLStreamWriter;

public class XMLFileWriter implements Observer {
	private int documentCounter;
	private String filePrefix;
	private int fileCounter;
	private String baseFolder;
	private String filePath;
	// private BufferedWriter bw;
	// private FileWriter fw;
	private int maxDocumentCounter;
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

	ArrayList<OMElement> docs;
	
	public XMLFileWriter() throws IOException {
		init();
		createFolder();
		factory = OMAbstractFactory.getOMFactory();
		root = factory.createOMElement(rootName);


	}

	public XMLFileWriter(String location) throws IOException {
		String temp[] = location.split("/");
		String createDir = "";
		if(!location.startsWith("/")){
			createDir += temp[0];
			File dir = new File(createDir);
			dir.mkdir();
		}
		
		for(int i=1; i<temp.length; i++){
			createDir += "/"+temp[i];
			File dir = new File(createDir);
			dir.mkdir();
		}
		filePath = createDir;
		
		
		init();
		factory = OMAbstractFactory.getOMFactory();
		root = factory.createOMElement(rootName);
		
	}

	private void init() {
		documentCounter = 0;
		filePrefix = "L";
		fileCounter = 0;
		maxDocumentCounter = 100;
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

		docs = new ArrayList<>();
		
		fileCounter = new File(filePath).list().length;
		System.out.println(fileCounter);
		System.out.println(filePath);
		
	}

	public int getDocumentCounter() {
		 return documentCounter;
	}

	public void addDocument(String page, String url) throws IOException,
			XMLStreamException {
		Parser parser = new DinaminaParser(page, url);

		OMElement doc = factory.createOMElement(postName);

		OMElement date = factory.createOMElement(dateName);
		OMElement year = factory.createOMElement(yearName);
		year.setText(parser.getYear());
		date.addChild(year);

		OMElement month = factory.createOMElement(monthName);
		month.setText(parser.getMonth());
		date.addChild(month);

		OMElement day = factory.createOMElement(dayName);
		day.setText(parser.getDate());
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
			author.setText(parser.getAuthor());
		} catch (Exception e) {
			author.setText("");
		}

		doc.addChild(author);

		OMElement content = factory.createOMElement(contentName);
		content.setText(parser.getContent());
		doc.addChild(content);
		// root.addChild(doc);

		docs.add(doc);

	}

	public void writeToFile() throws IOException, XMLStreamException {
		
		path = filePath + "/" + filePrefix + String.format("%04d", fileCounter)
				+ ".xml";
		OutputStream out = new FileOutputStream(path);
		XMLStreamWriter writer = StAXUtils.createXMLStreamWriter(out);
		writer = new IndentingXMLStreamWriter(writer);
		root.serialize(writer);
		writer.flush();
		fileCounter++;
	}
	
	public void writeToFile(String fileName) throws IOException, XMLStreamException {
		path = filePath + "/" + fileName + ".xml";
		OutputStream out = new FileOutputStream(path);
		XMLStreamWriter writer = StAXUtils.createXMLStreamWriter(out);
//		writer = new IndentingXMLStreamWriter(writer);
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

	@Override
	public void update(Observable o, Object arg) {
		String message = (String)arg;
		
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
