package io.ucoin.app.sqlite;

import android.provider.BaseColumns;

public interface SQLiteView {

    public static final class Wallet implements BaseColumns {
        public static final String VIEW_NAME = "walletView";
        public static final String CURRENCY_ID = SQLiteTable.Wallet.CURRENCY_ID;
        public static final String CURRENCY_NAME = SQLiteTable.Currency.CURRENCY_NAME;
        public static final String SALT = SQLiteTable.Wallet.SALT;
        public static final String PUBLIC_KEY = SQLiteTable.Wallet.PUBLIC_KEY;
        public static final String PRIVATE_KEY = SQLiteTable.Wallet.PRIVATE_KEY;
        public static final String ALIAS = SQLiteTable.Wallet.ALIAS;
        public static final String QUANTITATIVE_AMOUNT = "quantitative_amount";
        public static final String RELATIVE_AMOUNT = "relative_amount";
    }

    public static final class Certification implements BaseColumns {
        public static final String VIEW_NAME = "certificationView";
        public static final String IDENTITY_ID = SQLiteTable.Certification.IDENTITY_ID;
        public static final String MEMBER_ID = SQLiteTable.Certification.MEMBER_ID;
        public static final String TYPE = SQLiteTable.Certification.TYPE;
        public static final String BLOCK = SQLiteTable.Certification.BLOCK;
        public static final String MEDIAN_TIME = SQLiteTable.Certification.MEDIAN_TIME;
        public static final String SIGNATURE = SQLiteTable.Certification.SIGNATURE;
        public static final String UID = SQLiteTable.Member.UID;
    }
}
