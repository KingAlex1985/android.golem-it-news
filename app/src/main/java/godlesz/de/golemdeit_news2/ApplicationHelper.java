package godlesz.de.golemdeit_news2;

import android.app.Application;
import android.content.Context;

import java.util.ArrayList;

import godlesz.de.golemdeit_news2.database.News2Content;
import godlesz.de.golemdeit_news2.database.News2DbController;

public class ApplicationHelper extends Application {
    public static final String TAG = ApplicationHelper.class.getSimpleName();

    private static ApplicationHelper _instance;
    private static Context _appContext;
    private static int _fontSize = 200;
    private static ArrayList<String> _urlIdList;
    private static ArrayList<String> urlIdWithEmptyTextList;
    private static ArrayList<News2Content> news2ContentArrayList;


    private static boolean _night_view = false;

    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;

        this.setAppContext(getApplicationContext());
    }

    public static ApplicationHelper getInstance(){
        return _instance;
    }

    public static Context getAppContext() {
        return _appContext;
    }


    public void setAppContext(Context appContext) {
        _appContext = appContext;
    }

    public static boolean is_night_view() {
        return _night_view;
    }

    public static void set_night_view(boolean _night_view) {
        ApplicationHelper._night_view = _night_view;
    }

    public static int get_fontSize() {
        return _fontSize;
    }

    public static void set_fontSize(int _fontSize) {
        ApplicationHelper._fontSize = _fontSize;
    }

    public static ArrayList<String> getUrlIdList() {
        return _urlIdList;
    }

    public static void setUrlIdList(ArrayList<String> urlIdList) {
        _urlIdList = urlIdList;
    }

    public static ArrayList<News2Content> getNews2ContentArrayList() {
        return news2ContentArrayList;
    }

    public static void setNews2ContentArrayList(ArrayList<News2Content> news2ContentArrayList) {
        ApplicationHelper.news2ContentArrayList = news2ContentArrayList;
    }

    public static ArrayList<String> getUrlIdWithEmptyTextList() {
        return urlIdWithEmptyTextList;
    }

    public static void setUrlIdWithEmptyTextList(ArrayList<String> urlIdWithEmptyTextList) {
        ApplicationHelper.urlIdWithEmptyTextList = urlIdWithEmptyTextList;
    }
}
