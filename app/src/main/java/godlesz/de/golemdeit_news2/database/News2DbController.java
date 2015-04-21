package godlesz.de.golemdeit_news2.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AlexAsusN56V on 21.04.2015.
 *
 * Useing this class as a DAO
 */
public class News2DbController {
    private static final String TAG = News2DbController.class.getSimpleName();

    private News2DatabaseHelper mDbHelper;
    private SQLiteDatabase db;

    public News2DbController(Context context){
        if (mDbHelper == null){
            mDbHelper = new News2DatabaseHelper(context);
        }
    }


    public long insertNewsDataInDatabase(News2Content newsContent){
        long insertId = -1;
        if(newsContent != null){
            // Gets the data repository in write mode
            openDbWithWriteAndReadPermission();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();

            values.put(News2ContentTable.COLUMN_TITLE, newsContent.getTitleSql());
            values.put(News2ContentTable.COLUMN_LINK, newsContent.getLinkSql());
            values.put(News2ContentTable.COLUMN_DESCRIPTION, newsContent.getDescriptionSql());
            values.put(News2ContentTable.COLUMN_WHOLETEXT, newsContent.getWholetextSql());
            values.put(News2ContentTable.COLUMN_PUBDATE, newsContent.getPubdateSql());
            values.put(News2ContentTable.COLUMN_THUMBNAILURL, newsContent.getThumbnailUrlSql());
            values.put(News2ContentTable.COLUMN_COMMENTCOUNT, newsContent.getCommentCountSql());
            values.put(News2ContentTable.COLUMN_COMMENTURL, newsContent.getCommentUrlSql());

            insertId = db.insert(News2ContentTable.TABLE_NEWS_CONTENT,null, values);
        }
        return insertId;
    }

    public News2Content getOneContentEntryById(long insertId){
        News2Content newsContent;

        openDbWithReadPermission();

        String[] projection = News2ContentTable.ALL_COLUMNS;
        String selection = News2ContentTable.COLUMN_ID + " = " + insertId;
        String[] selectionArgs = null;

        // How you want the results sorted in the resulting Cursor; example:
        // String sortOrder = News2ContentTable.COLUMN_TITLE + " DESC";
        String sortOrder = null;

        Cursor cursor = db.query(News2ContentTable.TABLE_NEWS_CONTENT, // The table to query
                projection, // The columns to return
                selection, // The columns for the WHERE clause
                selectionArgs, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                sortOrder // The sort order
        );

        cursor.moveToFirst();
        newsContent = cursorToNews2Content(cursor);

        cursor.close();
        closeDB();

        return newsContent;
    }

    public long deleteOneContentEntryById(long insertId){
        long amountOfDeletedEntries = -1;

        try {
            // Gets the data repository in write mode
            openDbWithWriteAndReadPermission();

            // Define 'where' part of query.
            String selection = News2ContentTable.COLUMN_ID + " LIKE ?";

            // Specify arguments in placeholder order.
            String[] selectionArgs = { String.valueOf(insertId) };

            amountOfDeletedEntries = db.delete(News2ContentTable.TABLE_NEWS_CONTENT, selection, selectionArgs);
        } catch (SQLiteException e) {
            Log.e(TAG, "deleteOneContentEntryById()", e);
            return -1;
        } finally {
            Log.d(TAG, "deleteOneContentEntryById() succesfull. Amount of deleted entries: " + amountOfDeletedEntries);
            closeDB();
        }
        return amountOfDeletedEntries;
    }

    /************************************************
     * Delete all entries in database
     ************************************************/
    public long deleteAllEntriesInDatabase() {
        long amountOfDeletedEntries = -1;
        try {
            // Gets the data repository in write mode
            openDbWithWriteAndReadPermission();
            // Anweisung zum Löschen der Daten
            amountOfDeletedEntries = db.delete(News2ContentTable.TABLE_NEWS_CONTENT, null, null);
        } catch (SQLiteException e) {
            Log.e(TAG, "deleteEntriesInDatabase()", e);
            return -1;
        } finally {
            Log.d(TAG, "deleteEntriesInDatabase() succesfull. Amount of deleted entries: " + amountOfDeletedEntries);
            closeDB();
        }
        return amountOfDeletedEntries;
    }

    public List<News2Content> getAllNews(){
        List<News2Content> allNews = new ArrayList<News2Content>();

        openDbWithReadPermission();

        String[] projection = News2ContentTable.ALL_COLUMNS;
        String selection = null;
        String[] selectionArgs = null;

        // How you want the results sorted in the resulting Cursor; example:
        // String sortOrder = News2ContentTable.COLUMN_TITLE + " DESC";
        String sortOrder = null;

        Cursor cursor = db.query(News2ContentTable.TABLE_NEWS_CONTENT, // The table to query
                projection, // The columns to return
                selection, // The columns for the WHERE clause
                selectionArgs, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                sortOrder // The sort order
                );

        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            News2Content newsContent = cursorToNews2Content(cursor);
            allNews.add(newsContent);
            cursor.moveToNext();
        }

        cursor.close();
        closeDB();
        return allNews;
    }


    private News2Content cursorToNews2Content(Cursor cursor){
        News2Content newsContent = new News2Content();

        newsContent.setIdSql(cursor.getLong(0));
        newsContent.setTitleSql(cursor.getString(1));
        newsContent.setLinkSql(cursor.getString(2));
        newsContent.setDescriptionSql(cursor.getString(3));
        newsContent.setWholetextSql(cursor.getString(4));
        newsContent.setPubdateSql(cursor.getString(5));
        newsContent.setThumbnailUrlSql(cursor.getString(6));
        newsContent.setCommentCountSql(cursor.getString(7));
        newsContent.setCommentUrlSql(cursor.getString(8));

        return newsContent;
    }


    /***********************************************************
     * Öffnet eine Datenbankverbindung mit Lese-Rechten.
     *
     * @throws SQLException
     **********************************************************/
    public void openDbWithReadPermission() throws SQLException {
        if (db == null) {
            db = mDbHelper.getReadableDatabase();
            Log.d(TAG, "openDbWithREADPermission() was called. db was null.");
        } else {
            if (db.isOpen() == false) {
                db = mDbHelper.getReadableDatabase();
                Log.d(TAG, "openDbWithREADPermission() was called. db is not null but wass closed. db is open now.");
            } else {
                Log.d(TAG, "openDbWithREADPermission() was called. db is still open.");
            }
        }
    }

    /*****************************************************************
     * Öffnet eine Datenbankverbindung mit Schreib- und Leserechten.
     *
     * @throws SQLException
     ******************************************************************/
    public void openDbWithWriteAndReadPermission() throws SQLException {
        if (db == null) {
            db = mDbHelper.getWritableDatabase();
            Log.d(TAG, "openDbWithWriteAndReadPermission() was called. db == null.");
        } else {
            if (db.isOpen() == false) {
                db = mDbHelper.getWritableDatabase();
                Log.d(TAG, "openDbWithWriteAndReadPermission() was called. db is not null but wass closed. db is open now.");
            } else {
                Log.d(TAG, "openDbWithWriteAndReadPermission() was called. db is still open.");
            }
        }
    }

    /******************************************************************
     * Schliesst eine offene Datenbank
     *****************************************************************/
    public void closeDB() {
        if (mDbHelper != null) {
            mDbHelper.close();
            Log.d(TAG, "closeDB() was called. Datenbank wird geschlossen.");
        }
    }
}
