package com.sun.es;

import com.alibaba.fastjson.JSON;
import com.sun.BaseTest;
import com.sun.bean.Product;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

public class NewEsApi /*extends BaseTest*/ {
    private final static String HOST="192.168.204.111";//服务器地址

    private final static int PORT=9300; //http请求连接

    private TransportClient client = null;

    private final String index="bigdata";
    private final String type="product";

    @Before
    public void getEsClient() throws UnknownHostException {
            client = new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName(HOST), PORT));

            System.out.println("client : "+client);


    }

    @Test
    public void termQuery(){
        SearchHits hits = client.prepareSearch(index)
                .setTypes(type)
                //.setQuery(QueryBuilders.termQuery("name", "name1"))
                .setQuery(QueryBuilders.matchQuery("name", "ame1"))
                .get()
                .getHits();
        System.out.println("hits:"+hits.totalHits);
        hits.forEach(p->{
                    System.out.println(p.getSourceAsString());
                }
        );

    }

    /**
     * fuzzy的这个模糊查询
     * 这个 模糊查询  主要支持 后缀查询
     */
    @Test
    public void fuzzyQuery(){

        SearchHits hits = client.prepareSearch(index)
                .setTypes(type)
                .setQuery(QueryBuilders.fuzzyQuery("name", "namex"))
                .get()
                .getHits();
        System.out.println("hits:"+hits.totalHits);
       hits.forEach(p->{
           System.out.println(p.getSourceAsString());
               }
       );

    }

    /**
     * SearchType的使用
     */
    @Test
    public void SearchType(){

        SearchResponse searchResponse = client.prepareSearch(index)
                .setTypes(type)
                .setSearchType(SearchType.DEFAULT)
                .setFrom((1 - 1) * 1)
                .setSize(1)
                .setQuery(QueryBuilders.termQuery("name", "name1"))
                .get();
        SearchHits hits = searchResponse.getHits();

        hits.forEach(hit->{
            System.out.println(hit.getSourceAsString());
        });
    }

    @Test
    public void delete(){

        DeleteResponse deleteResponse = client.prepareDelete(index, type, "0").get();

        System.out.println(deleteResponse.toString());

    }

    /**
     *  针对 ip进行修改
     */
    @Test
    public void update(){
        Product product = new Product();
        product.setAge(11);
        UpdateResponse updateResponse = client.prepareUpdate(index, type, "1")
                .setDoc(JSON.toJSONString(product), XContentType.JSON)
                .get();

        System.out.println("更新后的数据"+updateResponse.toString());
    }

    /**
     * 查询 根据 id
     */
    @Test
    public void search(){
        GetResponse documentFields = client.prepareGet(index, type, "1").get();
        String sourceAsString = documentFields.getSourceAsString();
        //这是 将一个 document 转换为 map
        Map<String, Object> source = documentFields.getSource();

        System.out.println("asString 输出:"+sourceAsString);
        System.out.println(source.get("name"));
    }



    /**
     * 创建这只是其中的一种
     * 还有 map
     * XContentTypeBuild等
     */
    @Test
    public void creteIndex(){

        Product product = new Product();
        product.setAge(0);
        product.setName("name0");
        IndexResponse indexResponse = client.prepareIndex(index, type, "0").setSource(JSON.toJSONString(product), XContentType.JSON).get();

        System.out.println("返回结果"+indexResponse.toString());

    }



}
