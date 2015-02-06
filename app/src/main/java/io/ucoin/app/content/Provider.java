package io.ucoin.app.content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import io.ucoin.app.R;
import io.ucoin.app.sqlite.Contract;
import io.ucoin.app.sqlite.SQLiteHelper;

/*
 * Define an implementation of ContentProvider that stubs out
 * all methods
 */
public class Provider extends ContentProvider implements Contract {

    public static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int CURRENCY = 10;
    private static final int CURRENCY_ID = 11;
    private static final int IDENTITY = 20;
    private static final int IDENTITY_ID = 21;
    private static final int NODE = 30;
    private static final int NODE_ID = 31;
    private static final int ENDPOINT = 40;
    private static final int ENDPOINT_ID = 41;
    private static final int WALLET = 50;
    private static final int WALLET_ID = 51;
    private static final int BALANCE_ID = 52;
    private static final int SOURCE = 60;
    private static final int SOURCE_ID = 61;
    private static final int TX = 70;
    private static final int TX_ID = 71;


    public static Uri CURRENCY_URI;
    public static Uri IDENTITY_URI;
    public static Uri PEER_URI;
    public static Uri ENDPOINT_URI;
    public static Uri WALLET_URI;
    public static Uri BALANCE_URI;
    public static Uri SOURCE_URI;
    public static Uri TX_URI;

    private SQLiteHelper mSQLiteHelper;

