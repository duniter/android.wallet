package io.ucoin.app.sqlite;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import io.ucoin.app.enums.CertificationType;

public class SQLiteHelper extends SQLiteOpenHelper implements SQLiteTable {

    private static final String INTEGER = " INTEGER ";
    private static final String REAL = " REAL ";
    private static final String TEXT = " TEXT ";
    private static final String UNIQUE = " UNIQUE ";
    private static final String NOTNULL = " NOT NULL ";
    private static final String NULL = "  NULL ";
    private static final String COMMA = ", ";
    private static final String AS = " AS ";
    private static final String DOT = ".";

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
                "FOREIGN KEY (" + Currency.IDENTITY_ID + ") REFERENCES " +
                Identity.TABLE_NAME + "(" + Identity._ID + ")" +
                ")";
        db.execSQL(CREATE_TABLE_CURRENCY);

        String CREATE_TABLE_BLOCK = "CREATE TABLE " + Block.TABLE_NAME + "(" +
                Block._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA +
                Block.CURRENCY_ID + INTEGER + NOTNULL + COMMA +
                Block.NUMBER + INTEGER + NOTNULL + COMMA +
                Block.MONETARY_MASS + INTEGER + NOTNULL + COMMA +
                Block.SIGNATURE + TEXT + NOTNULL + COMMA +
                "FOREIGN KEY (" + Block.CURRENCY_ID + ") REFERENCES " +
                Currency.TABLE_NAME + "(" + Currency._ID + ")" + COMMA +
                UNIQUE + "(" + Block.CURRENCY_ID + COMMA + Block.NUMBER + ")" +
                ")";
        db.execSQL(CREATE_TABLE_BLOCK);

        String CREATE_TABLE_IDENTITY = "CREATE TABLE " + Identity.TABLE_NAME + "(" +
                Identity._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA +
                Identity.CURRENCY_ID + INTEGER + UNIQUE + NOTNULL + COMMA +
                Identity.WALLET_ID + INTEGER + NOTNULL + COMMA +
                Identity.UID + TEXT + NOTNULL + COMMA +
                Identity.SELF + TEXT + COMMA +
                Identity.TIMESTAMP + INTEGER + COMMA +
                "FOREIGN KEY (" + Identity.CURRENCY_ID + ") REFERENCES " +
                Currency.TABLE_NAME + "(" + Currency._ID + ") ON DELETE CASCADE" + COMMA +
                "FOREIGN KEY (" + Identity.WALLET_ID + ") REFERENCES " +
                Wallet.TABLE_NAME + "(" + Wallet._ID + ")" +
                ")";
        db.execSQL(CREATE_TABLE_IDENTITY);

        String CREATE_TABLE_MEMBER = "CREATE TABLE " + Member.TABLE_NAME + "(" +
                Member._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA +
                Member.CURRENCY_ID + INTEGER + NOTNULL + COMMA +
                Member.UID + TEXT + NOTNULL + COMMA +
                Member.PUBLIC_KEY + TEXT + NOTNULL + COMMA +
                Member.IS_MEMBER + TEXT + NOTNULL + COMMA +
                Member.WAS_MEMBER + TEXT + NOTNULL + COMMA +
                Member.SELF + TEXT + NOTNULL + COMMA +
                Member.TIMESTAMP + INTEGER + NOTNULL + COMMA +
                "FOREIGN KEY (" + Member.CURRENCY_ID + ") REFERENCES " +
                Currency.TABLE_NAME + "(" + Currency._ID + ") ON DELETE CASCADE" + COMMA +
                UNIQUE + "(" + Member.CURRENCY_ID + COMMA + Member.UID + COMMA + Member.PUBLIC_KEY + ")" +
                ")";
        db.execSQL(CREATE_TABLE_MEMBER);

