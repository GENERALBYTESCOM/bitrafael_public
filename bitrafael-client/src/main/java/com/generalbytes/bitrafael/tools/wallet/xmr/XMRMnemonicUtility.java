/*************************************************************************************
 * Copyright (C) 2018 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.bitrafael.tools.wallet.xmr;

import com.google.common.base.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;
import com.google.common.io.LineProcessor;
import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;


public final class XMRMnemonicUtility {
    private static Dictionary dictionary = null;
    private static final Splitter WORD_SPLITTER = Splitter.on(CharMatcher.whitespace()).trimResults().omitEmptyStrings();
    public XMRMnemonicUtility() {
    }

    private static int mn_mod(int value, int mod) {
        return value < 0 ? mod + value : value % mod;
    }

    private static void putInteger(byte[] buffer, int currentIndex, int value) {
        buffer[currentIndex + 0] = (byte) (value & 0xFF);
        buffer[currentIndex + 1] = (byte) ((value >> 8) & 0xFF);
        buffer[currentIndex + 2] = (byte) ((value >> 16) & 0xFF);
        buffer[currentIndex + 3] = (byte) ((value >> 24) & 0xFF);
    }


    private static int getInteger(byte[] buffer, int currentIndex) {
        return    (buffer[currentIndex + 3] & 0xFF) << 24
                | (buffer[currentIndex + 2] & 0xFF) << 16
                | (buffer[currentIndex + 1] & 0xFF) << 8
                |  buffer[currentIndex + 0] & 0xFF;
    }

    public static String toMnemonic(byte[] entropy) {
        final Dictionary dictionary = getDictionary();
        final int N = dictionary.getSize();
        int entropyIndex = 0;
        ArrayList<String> encoded = Lists.newArrayList();
        encoded.ensureCapacity(entropy.length / 8 * 3);
        while (entropyIndex < entropy.length) {
            long subValue = getInteger(entropy, entropyIndex) & 0xFFFFFFFFL;
            entropyIndex += 4;
            int w1 = (int) (subValue % N);
            int w2 = (int) (subValue / N + w1) % N;
            int w3 = (int) (subValue / N / N + w2) % N;
            encoded.add(dictionary.convert(w1));
            encoded.add(dictionary.convert(w2));
            encoded.add(dictionary.convert(w3));
        }
        //add checksum
        StringBuilder sb = new StringBuilder();
        for (String word : encoded) {
            sb.append(word.substring(0,3));
        }
        CRC32 crc32 = new CRC32();
        crc32.update(sb.toString().getBytes());
        int wCrc  = (int)(crc32.getValue() % (long)encoded.size());
        encoded.add(encoded.get(wCrc));

        return Joiner.on(' ').join(encoded);
    }

    private static Dictionary getDictionary() {
        if (dictionary == null) {
            URL url = Resources.getResource("xmr_english_dictionary.txt");
            ByteSource source = Resources.asByteSource(url);
            dictionary = new Dictionary(source);
        }
        return dictionary;

    }


    public static byte[] toEntropy(CharSequence mnemonicSequence) {
        final Dictionary dictionary = getDictionary();
        final int N = dictionary.getSize();

        String[] mnemonicWords = Iterables.toArray(WORD_SPLITTER.split(mnemonicSequence), String.class);
        int wordsLength = mnemonicWords.length;
        String checkSumWord = null;
        if (wordsLength == 25 || wordsLength == 13) {
            wordsLength--; //remove checksum
            checkSumWord = mnemonicWords[wordsLength];
        }
        byte[] entropy = new byte[wordsLength * 4 / 3];
        int entropyIndex = 0;
        Converter<String, Integer> reverseDictionary = dictionary.reverse();
        if (wordsLength % 3 != 0) {
            throw new IllegalArgumentException("Mnemonic sequence is not a multiple of 3");
        }
        for (int i = 0; i < wordsLength; i += 3) {
            String word1 = mnemonicWords[i].toLowerCase();
            String word2 = mnemonicWords[i + 1].toLowerCase();
            String word3 = mnemonicWords[i + 2].toLowerCase();
            Integer w1 = reverseDictionary.convert(word1);
            Integer w2 = reverseDictionary.convert(word2);
            Integer w3 = reverseDictionary.convert(word3);
            /* NOTE: Impossible scenario as covert only returns null IFF null is passed in */

            //noinspection ConstantConditions
            int subValue = w1 + N * mn_mod(w2 - w1, N) + N * N * mn_mod(w3 - w2, N);
            /* Convert to 4 bytes */
            putInteger(entropy, entropyIndex, subValue);
            entropyIndex += 4;
        }
        if (checkSumWord != null) {
            //seed contained checksum word lets validate it.
            //add checksum
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < wordsLength; i++) {
                sb.append(mnemonicWords[i].substring(0,3));
            }
            CRC32 crc32 = new CRC32();
            crc32.update(sb.toString().getBytes());
            int wCrc  = (int)(crc32.getValue() % (long)wordsLength);
            if (!checkSumWord.equalsIgnoreCase(mnemonicWords[wCrc])) {
                //checksum failed;
                return null;
            }
        }

        return entropy;
    }

    public static class Dictionary extends Converter<Integer, String> {
        private final ImmutableList<String> indexToWordMap;
        private final ImmutableMap<String, Integer> wordToIndexMap;

        public Dictionary(List<String> wordList) {
            indexToWordMap = ImmutableList.copyOf(wordList);
            ImmutableMap.Builder<String, Integer> builder = ImmutableMap.builder();
            for (int i = 0; i < indexToWordMap.size(); i++) {
                builder.put(indexToWordMap.get(i), i);
            }
            wordToIndexMap = builder.build();
        }

        public Dictionary(ByteSource dictionaryDataSource) {
            this(resourceToLines(dictionaryDataSource));
        }

        private static ImmutableList<String> resourceToLines(ByteSource dataSource) {
            LineProcessor<ImmutableList<String>> lineProcess = new LineProcessor<ImmutableList<String>>() {
                final ImmutableList.Builder<String> result = ImmutableList.builder();

                @Override
                public boolean processLine(String line) throws IOException {
                    if (line.startsWith("#") || line.isEmpty()) {
                        return true;
                    }
                    line = Normalizer.normalize(line, Normalizer.Form.NFKD);
                    result.add(line);
                    return true;
                }

                @Override
                public ImmutableList<String> getResult() {
                    return result.build();
                }
            };
            try {
                return dataSource.asCharSource(Charsets.UTF_8).readLines(lineProcess);
            } catch (IOException e) {
                throw new IllegalArgumentException("Input source was bad", e);
            }
        }

        @Override
        public boolean equals(Object that) {
            if (this == that) {
                return true;
            }
            if (that == null || getClass() != that.getClass()) {
                return false;
            }
            Dictionary thatDictionary = (Dictionary) that;
            return Objects.equal(indexToWordMap, thatDictionary.indexToWordMap);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(indexToWordMap);
        }

        @Override
        protected String doForward(Integer integer) {
            Preconditions.checkArgument(integer >= 0 && integer < indexToWordMap.size(), "Unknown dictionary index %s", integer);
            return indexToWordMap.get(integer);
        }

        @Override
        protected Integer doBackward(String word) {
            Integer result = wordToIndexMap.get(word);
            Preconditions.checkArgument(null != result, "Unknown dictionary word");
            return result;
        }

        public int getSize() {
            return indexToWordMap.size();
        }
    }

}
