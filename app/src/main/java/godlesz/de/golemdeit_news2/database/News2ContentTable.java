package godlesz.de.golemdeit_news2.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by AlexAsusN56V on 21.04.2015.
 */
public class News2ContentTable {
    // Database table
    public static final String TABLE_NEWS_CONTENT = "tablenewscontent";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_WHOLETEXT = "wholetext";
    public static final String COLUMN_PUBDATE = "pubdate";
    public static final String COLUMN_THUMBNAILURL = "thumbnailurl";
    public static final String COLUMN_COMMENTCOUNT = "commentcout";
    public static final String COLUMN_COMMENTURL = "commenturl";

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_NEWS_CONTENT
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TITLE + " text not null, "
            + COLUMN_LINK + " text not null, "
            + COLUMN_DESCRIPTION + " text, "
            + COLUMN_WHOLETEXT + " text, "
            + COLUMN_PUBDATE + " text, "
            + COLUMN_THUMBNAILURL + " text, "
            + COLUMN_COMMENTCOUNT + " text, "
            + COLUMN_COMMENTURL + " text"
            + ");";

    public static final String[] ALL_COLUMNS = new String[]{
            COLUMN_ID,
            COLUMN_TITLE,
            COLUMN_LINK,
            COLUMN_DESCRIPTION,
            COLUMN_WHOLETEXT,
            COLUMN_PUBDATE,
            COLUMN_THUMBNAILURL,
            COLUMN_COMMENTCOUNT,
            COLUMN_COMMENTURL
    };

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(News2ContentTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWS_CONTENT);
        onCreate(database);
    }
}
