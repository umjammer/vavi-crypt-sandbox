/*
 * Copyright (c) 2003 by Naohide Sano, All rights rserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import vavi.util.Debug;


/**
 * opensign SSL.
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (vavi)
 * @version 0.00 031217 nsano initial version <br>
 */
public class t113_3 {

    /**
     * The program entry.
     */
    public static void main(String[] args) throws Exception {
        new t113_3(args);
    }

    /** */
    public t113_3(String[] args) throws Exception {
        URL url = new URL(args[0]);

        HttpURLConnection huc = (HttpURLConnection) url.openConnection();

        //----

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("MSKMF");
        kmf.init(null, null);
        KeyManager[] km = kmf.getKeyManagers();

        // �ؖ����̐M���������肷�邽�߂̃C���^�[�t�F�[�X
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("MSTMF");
        tmf.init((KeyStore) null);
        TrustManager[] tm = tmf.getTrustManagers();

        // �\�P�b�g�v���g�R������������SSLContext���쐬
        SSLContext sslContext = SSLContext.getInstance("SSL");
        // SSLContext��������
        sslContext.init(km, tm, new SecureRandom());
        // SSLContext��SocketFactory���擾
        SSLSocketFactory sslSF = sslContext.getSocketFactory();
        // URLConnection��SocketFactory���Z�b�g
        ((HttpsURLConnection) huc).setSSLSocketFactory(sslSF);

        //----

        // �z�X�g���𖳎�������
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
Debug.println(hostname + ", "+ session);
                return true;
            }
        };
        ((HttpsURLConnection) huc).setHostnameVerifier(hv);

        // HTML�t�@�C����Stream�Ŏ擾
        InputStream in = new BufferedInputStream(huc.getInputStream());
        OutputStream os = System.out;
        // OutputStream�ɏo��
        byte bb[] = new byte[1024];
        int length = 0;
        while ((length = in.read(bb, 0, bb.length)) != -1) {
            os.write(bb, 0, length);
        }
        in.close();
    }
}

/* */
