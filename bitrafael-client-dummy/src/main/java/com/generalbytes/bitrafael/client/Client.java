package com.generalbytes.bitrafael.client;

import com.generalbytes.bitrafael.client.api.IClient;
import com.generalbytes.bitrafael.server.api.dto.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class Client implements IClient {
    @Override
    public long getCurrentBlockchainHeight() {
        return 0;
    }

    @Override
    public BigDecimal getAddressBalance(String address) {
        return BigDecimal.ONE;
    }

    @Override
    public BigDecimal getAddressBalanceConfirmed(String address) {
        return BigDecimal.ONE;
    }

    @Override
    public AccountBalance getAccountBalance(String xpub) {
        return new AccountBalance(xpub,"nextAddr",0,"nextAddrChange",0,100000000,100000000);
    }

    @Override
    public TxInfo getAddressLastTransactionInfo(String address) {
        return null;
    }

    @Override
    public AddressInfo getAddressInfo(String address, int limit) {
        return null;
    }

    @Override
    public Map<String, TxInfo> getAddressesLastTransactionInfos(List<String> addresses) {
        return null;
    }

    @Override
    public Map<String, AddressInfo> getAddressesInfo(List<String> addresses, int limit) {
        return null;
    }

    @Override
    public Map<String, Integer> getAddressesAudits(List<String> addresses) {
        return null;
    }

    @Override
    public long getTransactionHeight(String txHash) {
        return 0;
    }

    @Override
    public long getTransactionConfirmations(String txHash) {
        return 0;
    }

    @Override
    public String send(String fromPrivateKey, BigDecimal amount, String toAddress) {
        return null;
    }

    @Override
    public String send(String fromPrivateKey, BigDecimal amount, String toAddress, BigDecimal fee) {
        return null;
    }

    @Override
    public String send(String[] fromPrivateKeys, BigDecimal[] fromAmounts, String[] toAddresses, BigDecimal[] toAmounts, BigDecimal fee) {
        return null;
    }

    @Override
    public BigDecimal convertAmount(BigDecimal fromAmount, String fromCurrency, String toCurrency) {
        return null;
    }

    @Override
    public List<AmountsPair> convertAmounts(List<AmountsPair> amountsPairs) {
        return null;
    }

    @Override
    public TxFeesInfo getRecommendedTransactionFeesPerByte() {
        return null;
    }

    @Override
    public TxFees getTransactionFees(List<String> txHashes) {
        return null;
    }

    @Override
    public RiskLevel getTransactionRiskLevel(String txHash) {
        return null;
    }
}
