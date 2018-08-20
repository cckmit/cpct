package com.zjtelcom.cpct.elastic.controller;

import com.zjtelcom.cpct.elastic.util.ElasticsearchUtil;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
@EnableAutoConfiguration
@RequestMapping("/esSearch")
public class EsSearchController {

    private static final Logger logger = LoggerFactory.getLogger(EsSearchController.class);

    @Autowired
    private TransportClient client;
    /**
     * 测试索引
     */
    private String indexName = "filebeat-bigdata";

    /**
     * 类型
     */
    private String esType = "external";

    /**
     * 本机id
     */
    private String localIp = "192.168.0.152";


    @RequestMapping("/index")
    public String createIndexWithMapping() throws IOException {
        ElasticsearchUtil.createIndexWithMapping("sky");
        return "OK";
    }
//    /**
//     * 查询测试
//     */
//    @RequestMapping("/testSearch")
//    public void testSearch() {
//        QueryBuilder qb2 = paramsBuilder();
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        sourceBuilder.query(qb2);
//        System.out.println(sourceBuilder.toString());
//
//        //查询建立
//        long begin = System.currentTimeMillis();
//        SearchRequestBuilder builder = client.prepareSearch(indexName).setTypes(esType);
//        SearchResponse myresponse = builder
//                .setQuery(qb2)
//                .setFrom(0).setSize(500)
////                .addSort("id", SortOrder.ASC)
//                //.addSort("inputtime", SortOrder.DESC)
//                .setExplain(true).execute().actionGet();
//        SearchHits hits = myresponse.getHits();
//        long cost = System.currentTimeMillis() - begin;
//        logger.info("import end. cost:[{}ms]", cost);
////        for(int i = 0; i < hits.getHits().length; i++) {
////            System.out.println(hits.getHits()[i].getSourceAsString());
////        }
//        logger.info("***************总条数**************："+hits.getHits().length);
//    }



    /**
     * 查询测试 bool查询
     */
    @RequestMapping("/boolSearch")
    public void boolSearch(int size) {
        QueryBuilder qb2 = boolBuilder();
        System.out.println("xxx+"+qb2.toString());
        //查询建立
        long begin = System.currentTimeMillis();

        SearchRequestBuilder response = client.prepareSearch(indexName);
        SearchResponse myresponse = response
                .setQuery(qb2)
                .setFrom(0).setSize(size)
//                .addSort("id", SortOrder.ASC)
                //.addSort("inputtime", SortOrder.DESC)
                .setExplain(true).execute().actionGet();
        //System.out.println("kkkk?+"+myresponse.toString());
        SearchHits hits = myresponse.getHits();
        long cost = System.currentTimeMillis() - begin;
        logger.info("import end. cost:[{}ms]", cost);
//        for(int i = 0; i < hits.getHits().length; i++) {
//            System.out.println(hits.getHits()[i].getSourceAsString());
//        }
        logger.info("***************总条数**************："+hits.getHits().length);
    }






