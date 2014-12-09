package com.davidgg.dobleembrague.rss;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * RSS Item
 *
 * @author DavidGG
 */
public class RSSItem {

    public static RSSItem getLast(String feedURL) {
        RSSItem lastItem = null;
        ArrayList<RSSItem> rssItems;
        rssItems = getRssItems(feedURL);

        if (rssItems != null) {
            lastItem = rssItems.get(0);
            rssItems.clear();
        }
        return lastItem;
    }


    public static ArrayList<RSSItem> getRssItems(String feedUrl) {

        ArrayList<RSSItem> rssItems = new ArrayList<RSSItem>();
        try {

            URL url = new URL(feedUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream is = conn.getInputStream();

                DocumentBuilderFactory dbf = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();

                Document document = db.parse(is);
                Element element = document.getDocumentElement();

                NodeList nodeList = element.getElementsByTagName("item");

                if (nodeList.getLength() > 0) {
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Element entry = (Element) nodeList.item(i);

                        String _title = ((Element) entry.getElementsByTagName(
                                "title").item(0)).getFirstChild()
                                .getNodeValue();
                        Date _pubDate = new Date(((Element) entry
                                .getElementsByTagName("pubDate").item(0))
                                .getFirstChild().getNodeValue());
                        String _creator = ((Element) entry
                                .getElementsByTagName("dc:creator").item(0))
                                .getFirstChild().getNodeValue();
                        String _link = ((Element) entry.getElementsByTagName(
                                "link").item(0)).getFirstChild().getNodeValue();
                        String _fullDesc = ((Element) entry
                                .getElementsByTagName("content:encoded")
                                .item(0)).getFirstChild().getNodeValue();

                        String fullDesc = _fullDesc
                                .replaceAll("<(.|\n)*?>", "");
                        fullDesc = fullDesc.replaceAll("&nbsp;", "");
                        fullDesc = fullDesc.split("Filed under:")[0];

                        // Se crea un item y se añade a la lista
                        RSSItem rssItem = new RSSItem(_title, fullDesc,
                                _pubDate, _creator, _link);
                        rssItems.add(rssItem);
                    }
                }

            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
        return rssItems;
    }

    private String title;
    private String description;
    private Date pubDate;
    private String link;
    private String creator;

    public RSSItem(String title, String description, Date pubDate,
                   String creator, String link) {
        this.title = title;
        this.description = description;
        this.pubDate = pubDate;
        this.creator = creator;
        this.link = link;
    }

    public String getCreator() {
        return creator;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm '-' dd/MM");
        String result = getTitle() + "\n( " + sdf.format(this.getPubDate())
                + " )";
        return result;
    }

}
