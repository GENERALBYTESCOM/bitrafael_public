package com.generalbytes.bitrafael.api.wallet.bch;

import com.generalbytes.bitrafael.api.client.IClient;
import com.generalbytes.bitrafael.api.wallet.Classification;
import com.generalbytes.bitrafael.api.wallet.IMasterPrivateKey;
import com.generalbytes.bitrafael.api.wallet.ISignature;
import com.generalbytes.bitrafael.api.wallet.IWalletTools;
import com.google.common.base.Joiner;
import org.bitcoincashj.core.*;
import org.bitcoincashj.crypto.MnemonicCode;
import org.bitcoincashj.crypto.MnemonicException;
import org.bitcoincashj.params.MainNetParams;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

public class WalletToolsBCH implements IWalletTools {

	private static final String MAIN_NET_PREFIX = "bitcoincash";

	private static final String CHARSET = "qpzry9x8gf2tvdw0s3jn54khce6mua7l";

	private static final Long[] POLYMOD_GENERATORS = new Long[] {
			Long.parseLong("98f2bc8e61", 16),
			Long.parseLong("79b76d99e2", 16),
			Long.parseLong("f33e5fb3c4", 16),
			Long.parseLong("ae2eabe2a8", 16),
			Long.parseLong("1e4f43e470", 16)};

	private static final long POLYMOD_AND_CONSTANT = Long.parseLong("07ffffffff", 16);

	private static final char[] CHARS = CHARSET.toCharArray();

	private static Map<Character, Integer> charPositionMap;

	static {
		charPositionMap = new HashMap<>();
		for (int i = 0; i < CHARS.length; i++) {
			charPositionMap.put(CHARS[i], i);
		}
		if (charPositionMap.size() != 32) {
			throw new RuntimeException("The charset must contain 32 unique characters.");
		}
	}

	@Override
	public Classification classify(String input) {
		if (input == null) {
            return new Classification(Classification.TYPE_UNKNOWN, false, null);
		}
		input = input.trim().replace("\n","");
		boolean containsPrefix = false;
		String prefix = null;
		if (input.contains(":")) {
            prefix = input.substring(0, input.indexOf(":"));
			//remove leading protocol
			input = input.substring(input.indexOf(":") + 1);
			containsPrefix = true;
		}

		//remove leading slashes
		if (input.startsWith("//")) {
			input = input.substring("//".length());
		}

		//remove things after
		if (input.contains("?")) {
			input = input.substring(0,input.indexOf("?"));
		}

		if((input.startsWith("p") ||
				input.startsWith("q") ||
				input.startsWith("P") ||
				input.startsWith("Q") ||
				input.startsWith("1") ||
				input.startsWith("3")) &&
				input.length() < 45) {
			try {
				if (isAddressValidInternal(input)) {
                    Classification classification = new Classification(Classification.TYPE_ADDRESS, IClient.BCH, input, containsPrefix, prefix);
                    return classification;
				}
			} catch (AddressFormatException e) {
				e.printStackTrace();
			}
		}
		return new Classification(Classification.TYPE_UNKNOWN,containsPrefix,prefix);
	}

	@Override
	public Classification classify(String input, String cryptoCurrencyHint) {
		return classify(input);
	}

