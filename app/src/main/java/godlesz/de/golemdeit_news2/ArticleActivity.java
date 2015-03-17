package godlesz.de.golemdeit_news2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import godlesz.de.golemdeit_news2.rss.RssItem;


public class ArticleActivity extends Activity {
    private ProgressDialog pDialog;
    private RssItem _rssItem;

    private class ArticleDetailsRequestTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Hide text
            WebView view = ((WebView)findViewById(R.id.article));
            view.setVisibility(View.GONE);
            // Display loading
            displayProgressBar("Loading ...");
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection con = Jsoup
                    .connect(strings[0])
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

                // Append comment
                if (_rssItem.getCommentUrl().length() > 0) {
                    text += "<div><a href=\"" + _rssItem.getCommentUrl() + "\">" + _rssItem.getCommentCount() + " Kommentare</a></div>";
                }

                return text;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            // Hide loading
            dismissProgressBar();

            if (result.length() == 0) {
                result = "<h1>Failed to fetch article</h1>" +
                    "Sorry, but I'm unable to fetch the article html data from Golem.de.<br />" +
                    "Please report this to GodLesZ, thank you!";
            }

            final Activity activity = ArticleActivity.this;

            WebView view = ((WebView)findViewById(R.id.article));
            view.setWebChromeClient(new WebChromeClient() {
                public void onProgressChanged(WebView view, int progress) {
                    activity.setProgress(progress * 1000);
                }
            });
            view.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(final WebView view, final String url){
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    return true;
                }

                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    Toast.makeText(activity, "Oh noes! " + description, Toast.LENGTH_SHORT).show();
                }
            });

            String text = "<html><head>"
                    + "<style type=\"text/css\">body{color: #fff; background-color: #000;}"
                    + "</style></head>"
                    + "<body>"
                    + result
                    + "</body></html>";

            // Set content and show
            if(ApplicationHelper.is_night_view()){
                view.loadData(text, "text/html; charset=UTF-8", null);
            } else {
                view.loadData(result, "text/html; charset=UTF-8", null);
            }
            view.setVisibility(View.VISIBLE);
        }
    }


    protected void displayProgressBar(String message) {
        pDialog = ProgressDialog.show(this, null, message);
    }

    protected void dismissProgressBar() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_article);

        _rssItem = (RssItem)getIntent().getSerializableExtra("rssItem");
        ArticleDetailsRequestTask task = new ArticleDetailsRequestTask();
        task.execute(_rssItem.getLink());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_article, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_item_share) {
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("text/plain");
            intent.putExtra("android.intent.extra.TEXT", _rssItem.getTitle() + "\r\n" + _rssItem.getLink());
            startActivity(Intent.createChooser(intent, "Share"));
        }

        if (id == R.id.menu_item_article_day_or_night_view) {
            if(ApplicationHelper.is_night_view() == true){
                ApplicationHelper.set_night_view(false);
            } else {
                ApplicationHelper.set_night_view(true);
            }

            // Refresh activity
            finish();
            startActivity(getIntent());

        }

        return super.onOptionsItemSelected(item);
    }

}
