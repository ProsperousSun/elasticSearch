package com.sun.es;


import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.Transport;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
public class EsClientConnect {

    private final static String HOST="106.14.83.42";//服务器地址

    private final static int PORT=9300; //http请求连接

    private TransportClient client = null;

    public static void main(String[] args) {

    }
    public TransportClient getEsClient() throws UnknownHostException {
        try {
            client = new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName(HOST), PORT));

            System.out.println("client : "+client);
            return client;
        }catch (Exception e){

        }
        return null;
    }
}
