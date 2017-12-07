package akuangsaechao.volleyspot;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

public class MyContentProvider extends ContentProvider {

    private final static String TAG = MyContentProvider.class.getSimpleName();

    static final String PROVIDER = "akuangsaechao.myapplication.myprovider";
    static final String URL = "content://" + PROVIDER + "/volleySpots";
    static final Uri URI = Uri.parse(URL);

    static final String _ID = "_id";
    static final String TITLE = "title";
    static final String ADDRESS = "address";


    Context mContext;

    private static HashMap<String, String> VOLLEY_SPOT_PROJECTION_MAP;

    static final int VOLLEY_SPOT = 1;
    static final int VOLLEY_SPOT_ID = 2;


    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER, "volleyspots", VOLLEY_SPOT);
        uriMatcher.addURI(PROVIDER, "volleyspots/#", VOLLEY_SPOT_ID);
    }

    private SQLiteDatabase db;
    static final String DATABASE_NAME = "myprovider";
    static final String VOLLEY_SPOT_TABLE_NAME = "volleySpots";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE =
            "CREATE TABLE " + VOLLEY_SPOT_TABLE_NAME +
                    "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " title TEXT NOT NULL, " +
                    " address TEXT NOT NULL);";

    private static class DB extends SQLiteOpenHelper {

        DB(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            db.execSQL("DROP TABLE IF EXISTS " + VOLLEY_SPOT_TABLE_NAME);
            onCreate(db);
        }

    }

    public MyContentProvider() {
    }

    private void notifyChange(Uri uri){
        ContentResolver resolver = mContext.getContentResolver();
        if (resolver != null )
            resolver.notifyChange(uri, null);
    }

    private int getMatchedID(Uri uri){

        int matchedID = uriMatcher.match(uri);
        if (!(matchedID == VOLLEY_SPOT || matchedID == VOLLEY_SPOT_ID))
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        return matchedID;
    }

    private String getIdString(Uri uri){
        return (_ID + " = " + uri.getPathSegments().get(1));
    }

    private String getSelectionWithID(Uri uri, String selection){
        String sel_str = getIdString(uri);
        if (!TextUtils.isEmpty(selection)){
            sel_str += "AND (" + selection + ")";
        }

        return sel_str;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        Log.v(TAG, "content provider: delete()");

        int count = 0;

        String sel_str = (getMatchedID(uri) == VOLLEY_SPOT_ID) ? getSelectionWithID(uri, selection) : selection;

        count = db.delete(VOLLEY_SPOT_TABLE_NAME, sel_str, selectionArgs);

        notifyChange(uri);
        return count;
    }

    @Override
    public String getType(Uri uri) {

        Log.v(TAG, "content provider: getType()");

        if (getMatchedID(uri) == VOLLEY_SPOT)
            return "VOLLEY_SPOT";
        else
            return "VOLLEY_SPOT_ID";
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        Log.v(TAG, "content provider: insert()");

        long row = db.insert(VOLLEY_SPOT_TABLE_NAME, "", values);

        if (row > 0){
            Uri _uri = ContentUris.withAppendedId(URI, row);
            notifyChange(_uri);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public boolean onCreate() {

        Log.v(TAG, "content provider: onCreate()");

        mContext = getContext();

        if (mContext == null){
            Log.e(TAG, "Failed to retrieve the context");
            return false;
        }
        DB dbHelper = new DB(mContext);
        db = dbHelper.getWritableDatabase();
        if (db == null){
            Log.e(TAG, "Failed to create a writable database");
            return false;
        }
        return true;

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Log.v(TAG, "content provider: query()");

        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();

        if(getMatchedID(uri) == VOLLEY_SPOT){
            sqLiteQueryBuilder.setTables(VOLLEY_SPOT_TABLE_NAME);
            sqLiteQueryBuilder.setProjectionMap(VOLLEY_SPOT_PROJECTION_MAP);
        } else {
            sqLiteQueryBuilder.appendWhere(getIdString(uri));
        }

        if (sortOrder == null || sortOrder == ""){
            sortOrder = TITLE;
        }
        Cursor c = sqLiteQueryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        c.setNotificationUri(mContext.getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        Log.v(TAG, "content provider: update()");

        int count = 0;
        int matchedID = getMatchedID(uri);

        String sel_str = (matchedID == VOLLEY_SPOT_ID)?
                getSelectionWithID(uri, selection) : selection;

        count = db.update(VOLLEY_SPOT_TABLE_NAME, values, sel_str, selectionArgs);

        notifyChange(uri);
        return count;
    }

}