    public static void initUris(Context context) {

        String AUTHORITY = context.getString(R.string.AUTHORITY);

        CURRENCY_URI = new Uri.Builder().scheme("content").authority(AUTHORITY)
                .path(context.getString(R.string.currency_uri)).build();
        IDENTITY_URI = new Uri.Builder().scheme("content").authority(AUTHORITY)
                .path(context.getString(R.string.identity_uri)).build();
        PEER_URI = new Uri.Builder().scheme("content").authority(AUTHORITY)
                .path(context.getString(R.string.peer_uri)).build();
        ENDPOINT_URI = new Uri.Builder().scheme("content").authority(AUTHORITY)
                .path(context.getString(R.string.endpoint_uri)).build();
        WALLET_URI = new Uri.Builder().scheme("content").authority(AUTHORITY)
                .path(context.getString(R.string.wallet_uri)).build();
        BALANCE_URI = new Uri.Builder().scheme("content").authority(AUTHORITY)
                .path(context.getString(R.string.balance_uri)).build();

        SOURCE_URI = new Uri.Builder().scheme("content").authority(AUTHORITY)
                .path(context.getString(R.string.source_uri)).build();
        TX_URI = new Uri.Builder().scheme("content").authority(AUTHORITY)
                .path(context.getString(R.string.tx_uri)).build();


        uriMatcher.addURI(AUTHORITY, context.getString(R.string.currency_uri), CURRENCY);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.currency_uri) + "#", CURRENCY_ID);

        uriMatcher.addURI(AUTHORITY, context.getString(R.string.identity_uri), IDENTITY);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.identity_uri) + "#", IDENTITY_ID);

        uriMatcher.addURI(AUTHORITY, context.getString(R.string.peer_uri), NODE);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.peer_uri) + "#", NODE_ID);

        uriMatcher.addURI(AUTHORITY, context.getString(R.string.endpoint_uri), ENDPOINT);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.endpoint_uri) + "#", ENDPOINT_ID);

        uriMatcher.addURI(AUTHORITY, context.getString(R.string.wallet_uri), WALLET);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.wallet_uri) + "#", WALLET_ID);

        uriMatcher.addURI(AUTHORITY, context.getString(R.string.balance_uri) + "#", BALANCE_ID);

        uriMatcher.addURI(AUTHORITY, context.getString(R.string.source_uri), SOURCE);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.source_uri) + "#", SOURCE_ID);

        uriMatcher.addURI(AUTHORITY, context.getString(R.string.tx_uri), TX);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.tx_uri) + "#", TX_ID);
    }

    @Override
    public boolean onCreate() {
        Log.d("PROVIDER", "onCreate()");
        Context context = getContext();
        initUris(context);
        mSQLiteHelper = new SQLiteHelper(context, context.getString(R.string.DBNAME),
                null, context.getResources().getInteger(R.integer.DBVERSION));

        return true;
    }

    @Override
    public Cursor query(
            Uri uri,
            String[] projection,
            String selection,
            String[] selectionArgs,
            String sortOrder) {
        SQLiteDatabase db = mSQLiteHelper.getReadableDatabase();
        int uriInt = uriMatcher.match(uri);

        Log.d("PROVIDER", "query=" + uri.toString());

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        Cursor cursor = null;

        switch (uriInt) {
            case CURRENCY:
                queryBuilder.setTables(Currency.TABLE_NAME);
                cursor = queryBuilder.query(db, projection, selection,
                        selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case CURRENCY_ID:
                queryBuilder.setTables(Currency.TABLE_NAME);
                cursor = queryBuilder.query(db, null,
                        Currency._ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null, null, null);
                break;
            case IDENTITY:
                queryBuilder.setTables(Identity.TABLE_NAME);
                cursor = queryBuilder.query(db, projection, selection,
                        selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case IDENTITY_ID:
                queryBuilder.setTables(Identity.TABLE_NAME);
                cursor = queryBuilder.query(db, null,
                        Identity._ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null, null, null);
                break;
            case NODE:
                queryBuilder.setTables(Peer.TABLE_NAME);
                cursor = queryBuilder.query(db, projection, selection,
                        selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case NODE_ID:
                queryBuilder.setTables(Peer.TABLE_NAME);
                cursor = queryBuilder.query(db, null,
                        Currency._ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null, null, null);
                break;
            case ENDPOINT:
                queryBuilder.setTables(Endpoint.TABLE_NAME);
                cursor = queryBuilder.query(db, projection, selection,
                        selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case ENDPOINT_ID:
                queryBuilder.setTables(Endpoint.TABLE_NAME);
                cursor = queryBuilder.query(db, null,
                        Currency._ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null, null, null);
                break;
            case WALLET:
                queryBuilder.setTables(Wallet.TABLE_NAME);
                cursor = queryBuilder.query(db, projection, selection,
                        selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case WALLET_ID:
                queryBuilder.setTables(Wallet.TABLE_NAME);
                cursor = queryBuilder.query(db, null,
                        Wallet._ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null, null, null);
                break;
            case BALANCE_ID:
                queryBuilder.setTables(Source.TABLE_NAME);
                cursor = queryBuilder.query(db,
                        new String[]{
                                Source.WALLET_ID,
                                "SUM(" + Source.AMOUNT + ") AS balance"
                        },
                        Source.WALLET_ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null, null, null
                );
                break;
            case SOURCE:
                queryBuilder.setTables(Source.TABLE_NAME);
                cursor = queryBuilder.query(db, projection, selection,
                        selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case SOURCE_ID:
                queryBuilder.setTables(Source.TABLE_NAME);
                cursor = queryBuilder.query(db, null,
                        Source._ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null, null, null);
                break;
            default:
                Log.d("PROVIDER", "NO MATCH URI=" + uri.getQuery());
                break;
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d("PROVIDER", "insert=" + uri.toString());

        SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
        int uriType = uriMatcher.match(uri);
        long id;
        switch (uriType) {
            case CURRENCY:
                id = db.insert(Currency.TABLE_NAME, null, values);
                uri = Uri.parse(CURRENCY_URI + Long.toString(id));
                break;
            case IDENTITY:
                id = db.insert(Identity.TABLE_NAME, null, values);
                uri = Uri.parse(IDENTITY_URI + Long.toString(id));
                break;
            case NODE:
                id = db.insert(Peer.TABLE_NAME, null, values);
                uri = Uri.parse(PEER_URI + Long.toString(id));
                break;
            case ENDPOINT:
                id = db.insert(Endpoint.TABLE_NAME, null, values);
                uri = Uri.parse(ENDPOINT_URI + Long.toString(id));
                break;
            case WALLET:
                id = db.insert(Wallet.TABLE_NAME, null, values);
                uri = Uri.parse(WALLET_URI + Long.toString(id));
                break;
            case SOURCE:
                id = db.insert(Source.TABLE_NAME, null, values);
                uri = Uri.parse(SOURCE_URI + Long.toString(id));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return uri;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();

        int uriType = uriMatcher.match(uri);
        int deletedRows = 0;

        switch (uriType) {
            case CURRENCY_ID:
                deletedRows = db.delete(Currency.TABLE_NAME,
                        Currency._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            case IDENTITY_ID:
                deletedRows = db.delete(Currency.TABLE_NAME,
                        Identity._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            case NODE_ID:
                deletedRows = db.delete(Peer.TABLE_NAME,
                        Peer._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            case ENDPOINT_ID:
                Log.w("PROVIDER", "Not handled: Endpoints shall be deleted on node deletion.");
                break;
            case WALLET_ID:
                deletedRows = db.delete(Wallet.TABLE_NAME,
                        Wallet._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            case SOURCE_ID:
                deletedRows = db.delete(Source.TABLE_NAME,
                        Source._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
        }

        Log.d("PROVIDER", "delete=" + uri.toString() + "deletedRows=" + deletedRows);

        getContext().getContentResolver().notifyChange(uri, null);
        return deletedRows;
    }

    /*
     * update() always returns "no rows affected" (0)
     */
    public int update(
            Uri uri,
            ContentValues values,
            String selection,
            String[] selectionArgs) {

        SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();

        int uriType = uriMatcher.match(uri);
        int insertedRows;

        switch (uriType) {
            case CURRENCY_ID:
                insertedRows = db.update(Currency.TABLE_NAME,
                        values,
                        Currency._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            case SOURCE_ID:
                insertedRows = db.update(Source.TABLE_NAME,
                        values,
                        Source._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        Log.d("PROVIDER", "update=" + uri.toString() + " updatedRows=" + insertedRows);


        return insertedRows;
    }
}