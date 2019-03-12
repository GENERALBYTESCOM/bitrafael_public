package com.generalbytes.bitrafael.api.wallet.smartcash;

import com.generalbytes.bitrafael.api.client.IClient;
import com.generalbytes.bitrafael.api.wallet.IMasterPrivateKey;
import com.generalbytes.bitrafael.api.wallet.IWalletTools;
import com.generalbytes.bitrafael.api.wallet.smart.WalletToolsSMART;
import org.junit.Before;
import org.junit.Test;


public class SmartcashjTest {
    private String seeed = "law neck away tomorrow drift drum step index offer mercy fever multiply rival radio trophy alpha salon gasp sauce divide year peace art misery";
    private String cryptoCurrency = IClient.SMART;
    private IWalletTools wt = new WalletToolsSMART();
    private IMasterPrivateKey m;

    @Before
    public void beforeTest() {
        this.wt = new WalletToolsSMART();
        this.m = wt.getMasterPrivateKey(this.seeed, "TREZOR", cryptoCurrency, IWalletTools.COIN_TYPE_SMART);
    }

    @Test
    public void testGenerateSeedMnemonicSeparatedBySpaces() {
        String seed = wt.generateSeedMnemonicSeparatedBySpaces();
        System.out.println(seed);
    }

    @Test
    public void testGetMasterPrivateKey() {
        System.out.println("Master private key : " + m.toString());
        //xpub661MyMwAqRbcEdc5XtaYWJfnm8da4Rwg3Xz97FG7REkiCk8P4vdGuWba1Uz4CpTuScNDseXdqtK2MAJsDuMFb8drEADG7WDhvnQzGUqcuwV
    }

    @Test
    public void testGenerateWalletPrivateKeyWithPrefix() {
        System.out.println(wt.generateWalletPrivateKeyWithPrefix("", cryptoCurrency));
    }

    @Test
    public void testGetAccountPUB() {
        String accPUB = wt.getAccountPUB(m, cryptoCurrency, 0);

        System.out.println("Account Pub : " + accPUB);
    }

    @Test
    public void testGetWalletAddress() {
        System.out.println(wt.getWalletAddress(m, cryptoCurrency, 0, 0, 0));
        //Assert : SfEQkUiciBppWGRYCMzuW2r1nu2rZK19XD
    }

    @Test
    public void testGetWalletPrivateKey() {
        System.out.println("GetWalletPrivateKey = " + wt.getWalletPrivateKey(m, cryptoCurrency, 0, 0, 0));
    }


    @Test
    public void testGetWalletAddressFromAccountPUB() {
        String accPUB = wt.getAccountPUB(m, cryptoCurrency, 0);
        System.out.println(accPUB);
        System.out.println("GetWalletAddressFromAccountPUB = " + wt.getWalletAddressFromAccountPUB(accPUB, cryptoCurrency, 0, 0));
    }

    @Test
    public void testGetWalletAddressFromPrivateKey() {
        System.out.println("Account pub : " + wt.getAccountPUB(m, cryptoCurrency, 0));
        String pvKey = wt.getWalletPrivateKey(m, cryptoCurrency, 0, 0, 0);
        String walletAddress = wt.getWalletAddressFromPrivateKey(pvKey, cryptoCurrency);

        System.out.println("Wallet Address = " + walletAddress);
    }

    @Test
    public void testSign() {

    }

    @Test
    public void testIsAddressValid() {

    }

    @Test
    public void testSupportedCryptoCurrencies() {

    }

    @Test
    public void classify() {

    }
}