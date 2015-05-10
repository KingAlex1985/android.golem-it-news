package godlesz.de.golemdeit_news2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import godlesz.de.golemdeit_news2.database.News2Content;
import godlesz.de.golemdeit_news2.database.News2DbController;
import godlesz.de.golemdeit_news2.rss.RssItem;


public class ArticleActivity extends Activity {
    public static final String TAG = ArticleActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private RssItem _rssItem;
    private MenuItem fontSizePlusMenuIem;
    private MenuItem fontSizeMinusMenuIem;
    private WebView view;
    private String wholeText = null;

    private class ArticleDetailsRequestTask extends AsyncTask<String, Void, String> {
        private String urlId = null;

        @Override
        protected void onPreExecute() {
            Log.e(TAG, "ArticleDetailsRequestTask$onPreExecute() was called");

            super.onPreExecute();
            // Hide text
            WebView view = ((WebView)findViewById(R.id.article));
            view.setVisibility(View.GONE);
            // Display loading
            displayProgressBar("Loading ...");
        }

        @Override
        protected String doInBackground(String... strings) {
            //Log.e(TAG, "ArticleDetailsRequestTask$doInBackground() was called. strings[0]=" + strings[0]);

            urlId = strings[0];

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
            Log.e(TAG, "ArticleDetailsRequestTask$onPostExecute() was called");
            if(urlId != null){
                //Log.e(TAG, "ArticleDetailsRequestTask$onPostExecute()urlId = " + urlId);
                //Log.e(TAG, "ArticleDetailsRequestTask$onPostExecute()testResult = " + result);

                if(result == null || result.length() == 0 || result.equals("")){
                    //Log.e(TAG, "ArticleDetailsRequestTask$onPostExecute() result is NULL or length == 0");
                } else {
                    try{
                        // save the result to the database
                        //News2DbController news2DbController = ApplicationHelper.getNews2DbController();
                        News2DbController news2DbController = new News2DbController(ApplicationHelper.getAppContext());
                        news2DbController.insertHtmlArticleByGivenUrlLink(urlId, result);

                    } catch (Exception e){
                        Log.e(TAG, "ArticleDetailsRequestTask$onPostExecute() Exception-Error ", e);
                    }
                }
            }

            // Hide loading
            dismissProgressBar();
            String tempResult = null;

            if (result.length() == 0) {
                if (urlId != null){
                    // getOneContentEntryByUrlId

                    try{
                        //News2DbController news2DbController = ApplicationHelper.getNews2DbController();
                        News2DbController news2DbController = new News2DbController(ApplicationHelper.getAppContext());
                        News2Content tempNews2Content = news2DbController.getOneContentEntryByUrlId(urlId);

                        if(tempNews2Content != null){
                            tempResult = tempNews2Content.getWholetextSql();
                        } else {
                            Log.e(TAG, "ArticleDetailsRequestTask$onPostExecute()tempNews2Content is NULL");
                        }

                    } catch (Exception e){
                        Log.e(TAG, "ArticleDetailsRequestTask$onPostExecute() Exception-Error ", e);
                    }

                }

                if(tempResult == null || tempResult.length() == 0 || tempResult.equals("")){
                    result = "<h1>Failed to fetch article</h1>" +
                            "Sorry, but I'm unable to fetch the article html data from Golem.de.<br />" +
                            "Please report this to GodLesZ, thank you!";
                } else {
                    result = tempResult;
                    //Log.e(TAG, "ArticleDetailsRequestTask$onPostExecute() tempResult=" + tempResult);

                }
            }

            //testWithWebView();

//            final Activity activity = ArticleActivity.this;
//
//            //WebView view = ((WebView)findViewById(R.id.article));
//            view.setWebChromeClient(new WebChromeClient() {
//                public void onProgressChanged(WebView view, int progress) {
//                    activity.setProgress(progress * 1000);
//                }
//            });
//            view.setWebViewClient(new WebViewClient() {
//                @Override
//                public boolean shouldOverrideUrlLoading(final WebView view, final String url){
//                    Uri uri = Uri.parse(url);
//                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                    startActivity(intent);
//                    return true;
//                }
//
//                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//                    Toast.makeText(activity, "Oh noes! " + description, Toast.LENGTH_SHORT).show();
//                }
//            });

            //Log.e(TAG, "ArticleDetailsRequestTask$onPostExecute() result = " + result);

            setArcticleToWebView(result);

//            String htmlContent = changeContentStyle(result);
//
//            view.loadData(htmlContent, "text/html; charset=UTF-8", null);
//            view.setVisibility(View.VISIBLE);
//
//            // Alex set Webview zoomable on API >= 11
//            view.getSettings().setBuiltInZoomControls(true);
//            view.getSettings().setDisplayZoomControls(true);
        }
    }
    //----------------------------------------------------------------------------------
    //------------------- END: of AsyncTask "ArticleDetailsRequestTask"-----------------
    //----------------------------------------------------------------------------------

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

