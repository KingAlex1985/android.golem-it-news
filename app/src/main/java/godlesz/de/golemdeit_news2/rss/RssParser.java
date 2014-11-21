package godlesz.de.golemdeit_news2.rss;

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

public class RssParser {

    // We don't use namespaces
    private final String XML_NAMESPACE = null;

    public List<RssItem> parse(InputStream inputStream) throws XmlPullParserException, IOException {
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
        Element itemElement = (Element)node;

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

            /*
            <item>
                <title>Digitale Agenda: Merkel fühlt sich im Neuland verfolgt</title>
                <link>http://rss.feedsportal.com/c/33374/f/578068/p/1/s/696021eb/l/0L0Sgolem0Bde0Cnews0Cdigitale0Eagenda0Emerkel0Efuehlt0Esich0Eim0Eneuland0Everfolgt0E14110E110A3430Erss0Bhtml/story01.htm</link>
                <description>"Ich bin doch nicht blöd": Bundeskanzlerin Merkel will sich nicht über ihre Einkaufspräferenzen im Netz äußern. Dass das Internet für sie immer noch Neuland ist, bewies sie wieder mit Aussagen zur Netzneutralität und zu Internetwerbung. (&lt;a href="http://www.golem.de/specials/digitale-agenda/"&gt;Digitale Agenda&lt;/a&gt;, &lt;a href="http://www.golem.de/specials/netzneutralitaet/"&gt;Netzneutralität&lt;/a&gt;) &lt;img src="http://cpx.golem.de/cpx.php?class=17&amp;amp;aid=110343&amp;amp;page=1&amp;amp;ts=1415217060" alt="" width="1" height="1" /&gt;&lt;img width='1' height='1' src='http://rss.feedsportal.com/c/33374/f/578068/p/1/s/696021eb/mf.gif' border='0'/&gt;&lt;br/&gt;&lt;br/&gt;&lt;a href="http://da.feedsportal.com/r/204223014498/u/218/f/578068/c/33374/s/696021eb/a2.htm"&gt;&lt;img src="http://da.feedsportal.com/r/204223014498/u/218/f/578068/c/33374/s/696021eb/a2.img" border="0"/&gt;&lt;/a&gt;&lt;img width="1" height="1" src="http://pi.feedsportal.com/r/204223014498/u/218/f/578068/c/33374/s/696021eb/a2t.img" border="0"/&gt;</description>
                <pubDate>Wed, 05 Nov 2014 19:51:00 GMT</pubDate>
                <comments>http://forum.golem.de/kommentare/internet/digitale-agenda-merkel-fuehlt-sich-im-neuland-verfolgt/87659,list.html</comments>
                <guid isPermaLink="false">http://www.golem.de/1411/110343-rss.html</guid>
                <content:encoded><![CDATA[<img src="http://www.golem.de/1411/110343-89571-i_rc.jpg" width="140" height="140" vspace="3" hspace="8" align="left">"Ich bin doch nicht blöd": Bundeskanzlerin Merkel will sich nicht über ihre Einkaufspräferenzen im Netz äußern. Dass das Internet für sie immer noch Neuland ist, bewies sie wieder mit Aussagen zur Netzneutralität und zu Internetwerbung. (<a href="http://www.golem.de/specials/digitale-agenda/">Digitale Agenda</a>, <a href="http://www.golem.de/specials/netzneutralitaet/">Netzneutralität</a>) <img src="http://cpx.golem.de/cpx.php?class=17&#38;aid=110343&#38;page=1&#38;ts=1415217060" alt="" width="1" height="1" /><img width='1' height='1' src='http://rss.feedsportal.com/c/33374/f/578068/p/1/s/696021eb/mf.gif' border='0'/><br/><br/><a href="http://da.feedsportal.com/r/204223014498/u/218/f/578068/c/33374/s/696021eb/a2.htm"><img src="http://da.feedsportal.com/r/204223014498/u/218/f/578068/c/33374/s/696021eb/a2.img" border="0"/></a><img width="1" height="1" src="http://pi.feedsportal.com/r/204223014498/u/218/f/578068/c/33374/s/696021eb/a2t.img" border="0"/>]]></content:encoded>
                <slash:comments>41</slash:comments>
            </item>
             */
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

}