        String CREATE_TABLE_CERTIFICATION = "CREATE TABLE " + Certification.TABLE_NAME + "(" +
                Certification._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA +
                Certification.IDENTITY_ID + INTEGER + NOTNULL + COMMA +
                Certification.MEMBER_ID + INTEGER + NOTNULL + COMMA +
                Certification.TYPE + TEXT + NOTNULL +
                " CHECK (" + Certification.TYPE + " IN (\"" +
                CertificationType.BY.name() + "\", \"" +
                CertificationType.OF.name() + "\"))" + COMMA +
                Certification.BLOCK + INTEGER + NOTNULL + COMMA +
                Certification.MEDIAN_TIME + INTEGER + NOTNULL + COMMA +
                Certification.SIGNATURE + TEXT + UNIQUE + NOTNULL + COMMA +
                "FOREIGN KEY (" + Certification.IDENTITY_ID + ") REFERENCES " +
                Identity.TABLE_NAME + "(" + Identity._ID + ") ON DELETE CASCADE" + COMMA +
                "FOREIGN KEY (" + Certification.MEMBER_ID + ") REFERENCES " +
                Member.TABLE_NAME + "(" + Member._ID + ") ON DELETE CASCADE" +
                ")";
        db.execSQL(CREATE_TABLE_CERTIFICATION);

        String CREATE_TABLE_WALLET = "CREATE TABLE " + Wallet.TABLE_NAME + "(" +
                Wallet._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA +
                Wallet.CURRENCY_ID + INTEGER + NOTNULL + COMMA +
                Wallet.SALT + TEXT + NOTNULL + COMMA +
                Wallet.PUBLIC_KEY + TEXT + NOTNULL + COMMA +
                Wallet.PRIVATE_KEY + TEXT + COMMA +
                Wallet.ALIAS + TEXT + COMMA +
                "FOREIGN KEY (" + Wallet.CURRENCY_ID + ") REFERENCES " +
                Currency.TABLE_NAME + "(" + Currency._ID + ") ON DELETE CASCADE" + COMMA +
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
                Endpoint.PEER_ID + INTEGER + COMMA +
                Endpoint.PROTOCOL + TEXT + COMMA +
                Endpoint.URL + TEXT + COMMA +
                Endpoint.IPV4 + TEXT + COMMA +
                Endpoint.IPV6 + TEXT + COMMA +
                Endpoint.PORT + INTEGER + COMMA +
                "FOREIGN KEY (" + Endpoint.PEER_ID + ") REFERENCES " +
                Peer.TABLE_NAME + "(" + Peer._ID + ") ON DELETE CASCADE " + COMMA +
                " UNIQUE (" + Endpoint.PROTOCOL + COMMA + Endpoint.URL + COMMA + Endpoint.IPV4 + COMMA +
                Endpoint.IPV6 + COMMA + Endpoint.PORT +")" +
                ")";
        db.execSQL(CREATE_TABLE_ENDPOINT);

        String CREATE_TABLE_PENDING_ENDPOINT = "CREATE TABLE " + PendingEndpoint.TABLE_NAME + "(" +
                PendingEndpoint._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA +
                PendingEndpoint.ADDRESS + TEXT + NOTNULL +COMMA +
                PendingEndpoint.PORT + INTEGER + NOTNULL +
                ")";
        db.execSQL(CREATE_TABLE_PENDING_ENDPOINT);

        String CREATE_TABLE_SOURCE = "CREATE TABLE " + Source.TABLE_NAME + "(" +
                Source._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA +
                Source.WALLET_ID + INTEGER + NOTNULL + COMMA +
                Source.FINGERPRINT + TEXT + NOTNULL + COMMA +
                Source.TYPE + TEXT + NOTNULL + COMMA +
                Source.AMOUNT + INTEGER + NOTNULL + COMMA +
                Source.NUMBER + INTEGER + NOTNULL + COMMA +
                UNIQUE + "(" + Source.FINGERPRINT + ")" + COMMA +
                "FOREIGN KEY (" + Source.WALLET_ID + ") REFERENCES " +
                Wallet.TABLE_NAME + "(" + Wallet._ID + ") ON DELETE CASCADE" +
                ")";
        db.execSQL(CREATE_TABLE_SOURCE);

        String CREATE_TABLE_TX = "CREATE TABLE " + Tx.TABLE_NAME + "(" +
                Tx._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA +
                Tx.WALLET_ID + INTEGER + NOTNULL + COMMA +
                Tx.COMMENT + TEXT + NOTNULL + COMMA +
                Tx.HASH + TEXT + NOTNULL + COMMA +
                Tx.BLOCK + INTEGER + NOTNULL + COMMA +
                Tx.TIME + INTEGER + NOTNULL + COMMA +
                Tx.DIRECTION + TEXT + NOTNULL + COMMA +
                "FOREIGN KEY (" + Tx.WALLET_ID + ") REFERENCES " +
                Wallet.TABLE_NAME + "(" + Wallet._ID + ") ON DELETE CASCADE" + COMMA +
                UNIQUE + "(" + Tx.WALLET_ID + COMMA + Tx.HASH + ")" +
                ")";
        db.execSQL(CREATE_TABLE_TX);

