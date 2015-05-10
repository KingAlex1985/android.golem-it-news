package godlesz.de.golemdeit_news2.rss;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import godlesz.de.golemdeit_news2.ApplicationHelper;
import godlesz.de.golemdeit_news2.database.News2Content;
import godlesz.de.golemdeit_news2.database.News2DbController;

public class RssParser {
    public static final String TAG = RssParser.class.getSimpleName();

    Context myContext = ApplicationHelper.getAppContext();

    //private static News2DbController news2DbController = ApplicationHelper.getNews2DbController();
    News2DbController news2DbController = new News2DbController(ApplicationHelper.getAppContext());

    // We don't use namespaces
    private final String XML_NAMESPACE = null;

    public List<RssItem> parse(InputStream inputStream) throws XmlPullParserException, IOException {
        //Log.e(TAG, "parse() was called.");

        List<RssItem> items = new ArrayList<RssItem>();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(inputStream));
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("item");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                RssItem item = readRssItemFromNode(node);
                items.add(item);
            }

            /*
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, null);
            parser.nextTag();
            return readFeed(parser);
            */
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
        }

        return items;
    }

    private RssItem readRssItemFromNode(Node node) {
        //Log.e(TAG, "readRssItemFromNode() was called.");

        Element itemElement = (Element) node;

        RssItem item = new RssItem();
        item.setTitle(itemElement.getElementsByTagName("title").item(0).getFirstChild().getNodeValue());
        item.setLink(itemElement.getElementsByTagName("guid").item(0).getFirstChild().getNodeValue());
        item.setDescription(itemElement.getElementsByTagName("description").item(0).getFirstChild().getNodeValue());
        item.setPubDate(itemElement.getElementsByTagName("pubDate").item(0).getFirstChild().getNodeValue());
        item.readFromEncodedContent(itemElement.getElementsByTagName("content:encoded").item(0).getFirstChild().getNodeValue());

        NodeList nodeListComments = itemElement.getElementsByTagName("comments");
        if (nodeListComments.getLength() > 0) {
            item.setCommentUrl(itemElement.getElementsByTagName("comments").item(0).getFirstChild().getNodeValue());
            item.setCommentCount(itemElement.getElementsByTagName("slash:comments").item(0).getFirstChild().getNodeValue());
        }

        // Alex Daten in DB speichern
        News2Content tempNews2Content = new News2Content();
        tempNews2Content.setTitleSql(item.getTitle());
        tempNews2Content.setDescriptionSql(item.getDescription());
        tempNews2Content.setPubdateSql(item.getPubDate());
        tempNews2Content.setCommentUrlSql(item.getCommentUrl());
        tempNews2Content.setCommentCountSql(item.getCommentCount());
        tempNews2Content.setLinkSql(item.getLink());

        saveState(tempNews2Content);

        return item;
    }

    private List<RssItem> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "rss");
        List<RssItem> items = new ArrayList<RssItem>();
        RssItem item = new RssItem();

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals("title")) {
                item.setTitle(readSimpleTag(parser, "title"));
            } else if (name.equals("guid")) {
                item.setLink(readSimpleTag(parser, "guid"));
            } else if (name.equals("description")) {
                item.setDescription(readSimpleTag(parser, "description"));
            } else if (name.equals("pubDate")) {
                item.setPubDate(readSimpleTag(parser, "pubDate"));
            } else if (name.equals("comments")) {
                item.setCommentUrl(readSimpleTag(parser, "comments"));
            } else if (name.equals("slash:comments")) {
                item.setCommentCount(readSimpleTag(parser, "slash:comments"));
            } else if (name.equals("content:encoded")) {
                item.readFromEncodedContent(readSimpleTag(parser, "content:encoded"));
            }

            if (item.isValid()) {
                items.add(item);
                item = new RssItem();
            }
        }

        return items;
    }

    private String readSimpleTag(XmlPullParser parser, String tagName) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, XML_NAMESPACE, tagName);
        String link = readText(parser);
        parser.require(XmlPullParser.END_TAG, XML_NAMESPACE, tagName);
        return link;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void saveState(News2Content news2Content) {
        //Log.e(TAG, "saveState() was called");

        if (news2DbController != null) {
            if (!news2DbController.checkIfGuidAlreadyExists(news2Content.getLinkSql())) {
                news2DbController.insertNewsDataInDatabase(news2Content);
            }
        }
    }
}