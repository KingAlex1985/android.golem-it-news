package godlesz.de.golemdeit_news2.util;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;

import godlesz.de.golemdeit_news2.ApplicationHelper;
import godlesz.de.golemdeit_news2.database.News2DbController;

public class ArticleTextService extends IntentService{

    public static final String TAG = ArticleTextService.class.getSimpleName();
    public static final String TAG_RECEIVER = "articletextreceiver";

    public ArticleTextService(){
        super("ArticleTextService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Alex service starting", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Log.e(TAG, "onHandleIntent() was called");
        String urlTemp = "";

        ArrayList<String> urlList =  intent.getStringArrayListExtra(TAG_RECEIVER);
        News2DbController news2DbController = new News2DbController(ApplicationHelper.getAppContext());

        if (urlList != null){
            for (String url : urlList) {
                urlTemp = url;

                try {
                    Connection con = Jsoup
                            .connect(urlTemp)
                            .userAgent("Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.104 Safari/537.36")
                            .timeout(5000);
                    Connection.Response resp = con.execute();
                    Document doc = resp.parse();
                    doc.select("script, iframe, img, ol.list-chapters, div.gg_embeddedSubText, .golem-gallery2-nojs").remove();

                    String text = doc.body().select("div.formatted").toString();

                    // Search for next page: "head link[rel=next]"
                    Element nextLink = doc.head().select("link[rel=\"next\"]").first();
                    while (nextLink != null) {
                        String nextPageLink = "http://golem.de" + nextLink.attr("href");
                        con = Jsoup
                                .connect(nextPageLink)
                                .userAgent("Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.104 Safari/537.36")
                                .timeout(5000);
                        resp = con.execute();
                        doc = resp.parse();
                        doc.select("script, iframe, img, ol.list-chapters, div.gg_embeddedSubText, .golem-gallery2-nojs").remove();

                        text += doc.body().select("div.formatted").toString();

                        nextLink = doc.head().select("link[rel=\"next\"]").first();
                    }

//            // Append comment
//            if (_rssItem.getCommentUrl().length() > 0) {
//                text += "<div><a href=\"" + _rssItem.getCommentUrl() + "\">" + _rssItem.getCommentCount() + " Kommentare</a></div>";
//            }

                    //Log.e(TAG, "onHandleIntent() text = " + text);

                    news2DbController.insertHtmlArticleByGivenUrlLink(urlTemp, text);

                } catch (Exception e) {
                    Log.e(TAG, "onHandleIntent() Exception Error", e);
                }
            }

            Toast.makeText(this, "Alle Artikel nun offline zum Lesen bereit.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "onHandleIntent() All article texts are downloaded now and are available for offline reading!");

        } else {
            Log.e(TAG, "onHandleIntent() urlList is NULL");
        }


    }
}
