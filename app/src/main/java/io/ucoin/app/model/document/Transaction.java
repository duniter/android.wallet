package io.ucoin.app.model.document;

import java.util.ArrayList;

import io.ucoin.app.enumeration.DocumentType;
import io.ucoin.app.model.UcoinSource;
import io.ucoin.app.service.CryptoService;
import io.ucoin.app.technical.crypto.AddressFormatException;
import io.ucoin.app.technical.crypto.Base58;

public class Transaction {

    private final String mType = DocumentType.Transaction.name();
    private final String mVersion = Integer.toString(1);

    private String mCurrency;
    private ArrayList<String> mIssuers = new ArrayList<>();
    private ArrayList<String> mInputs = new ArrayList<>();
    private ArrayList<String> mOutputs = new ArrayList<>();
    private String mComment;
    private ArrayList<String> mSignatures = new ArrayList<>();

    public void setCurrency(String currency)
    {
        mCurrency = currency;
    }

    public void addIssuer(String publicKey) {
        mIssuers.add(publicKey);
    }

    public void addInput(UcoinSource source, int index) {
        String input = index +":" + source.type().name() + ":" + source.number() + ":" + source.fingerprint() + ":" + source.amount();
        mInputs.add(input);
    }

    public void addOuput(String publicKey, Long amount) {
        String output = publicKey + ":" + amount;
        mOutputs.add(output);
    }

    public  void setComment(String comment) {
        mComment = comment;
    }

    public void addSignature(String signature) {
        mSignatures.add(signature);
    }

    private String unsignedDocument() {
        String s = "Version: " + mVersion + "\n" +
                "Type: " + mType + "\n" +
                "Currency: " + mCurrency + "\n";

        s += "Issuers:" + "\n";
        for (String issuer : mIssuers) {
            s += issuer + "\n";
        }

        s += "Inputs:" + "\n";
        for (String input : mInputs) {
            s += input + "\n";
        }

        s += "Outputs:" + "\n";
        for (String output : mOutputs) {
            s += output + "\n";
        }

        s += "Comment: " + mComment + "\n";
        return s;
    }

    public String toString() {
        String s = "Version: " + mVersion + "\n" +
                "Type: " + mType + "\n" +
                "Currency: " + mCurrency + "\n";

        s += "Issuers:" + "\n";
        for (String issuer : mIssuers) {
            s += issuer + "\n";
        }

        s += "Inputs:" + "\n";
        for (String input : mInputs) {
            s += input + "\n";
        }

        s += "Outputs:" + "\n";
        for (String output : mOutputs) {
            s += output + "\n";
        }

        s += "Comment: " + mComment + "\n";

        for (String signature : mSignatures) {
            s += signature + "\n";
        }

        return s;
    }

    public String sign(String privateKey) throws AddressFormatException {
        CryptoService service = new CryptoService();
        return service.sign(unsignedDocument(), Base58.decode(privateKey));
    }
}
