package io.ucoin.app.model;

import io.ucoin.app.enumeration.DayOfWeek;
import io.ucoin.app.enumeration.Month;

public interface UcoinUd extends SqlRow {
    Long walletId();

    Long block();

    Boolean consumed();

    Long time();

    Long quantitativeAmount();

    Double relativeAmountThen();

    Double relativeAmountNow();

    String currencyName();

    Integer year();

    Month month();

    DayOfWeek dayOfWeek();

    Integer day();

    String hour();

    UcoinWallet wallet();
}

