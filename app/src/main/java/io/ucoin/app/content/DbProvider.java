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

import io.ucoin.app.R;
import io.ucoin.app.sqlite.SQLiteHelper;
import io.ucoin.app.sqlite.SQLiteTable;
import io.ucoin.app.sqlite.SQLiteView;

public class DbProvider extends ContentProvider implements SQLiteTable {

    public static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int CURRENCY = 10;
    private static final int CURRENCY_ID = 11;
    private static final int IDENTITY = 20;
    private static final int IDENTITY_ID = 21;
    private static final int PEER = 30;
    private static final int PEER_ID = 31;
    private static final int ENDPOINT = 40;
    private static final int ENDPOINT_ID = 41;
    private static final int WALLET = 50;
    private static final int WALLET_ID = 51;
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
    private static final int MEMBER = 90;
    private static final int MEMBER_ID = 91;
    private static final int CERTIFICATION = 100;
    private static final int CERTIFICATION_ID = 101;
    private static final int BLOCK = 110;
    private static final int BLOCK_ID = 111;
    private static final int UD = 120;
    private static final int UD_ID = 121;
    private static final int MEMBERSHIP = 130;
    private static final int MEMBERSHIP_ID = 131;
    private static final int SELF_CERTIFICATION = 140;
    private static final int SELF_CERTIFICATION_ID = 141;
    private static final int CONTACT = 150;
    private static final int CONTACT_ID = 151;

    private static final int OPERATION = 200;
    private static final int OPERATION_ID = 201;


    public static Uri CURRENCY_URI;
    public static Uri IDENTITY_URI;
    public static Uri PEER_URI;
    public static Uri ENDPOINT_URI;
    public static Uri WALLET_URI;
    public static Uri SOURCE_URI;
    public static Uri TX_URI;
    public static Uri TX_ISSUER_URI;
    public static Uri TX_INPUT_URI;
    public static Uri TX_OUTPUT_URI;
    public static Uri TX_SIGNATURE_URI;
    public static Uri MEMBER_URI;
    public static Uri CERTIFICATION_URI;
    public static Uri BLOCK_URI;
    public static Uri UD_URI;
    public static Uri MEMBERSHIP_URI;
    public static Uri SELF_CERTIFICATION_URI;
    public static Uri CONTACT_URI;
    public static Uri OPERATION_URI;

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
        UD_URI = new Uri.Builder().scheme("content").authority(AUTHORITY)
                .path(context.getString(R.string.ud_uri)).build();
        MEMBERSHIP_URI = new Uri.Builder().scheme("content").authority(AUTHORITY)
                .path(context.getString(R.string.membership_uri)).build();
        SELF_CERTIFICATION_URI = new Uri.Builder().scheme("content").authority(AUTHORITY)
                .path(context.getString(R.string.self_certification_uri)).build();
        CONTACT_URI = new Uri.Builder().scheme("content").authority(AUTHORITY)
                .path(context.getString(R.string.contact_uri)).build();
        OPERATION_URI = new Uri.Builder().scheme("content").authority(AUTHORITY)
                .path(context.getString(R.string.operation_uri)).build();


