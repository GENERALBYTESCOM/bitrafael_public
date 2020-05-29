package com.generalbytes.bitrafael.tools.api.wallet;

public interface IClassificator {
    Classification classify(String input);
    Classification classify(String input, String cryptoCurrencyHint);
}
