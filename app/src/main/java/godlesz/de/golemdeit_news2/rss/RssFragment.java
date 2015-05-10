package godlesz.de.golemdeit_news2.rss;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import godlesz.de.golemdeit_news2.ApplicationHelper;
import godlesz.de.golemdeit_news2.ArticleActivity;
import godlesz.de.golemdeit_news2.R;
import godlesz.de.golemdeit_news2.database.News2Content;
import godlesz.de.golemdeit_news2.database.News2DbController;

public class RssFragment extends Fragment implements AdapterView.OnItemClickListener {

    public static final String TAG = RssFragment.class.getSimpleName();

    private ProgressBar progressBar;
    private ListView listView;
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Log.e(TAG, "onCreate() was called");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onResume() {
        //Log.e(TAG, "onResume() was called");
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Log.e(TAG, "onCreateView() was called");
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_layout, container, false);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            listView = (ListView) view.findViewById(R.id.listView);


            if (ApplicationHelper.is_night_view() == true) {
                listView.setBackgroundColor(Color.BLACK);
            } else {
                listView.setBackgroundColor(Color.WHITE);
            }

            listView.setOnItemClickListener(this);
            startService();

            return view;
        }

        // If we are returning from a configuration change:
        // "view" is still attached to the previous view hierarchy
        // so we need to remove it and re-attach it to the current one
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }

        return view;
    }

    public void startService() {
        //Log.e(TAG, "startService() was called");
        Intent intent = new Intent(getActivity(), RssService.class);
        intent.putExtra(RssService.TAG_RECEIVER, resultReceiver);
        getActivity().startService(intent);
    }

    /**
     * Once the {@link RssService} finishes its task, the result is sent to this ResultReceiver.
     */
    private final ResultReceiver resultReceiver = new ResultReceiver(new Handler()) {
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            //Log.e(TAG, "ResultReceiver$onReceiveResult() was called");
            List<RssItem> items = new ArrayList<RssItem>();
            try {
                Serializable data = resultData.getSerializable(RssService.TAG_ITEMS);
                if (data instanceof List) {
                    for (int i = 0; i < ((List<?>) data).size(); i++) {
                        Object item = ((List<?>) data).get(i);
                        if (item instanceof RssItem) {
                            items.add((RssItem) item);
                        }
                    }
                }
            } catch (ClassCastException e) {
                e.printStackTrace();
            }

            if (items.size() > 0) {
                RssAdapter adapter = new RssAdapter(getActivity(), items);
                listView.setAdapter(adapter);
            } else {

                //News2DbController news2DbController = ApplicationHelper.getNews2DbController();
                News2DbController news2DbController = new News2DbController(ApplicationHelper.getAppContext());

                List<News2Content> newsDBList = news2DbController.getAllNews();

                for (News2Content news2Content : newsDBList) {
                    RssItem tempRssItem = new RssItem();
                    tempRssItem.setCommentCount(news2Content.getCommentCountSql());
                    tempRssItem.setCommentUrl(news2Content.getCommentUrlSql());
                    tempRssItem.setDescription(news2Content.getDescriptionSql());
                    tempRssItem.setLink(news2Content.getLinkSql());
                    tempRssItem.setPubDate(news2Content.getPubdateSql());
                    tempRssItem.setTitle(news2Content.getTitleSql());
                    tempRssItem.setThumbnailUrl(news2Content.getThumbnailUrlSql());

                    items.add(tempRssItem);
                }

                if (items.size() > 0) {
                    RssAdapter adapter = new RssAdapter(getActivity(), items);
                    listView.setAdapter(adapter);
                } else {
                    Toast.makeText(getActivity(), "An error occured while downloading the rss feed.",
                            Toast.LENGTH_LONG).show();
                }
            }
            progressBar.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Log.e(TAG, "onItemClick() was called");
        RssAdapter adapter = (RssAdapter) parent.getAdapter();
        RssItem item = (RssItem) adapter.getItem(position);

        Intent articleDetails = new Intent(getActivity(), ArticleActivity.class);
        articleDetails.putExtra("rssItem", item);
        startActivity(articleDetails);
    }

}