        uriMatcher.addURI(AUTHORITY, context.getString(R.string.currency_uri), CURRENCY);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.currency_uri) + "#", CURRENCY_ID);

        uriMatcher.addURI(AUTHORITY, context.getString(R.string.identity_uri), IDENTITY);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.identity_uri) + "#", IDENTITY_ID);

        uriMatcher.addURI(AUTHORITY, context.getString(R.string.peer_uri), PEER);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.peer_uri) + "#", PEER_ID);

        uriMatcher.addURI(AUTHORITY, context.getString(R.string.endpoint_uri), ENDPOINT);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.endpoint_uri) + "#", ENDPOINT_ID);

        uriMatcher.addURI(AUTHORITY, context.getString(R.string.wallet_uri), WALLET);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.wallet_uri) + "#", WALLET_ID);

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

        uriMatcher.addURI(AUTHORITY, context.getString(R.string.ud_uri), UD);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.ud_uri) + "#", UD_ID);

        uriMatcher.addURI(AUTHORITY, context.getString(R.string.membership_uri), MEMBERSHIP);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.membership_uri) + "#", MEMBERSHIP_ID);

        uriMatcher.addURI(AUTHORITY, context.getString(R.string.self_certification_uri), SELF_CERTIFICATION);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.self_certification_uri) + "#", SELF_CERTIFICATION_ID);

        uriMatcher.addURI(AUTHORITY, context.getString(R.string.contact_uri), CONTACT);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.contact_uri) + "#", CONTACT_ID);

        uriMatcher.addURI(AUTHORITY, context.getString(R.string.operation_uri), OPERATION);
        uriMatcher.addURI(AUTHORITY, context.getString(R.string.operation_uri) + "#", OPERATION_ID);
    }

    @Override
    public boolean onCreate() {
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

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        Cursor cursor;

        switch (uriInt) {
            case CURRENCY:
                queryBuilder.setTables(SQLiteView.Currency.VIEW_NAME);
                cursor = queryBuilder.query(db, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case CURRENCY_ID:
                queryBuilder.setTables(SQLiteView.Currency.VIEW_NAME);
                cursor = queryBuilder.query(db, null,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null, null, null);
                break;
            case IDENTITY:
                queryBuilder.setTables(SQLiteView.Identity.VIEW_NAME);
                cursor = queryBuilder.query(db, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case IDENTITY_ID:
                queryBuilder.setTables(SQLiteView.Identity.VIEW_NAME);
                cursor = queryBuilder.query(db, null,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null, null, null);
                break;
            case PEER:
                queryBuilder.setTables(Peer.TABLE_NAME);
                cursor = queryBuilder.query(db, projection, selection,
                        selectionArgs, null, null, sortOrder);
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
                break;
            case ENDPOINT_ID:
                queryBuilder.setTables(Endpoint.TABLE_NAME);
                cursor = queryBuilder.query(db, null,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null, null, null);
                break;
            case WALLET:
                queryBuilder.setTables(SQLiteView.Wallet.VIEW_NAME);
                cursor = queryBuilder.query(db, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case WALLET_ID:
                queryBuilder.setTables(SQLiteView.Wallet.VIEW_NAME);
                cursor = queryBuilder.query(db, null,
                        Wallet._ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null, null, null);
                break;
            case SOURCE:
                queryBuilder.setTables(Source.TABLE_NAME);
                cursor = queryBuilder.query(db, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case SOURCE_ID:
                queryBuilder.setTables(Source.TABLE_NAME);
                cursor = queryBuilder.query(db, null,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null, null, null);
                break;
            case MEMBER:
                queryBuilder.setTables(SQLiteView.Member.VIEW_NAME);
                cursor = queryBuilder.query(db, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case MEMBER_ID:
                queryBuilder.setTables(SQLiteView.Member.VIEW_NAME);
                cursor = queryBuilder.query(db, null,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null, null, null);
                break;
            case CERTIFICATION:
                queryBuilder.setTables(SQLiteView.Certification.VIEW_NAME);
                cursor = queryBuilder.query(db, projection, selection,
                        selectionArgs, null, null, sortOrder);
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
                break;
            case BLOCK_ID:
                queryBuilder.setTables(Block.TABLE_NAME);
                cursor = queryBuilder.query(db, null,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null, null, null);
                break;
            case TX:
                queryBuilder.setTables(SQLiteView.Tx.VIEW_NAME);
                cursor = queryBuilder.query(db, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case TX_ID:
                queryBuilder.setTables(SQLiteView.Tx.VIEW_NAME);
                cursor = queryBuilder.query(db, null,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null, null, null);
                break;
            case TX_ISSUER:
                queryBuilder.setTables(TxIssuer.TABLE_NAME);
                cursor = queryBuilder.query(db, projection, selection,
                        selectionArgs, null, null, sortOrder);
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
                break;
            case TX_SIGNATURE_ID:
                queryBuilder.setTables(TxSignature.TABLE_NAME);
                cursor = queryBuilder.query(db, null,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null, null, null);
                break;
            case UD:
                queryBuilder.setTables(SQLiteView.Ud.VIEW_NAME);
                cursor = queryBuilder.query(db, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case UD_ID:
                queryBuilder.setTables(SQLiteView.Ud.VIEW_NAME);
                cursor = queryBuilder.query(db, null,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null, null, null);
                break;
            case MEMBERSHIP:
                queryBuilder.setTables(SQLiteView.Membership.VIEW_NAME);
                cursor = queryBuilder.query(db, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case MEMBERSHIP_ID:
                queryBuilder.setTables(SQLiteView.Membership.VIEW_NAME);
                cursor = queryBuilder.query(db, null,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null, null, null);
                break;
            case SELF_CERTIFICATION:
                queryBuilder.setTables(SelfCertification.TABLE_NAME);
                cursor = queryBuilder.query(db, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case SELF_CERTIFICATION_ID:
                queryBuilder.setTables(SelfCertification.TABLE_NAME);
                cursor = queryBuilder.query(db, null,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null, null, null);
                break;
            case CONTACT:
                queryBuilder.setTables(Contact.TABLE_NAME);
                cursor = queryBuilder.query(db, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case CONTACT_ID:
                queryBuilder.setTables(Contact.TABLE_NAME);
                cursor = queryBuilder.query(db, null,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null, null, null);
                break;
            case OPERATION:
                queryBuilder.setTables(SQLiteView.Operation.VIEW_NAME);
                cursor = queryBuilder.query(db, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case OPERATION_ID:
                queryBuilder.setTables(SQLiteView.Operation.VIEW_NAME);
                cursor = queryBuilder.query(db, null,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null, null, null);
                break;

            default:
                throw new RuntimeException("NO MATCH URI : " + uri.getQuery());
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

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
            case WALLET:
                id = db.insertWithOnConflict(Wallet.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                uri = Uri.parse(WALLET_URI + Long.toString(id));
                break;
            case SOURCE:
                id = db.insertWithOnConflict(Source.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                uri = Uri.parse(SOURCE_URI + Long.toString(id));
                break;
            case MEMBER:
                id = db.insertWithOnConflict(Member.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                uri = Uri.parse(MEMBER_URI + Long.toString(id));
                break;
            case CERTIFICATION:
                id = db.insertWithOnConflict(Certification.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                uri = Uri.parse(CERTIFICATION_URI + Long.toString(id));
                break;
            case BLOCK:
                id = db.insertWithOnConflict(Block.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                uri = Uri.parse(BLOCK_URI + Long.toString(id));
                break;
            case TX:
                id = db.insertWithOnConflict(Tx.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                uri = Uri.parse(TX_URI + Long.toString(id));
                break;
            case TX_ISSUER:
                id = db.insertWithOnConflict(TxIssuer.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_FAIL);
                uri = Uri.parse(TX_ISSUER_URI + Long.toString(id));
                break;
            case TX_INPUT:
                id = db.insertWithOnConflict(TxInput.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_FAIL);
                uri = Uri.parse(TX_INPUT_URI + Long.toString(id));
                break;
            case TX_OUTPUT:
                id = db.insertWithOnConflict(TxOutput.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_FAIL);
                uri = Uri.parse(TX_OUTPUT_URI + Long.toString(id));
                break;
            case TX_SIGNATURE:
                id = db.insertWithOnConflict(TxSignature.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_FAIL);
                uri = Uri.parse(TX_SIGNATURE_URI + Long.toString(id));
                break;
            case UD:
                id = db.insertWithOnConflict(Ud.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                uri = Uri.parse(UD_URI + Long.toString(id));
                break;
            case MEMBERSHIP:
                id = db.insertWithOnConflict(Membership.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                uri = Uri.parse(MEMBERSHIP_URI + Long.toString(id));
                break;
            case SELF_CERTIFICATION:
                id = db.insertWithOnConflict(SelfCertification.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                uri = Uri.parse(SELF_CERTIFICATION_URI + Long.toString(id));
                break;
            case CONTACT:
                id = db.insertWithOnConflict(Contact.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                uri = Uri.parse(CONTACT_URI + Long.toString(id));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        notifyChange(uriType);
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
            case BLOCK:
                deletedRows = db.delete(Block.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case BLOCK_ID:
                deletedRows = db.delete(Block.TABLE_NAME,
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
            case SELF_CERTIFICATION_ID:
                deletedRows = db.delete(SelfCertification.TABLE_NAME,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            case CONTACT_ID:
                deletedRows = db.delete(Contact.TABLE_NAME,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;

        }

       notifyChange(uriType);
        return deletedRows;
    }

    @Override
    public int update(
            Uri uri,
            ContentValues values,
            String selection,
            String[] selectionArgs) {

        SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();

        int uriType = uriMatcher.match(uri);
        int updatedRows;

        switch (uriType) {
            case CURRENCY_ID:
                updatedRows = db.update(Currency.TABLE_NAME,
                        values,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            case BLOCK_ID:
                updatedRows = db.update(Block.TABLE_NAME,
                        values,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            case WALLET_ID:
                updatedRows = db.update(Wallet.TABLE_NAME,
                        values,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            case IDENTITY_ID:
                updatedRows = db.update(Identity.TABLE_NAME,
                        values,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            case SOURCE_ID:
                updatedRows = db.update(Source.TABLE_NAME,
                        values,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            case MEMBER_ID:
                updatedRows = db.update(Member.TABLE_NAME,
                        values,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            case ENDPOINT_ID:
                updatedRows = db.update(Endpoint.TABLE_NAME,
                        values,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            case TX_ID:
                updatedRows = db.update(Tx.TABLE_NAME,
                        values,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            case UD_ID:
                updatedRows = db.update(Ud.TABLE_NAME,
                        values,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            case MEMBERSHIP_ID:
                updatedRows = db.update(Membership.TABLE_NAME,
                        values,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            case SELF_CERTIFICATION_ID:
                updatedRows = db.update(SelfCertification.TABLE_NAME,
                        values,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            case CONTACT_ID:
                updatedRows = db.update(Contact.TABLE_NAME,
                        values,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;

            case CERTIFICATION_ID:
                updatedRows = db.update(Certification.TABLE_NAME,
                        values,
                        BaseColumns._ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        notifyChange(uriType);
        return updatedRows;
    }

    private void notifyChange(int uriType) {
        switch (uriType) {
            case CURRENCY:
            case CURRENCY_ID:
                getContext().getContentResolver().notifyChange(CURRENCY_URI, null);
                break;
            case IDENTITY:
            case IDENTITY_ID:
                getContext().getContentResolver().notifyChange(IDENTITY_URI, null);
                notifyChange(CURRENCY);
                break;
            case PEER:
            case PEER_ID:
                getContext().getContentResolver().notifyChange(PEER_URI, null);
                notifyChange(CURRENCY);
                break;
            case ENDPOINT:
            case ENDPOINT_ID:
                getContext().getContentResolver().notifyChange(ENDPOINT_URI, null);
                notifyChange(PEER);
                break;
            case WALLET:
            case WALLET_ID:
                getContext().getContentResolver().notifyChange(WALLET_URI, null);
                notifyChange(CURRENCY);
                break;
            case SOURCE:
            case SOURCE_ID:
                getContext().getContentResolver().notifyChange(SOURCE_URI, null);
                notifyChange(WALLET);
                break;
            case TX:
            case TX_ID:
            case TX_ISSUER:
            case TX_ISSUER_ID:
            case TX_INPUT:
            case TX_INPUT_ID:
            case TX_OUTPUT:
            case TX_OUTPUT_ID:
            case TX_SIGNATURE:
            case TX_SIGNATURE_ID:
                getContext().getContentResolver().notifyChange(TX_URI, null);
                getContext().getContentResolver().notifyChange(OPERATION_URI, null);
                notifyChange(WALLET);
                break;
            case MEMBER:
            case MEMBER_ID:
                getContext().getContentResolver().notifyChange(MEMBER_URI, null);
                notifyChange(CERTIFICATION);
                notifyChange(CURRENCY);
                break;
            case CERTIFICATION:
            case CERTIFICATION_ID:
                getContext().getContentResolver().notifyChange(CERTIFICATION_URI, null);
                getContext().getContentResolver().notifyChange(IDENTITY_URI, null);
                break;
            case BLOCK:
            case BLOCK_ID:
                getContext().getContentResolver().notifyChange(UD_URI, null);
                getContext().getContentResolver().notifyChange(CURRENCY_URI, null);
                getContext().getContentResolver().notifyChange(WALLET_URI, null);
                getContext().getContentResolver().notifyChange(BLOCK_URI, null);
                notifyChange(CURRENCY);
                break;
            case UD:
            case UD_ID:
                getContext().getContentResolver().notifyChange(UD_URI, null);
                getContext().getContentResolver().notifyChange(OPERATION_URI, null);
                notifyChange(WALLET);
                break;
            case MEMBERSHIP:
            case MEMBERSHIP_ID:
                getContext().getContentResolver().notifyChange(MEMBERSHIP_URI, null);
                break;
            case SELF_CERTIFICATION:
            case SELF_CERTIFICATION_ID:
                getContext().getContentResolver().notifyChange(SELF_CERTIFICATION_URI, null);
                break;
            case CONTACT:
            case CONTACT_ID:
                getContext().getContentResolver().notifyChange(CONTACT_URI, null);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI type: " + uriType);
        }
    }
}