    private BoolQueryBuilder   boolBuilder(){
        BoolQueryBuilder boolQueryBuilder=QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("TEST629",630))
                .must(QueryBuilders.matchQuery("TEST329","330"))
                .must(QueryBuilders.matchQuery("date",Long.valueOf("1532323561448")))
                        .must(QueryBuilders.matchQuery("TEST100",101))
                        .must(QueryBuilders.matchQuery("TEST101",102))
                        .must(QueryBuilders.matchQuery("TEST102",103))
                        .must(QueryBuilders.matchQuery("TEST103",104))
                        .must(QueryBuilders.matchQuery("TEST104",105))
                        .must(QueryBuilders.matchQuery("TEST105",106))
                        .must(QueryBuilders.matchQuery("TEST106",107))
                        .must(QueryBuilders.matchQuery("TEST107",108))
                        .must(QueryBuilders.matchQuery("TEST108",109))
                        .must(QueryBuilders.matchQuery("TEST109",110))
                        .must(QueryBuilders.matchQuery("TEST110",111))
                        .must(QueryBuilders.matchQuery("TEST111",112))
                        .must(QueryBuilders.matchQuery("TEST112",113))
                        .must(QueryBuilders.matchQuery("TEST113",114))
                        .must(QueryBuilders.matchQuery("TEST114",115))
                        .must(QueryBuilders.matchQuery("TEST115",116))
                        .must(QueryBuilders.matchQuery("TEST116",117))
                        .must(QueryBuilders.matchQuery("TEST117",118))
                        .must(QueryBuilders.matchQuery("TEST118",119))
                        .must(QueryBuilders.matchQuery("TEST119",120))
                        .must(QueryBuilders.matchQuery("TEST120",121))
                        .must(QueryBuilders.matchQuery("TEST121",122))
                        .must(QueryBuilders.matchQuery("TEST122",123))
                        .must(QueryBuilders.matchQuery("TEST123",124))
                        .must(QueryBuilders.matchQuery("TEST124",125))
                        .must(QueryBuilders.matchQuery("TEST125",126))
                        .must(QueryBuilders.matchQuery("TEST126",127))
                        .must(QueryBuilders.matchQuery("TEST127",128))
                        .must(QueryBuilders.matchQuery("TEST128",129))
                        .must(QueryBuilders.matchQuery("TEST129",130))
                        .must(QueryBuilders.matchQuery("TEST130",131))
                        .must(QueryBuilders.matchQuery("TEST131",132))
                        .must(QueryBuilders.matchQuery("TEST132",133))
                        .must(QueryBuilders.matchQuery("TEST133",134))
                        .must(QueryBuilders.matchQuery("TEST134",135))
                        .must(QueryBuilders.matchQuery("TEST135",136))
                        .must(QueryBuilders.matchQuery("TEST136",137))
                        .must(QueryBuilders.matchQuery("TEST137",138))
                        .must(QueryBuilders.matchQuery("TEST138",139))
                        .must(QueryBuilders.matchQuery("TEST139",140))
                        .must(QueryBuilders.matchQuery("TEST140",141))
                        .must(QueryBuilders.matchQuery("TEST141",142))
                        .must(QueryBuilders.matchQuery("TEST142",143))
                        .must(QueryBuilders.matchQuery("TEST143",144))
                        .must(QueryBuilders.matchQuery("TEST144",145))
                        .must(QueryBuilders.matchQuery("TEST145",146))
                        .must(QueryBuilders.matchQuery("TEST146",147))
                        .must(QueryBuilders.matchQuery("TEST147",148))
                        .must(QueryBuilders.matchQuery("TEST148",149))
                        .must(QueryBuilders.matchQuery("TEST149",150))
                        .must(QueryBuilders.matchQuery("TEST150",151))
                      /*  .must(QueryBuilders.matchQuery("TEST151",152))
                        .must(QueryBuilders.matchQuery("TEST152",153))
                        .must(QueryBuilders.matchQuery("TEST153",154))
                        .must(QueryBuilders.matchQuery("TEST154",155))
                        .must(QueryBuilders.matchQuery("TEST155",156))
                        .must(QueryBuilders.matchQuery("TEST156",157))
                        .must(QueryBuilders.matchQuery("TEST157",158))
                        .must(QueryBuilders.matchQuery("TEST158",159))
                        .must(QueryBuilders.matchQuery("TEST159",160))
                        .must(QueryBuilders.matchQuery("TEST160",161))
                        .must(QueryBuilders.matchQuery("TEST161",162))
                        .must(QueryBuilders.matchQuery("TEST162",163))
                        .must(QueryBuilders.matchQuery("TEST163",164))
                        .must(QueryBuilders.matchQuery("TEST164",165))
                        .must(QueryBuilders.matchQuery("TEST165",166))
                        .must(QueryBuilders.matchQuery("TEST166",167))
                        .must(QueryBuilders.matchQuery("TEST167",168))
                        .must(QueryBuilders.matchQuery("TEST168",169))
                        .must(QueryBuilders.matchQuery("TEST169",170))
                        .must(QueryBuilders.matchQuery("TEST170",171))
                        .must(QueryBuilders.matchQuery("TEST171",172))
                        .must(QueryBuilders.matchQuery("TEST172",173))
                        .must(QueryBuilders.matchQuery("TEST173",174))
                        .must(QueryBuilders.matchQuery("TEST174",175))
                        .must(QueryBuilders.matchQuery("TEST175",176))
                        .must(QueryBuilders.matchQuery("TEST176",177))
                        .must(QueryBuilders.matchQuery("TEST177",178))
                        .must(QueryBuilders.matchQuery("TEST178",179))
                        .must(QueryBuilders.matchQuery("TEST179",180))
                        .must(QueryBuilders.matchQuery("TEST180",181))
                        .must(QueryBuilders.matchQuery("TEST181",182))
                        .must(QueryBuilders.matchQuery("TEST182",183))
                        .must(QueryBuilders.matchQuery("TEST183",184))
                        .must(QueryBuilders.matchQuery("TEST184",185))
                        .must(QueryBuilders.matchQuery("TEST185",186))
                        .must(QueryBuilders.matchQuery("TEST186",187))
                        .must(QueryBuilders.matchQuery("TEST187",188))
                        .must(QueryBuilders.matchQuery("TEST188",189))
                        .must(QueryBuilders.matchQuery("TEST189",190))
                        .must(QueryBuilders.matchQuery("TEST190",191))
                        .must(QueryBuilders.matchQuery("TEST191",192))
                        .must(QueryBuilders.matchQuery("TEST192",193))
                        .must(QueryBuilders.matchQuery("TEST193",194))
                        .must(QueryBuilders.matchQuery("TEST194",195))
                        .must(QueryBuilders.matchQuery("TEST195",196))
                        .must(QueryBuilders.matchQuery("TEST196",197))
                        .must(QueryBuilders.matchQuery("TEST197",198))
                        .must(QueryBuilders.matchQuery("TEST198",199))
                        .must(QueryBuilders.matchQuery("TEST199",200))
                        .must(QueryBuilders.matchQuery("TEST200",201))*/;

        return boolQueryBuilder;
    }







}
