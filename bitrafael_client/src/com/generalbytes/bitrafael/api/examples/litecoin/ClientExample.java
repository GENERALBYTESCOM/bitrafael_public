package com.generalbytes.bitrafael.api.examples.litecoin;

import com.generalbytes.bitrafael.api.client.Client;
import com.generalbytes.bitrafael.api.client.IClient;
import com.generalbytes.bitrafael.api.dto.AccountBalance;

/**
 * Created by b00lean on 30.5.17.
 */
public class ClientExample {
    public static void main(String[] args) {
        String cryptoCurrency = IClient.LTC;
        IClient c = new Client("https://coin.cz", cryptoCurrency);
        final String testXpub = "Ltub2ZAmuQgUPyAhZahH9yYmdPRUFBBLC9a77iUWEvDh6VfY5webf8UD8rcaiJYpSaBZHEyM5PZ5iXLpa4CQU9Umcp8LokYUCAEqXG8yUqaaqoL";
        final AccountBalance account = c.getAccountBalance(testXpub);
        String nextReceivingAddress = account.getNextReceivingAddress();
        final int nextReceivingIndex = account.getNextReceivingIndex();
        System.out.println("nextReceivingIndex = " + nextReceivingIndex);
        System.out.println("nextReceivingAddress = " + nextReceivingAddress);

    }
}
