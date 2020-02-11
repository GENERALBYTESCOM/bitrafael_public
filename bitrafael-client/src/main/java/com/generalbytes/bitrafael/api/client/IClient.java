/*************************************************************************************
 * Copyright (C) 2016 GENERAL BYTES s.r.o. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.bitrafael.api.client;

import com.generalbytes.bitrafael.api.dto.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IClient {
    BigDecimal FEE_LOW = new BigDecimal("-1");
    BigDecimal FEE_MEDIUM = new BigDecimal("-2");
    BigDecimal FEE_HIGH = new BigDecimal("-3");

    String BTC = "BTC";
    String LBTC = "LBTC";
    String DASH = "DASH";
    String LTC = "LTC";
    String XMR = "XMR";
    String ETH = "ETH";
    String ETC = "ETC";
    String BCH = "BCH";

    int AUDIT_RESULT_OK                     = 0;
    int AUDIT_RESULT_FRAUD_ATTEMPT_DETECTED = 1;

    long getCurrentBlockchainHeight();

    BigDecimal getAddressBalance(String address);
    BigDecimal getAddressBalanceConfirmed(String address);
    AccountBalance getAccountBalance(String xpub);

    TxInfo getAddressLastTransactionInfo(String address);
    AddressInfo getAddressInfo(String address, int limit);
    Map<String,TxInfo> getAddressesLastTransactionInfos(List<String> addresses);
    Map<String,AddressInfo> getAddressesInfo(List<String> addresses, int limit);
    Map<String, Integer> getAddressesAudits(List<String> addresses);

    long getTransactionHeight(String txHash);
    long getTransactionConfirmations(String txHash);

    String send(String fromPrivateKey, BigDecimal amount, String toAddress); //LOW fee
    String send(String fromPrivateKey, BigDecimal amount, String toAddress, BigDecimal fee);
    String send(String[] fromPrivateKeys, BigDecimal[] fromAmounts, String[] toAddresses, BigDecimal[] toAmounts, BigDecimal fee);

    BigDecimal convertAmount(BigDecimal fromAmount, String fromCurrency, String toCurrency);
    List<AmountsPair> convertAmounts(List<AmountsPair> amountsPairs);
    TxFeesInfo getRecommendedTransactionFeesPerByte();

    TxFees getTransactionFees(List<String> txHashes);

    public RiskLevel getTransactionRiskLevel(String txHash);

    public enum RiskLevel {
        high("high"),
        low("low"),
        none("none");

        String level;

        RiskLevel(String level) {
            this.level = level;
        }

        public String getLevel() {
            return level;
        }
    }
}