        String CREATE_TABLE_TX_ISSUER = "CREATE TABLE " + TxIssuer.TABLE_NAME + "(" +
                TxIssuer._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA +
                TxIssuer.TX_ID + INTEGER + NOTNULL + COMMA +
                TxIssuer.PUBLIC_KEY + TEXT + NOTNULL + COMMA +
                TxIssuer.ISSUER_ORDER + INTEGER + NOTNULL + COMMA +
                "FOREIGN KEY (" + TxIssuer.TX_ID + ") REFERENCES " +
                Tx.TABLE_NAME + "(" + Tx._ID + ") ON DELETE CASCADE" + COMMA +
                UNIQUE + "(" + TxIssuer.TX_ID + COMMA + TxIssuer.PUBLIC_KEY + ")" + COMMA +
                UNIQUE + "(" + TxIssuer.TX_ID + COMMA + TxIssuer.ISSUER_ORDER + ")" +
                ")";
        db.execSQL(CREATE_TABLE_TX_ISSUER);


        String CREATE_TABLE_TX_SIGNATURE = "CREATE TABLE " + TxSignature.TABLE_NAME + "(" +
                TxSignature._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA +
                TxSignature.TX_ID + INTEGER + NOTNULL + COMMA +
                TxSignature.VALUE + TEXT + NOTNULL + COMMA +
                TxSignature.ISSUER_ORDER + INTEGER + NOTNULL + COMMA +
                "FOREIGN KEY (" + TxSignature.TX_ID + ") REFERENCES " +
                Tx.TABLE_NAME + "(" + Tx._ID + ") ON DELETE CASCADE" + COMMA +
                UNIQUE + "(" + TxSignature.TX_ID + COMMA + TxSignature.VALUE + ")" +
                ")";
        db.execSQL(CREATE_TABLE_TX_SIGNATURE);

        String CREATE_TABLE_TX_INPUT = "CREATE TABLE " + TxInput.TABLE_NAME + "(" +
                TxInput._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA +
                TxInput.TX_ID + INTEGER + NOTNULL + COMMA +
                TxInput.ISSUER_INDEX + INTEGER + NOTNULL + COMMA +
                TxInput.TYPE + TEXT + NOTNULL + COMMA +
                TxInput.NUMBER + INTEGER + NOTNULL + COMMA +
                TxInput.FINGERPRINT + TEXT + NOTNULL + COMMA +
                TxInput.AMOUNT + INTEGER + NOTNULL + COMMA +
                "FOREIGN KEY (" + TxInput.TX_ID + ") REFERENCES " +
                Tx.TABLE_NAME + "(" + Tx._ID + ") ON DELETE CASCADE" +
                ")";
        db.execSQL(CREATE_TABLE_TX_INPUT);

