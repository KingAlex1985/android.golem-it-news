package godlesz.de.golemdeit_news2.rss;

import android.text.format.DateFormat;

import org.jsoup.Jsoup;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import godlesz.de.golemdeit_news2.util.RfcDateParser;

public class RssItem implements Serializable {
    private String _title = "";
    private String _link = "";
    private String _description = "";
    private String _pubDate = "";
    private String _thumbnailUrl = "";
    private String _commentCount = "";
    private String _commentUrl = "";


    public RssItem() {
    }


    public boolean isValid() {
        return
            getTitle().length() > 0 &&
            getLink().length() > 0 &&
            getDescription().length() > 0 &&
            getPubDate().length() > 0 &&
            getThumbnailUrl().length() > 0 &&
            getCommentCount().length() > 0 &&
            getCommentUrl().length() > 0
            ;
    }

    public void readFromEncodedContent(String text) {
        Matcher match = Pattern.compile("^<img src=\"([^\"]*)\"").matcher(text);
        if (match.find()) {
            setThumbnailUrl(match.group(1));
        }
    }


    public String getTitle() {
        return _title;
    }
    public String getTitleFormatted() {
        int i = getTitle().indexOf(':');
        if (i == -1) {
            return getTitle();
        }

        return
            getTitle().substring(0, i + 1).trim() + "\r\n" +
            getTitle().substring(i + 1).trim();
    }
    public RssItem setTitle(String title) {
        _title = title;
        return this;
    }

    public String getLink() {
        return _link;
    }
    public RssItem setLink(String link) {
        _link = link;
        return this;
    }

    public String getDescription() {
        return _description;
    }
    public RssItem setDescription(String description) {
        _description = Jsoup.parse(description).text();
        return this;
    }

    public String getPubDate() {
        return _pubDate;
    }
    public String getPubDateFormatted() {
        RfcDateParser parser = new RfcDateParser(getPubDate());
        Date date = parser.getDate();
        if (date == null) {
            return getPubDate();
        }

        String timeFormatted = new SimpleDateFormat("HH:mm", Locale.GERMANY).format(date) + " Uhr";

        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        int currentDoy = cal.get(Calendar.DAY_OF_YEAR);
        cal.setTime(date);

        if (currentYear == cal.get(Calendar.YEAR)) {
            if (currentDoy == cal.get(Calendar.DAY_OF_YEAR)) {
                return "Heute, " + timeFormatted;
            }
            else if ((currentDoy - 1) == cal.get(Calendar.DAY_OF_YEAR)) {
                return "Gestern, " + timeFormatted;
            }
        }

        return DateFormat.format("dd.MM.yyyy", date).toString() + ", " + timeFormatted;
    }
    public RssItem setPubDate(String pubDate) {
        _pubDate = pubDate;
        return this;
    }

    public String getCommentCount() {
        return _commentCount;
    }
    public RssItem setCommentCount(String commentCount) {
        _commentCount = commentCount;
        return this;
    }

    public String getCommentUrl() {
        return _commentUrl;
    }
    public RssItem setCommentUrl(String commentUrl) {
        _commentUrl = commentUrl;
        return this;
    }

    public String getThumbnailUrl() {
        return _thumbnailUrl;
    }
    public RssItem setThumbnailUrl(String thumbnailUrl) {
        _thumbnailUrl = thumbnailUrl;
        return this;
    }

}
