package io.ucoin.app.sqlite;

import android.provider.BaseColumns;

public interface SQLiteTable {

    public static final class Currency implements BaseColumns {
        public static final String TABLE_NAME = "currency";

        public static final String IDENTITY_ID = "identity_id";

        public static final String CURRENCY_NAME = "currency_name";
        public static final String C = "c";
        public static final String DT = "dt";
        public static final String UD0 = "ud0";
        public static final String SIGDELAY = "sig_delay";
        public static final String SIGVALIDITY = "sig_validity";
        public static final String SIGQTY = "sig_qty";
        public static final String SIGWOT = "sig_woT";
        public static final String MSVALIDITY = "ms_validity";
        public static final String STEPMAX = "step_max";
        public static final String MEDIANTIMEBLOCKS = "median_time_blocks";
        public static final String AVGGENTIME = "avg_gen_time";
        public static final String DTDIFFEVAL = "dt_diff_eval";
        public static final String BLOCKSROT = "blocks_rot";
        public static final String PERCENTROT = "percent_rot";
    }

    public static final class Block implements BaseColumns {
        public static final String TABLE_NAME = "block";
        public static final String CURRENCY_ID = "currency_id";
        public static final String NUMBER = "number";
        public static final String MONETARY_MASS = "monetary_mass";
        public static final String SIGNATURE = "signature";
    }

    public static final class Identity implements BaseColumns {
        public static final String TABLE_NAME = "Identity";
        public static final String CURRENCY_ID = "currency_id";
        public static final String WALLET_ID = "wallet_id";
        public static final String UID = "uid";
        public static final String SELF = "self";
        public static final String TIMESTAMP = "timestamp";
    }

    public static final class Member implements BaseColumns {
        public static final String TABLE_NAME = "member";
        public static final String CURRENCY_ID = "currency_id";
        public static final String UID = "uid";
        public static final String PUBLIC_KEY = "public_key";
        public static final String IS_MEMBER = "is_member";
        public static final String WAS_MEMBER = "was_member";
        public static final String SELF = "self";
        public static final String TIMESTAMP = "timestamp";
    }

    public static final class Certification implements BaseColumns {
        public static final String TABLE_NAME = "certification";
        public static final String IDENTITY_ID = "identity_id";
        public static final String MEMBER_ID = "member_id";
        public static final String TYPE = "type";
        public static final String BLOCK = "block";
        public static final String MEDIAN_TIME = "median_time";
        public static final String SIGNATURE = "signature";
    }

    public static final class Wallet implements BaseColumns {
        public static final String TABLE_NAME = "wallet";
        public static final String CURRENCY_ID = "currency_id";
        public static final String SALT = "salt";
        public static final String PUBLIC_KEY = "public_key";
        public static final String PRIVATE_KEY = "private_key";
        public static final String ALIAS = "alias";
    }

    public static final class Source implements BaseColumns {
        public static final String TABLE_NAME = "source";
        public static final String WALLET_ID = "wallet_id";
        public static final String TYPE = "type";
        public static final String FINGERPRINT = "fingerprint";
        public static final String AMOUNT = "amount";
        public static final String NUMBER = "number";
    }

    public static final class Peer implements BaseColumns {
        public static final String TABLE_NAME = "peer";
        public static final String CURRENCY_ID = "currency_id";
        public static final String PUBLIC_KEY = "public_key";
        public static final String SIGNATURE = "signature";
    }

    public static final class Endpoint implements BaseColumns {
        public static final String TABLE_NAME = "endpoint";
        public static final String PEER_ID = "peer_id";
        public static final String PROTOCOL = "protocol";
        public static final String URL = "url";
        public static final String IPV4 = "ipv4";
        public static final String IPV6 = "ipv6";
        public static final String PORT = "port";
    }

    public static final class PendingEndpoint implements BaseColumns {
        public static final String TABLE_NAME = "pending_endpoint";
        public static final String ADDRESS = "address";
        public static final String PORT = "port";
    }

    public static final class Tx implements BaseColumns {
        public static final String TABLE_NAME = "tx";
        public static final String WALLET_ID = "wallet_id";
        public static final String COMMENT = "comment";
        public static final String HASH = "hash";
        public static final String BLOCK = "block";
        public static final String TIME = "time";
        public static final String DIRECTION = "direction";
    }

    public static final class TxIssuer implements BaseColumns {
        public static final String TABLE_NAME = "tx_issuer";
        public static final String TX_ID = "tx_id";
        public static final String PUBLIC_KEY = "public_key";
        public static final String ISSUER_ORDER = "issuer_order";
    }

    public static final class TxSignature implements BaseColumns {
        public static final String TABLE_NAME = "tx_signature";
        public static final String TX_ID = "tx_id";
        public static final String VALUE = "value";
        public static final String ISSUER_ORDER = "issuer_order";
    }

    public static final class TxInput implements BaseColumns {
        public static final String TABLE_NAME = "tx_input";
        public static final String TX_ID = "tx_id";
        public static final String ISSUER_INDEX = "issuer_index";
        public static final String TYPE = "type";
        public static final String NUMBER = "number";
        public static final String FINGERPRINT = "fingerprint";
        public static final String AMOUNT = "amount";
    }

    public static final class TxOutput implements BaseColumns {
        public static final String TABLE_NAME = "tx_output";
        public static final String TX_ID = "tx_id";
        public static final String PUBLIC_KEY = "public_key";
        public static final String AMOUNT = "amount";
    }

}