        String CREATE_TABLE_TX_OUTPUT = "CREATE TABLE " + TxOutput.TABLE_NAME + "(" +
                TxOutput._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA +
                TxOutput.TX_ID + INTEGER + NOTNULL + COMMA +
                TxOutput.PUBLIC_KEY + TEXT + NOTNULL + COMMA +
                TxOutput.AMOUNT + INTEGER + NOTNULL + COMMA +
                "FOREIGN KEY (" + TxOutput.TX_ID + ") REFERENCES " +
                Tx.TABLE_NAME + "(" + Tx._ID + ") ON DELETE CASCADE" +
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
            try {
                db.execSQL("DROP VIEW " + SQLiteView.Wallet.VIEW_NAME);
                db.execSQL("DROP VIEW " + SQLiteView.Certification.VIEW_NAME);
            } catch (SQLiteException e) {
                Log.d("SQLITEHELPER", e.getMessage());
            }
            String CREATE_VIEW_WALLET = "CREATE VIEW " + SQLiteView.Wallet.VIEW_NAME +
                    " AS SELECT " +
                    Wallet.TABLE_NAME + DOT + Wallet._ID + AS + SQLiteView.Wallet._ID + COMMA +
                    Wallet.TABLE_NAME + DOT + Wallet.CURRENCY_ID + AS + SQLiteView.Wallet.CURRENCY_ID + COMMA +
                    Currency.CURRENCY_NAME + AS + SQLiteView.Wallet.CURRENCY_NAME + COMMA +
                    Wallet.TABLE_NAME + DOT + Wallet.SALT + AS + SQLiteView.Wallet.SALT + COMMA +
                    Wallet.TABLE_NAME + DOT + Wallet.PUBLIC_KEY + AS + SQLiteView.Wallet.PUBLIC_KEY + COMMA +
                    Wallet.TABLE_NAME + DOT + Wallet.PRIVATE_KEY + AS + SQLiteView.Wallet.PRIVATE_KEY + COMMA +
                    Wallet.TABLE_NAME + DOT + Wallet.ALIAS + AS + SQLiteView.Wallet.ALIAS + COMMA +
                    " SUM (" + Source.TABLE_NAME + DOT + Source.AMOUNT + ")" + AS + SQLiteView.Wallet.QUANTITATIVE_AMOUNT + COMMA +
                    " ROUND ( CAST (" + Block.TABLE_NAME + DOT + Block.MONETARY_MASS + " AS REAL(5,2)) / SUM (" + Source.TABLE_NAME + DOT + Source.AMOUNT + "), 2)" + AS + SQLiteView.Wallet.RELATIVE_AMOUNT +
                    " FROM " + Wallet.TABLE_NAME +
                    " JOIN " + Currency.TABLE_NAME +
                    " ON " + Currency.TABLE_NAME + DOT + Currency._ID + "=" + Wallet.TABLE_NAME + DOT + Wallet.CURRENCY_ID +
                    " LEFT JOIN " + Block.TABLE_NAME +
                    " ON " + Currency.TABLE_NAME + DOT + Currency._ID + "=" + Block.TABLE_NAME + DOT + Block.CURRENCY_ID +
                    " LEFT JOIN (SELECT " + Block.CURRENCY_ID + COMMA + "MAX(" + Block.NUMBER + ") AS bNumber " +
                    " FROM " + Block.TABLE_NAME + " GROUP BY " + Block.CURRENCY_ID + ") AS B" +
                    " ON " + Block.TABLE_NAME + DOT + Block.CURRENCY_ID + "= B." + Block.CURRENCY_ID +
                    " AND " + Block.TABLE_NAME + DOT + Block.NUMBER + "= B.bNumber" +
                    " LEFT JOIN " + Source.TABLE_NAME +
                    " ON " + Wallet.TABLE_NAME + DOT + Wallet._ID + "=" + Source.TABLE_NAME + DOT + Source.WALLET_ID +
                    " GROUP BY " + Wallet.TABLE_NAME + DOT + Wallet._ID;

            db.execSQL(CREATE_VIEW_WALLET);

            String CREATE_VIEW_CERTIFICATION = "CREATE VIEW " + SQLiteView.Certification.VIEW_NAME +
                    " AS SELECT " +
                    Certification.TABLE_NAME + DOT + Certification._ID + AS + SQLiteView.Certification._ID + COMMA +
                    Certification.TABLE_NAME + DOT + Certification.IDENTITY_ID + AS + SQLiteView.Certification.IDENTITY_ID + COMMA +
                    Certification.TABLE_NAME + DOT + Certification.MEMBER_ID + AS + SQLiteView.Certification.MEMBER_ID + COMMA +
                    Certification.TABLE_NAME + DOT + Certification.TYPE + AS + SQLiteView.Certification.TYPE + COMMA +
                    Certification.TABLE_NAME + DOT + Certification.BLOCK + AS + SQLiteView.Certification.BLOCK + COMMA +
                    Certification.TABLE_NAME + DOT + Certification.MEDIAN_TIME + AS + SQLiteView.Certification.MEDIAN_TIME + COMMA +
                    Certification.TABLE_NAME + DOT + Certification.SIGNATURE + AS + SQLiteView.Certification.SIGNATURE + COMMA +
                    Member.TABLE_NAME + DOT + Member.UID + AS + SQLiteView.Certification.UID +
                    " FROM " + Certification.TABLE_NAME +
                    " JOIN " + Member.TABLE_NAME +
                    " ON " + Certification.TABLE_NAME + DOT + Certification.MEMBER_ID + "=" + Member.TABLE_NAME + DOT + Member._ID;
            db.execSQL(CREATE_VIEW_CERTIFICATION);

        }
    }

}
