package godlesz.de.golemdeit_news2;

import android.app.Application;
import android.content.Context;

public class ApplicationHelper extends Application {
    private static ApplicationHelper _instance;
    private static Context _appContext;

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
}
