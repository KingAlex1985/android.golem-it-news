package godlesz.de.golemdeit_news2;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import godlesz.de.golemdeit_news2.rss.RssFragment;


public class MainActivity extends Activity {
    private RssFragment _rssFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_item_refresh) {
            addOrReplaceRssFragment(true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
