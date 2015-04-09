package io.ucoin.app.content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import io.ucoin.app.R;
import io.ucoin.app.sqlite.SQLiteHelper;
import io.ucoin.app.sqlite.SQLiteTable;
import io.ucoin.app.sqlite.SQLiteView;

public class Provider extends ContentProvider implements SQLiteTable {

    public static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int CURRENCY = 10;
    private static final int CURRENCY_ID = 11;
    private static final int IDENTITY = 20;
    private static final int IDENTITY_ID = 21;
    private static final int PEER = 30;
    private static final int PEER_ID = 31;
    private static final int ENDPOINT = 40;
    private static final int ENDPOINT_ID = 41;
    private static final int ENDPOINT_PENDING = 42;
    private static final int ENDPOINT_PENDING_ID = 43;
    private static final int WALLET = 50;
    private static final int WALLET_ID = 51;
    private static final int BALANCE_ID = 52;
    private static final int SOURCE = 60;
    private static final int SOURCE_ID = 61;
    private static final int TX = 70;
    private static final int TX_ID = 71;
    private static final int TX_ISSUER = 72;
    private static final int TX_ISSUER_ID = 73;
    private static final int TX_INPUT = 74;
    private static final int TX_INPUT_ID = 75;
    private static final int TX_OUTPUT = 76;
    private static final int TX_OUTPUT_ID = 77;
    private static final int TX_SIGNATURE = 78;
    private static final int TX_SIGNATURE_ID = 79;
    private static final int MEMBER = 80;
    private static final int MEMBER_ID = 81;
    private static final int CERTIFICATION = 90;
    private static final int CERTIFICATION_ID = 91;
    private static final int BLOCK = 100;
    private static final int BLOCK_ID = 101;


    public static Uri CURRENCY_URI;
    public static Uri IDENTITY_URI;
    public static Uri PEER_URI;
    public static Uri ENDPOINT_URI;
    public static Uri ENDPOINT_PENDING_URI;
    public static Uri WALLET_URI;
    public static Uri BALANCE_URI;
    public static Uri SOURCE_URI;
    public static Uri TX_URI;
    public static Uri TX_ISSUER_URI;
    public static Uri TX_INPUT_URI;
    public static Uri TX_OUTPUT_URI;
    public static Uri TX_SIGNATURE_URI;
    public static Uri MEMBER_URI;
    public static Uri CERTIFICATION_URI;
    public static Uri BLOCK_URI;

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
        ENDPOINT_PENDING_URI = new Uri.Builder().scheme("content").authority(AUTHORITY)
                .path(context.getString(R.string.endpoint_pending_uri)).build();
        WALLET_URI = new Uri.Builder().scheme("content").authority(AUTHORITY)
                .path(context.getString(R.string.wallet_uri)).build();
        BALANCE_URI = new Uri.Builder().scheme("content").authority(AUTHORITY)
                .path(context.getString(R.string.balance_uri)).build();

        SOURCE_URI = new Uri.Builder().scheme("content").authority(AUTHORITY)
                .path(context.getString(R.string.source_uri)).build();
        TX_URI = new Uri.Builder().scheme("content").authority(AUTHORITY)
                .path(context.getString(R.string.tx_uri)).build();
        TX_ISSUER_URI = new Uri.Builder().scheme("content").authority(AUTHORITY)
                .path(context.getString(R.string.tx_issuer_uri)).build();
        TX_INPUT_URI = new Uri.Builder().scheme("content").authority(AUTHORITY)
                .path(context.getString(R.string.tx_input_uri)).build();
        TX_OUTPUT_URI = new Uri.Builder().scheme("content").authority(AUTHORITY)
                .path(context.getString(R.string.tx_output_uri)).build();
        TX_SIGNATURE_URI = new Uri.Builder().scheme("content").authority(AUTHORITY)
                .path(context.getString(R.string.tx_signature_uri)).build();
        MEMBER_URI = new Uri.Builder().scheme("content").authority(AUTHORITY)
                .path(context.getString(R.string.member_uri)).build();
        CERTIFICATION_URI = new Uri.Builder().scheme("content").authority(AUTHORITY)
                .path(context.getString(R.string.certification_uri)).build();

