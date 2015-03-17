package godlesz.de.golemdeit_news2;

import android.app.Application;
import android.content.Context;

public class ApplicationHelper extends Application {
    public static final String TAG = ApplicationHelper.class.getSimpleName();

    private static ApplicationHelper _instance;
    private static Context _appContext;

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
}
