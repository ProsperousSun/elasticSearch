package com.sun.es;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.UnknownHostException;

@Service
public class EsIndexCreate {

    public static final String ES_INDEX_LEARN="es_index_learn";
    @Resource
    private EsClientConnect esClientConnect;

    public void EsindexCreateMethod() throws UnknownHostException {
        TransportClient esClient = esClientConnect.getEsClient();

        CreateIndexResponse indexResponse = esClient.admin().indices().prepareCreate(ES_INDEX_LEARN).get();

        System.out.println(indexResponse);
    }
}