        BLOCK_URI = new Uri.Builder().scheme("content").authority(AUTHORITY)
                .path(context.getString(R.string.block_uri)).build();


        uriMatcher.addURI(AUTHORITY, context.getString(R.string.currency_uri), CURRENCY);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.currency_uri) + "#", CURRENCY_ID);

        uriMatcher.addURI(AUTHORITY, context.getString(R.string.identity_uri), IDENTITY);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.identity_uri) + "#", IDENTITY_ID);

        uriMatcher.addURI(AUTHORITY, context.getString(R.string.peer_uri), PEER);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.peer_uri) + "#", PEER_ID);

        uriMatcher.addURI(AUTHORITY, context.getString(R.string.endpoint_uri), ENDPOINT);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.endpoint_uri) + "#", ENDPOINT_ID);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.endpoint_pending_uri), ENDPOINT_PENDING);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.endpoint_pending_uri) + "#", ENDPOINT_PENDING_ID);

        uriMatcher.addURI(AUTHORITY, context.getString(R.string.wallet_uri), WALLET);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.wallet_uri) + "#", WALLET_ID);

        uriMatcher.addURI(AUTHORITY, context.getString(R.string.balance_uri) + "#", BALANCE_ID);

        uriMatcher.addURI(AUTHORITY, context.getString(R.string.source_uri), SOURCE);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.source_uri) + "#", SOURCE_ID);

        uriMatcher.addURI(AUTHORITY, context.getString(R.string.tx_uri), TX);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.tx_uri) + "#", TX_ID);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.tx_issuer_uri), TX_ISSUER);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.tx_issuer_uri) + "#", TX_ISSUER_ID);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.tx_input_uri), TX_INPUT);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.tx_input_uri) + "#", TX_INPUT_ID);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.tx_output_uri), TX_OUTPUT);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.tx_output_uri) + "#", TX_OUTPUT_ID);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.tx_signature_uri), TX_SIGNATURE);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.tx_signature_uri) + "#", TX_SIGNATURE_ID);

        uriMatcher.addURI(AUTHORITY, context.getString(R.string.member_uri), MEMBER);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.member_uri) + "#", MEMBER_ID);

        uriMatcher.addURI(AUTHORITY, context.getString(R.string.certification_uri), CERTIFICATION);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.certification_uri) + "#", CERTIFICATION_ID);

        uriMatcher.addURI(AUTHORITY, context.getString(R.string.block_uri), BLOCK);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.block_uri) + "#", BLOCK_ID);
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

        //Log.d("PROVIDER", "query=" + uri.toString());

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
                        BaseColumns._ID + "=?",
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
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null, null, null);
                break;
            case PEER:
                queryBuilder.setTables(Peer.TABLE_NAME);
                cursor = queryBuilder.query(db, projection, selection,
                        selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case PEER_ID:
                queryBuilder.setTables(Peer.TABLE_NAME);
                cursor = queryBuilder.query(db, null,
                        BaseColumns._ID + "=?",
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
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null, null, null);
                break;
            case ENDPOINT_PENDING:
                queryBuilder.setTables(PendingEndpoint.TABLE_NAME);
                cursor = queryBuilder.query(db, projection, selection,
                        selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case ENDPOINT_PENDING_ID:
                queryBuilder.setTables(PendingEndpoint.TABLE_NAME);
                cursor = queryBuilder.query(db, null,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null, null, null);
                break;
            case WALLET:
                queryBuilder.setTables(SQLiteView.Wallet.VIEW_NAME);
                cursor = queryBuilder.query(db, projection, selection,
                        selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case WALLET_ID:
                queryBuilder.setTables(SQLiteView.Wallet.VIEW_NAME);
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
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null, null, null);
                break;
            case MEMBER:
                queryBuilder.setTables(Member.TABLE_NAME);
                cursor = queryBuilder.query(db, projection, selection,
                        selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case MEMBER_ID:
                queryBuilder.setTables(Member.TABLE_NAME);
                cursor = queryBuilder.query(db, null,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null, null, null);
                break;
            case CERTIFICATION:
                queryBuilder.setTables(SQLiteView.Certification.VIEW_NAME);
                cursor = queryBuilder.query(db, projection, selection,
                        selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case CERTIFICATION_ID:
                queryBuilder.setTables(SQLiteView.Certification.VIEW_NAME);
                cursor = queryBuilder.query(db, null,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null, null, null);
                break;
            case BLOCK:
                queryBuilder.setTables(Block.TABLE_NAME);
                cursor = queryBuilder.query(db, projection, selection,
                        selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case BLOCK_ID:
                queryBuilder.setTables(Block.TABLE_NAME);
                cursor = queryBuilder.query(db, null,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null, null, null);
                break;
            case TX:
                queryBuilder.setTables(Tx.TABLE_NAME);
                cursor = queryBuilder.query(db, projection, selection,
                        selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case TX_ID:
                queryBuilder.setTables(Tx.TABLE_NAME);
                cursor = queryBuilder.query(db, null,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null, null, null);
                break;
            case TX_ISSUER:
                queryBuilder.setTables(TxIssuer.TABLE_NAME);
                cursor = queryBuilder.query(db, projection, selection,
                        selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case TX_ISSUER_ID:
                queryBuilder.setTables(TxIssuer.TABLE_NAME);
                cursor = queryBuilder.query(db, null,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null, null, null);
                break;
            case TX_INPUT:
                queryBuilder.setTables(TxInput.TABLE_NAME);
                cursor = queryBuilder.query(db, projection, selection,
                        selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case TX_INPUT_ID:
                queryBuilder.setTables(TxInput.TABLE_NAME);
                cursor = queryBuilder.query(db, null,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null, null, null);
                break;
            case TX_OUTPUT:
                queryBuilder.setTables(TxOutput.TABLE_NAME);
                cursor = queryBuilder.query(db, projection, selection,
                        selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case TX_OUTPUT_ID:
                queryBuilder.setTables(TxOutput.TABLE_NAME);
                cursor = queryBuilder.query(db, null,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null, null, null);
                break;
            case TX_SIGNATURE:
                queryBuilder.setTables(TxSignature.TABLE_NAME);
                cursor = queryBuilder.query(db, projection, selection,
                        selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case TX_SIGNATURE_ID:
                queryBuilder.setTables(TxSignature.TABLE_NAME);
                cursor = queryBuilder.query(db, null,
                        BaseColumns._ID + "=?",
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
                id = db.insertWithOnConflict(Currency.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                uri = Uri.parse(CURRENCY_URI + Long.toString(id));
                break;
            case IDENTITY:
                id = db.insert(Identity.TABLE_NAME, null, values);
                uri = Uri.parse(IDENTITY_URI + Long.toString(id));
                break;
            case PEER:
                id = db.insertWithOnConflict(Peer.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                uri = Uri.parse(PEER_URI + Long.toString(id));
                break;
            case ENDPOINT:
                id = db.insertWithOnConflict(Endpoint.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                uri = Uri.parse(ENDPOINT_URI + Long.toString(id));
                break;
            case ENDPOINT_PENDING:
                id = db.insertWithOnConflict(PendingEndpoint.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                uri = Uri.parse(ENDPOINT_PENDING_URI + Long.toString(id));
                break;
            case WALLET:
                id = db.insertWithOnConflict(Wallet.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                uri = Uri.parse(WALLET_URI + Long.toString(id));
                break;
            case SOURCE:
                id = db.insert(Source.TABLE_NAME, null, values);
                uri = Uri.parse(SOURCE_URI + Long.toString(id));
                getContext().getContentResolver().notifyChange(WALLET_URI, null);
                break;
            case MEMBER:
                id = db.insert(Member.TABLE_NAME, null, values);
                uri = Uri.parse(MEMBER_URI + Long.toString(id));
                break;
            case CERTIFICATION:
                id = db.insert(Certification.TABLE_NAME, null, values);
                uri = Uri.parse(CERTIFICATION_URI + Long.toString(id));
                break;
            case BLOCK:
                id = db.insert(Block.TABLE_NAME, null, values);
                uri = Uri.parse(BLOCK_URI + Long.toString(id));
                break;
            case TX:
                id = db.insertWithOnConflict(Tx.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                uri = Uri.parse(TX_URI + Long.toString(id));
                break;
            case TX_ISSUER:
                id = db.insertWithOnConflict(TxIssuer.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                uri = Uri.parse(TX_ISSUER_URI + Long.toString(id));
                break;
            case TX_INPUT:
                id = db.insertWithOnConflict(TxInput.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                uri = Uri.parse(TX_INPUT_URI + Long.toString(id));
                break;
            case TX_OUTPUT:
                id = db.insertWithOnConflict(TxOutput.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                uri = Uri.parse(TX_OUTPUT_URI + Long.toString(id));
                break;
            case TX_SIGNATURE:
                id = db.insertWithOnConflict(TxSignature.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                uri = Uri.parse(TX_SIGNATURE_URI + Long.toString(id));
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
            case CURRENCY:
                deletedRows = db.delete(Currency.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case CURRENCY_ID:
                deletedRows = db.delete(Currency.TABLE_NAME,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            case IDENTITY:
                deletedRows = db.delete(Identity.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case IDENTITY_ID:
                deletedRows = db.delete(Identity.TABLE_NAME,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            case PEER:
                deletedRows = db.delete(Peer.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case PEER_ID:
                deletedRows = db.delete(Peer.TABLE_NAME,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            case ENDPOINT:
                deletedRows = db.delete(Endpoint.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case ENDPOINT_ID:
                deletedRows = db.delete(Endpoint.TABLE_NAME,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            case ENDPOINT_PENDING:
                deletedRows = db.delete(PendingEndpoint.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case ENDPOINT_PENDING_ID:
                deletedRows = db.delete(PendingEndpoint.TABLE_NAME,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            case WALLET:
                deletedRows = db.delete(Wallet.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case WALLET_ID:
                deletedRows = db.delete(Wallet.TABLE_NAME,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            case SOURCE:
                deletedRows = db.delete(Source.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case SOURCE_ID:
                deletedRows = db.delete(Source.TABLE_NAME,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            case MEMBER:
                deletedRows = db.delete(Member.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case MEMBER_ID:
                deletedRows = db.delete(Member.TABLE_NAME,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            case CERTIFICATION:
                deletedRows = db.delete(Certification.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case CERTIFICATION_ID:
                deletedRows = db.delete(Certification.TABLE_NAME,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            case TX:
                deletedRows = db.delete(Tx.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case TX_ID:
                deletedRows = db.delete(Tx.TABLE_NAME,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            case TX_ISSUER:
                deletedRows = db.delete(TxIssuer.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case TX_ISSUER_ID:
                deletedRows = db.delete(TxIssuer.TABLE_NAME,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            case TX_INPUT:
                deletedRows = db.delete(TxInput.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case TX_INPUT_ID:
                deletedRows = db.delete(TxInput.TABLE_NAME,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            case TX_OUTPUT:
                deletedRows = db.delete(TxOutput.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case TX_OUTPUT_ID:
                deletedRows = db.delete(TxOutput.TABLE_NAME,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            case TX_SIGNATURE:
                deletedRows = db.delete(TxSignature.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case TX_SIGNATURE_ID:
                deletedRows = db.delete(TxSignature.TABLE_NAME,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;

        }

        Log.d("PROVIDER", "delete=" + uri.toString() + "\ndeletedRows=" + deletedRows);

        getContext().getContentResolver().notifyChange(uri, null);
        return deletedRows;
    }

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
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            case SOURCE_ID:
                insertedRows = db.update(Source.TABLE_NAME,
                        values,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            case MEMBER_ID:
                insertedRows = db.update(Member.TABLE_NAME,
                        values,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            case ENDPOINT_ID:
                insertedRows = db.update(Endpoint.TABLE_NAME,
                        values,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                getContext().getContentResolver().notifyChange(uri, null, false);

                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        Log.d("PROVIDER", "authrority=" + uri.getAuthority() + " update=" + uri.toString() + " updatedRows=" + insertedRows);


        return insertedRows;
    }
}