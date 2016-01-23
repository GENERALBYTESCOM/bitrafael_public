package com.generalbytes.bitrafael.api.wallet;

/**
 * Created by b00lean on 23.1.16.
 */
public interface IWalletTools {
    public MasterPrivateKey getMasterPrivateKey(String seedMnemonicSeparatedBySpaces, String password);
    public String getWalletAddress(MasterPrivateKey master, int subsystemId, int accountId, int purposeId, int childId);
    public String getWalletPrivateKey(MasterPrivateKey master, int subsystemId, int accountId, int purposeId, int childId);
    public String getAccountXPUB(MasterPrivateKey master, int subsystemId, int accountId);
    public String getAddressFromPrivateKey(String privateKey);
}