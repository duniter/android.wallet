package io.ucoin.app;
import io.ucoin.app.model.UcoinCurrencies;

public class Application extends android.app.Application{

    private UcoinCurrencies mCurrencies;

    public Application() {
        super();
        mCurrencies = null;
    }

    public UcoinCurrencies getCurrencies() { return mCurrencies; }
    public void setCurrencies(UcoinCurrencies currencies) {
        mCurrencies = currencies;
    }
}
