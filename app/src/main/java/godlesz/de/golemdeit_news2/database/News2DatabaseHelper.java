package godlesz.de.golemdeit_news2.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by AlexAsusN56V on 21.04.2015.
 */
public class News2DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "newstable.db";
    private static final int DATABASE_VERSION = 1;

    public News2DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        News2ContentTable.onCreate(database);
    }

    // Method is called during an upgrade of the database,
    // e.g. if you increase the database version
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {
        News2ContentTable.onUpgrade(database, oldVersion, newVersion);
    }
}