        view = ((WebView)findViewById(R.id.article));

        _rssItem = (RssItem)getIntent().getSerializableExtra("rssItem");

        boolean wholeTextIsGiven = checkIfWholeTextToGivenUrlIsGiven(_rssItem.getLink());
        Log.e(TAG, "onCreate() wholeTextIsGiven = " + wholeTextIsGiven);
        if(wholeTextIsGiven){
            setArcticleToWebView(wholeText);
        } else {
            ArticleDetailsRequestTask task = new ArticleDetailsRequestTask();
            //Log.e(TAG, "ArticleDetailsRequestTask$onCreate() rssItem.getLink() = " + _rssItem.getLink());
            task.execute(_rssItem.getLink());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_article, menu);
        fontSizeMinusMenuIem = menu.getItem(2);
        fontSizePlusMenuIem = menu.getItem(3);

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

        if(id == R.id.menu_item_article_fontsize_plus){
            int fontSize = ApplicationHelper.get_fontSize();
            if(fontSize <= 400){
                int newFontSize = fontSize + 20;
                ApplicationHelper.set_fontSize(newFontSize);

                if(newFontSize>= 400){
                    fontSizePlusMenuIem.setEnabled(false);
                } else {
                    fontSizePlusMenuIem.setEnabled(true);
                }

                // Refresh activity
                finish();
                startActivity(getIntent());
            }
        }

        if(id == R.id.menu_item_article_fontsize_minus){
            int fontSize = ApplicationHelper.get_fontSize();
            if(fontSize >= 60){
                int newFontSize = fontSize - 20;
                ApplicationHelper.set_fontSize(newFontSize);

                if(newFontSize <= 60){
                    fontSizeMinusMenuIem.setEnabled(false);
                } else {
                    fontSizeMinusMenuIem.setEnabled(true);
                }

                // Refresh activity
                finish();
                startActivity(getIntent());
            }
        }


        return super.onOptionsItemSelected(item);
    }

    private String changeContentStyle(String result){
        String text;
        int fontSize = ApplicationHelper.get_fontSize();

        if(ApplicationHelper.is_night_view()){

            text = "<html><head>"
                    + "<style type=\"text/css\">body{color: #FFF; background-color: #000;"
                    + "font-size:" + fontSize + "%;}"
                    + "</style></head>"
                    + "<body>"
                    + result
                    + "</body></html>";
        } else {
            text = "<html><head>"
                    + "<style type=\"text/css\">body{color: #000; background-color: #FFF;"
                    + "font-size:" + fontSize + "%;}"
                    + "</style></head>"
                    + "<body>"
                    + result
                    + "</body></html>";
        }

        return text;
    }

    private void testWithWebView(){
        Log.e(TAG, "testWithWebView() was called ");
        final Activity activity = ArticleActivity.this;

        //WebView view = ((WebView)findViewById(R.id.article));
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
    }

    private boolean checkIfWholeTextToGivenUrlIsGiven(String urlId){
        boolean wholeTextIsGiven = false;
        wholeText = null;

        News2DbController news2DbController = new News2DbController(ApplicationHelper.getAppContext());
        News2Content tempNews2Content = news2DbController.getOneContentEntryByUrlId(urlId);

        if(tempNews2Content != null){
            String wholeTextTemp = tempNews2Content.getWholetextSql();
            if(wholeTextTemp != null){
                if(!wholeTextTemp.equals("")){
                    wholeText = wholeTextTemp;
                    wholeTextIsGiven = true;
                } else {
                    Log.e(TAG, "checkIfWholeTextToGivenUrlIsGiven() wholeTextTemp is EMPTY");
                }
            } else {
                Log.e(TAG, "checkIfWholeTextToGivenUrlIsGiven() wholeTextTemp is NULL");
            }
        } else {
            Log.e(TAG, "checkIfWholeTextToGivenUrlIsGiven() tempNews2Content is NULL");
        }
        return wholeTextIsGiven;
    }

    private void setArcticleToWebView(String wholeText){
        String htmlContent = changeContentStyle(wholeText);

        view.loadData(htmlContent, "text/html; charset=UTF-8", null);
        view.setVisibility(View.VISIBLE);

        // Alex set Webview zoomable on API >= 11
        view.getSettings().setBuiltInZoomControls(true);
        view.getSettings().setDisplayZoomControls(true);
    }

}
