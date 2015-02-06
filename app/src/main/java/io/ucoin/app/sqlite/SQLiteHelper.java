package io.ucoin.app.sqlite;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper implements Contract {

    private static final String INTEGER = " INTEGER ";
    private static final String REAL = " REAL ";
    private static final String TEXT = " TEXT ";
    private static final String UNIQUE = " UNIQUE ";
    private static final String NOTNULL = " NOT NULL";
    private static final String COMMA = ",";

    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_CURRENCY = "CREATE TABLE " + Currency.TABLE_NAME + "(" +
                Currency._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA +
                Currency.IDENTITY_ID + INTEGER + COMMA +
                Currency.CURRENCY_NAME + TEXT + NOTNULL + UNIQUE + COMMA +
                Currency.C + REAL + NOTNULL + COMMA +
                Currency.DT + INTEGER + NOTNULL + COMMA +
                Currency.UD0 + INTEGER + NOTNULL + COMMA +
                Currency.SIGDELAY + INTEGER + NOTNULL + COMMA +
                Currency.SIGVALIDITY + INTEGER + NOTNULL + COMMA +
                Currency.SIGQTY + INTEGER + NOTNULL + COMMA +
                Currency.SIGWOT + INTEGER + NOTNULL + COMMA +
                Currency.MSVALIDITY + INTEGER + NOTNULL + COMMA +
                Currency.STEPMAX + INTEGER + NOTNULL + COMMA +
                Currency.MEDIANTIMEBLOCKS + INTEGER + NOTNULL + COMMA +
                Currency.AVGGENTIME + INTEGER + NOTNULL + COMMA +
                Currency.DTDIFFEVAL + INTEGER + NOTNULL + COMMA +
                Currency.BLOCKSROT + INTEGER + NOTNULL + COMMA +
                Currency.PERCENTROT + REAL + NOTNULL + COMMA +
                Currency.MEMBERS_COUNT + INTEGER + NOTNULL + COMMA +
                Currency.FIRST_BLOCK_SIGNATURE + TEXT + UNIQUE + NOTNULL + COMMA +
                "FOREIGN KEY (" + Currency.IDENTITY_ID + ") REFERENCES " +
                Identity.TABLE_NAME + "(" + Identity._ID + ")" +
                ")";
        db.execSQL(CREATE_TABLE_CURRENCY);

        String CREATE_TABLE_IDENTITY = "CREATE TABLE " + Identity.TABLE_NAME + "(" +
                Identity._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA +
                Identity.CURRENCY_ID + INTEGER + NOTNULL + COMMA +
                Identity.WALLET_ID + INTEGER + NOTNULL + COMMA +
                Identity.UID + TEXT + NOTNULL + COMMA +
                Identity.SELF + TEXT + COMMA +
                Identity.TIMESTAMP + INTEGER + COMMA +
                "FOREIGN KEY (" + Identity.CURRENCY_ID + ") REFERENCES " +
                Currency.TABLE_NAME + "(" + Currency._ID + ")" + COMMA +
                "FOREIGN KEY (" + Identity.WALLET_ID + ") REFERENCES " +
                Wallet.TABLE_NAME + "(" + Wallet._ID + ")" +
                ")";
        db.execSQL(CREATE_TABLE_IDENTITY);

        String CREATE_TABLE_WALLET = "CREATE TABLE " + Wallet.TABLE_NAME + "(" +
                Wallet._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA +
                Wallet.CURRENCY_ID + TEXT + NOTNULL + COMMA +
                Wallet.SALT + TEXT + NOTNULL + COMMA +
                Wallet.PUBLIC_KEY + TEXT + NOTNULL + COMMA +
                Wallet.PRIVATE_KEY + TEXT + COMMA +
                Wallet.ALIAS + TEXT + COMMA +
                "FOREIGN KEY (" + Wallet.CURRENCY_ID + ") REFERENCES " +
                Currency.TABLE_NAME + "(" + Currency._ID + ")" +
                UNIQUE + "(" + Wallet.CURRENCY_ID + COMMA + Wallet.PUBLIC_KEY + ")" +
                ")";
        db.execSQL(CREATE_TABLE_WALLET);

        String CREATE_TABLE_PEER = "CREATE TABLE " + Peer.TABLE_NAME + "(" +
                Peer._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA +
                Peer.CURRENCY_ID + INTEGER + NOTNULL + COMMA +
                Peer.PUBLIC_KEY + TEXT + NOTNULL + COMMA +
                Peer.SIGNATURE + TEXT + NOTNULL + UNIQUE + COMMA +
                "FOREIGN KEY (" + Peer.CURRENCY_ID + ") REFERENCES " +
                Currency.TABLE_NAME + "(" + Currency._ID + ") ON DELETE CASCADE" +
                ")";
        db.execSQL(CREATE_TABLE_PEER);

        String CREATE_TABLE_ENDPOINT = "CREATE TABLE " + Endpoint.TABLE_NAME + "(" +
                Endpoint._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA +
                Endpoint.PEER_ID + INTEGER + NOTNULL + COMMA +
                Endpoint.URL + TEXT + COMMA +
                Endpoint.IPV4 + TEXT + COMMA +
                Endpoint.IPV6 + TEXT + COMMA +
                Endpoint.PORT + INTEGER + COMMA +
                "FOREIGN KEY (" + Endpoint.PEER_ID + ") REFERENCES " +
                Peer.TABLE_NAME + "(" + Peer._ID + ") ON DELETE CASCADE" +
                ")";
        db.execSQL(CREATE_TABLE_ENDPOINT);

        String CREATE_TABLE_SOURCE = "CREATE TABLE " + Source.TABLE_NAME + "(" +
                Source._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA +
                Source.WALLET_ID + INTEGER + NOTNULL + COMMA +
                Source.FINGERPRINT + TEXT + NOTNULL + COMMA +
                Source.TYPE + TEXT + NOTNULL + COMMA +
                Source.AMOUNT + INTEGER + NOTNULL + COMMA +
                Source.NUMBER + INTEGER + NOTNULL + COMMA +
                UNIQUE + "(" + Source.FINGERPRINT + ")" + COMMA +
                "FOREIGN KEY (" + Source.WALLET_ID + ") REFERENCES " +
                Wallet.TABLE_NAME + "(" + Wallet._ID + ")" +
                ")";
        db.execSQL(CREATE_TABLE_SOURCE);

        String CREATE_TABLE_TX = "CREATE TABLE " + Tx.TABLE_NAME + "(" +
                Tx._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA +
                Tx.CURRENCY_NAME + TEXT + NOTNULL + COMMA +
                Tx.COMMENT + TEXT + NOTNULL + COMMA +
                Tx.BLOCK + INTEGER + COMMA +
                "FOREIGN KEY (" + Tx.CURRENCY_NAME + ") REFERENCES " +
                Currency.TABLE_NAME + "(" + Currency.CURRENCY_NAME + ")" +
                ")";
        db.execSQL(CREATE_TABLE_TX);

        String CREATE_TABLE_TX_SIGNATURE = "CREATE TABLE " + TxSignature.TABLE_NAME + "(" +
                TxSignature._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA +
                TxSignature.TX_ID + INTEGER + NOTNULL + COMMA +
                TxSignature.VALUE + TEXT + NOTNULL + COMMA +
                TxSignature.ISSUER_ORDER + INTEGER + NOTNULL + COMMA +
                "FOREIGN KEY (" + TxSignature.TX_ID + ") REFERENCES " +
                Tx.TABLE_NAME + "(" + Tx._ID + ")" + COMMA +
                UNIQUE + "(" + TxSignature.TX_ID + COMMA + TxSignature.VALUE + ")" +
                ")";
        db.execSQL(CREATE_TABLE_TX_SIGNATURE);

        String CREATE_TABLE_TX_INPUT = "CREATE TABLE " + TxInput.TABLE_NAME + "(" +
                TxInput._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA +
                TxInput.TX_ID + INTEGER + NOTNULL + COMMA +
                TxInput.ISSUER_ORDER + INTEGER + NOTNULL + COMMA +
                TxInput.SOURCE_FINGERPRINT + TEXT + NOTNULL + COMMA +
                TxInput.KEY + TEXT + NOTNULL + COMMA +
                TxInput.SIGNATURE + TEXT + NOTNULL + COMMA +
                TxInput.AMOUNT + INTEGER + NOTNULL + COMMA +
                "FOREIGN KEY (" + TxInput.TX_ID + ") REFERENCES " +
                Tx.TABLE_NAME + "(" + Tx._ID + ")" + COMMA +
                "FOREIGN KEY (" + TxInput.SOURCE_FINGERPRINT + ") REFERENCES " +
                Source.TABLE_NAME + "(" + Source.FINGERPRINT + ")" + COMMA +
                "FOREIGN KEY (" + TxInput.ISSUER_ORDER + ") REFERENCES " +
                TxSignature.TABLE_NAME + "(" + TxSignature.ISSUER_ORDER + ")" +
                ")";
        db.execSQL(CREATE_TABLE_TX_INPUT);

        String CREATE_TABLE_TX_OUTPUT = "CREATE TABLE " + TxOutput.TABLE_NAME + "(" +
                TxOutput._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA +
                TxOutput.TX_ID + INTEGER + NOTNULL + COMMA +
                TxOutput.KEY + TEXT + NOTNULL + COMMA +
                TxOutput.AMOUNT + INTEGER + NOTNULL + COMMA +
                "FOREIGN KEY (" + TxOutput.TX_ID + ") REFERENCES " +
                Tx.TABLE_NAME + "(" + Tx._ID + ")" +
                ")";
        db.execSQL(CREATE_TABLE_TX_OUTPUT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            //enable FOREIGN KEY constraint
            db.execSQL("PRAGMA foreign_keys=ON");
        }
    }
}
