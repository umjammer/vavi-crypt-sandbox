/*
 * Copyright (c) 2009 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.crypto.camellia;

import java.security.Key;
import java.security.KeyFactory;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;

import org.junit.Test;

import vavi.crypto.camellia.Camellia;
import vavi.crypto.camellia.CamelliaCipher;
import vavi.crypto.camellia.CamelliaCipherProvider;

import static org.junit.Assert.assertEquals;


/**
 * CamelliaTest.
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 2009/02/21 nsano initial version <br>
 */
public class CamelliaTest {

    static {
        int r = Security.addProvider(new CamelliaCipherProvider());
        System.err.println("pos: " + r);
        Arrays.asList(Security.getProviders()).forEach(System.err::println);
    }

    /** raw method */
    @Test
    public void test00() throws Exception {
        String key = "sanonaohide01234";

        int[] keyInts = new int[16];
        byte[] keyBytes = key.getBytes();
        for (int i = 0; i < keyBytes.length; i++) {
            keyInts[i] = keyBytes[i] & 0xff;
        }

        int[] keyTable = new int[52];

        Camellia camellia = new Camellia();
        camellia.genEkey(keyInts, keyTable);
//        Camellia camellia2 = new Camellia();
//        camellia2.genEkey(keyInts, keyTable);

        String data = "本日は晴天なり。";
//        String data = "abcdefghijklmn";
//        String data = "abcd";
        byte[] dataBytes0 = data.getBytes("UTF-8");
        int pad = dataBytes0.length % 4;
        byte[] dataBytes;
        if (pad == 0) {
            dataBytes = dataBytes0;
        } else {
            dataBytes = new byte[dataBytes0.length + (4 - pad)];
            System.arraycopy(dataBytes0, 0, dataBytes, 0, dataBytes0.length);
        }
//System.err.println(StringUtil.getDump(dataBytes));
        int[] cryptedInts = new int[dataBytes.length];
        for (int i = 0; i < dataBytes.length; i += 4) {
            int[] in = new int[4];
            int[] out = new int[4];
            for (int j = 0; j < 4; j++) {
                in[j] = dataBytes[i + j] & 0xff;
            }
            camellia.encryptBlock(in, keyTable, out);
            for (int j = 0; j < 4; j++) {
                cryptedInts[i + j] = out[j];
//System.err.printf("E: in[%02d]=%02x, out[%02d]=%08x\n", i + j, dataBytes[i + j], i + j, cryptedInts[i + j]);
            }
        }

        int[] decryptedInts = new int[cryptedInts.length];
        for (int i = 0; i < cryptedInts.length; i += 4) {
            int[] in = new int[4];
            int[] out = new int[4];
            for (int j = 0; j < 4; j++) {
                in[j] = cryptedInts[i + j];
            }
            camellia.decryptBlock(in, keyTable, out);
            for (int j = 0; j < 4; j++) {
                decryptedInts[i + j] = out[j];
//System.err.printf("D: in[%02d]=%08x, out[%02d]=%02x\n", i + j, cryptedInts[i + j], i + j, decryptedInts[i + j]);
            }
        }

        byte[] decryptedBytes = new byte[decryptedInts.length];
        for (int i = 0; i < decryptedInts.length; i++) {
            decryptedBytes[i] = (byte) decryptedInts[i];
        }
//System.err.println(StringUtil.getDump(decryptedBytes));

System.err.println(new String(decryptedBytes, "UTF-8"));
        assertEquals(data, new String(decryptedBytes, "UTF-8"));
    }

    /** raw jce method */
    @Test
    public void test01() throws Exception {
        CamelliaCipher cipher = new CamelliaCipher();
        SecureRandom random = new SecureRandom();
        Key key = new CamelliaCipher.CamelliaKey("sanonaohide01234");
        cipher.engineInit(Cipher.ENCRYPT_MODE, key, random);
        String plain = "本日は晴天なり。";
        byte[] input = plain.getBytes("UTF-8");
        byte[] encrypted = cipher.engineDoFinal(input, 0, input.length);
//System.err.println("encrypted: " + encrypted.length);
        cipher.engineInit(Cipher.DECRYPT_MODE, key, random);
        byte[] decrypted = cipher.engineDoFinal(encrypted, 0, encrypted.length);
System.err.println(new String(decrypted, "UTF-8"));
        assertEquals(plain, new String(decrypted, "UTF-8"));
    }

    /** jce */
    @Test
    public void test02() throws Exception {
        Cipher cipher = Cipher.getInstance("Camellia", "Camellia");
        SecureRandom random = new SecureRandom();
        KeySpec keySpec = new CamelliaCipher.CamelliaKeySpec("sanonaohide01234");
        Key key = KeyFactory.getInstance("Camellia").generatePrivate(keySpec);
        cipher.init(Cipher.ENCRYPT_MODE, key, random);
        String plain = "本日は晴天なり。";
        byte[] input = plain.getBytes("UTF-8");
        byte[] encrypted = cipher.doFinal(input, 0, input.length);
//System.err.println("encrypted: " + encrypted.length);
        cipher.init(Cipher.DECRYPT_MODE, key, random);
        byte[] decrypted = cipher.doFinal(encrypted, 0, encrypted.length);
System.err.println(new String(decrypted, "UTF-8"));
        assertEquals(plain, new String(decrypted, "UTF-8"));
    }

    //----

    /**
     * <pre>
     * -Djava.security.policy=<i>url</i>
     * -Djava.security.policy=target/classes/camellia.policy
     * </pre>
     * <code>java.security.policy</code> MacOSX 1.8 Oracle: はじめからOK
     */
    public static void main(String[] args) throws Exception {
        Security.addProvider(new CamelliaCipherProvider());
        Cipher cipher = Cipher.getInstance("Camellia", "Camellia");
        SecureRandom random = new SecureRandom();
        Key key = new CamelliaCipher.CamelliaKey("sanonaohide01234");
        cipher.init(Cipher.ENCRYPT_MODE, key, random);
        String plain = "本日は晴天なり。";
        byte[] input = plain.getBytes("UTF-8");
        byte[] encrypted = cipher.doFinal(input, 0, input.length);
        cipher.init(Cipher.DECRYPT_MODE, key, random);
        byte[] decrypted = cipher.doFinal(encrypted, 0, encrypted.length);
        System.err.println(new String(decrypted, "UTF-8"));
    }
}

/* */
