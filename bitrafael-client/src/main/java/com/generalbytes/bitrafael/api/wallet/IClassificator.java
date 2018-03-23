package com.generalbytes.bitrafael.api.wallet;

public interface IClassificator {
    Classification classify(String input);
    Classification classify(String input, String cryptoCurrencyHint);
}
