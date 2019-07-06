package com.sun.es;

import com.alibaba.fastjson.JSON;
import com.sun.BaseTest;
import com.sun.bean.EsIndex;
import com.sun.bean.Person;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;

import javax.annotation.Resource;
import javax.jms.XAConnectionFactory;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.sun.es.EsIndexCreate.ES_INDEX_LEARN;
import static org.junit.Assert.*;

public class EsIndexCreateTest extends BaseTest {
    @Resource
    private EsIndexCreate esIndexCreate;

    @Resource EsClientConnect esClientConnect;


    /**
     * 创建索引
     * @throws UnknownHostException
     */
    @Test
    public void esindexCreateMethod() throws UnknownHostException {
        TransportClient esClient = esClientConnect.getEsClient();
        //1.
        //CreateIndexResponse indexResponse = esClient.admin().indices().prepareCreate(ES_INDEX_LEARN).get();
        HashMap<String, Object> map = new HashMap<>();
        map.put("name","lirao.sun");
        map.put("age",15);
        EsIndex esIndex = new EsIndex();
        esIndex.setIndex(ES_INDEX_LEARN);
        esIndex.setId("1");
        esIndex.setType("people");
        IndexResponse indexResponse = esClient
                .prepareIndex(ES_INDEX_LEARN, "people","1")
                .setSource(map, XContentType.JSON).get();
        System.out.println(indexResponse);
    }
    /**
     * 删除索引
     */
    @Test
    public void esIndexDelete() throws UnknownHostException {
        TransportClient esClient = esClientConnect.getEsClient();
        esClient.admin().indices().prepareDelete(ES_INDEX_LEARN).get();

    }
    public static final String ES_TYPE="people";
    /**
     *  新增，JSONString的方法
     */
    @Test
    public void esAddIndex() throws UnknownHostException {
        TransportClient esClient = esClientConnect.getEsClient();
        Person person = new Person();
        person.setName("lirao.sun11");
        person.setAge(16);
        /**
         * 如果增加 document的话，不指定 docementId，那么会生成一个随机的  documentId
         * IndexResponse indexResponse =
         *                 esClient.prepareIndex(ES_INDEX_LEARN, ES_TYPE)
         *                         .setSource(JSON.toJSONString(person), XContentType.JSON)
         *                         .get();
         *         System.out.println(indexResponse.getId());
         * 如果指定了 did，如果存在会 覆盖之前的
         * 不存在就新建立一个 document
         */
        IndexResponse indexResponse =
                esClient.prepareIndex(ES_INDEX_LEARN, ES_TYPE,"1")
                        .setSource(JSON.toJSONString(person), XContentType.JSON)
                        .get();
        System.out.println(indexResponse.getId());

    }

    /**
     * 插入的document的时候，如果不指定documentId，那么documentId会随机生成
     * @throws UnknownHostException
     */
    @Test
    public void esAddDocumentUerMap() throws UnknownHostException {
        TransportClient esClient = esClientConnect.getEsClient();
        HashMap<String, Object> map = new HashMap<>();
        map.put("name","lirao.sun.map.addDocument");
        map.put("age",30);
        IndexResponse indexResponse =
                esClient.prepareIndex(ES_INDEX_LEARN, ES_TYPE)
                        .setSource(map)
                        .get();
        //A8lTx2sBD9pvcKBwcZRl  随机生成
        System.out.println(indexResponse.getId());

    }
    /**
     * 查询单一的 document
     */
    @Test
    public void search() throws UnknownHostException {
        TransportClient esClient = esClientConnect.getEsClient();
        //AslIx2sBD9pvcKBwsZQH,随机的一个 documentId
        GetResponse response = esClient.prepareGet(ES_INDEX_LEARN, ES_TYPE, "48").get();
        String sourceAsString = response.getSourceAsString();
        System.out.println(sourceAsString);


    }
    /**
     * 查询 索引
     */
    @Test
    public void searchIndex() throws UnknownHostException {
        TransportClient esClient = esClientConnect.getEsClient();
        MultiGetResponse responses = esClient.prepareMultiGet().add(ES_INDEX_LEARN, ES_TYPE, "1")
                .add(ES_INDEX_LEARN, ES_TYPE, "2").get();

        MultiGetItemResponse[] responses1 = responses.getResponses();
        for (MultiGetItemResponse reps: responses1) {
            if (reps.isFailed()){
                System.out.println("ssss");
            }else{
                System.out.println(reps.getResponse().getSourceAsString());
            }
        }
    }

    /**
     * 修改操作
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void update() throws IOException, ExecutionException, InterruptedException {
        TransportClient esClient = esClientConnect.getEsClient();
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index(ES_INDEX_LEARN);
        updateRequest.type(ES_TYPE);
        updateRequest.id("1");
        updateRequest.doc(
                XContentFactory.jsonBuilder().startObject()
                .field("name","lirao.sunUpdate")
                .endObject()
        );
        UpdateResponse updateResponse = esClient.update(updateRequest).get();
        System.out.println(updateRequest);
    }

    /**
     * 修改失败就插入
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void upinsert() throws IOException, ExecutionException, InterruptedException {
        TransportClient esClient = esClientConnect.getEsClient();
        //构建修改不存在 插入的东西
        XContentBuilder insertBuilder= XContentFactory
                .jsonBuilder().startObject()
                .field("name","lirao.sunupinsert")
                .field("age",18)
                .endObject();
        //创建更新请求体
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index(ES_INDEX_LEARN);
        updateRequest.type(ES_TYPE);
        updateRequest.id("48");
        //如果更新失败，就插入
        UpdateRequest upsert = updateRequest.doc(XContentFactory.jsonBuilder()
                .startObject()
                .field("name", "bucunzai")
                .endObject()).upsert(insertBuilder);
        ActionFuture<UpdateResponse> update = esClient.update(upsert);
        UpdateResponse updateResponse = update.get();
        System.out.println(updateRequest);

    }

    @Test
    public void deleteDocument() throws UnknownHostException {
        TransportClient esClient = esClientConnect.getEsClient();
        DeleteRequestBuilder deleteRequestBuilder = esClient.prepareDelete(ES_INDEX_LEARN, ES_TYPE, "48");
        DeleteResponse deleteResponse = deleteRequestBuilder.get();
        System.out.println(deleteResponse.getResult());

    }

}