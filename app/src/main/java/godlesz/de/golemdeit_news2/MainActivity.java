package godlesz.de.golemdeit_news2;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import godlesz.de.golemdeit_news2.database.News2Content;
import godlesz.de.golemdeit_news2.database.News2DbController;
import godlesz.de.golemdeit_news2.rss.RssFragment;
import godlesz.de.golemdeit_news2.rss.RssService;
import godlesz.de.golemdeit_news2.util.ArticleTextService;


public class MainActivity extends Activity {
    public static final String TAG = MainActivity.class.getSimpleName();

    private RssFragment _rssFragment;
    private ArrayList<String> urlIdList;
    private ArrayList<String> urlIdWithEmptyTextList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.e(TAG, "onCreate() was called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            addOrReplaceRssFragment(false);
        }
    }

    private void addOrReplaceRssFragment(boolean replace) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        _rssFragment = new RssFragment();
        if (replace) {
            transaction.replace(R.id.fragment_container, _rssFragment);
        } else {
            transaction.add(R.id.fragment_container, _rssFragment);
        }
        transaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("fragment_added", true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        //Log.e(TAG, "onResume() was called");
        super.onResume();

        urlIdList = readUrlIdListFromDatabase();
        ApplicationHelper.setUrlIdList(urlIdList);

        urlIdWithEmptyTextList = readUrlIdListWithEmptyArticleTextFromDatabase();
        ApplicationHelper.setUrlIdWithEmptyTextList(urlIdWithEmptyTextList);

        readDataFromDatabase();

        startArticleTextService();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e(TAG, "onOptionsItemSelected() was called");
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_item_refresh) {
            addOrReplaceRssFragment(true);
            return true;
        }

        if (id == R.id.menu_item_day_or_night_view) {
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

    private void readDataFromDatabase(){
        //Log.e(TAG, "readDataFromDatabase() was called");

        //News2DbController news2DbController = ApplicationHelper.getNews2DbController();
        News2DbController news2DbController = new News2DbController(ApplicationHelper.getAppContext());
        List<News2Content> contentList = news2DbController.getAllNews();



        if(contentList != null){
            if(!contentList.isEmpty()){

                ApplicationHelper.setNews2ContentArrayList( (ArrayList)contentList);

//                int i = 1;
//                for (News2Content news2Content : contentList) {
//                    Log.e(TAG, "readDataFromDatabase() index="+ i
//                            + " idSql=" + news2Content.getIdSql()
//                            + " pubdate=" + news2Content.getPubdateSql()
//                            + " guid(link)=" + news2Content.getLinkSql()
//                            //+ " value=" + news2Content.getTitleSql()
//                            //+ " descriptionSql=" + news2Content.getDescriptionSql()
//                            //+ " wholeText=" + news2Content.getWholetextSql()
//                    );
//                    i++;
//                }
            } else {
                Log.e(TAG, "readDataFromDatabase() contentList is EMPTY.");
            }
        } else {
            Log.e(TAG, "readDataFromDatabase() contentList is NULL.");
        }

    }

    private ArrayList<String> readUrlIdListFromDatabase(){
        //Log.e(TAG, "readUrlIdListFromDatabase() was called");

        News2DbController news2DbController = new News2DbController(ApplicationHelper.getAppContext());
        ArrayList<String> urlIdList = news2DbController.getAllUrlIds();

        if(urlIdList != null){
            if(!urlIdList.isEmpty()){
                // Do nothing
            } else {
                Log.e(TAG, "readUrlIdListFromDatabase() urlIdList is EMPTY.");
            }
        } else {
            Log.e(TAG, "readUrlIdListFromDatabase() urlIdList is NULL.");
        }

        return urlIdList;
    }

    private ArrayList<String> readUrlIdListWithEmptyArticleTextFromDatabase(){
        //Log.e(TAG, "readUrlIdListWithEmptyArticleTextFromDatabase() was called");

        News2DbController news2DbController = new News2DbController(ApplicationHelper.getAppContext());
        ArrayList<String> urlIdList = news2DbController.getAllUrlIdsWithEmptyArticleText();

        if(urlIdList != null){
            if(!urlIdList.isEmpty()){
                // Do nothing
                Log.e(TAG, "readUrlIdListWithEmptyArticleTextFromDatabase() urlIdList.size() = " + urlIdList.size());
            } else {
                Log.e(TAG, "readUrlIdListWithEmptyArticleTextFromDatabase() urlIdList is EMPTY.");
            }
        } else {
            Log.e(TAG, "readUrlIdListWithEmptyArticleTextFromDatabase() urlIdList is NULL.");
        }

        return urlIdList;
    }

    public void startArticleTextService() {
        //Log.e(TAG, "startArticleTextService() was called");
        Intent intent = new Intent(getApplicationContext(), ArticleTextService.class);

        ArrayList<String> urlListTemp = ApplicationHelper.getUrlIdWithEmptyTextList();

        if(urlListTemp != null){
            intent.putExtra(ArticleTextService.TAG_RECEIVER, urlListTemp);
            startService(intent);
        } else {
            Log.e(TAG, "startArticleTextService() urlListTemp is NULL");
        }

    }
}
