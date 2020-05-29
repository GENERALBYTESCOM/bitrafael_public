package com.generalbytes.bitrafael.tools.wallet;

import com.generalbytes.bitrafael.client.api.IClient;
import com.generalbytes.bitrafael.tools.api.wallet.Classification;
import com.generalbytes.bitrafael.tools.api.wallet.IMasterPrivateKey;
import com.generalbytes.bitrafael.tools.api.wallet.ISignature;
import com.generalbytes.bitrafael.tools.api.wallet.IWalletTools;

import java.util.Set;

public class WalletTools implements IWalletTools {
    @Override
    public String generateSeedMnemonicSeparatedBySpaces() {
        return "a h o j";
    }

    @Override
    public IMasterPrivateKey getMasterPrivateKey(String seedMnemonicSeparatedBySpaces, String password, String cryptoCurrency, int standard) {
        return null;
    }

    @Override
    public IMasterPrivateKey getMasterPrivateKey(String prv, String cryptoCurrency, int standard) {
        return null;
    }

    @Override
    public String getAccountPUB(IMasterPrivateKey master, String cryptoCurrency, int accountIndex) {
        return null;
    }

    @Override
    public String getWalletAddress(IMasterPrivateKey master, String cryptoCurrency, int accountIndex, int chainIndex, int index) {
        return null;
    }

    @Override
    public String getWalletPrivateKey(IMasterPrivateKey master, String cryptoCurrency, int accountIndex, int chainIndex, int index) {
        return null;
    }

    @Override
    public String getWalletAddressFromAccountPUB(String accountPUB, String cryptoCurrency, int chainIndex, int index) {
        return null;
    }

    @Override
    public String generateWalletPrivateKeyWithPrefix(String prefix, String cryptoCurrency) {
        return null;
    }

    @Override
    public String getWalletAddressFromPrivateKey(String privateKey, String cryptoCurrency) {
        return null;
    }

    @Override
    public ISignature sign(String privateKey, byte[] hashToSign, String cryptoCurrency) {
        return null;
    }

    @Override
    public boolean isAddressValid(String address, String cryptoCurrency) {
        return false;
    }

    @Override
    public Set<String> supportedCryptoCurrencies() {
        return null;
    }

    @Override
    public Classification classify(String input) {
        return new Classification(Classification.TYPE_ADDRESS, IClient.BTC,"1111111111111111111114oLvT2");
    }

    @Override
    public Classification classify(String input, String cryptoCurrencyHint) {
        return classify(input);
    }
}
