package corpus.sinhala.crowler;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.stream.XMLStreamException;

import corpus.sinhala.crowler.parser.LankadeepaParser;

public class LocalDataCollectorCrawler extends WebCrawler {

        Pattern filters = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g" + "|png|tiff?|mid|mp2|mp3|mp4"
                        + "|wav|avi|mov|mpeg|ram|m4v|pdf" + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

        CrawlStat myCrawlStat;
        XMLFileWriter xfw;

        public LocalDataCollectorCrawler() throws IOException {
                myCrawlStat = new CrawlStat();
                xfw = new XMLFileWriter();
        }

        @Override
        public boolean shouldVisit(WebURL url) {
                String href = url.getURL().toLowerCase();
                return !filters.matcher(href).matches() && href.startsWith("http://www.lankadeepa.lk/");
        }

        @Override
        public void visit(Page page) {
        	
                System.out.println("Visited: " + page.getWebURL().getURL());
                myCrawlStat.incProcessedPages();

                if (page.getParseData() instanceof HtmlParseData) {
                        HtmlParseData parseData = (HtmlParseData) page.getParseData();
                        new LankadeepaParser(page);
//                        System.out.println((new Parser(page).getTitle()));
                        try {
							
							xfw.addDocument(page);
						} catch (IOException | XMLStreamException e) {
							e.printStackTrace();
						}
                       
                        List<WebURL> links = parseData.getOutgoingUrls();
                        myCrawlStat.incTotalLinks(links.size());
                        try {
                                myCrawlStat.incTotalTextSize(parseData.getText().getBytes("UTF-8").length);
                        } catch (UnsupportedEncodingException ignored) {
                                // Do nothing
                        }
                }
                // We dump this crawler statistics after processing every 50 pages
                if (myCrawlStat.getTotalProcessedPages() % 50 == 0) {
                        dumpMyData();
                }
        }

        // This function is called by controller to get the local data of this
        // crawler when job is finished
        @Override
        public Object getMyLocalData() {
                return myCrawlStat;
        }

        // This function is called by controller before finishing the job.
        // You can put whatever stuff you need here.
        @Override
        public void onBeforeExit() {
        	if(xfw.getDocumentCounter()>0){
        		try {
					xfw.writeToFile();
				} catch (IOException | XMLStreamException e) {
					e.printStackTrace();
				}
        	}
                dumpMyData();
        }

        public void dumpMyData() {
                int id = getMyId();
                // This is just an example. Therefore I print on screen. You may
                // probably want to write in a text file.
                System.out.println("Crawler " + id + "> Processed Pages: " + myCrawlStat.getTotalProcessedPages());
                System.out.println("Crawler " + id + "> Total Links Found: " + myCrawlStat.getTotalLinks());
                System.out.println("Crawler " + id + "> Total Text Size: " + myCrawlStat.getTotalTextSize());
        }
}
