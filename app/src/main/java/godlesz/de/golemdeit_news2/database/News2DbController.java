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
            if(mDbHelper == null){
                Log.e(TAG, " Constructor:News2DbController() Error mDbHelper is still NULL.");
            }
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

    public boolean checkIfGuidAlreadyExists(String guid){
        boolean guidExists = false;

        openDbWithReadPermission();

        int count = -1;
        String[] guidStringArray = { guid };
        Cursor c = null;
        try {
            String query = "SELECT COUNT(*) FROM " + News2ContentTable.TABLE_NEWS_CONTENT + " WHERE " + News2ContentTable.COLUMN_LINK + " = ?";

            c = db.rawQuery(query, guidStringArray);
            if (c.moveToFirst()) {
                count = c.getInt(0);
            }
            if (count > 0) {
                guidExists = true;
                //Log.e(TAG, "checkIfGuidAlreadyExists() guid=" + guid + " already exists!");
            }
        } finally {
            if (c != null) {
                c.close();
            }
            closeDB();
        }
        return guidExists;
    }

    public void insertHtmlArticleByGivenUrlLink(String urlLink, String wholeText){
        openDbWithReadPermission();
        String table = News2ContentTable.TABLE_NEWS_CONTENT;

        ContentValues values = new ContentValues();
        values.put(News2ContentTable.COLUMN_WHOLETEXT, wholeText);

        String whereClause = News2ContentTable.COLUMN_LINK + " like '" + urlLink + "'";
        String[] whereArgs = null;
        db.update(table, values, whereClause, whereArgs);

        closeDB();
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

    public News2Content getOneContentEntryByUrlId(String urlId){
        News2Content newsContent;

        openDbWithReadPermission();

        String[] projection =News2ContentTable.ALL_COLUMNS;
        String selection = News2ContentTable.COLUMN_LINK + " like '" + urlId + "'";
        String[] selectionArgs = null;
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

    public ArrayList<String> getAllUrlIds(){
        //Log.e(TAG, "getAllUrlIds() was called.");

        ArrayList<String> urlIdList = new ArrayList<String>();
        openDbWithReadPermission();

        String[] projection = new String[]{ News2ContentTable.COLUMN_LINK};
        String selection = null;
        String[] selectionArgs = null;
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
        while (!cursor.isAfterLast()) {
            String stringTemp = cursor.getString(0);
            urlIdList.add(stringTemp);
            cursor.moveToNext();
        }


        cursor.close();
        closeDB();

        return urlIdList;
    }

    public ArrayList<String> getAllUrlIdsWithEmptyArticleText(){
        //Log.e(TAG, "getAllUrlIdsWithEmptyArticleText() was called.");

        ArrayList<String> urlIdList = new ArrayList<String>();
        openDbWithReadPermission();

        String[] projection = new String[]{ News2ContentTable.COLUMN_LINK};
        String selection = News2ContentTable.COLUMN_WHOLETEXT + " IS NULL OR " + News2ContentTable.COLUMN_WHOLETEXT + " = \"\"";
        String[] selectionArgs = null;
        String sortOrder = null;


        Cursor cursor = db.query(News2ContentTable.TABLE_NEWS_CONTENT, // The table to query
                projection, // The columns to return
                selection, // The columns for the WHERE clause
                selectionArgs, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                sortOrder // The sort order
        );

        if (cursor != null){
            int countTemp = cursor.getCount();
            if(countTemp > 0){
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    String stringTemp = cursor.getString(0);
                    Log.e(TAG, "getAllUrlIdsWithEmptyArticleText() stringTemp = " + stringTemp);
                    urlIdList.add(stringTemp);
                    cursor.moveToNext();
                }
            }

            cursor.close();
        } else {
            Log.e(TAG, "getAllUrlIdsWithEmptyArticleText() cursor is NULL");
        }

        closeDB();

        if(urlIdList != null){
            Log.e(TAG, "getAllUrlIdsWithEmptyArticleText() urlIdList.size() = " + urlIdList.size());
        } else{
            Log.e(TAG, "getAllUrlIdsWithEmptyArticleText() urlIdList is NULL");
        }

        return urlIdList;
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
            // Anweisung zum Loeschen der Daten
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
        //Log.e(TAG, "getAllNews() was called");
        List<News2Content> allNews = new ArrayList<News2Content>();

        try {
            openDbWithReadPermission();

            String[] projection = News2ContentTable.ALL_COLUMNS;
            String selection = null;
            String[] selectionArgs = null;

            // How you want the results sorted in the resulting Cursor; example:
            String sortOrder = News2ContentTable.COLUMN_LINK + " DESC";
            //String sortOrder = null;

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
        } catch (Exception e){
            Log.e(TAG, "getAllNews()", e);
            allNews = null;
        } finally {
            closeDB();
        }
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
            if(mDbHelper == null){
                Log.e(TAG, "openDbWithReadPermission() mDbHelper is NULL.");
            }
            db = mDbHelper.getReadableDatabase();
            Log.d(TAG, "openDbWithREADPermission() was called. db was null.");
            if(db == null){
                Log.e(TAG, "openDbWithREADPermission() Error: db is still NULL.");
            }
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