    /**
     * Method <i>generateSeedMnemonicSeparatedBySpaces()</i> is not implemented.
     *
     */
	@Override
	public String generateSeedMnemonicSeparatedBySpaces() {
        try {
            SecureRandom prng = SecureRandom.getInstance("SHA1PRNG");
            List<String> words = MnemonicCode.INSTANCE.toMnemonic(Sha256Hash.create(prng.generateSeed(32)).getBytes());
            return Joiner.on(" ").join(words);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (MnemonicException.MnemonicLengthException e) {
            e.printStackTrace();
        }
        return null;
	}

    /**
     *
     *
     * @param seedMnemonicSeparatedBySpaces
     * @param password
     * @param cryptoCurrency
     * @return
     */
	@Override
	public IMasterPrivateKey getMasterPrivateKey(String seedMnemonicSeparatedBySpaces, String password,
			String cryptoCurrency) {
		//Not implemented
        return null;
	}

    /**
     *
     *
     * @param xprv
     * @param cryptoCurrency
     * @return
     */
	@Override
	public IMasterPrivateKey getMasterPrivateKey(String xprv, String cryptoCurrency) {
		//Not implemented
		return null;
	}

    /**
     * The method is not implemented.
     *
     * @param master
     * @param cryptoCurrency
     * @param accountIndex
     * @return
     */
	@Override
	public String getAccountXPUB(IMasterPrivateKey master, String cryptoCurrency, int accountIndex) {
		//Not implemented
		return null;
	}

    /**
     * The method is not implemented.
     *
     * @param master
     * @param cryptoCurrency
     * @param accountIndex
     * @param chainIndex
     * @param index
     * @return
     */
	@Override
	public String getWalletAddress(IMasterPrivateKey master, String cryptoCurrency, int accountIndex, int chainIndex,
			int index) {
		//Not implemented
        return null;
	}

    /**
     * The method is not implemented.
     *
     * @param master
     * @param cryptoCurrency
     * @param accountIndex
     * @param chainIndex
     * @param index
     * @return
     */
	@Override
	public String getWalletPrivateKey(IMasterPrivateKey master, String cryptoCurrency, int accountIndex, int chainIndex, int index) {
		//Not implemented
        return null;
	}

    /**
     * The method is not implemented.
     *
     * @param accountXPUB
     * @param cryptoCurrency
     * @param chainIndex
     * @param index
     * @return
     */
	@Override
	public String getWalletAddressFromAccountXPUB(String accountXPUB, String cryptoCurrency, int chainIndex,
			int index) {
		//Not implemented
		return null;
	}

    /**
     * The method is not implemented.
     *
     * @param privateKey
     * @param cryptoCurrency
     * @return
     */
	@Override
	public String getWalletAddressFromPrivateKey(String privateKey, String cryptoCurrency) {
        DumpedPrivateKey dp = new DumpedPrivateKey(MainNetParams.get(),privateKey);
        return (new Address(MainNetParams.get(),dp.getKey().getPubKeyHash())) +"";
	}

    /**
     * The method is not implemented.
     *
     * @param privateKey
     * @param hashToSign
     * @param cryptoCurrency
     * @return
     */
	@Override
	public ISignature sign(String privateKey, byte[] hashToSign, String cryptoCurrency) {
        DumpedPrivateKey dp = new DumpedPrivateKey(MainNetParams.get(),privateKey);
        final ECKey key = dp.getKey();
        return new WalletToolsBCH.Signature(key.getPubKey(),key.sign(Sha256Hash.wrap(hashToSign)).encodeToDER());
	}

    /**
     * The method is not implemented.
     *
     * @param address
     * @param cryptoCurrency
     * @return
     */
	@Override
	public boolean isAddressValid(String address, String cryptoCurrency) {
        if (address == null) {
            return false;
        }

	    if(!"BCH".equalsIgnoreCase(cryptoCurrency)) {
            return false;
        }

        address = address.trim();
        if (address.startsWith("xpub")) {
            return false;
        }

        if (address.startsWith("1") || address.startsWith("3")) {
            try {
                Base58.decodeToBigInteger(address);
                Base58.decodeChecked(address);
            } catch (AddressFormatException e) {
                //log.error("isAddressValid - address = " + address);
                return false;
            }
            return true;
        } else if (address.startsWith("p") || address.startsWith("q") || address.startsWith("P") || address.startsWith("Q")){
            try {
                byte[] checksumData = concatenateByteArrays(concatenateByteArrays(getPrefixBytes(MAIN_NET_PREFIX), new byte[]{0x00}), decode(address));
                byte[] calculateChecksumBytesPolymod = calculateChecksumBytesPolymod(checksumData);
                return bytes2Long(calculateChecksumBytesPolymod) == 0l;
            } catch(RuntimeException e) {
                return false;
            }
        } else {
            return false;
        }
	}

	@Override
	public Set<String> supportedCryptoCurrencies() {
        final HashSet<String> result = new HashSet<String>();
        result.add(IClient.BCH);
        return result;
	}

    private int getCoinTypeByCryptoCurrency(String cryptoCurrency) {
        if (IClient.BTC.equalsIgnoreCase(cryptoCurrency)) {
            return COIN_TYPE_BITCOIN;
        }else if (IClient.BCH.equalsIgnoreCase(cryptoCurrency)) {
            return COIN_TYPE_BITCOIN_CASH;
        }else if (IClient.LTC.equalsIgnoreCase(cryptoCurrency)) {
            return COIN_TYPE_LITECOIN;
        }
        return COIN_TYPE_BITCOIN_CASH;
    }

	private static boolean isAddressValidInternal(String address) {
		if (address == null) {
			return false;
		}
		address = address.trim();
		if (address.startsWith("xpub")) {
			return false;
		}
		if (address.startsWith("1") || address.startsWith("3")) {
			try {
				Base58.decodeToBigInteger(address);
				Base58.decodeChecked(address);
			} catch (AddressFormatException e) {
				//log.error("isAddressValid - address = " + address);
				return false;
			}
			return true;
		} else if (address.startsWith("p") || address.startsWith("q") || address.startsWith("P") || address.startsWith("Q")){
			try {
				byte[] checksumData = concatenateByteArrays(concatenateByteArrays(getPrefixBytes(MAIN_NET_PREFIX), new byte[]{0x00}), decode(address));
				byte[] calculateChecksumBytesPolymod = calculateChecksumBytesPolymod(checksumData);
				return bytes2Long(calculateChecksumBytesPolymod) == 0l;
			} catch(RuntimeException e) {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Method calculateChecksumBytesPolymod calculates checksum from bitcoincash address
	 *
	 * @param checksumInput bitcoinhash address
	 * @return Returns a 40 bits checksum in form of 5 8-bit arrays. This still has
	 *         to me mapped to 5-bit array representation
	 */
	private static byte[] calculateChecksumBytesPolymod(byte[] checksumInput) {
		long l = 1;
		for (int i = 0; i < checksumInput.length; i++) {
			byte l0 = (byte)(l>>>35);
			l = ((l & POLYMOD_AND_CONSTANT)<<5) ^ (Long.parseLong(String.format("%02x", checksumInput[i]), 16));
			if ((l0 & 0x01) != 0) {
				l = l ^ POLYMOD_GENERATORS[0].longValue();
			}

			if ((l0 & 0x02) != 0) {
				l = l ^ POLYMOD_GENERATORS[1].longValue();
			}

			if ((l0 & 0x04) != 0) {
				l = l ^ POLYMOD_GENERATORS[2].longValue();
			}

			if ((l0 & 0x08) != 0) {
				l = l ^ POLYMOD_GENERATORS[3].longValue();
			}

			if ((l0 & 0x10) != 0) {
				l = l ^ POLYMOD_GENERATORS[4].longValue();
			}
		}

		byte[] checksum = long2Bytes((l ^ 1l));
		if (checksum.length == 5) {
			return checksum;
		} else {
			byte[] newChecksumArray = new byte[5];
			System.arraycopy(checksum, Math.max(0, checksum.length - 5), newChecksumArray, Math.max(0, 5 - checksum.length), Math.min(5, checksum.length));
			return newChecksumArray;
		}
	}

	/**
	 * Decode a base32 string back to the byte array representation
	 *
	 * @param base32String
	 * @return
	 */
	private static byte[] decode(String base32String) {
		byte[] bytes = new byte[base32String.length()];

		char[] charArray = base32String.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			Integer position = charPositionMap.get(charArray[i]);
			if (position == null) {
				throw new RuntimeException("There seems to be an invalid char: " + charArray[i]);
			}
			bytes[i] = (byte) ((int) position);
		}

		return bytes;
	}

	/**
	 * Method concatenateByteArrays concatenates two byte arrays
	 *
	 * @param first - first byte array
	 * @param second - second byte array
	 * @return concatenated byte array
	 */
	private static byte[] concatenateByteArrays(byte[] first, byte[] second) {
		byte[] concatenatedBytes = new byte[first.length + second.length];
		System.arraycopy(first, 0, concatenatedBytes, 0, first.length);
		System.arraycopy(second, 0, concatenatedBytes, first.length, second.length);
		return concatenatedBytes;
	}

	/**
	 * Method getPrefixBytes returns byte array for prefixString parameter
	 *
	 * @param prefixString prefix for bitcoincash
	 * @return byte array for bitcoincash
	 */
	private static byte[] getPrefixBytes(String prefixString) {
		byte[] prefixBytes = new byte[prefixString.length()];
		char[] charArray = prefixString.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			prefixBytes[i] = (byte) (charArray[i] & 0x1f);
		}

		return prefixBytes;
	}

	/**
	 * Method calculateBitLength calculates the size of bits representing the number of long format
	 *
	 * @param value number of type long
	 * @return bit length of value
	 */
	private static int calculateBitLength(long value) {
		return Long.SIZE-Long.numberOfLeadingZeros(value);
	}

	/**
	 * Method long2Bytes converts long into byte array
	 *
	 * @param l number of type long
	 * @return byte array
	 */
	private static byte[] long2Bytes(long l) {
		int bitLength = calculateBitLength(l);
		byte[] result = new byte[bitLength];
		for (int i = bitLength - 1; i >= 0; i--) {
			result[i] = (byte)(l & 0xFF);
			l >>= bitLength;
		}
		return result;
	}

	/**
	 * Method bytes2Long converts byte array to long
	 *
	 * @param b byte array
	 * @return result of type long
	 */
	private static long bytes2Long(byte[] b) {
		long result = 0;
		for (int i = 0; i < b.length; i++) {
			result <<= b.length;
			result |= (b[i] & 0xFF);
		}
		return result;
	}

    public class Signature implements ISignature {
        private byte[] publicKey;
        private byte[] signature;

        public Signature(byte[] publicKey, byte[] signature) {
            this.publicKey = publicKey;
            this.signature = signature;
        }

        @Override
        public byte[] getPublicKey() {
            return publicKey;
        }

        @Override
        public byte[] getSignature() {
            return signature;
        }
    }
}
