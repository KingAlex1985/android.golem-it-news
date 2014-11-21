package godlesz.de.golemdeit_news2.rss;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.List;

public class RssService extends IntentService {
    public static final String LOG_TAG = "RssService";

    public static final String RSS_LINK = "http://rss.golem.de/rss.php?feed=RSS2.0";

    public static final String TAG_ITEMS = "items";
    public static final String TAG_RECEIVER = "receiver";

    public RssService() {
        super("RssService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(LOG_TAG, "Service started");
        List<RssItem> rssItems = null;
        try {
            RssParser parser = new RssParser();
            InputStream stream = getInputStream(RSS_LINK);
            if (stream != null) {
                rssItems = parser.parse(stream);
            }
        } catch (XmlPullParserException e) {
            Log.w(e.getMessage(), e);
        } catch (IOException e) {
            Log.w(e.getMessage(), e);
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG_ITEMS, (Serializable) rssItems);
        ResultReceiver receiver = intent.getParcelableExtra(TAG_RECEIVER);
        receiver.send(0, bundle);
    }

    public InputStream getInputStream(String link) {
        try {
            URL url = new URL(link);
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            Log.w(LOG_TAG, "Exception while retrieving the input stream", e);
            return null;
        }
    }
}
