package com.zjtelcom.cpct.dubbo.out.impl;

import com.alibaba.fastjson.JSON;
import com.jcraft.jsch.ChannelSftp;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.domain.blacklist.BlackListDO;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.campaign.OpenCampaignScheEntity;
import com.zjtelcom.cpct.domain.grouping.TrialOperation;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.dubbo.out.OpenApiScheService;
import com.zjtelcom.cpct.service.campaign.OpenCampaignScheService;
import com.zjtelcom.cpct.service.impl.blacklist.BlackListCpctServiceImpl;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.ChannelUtil;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.SftpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OpenApiScheServiceImpl implements OpenApiScheService {

    private final static Logger log = LoggerFactory.getLogger(OpenApiScheServiceImpl.class);

    //600104 |user m600104 |passwd  Ftp_600104@2020 |path /jtppm/zc_mkt_campaign/600104
    private String ftpAddress = "10.128.28.3";
    private int ftpPort = 21;
//    private String ftpAddress = "134.108.5.141";
//    private int ftpPort = 2122;
    private String ftpName = "m600104";
    private String ftpPassword = "Ftp_600104@2020";
    private String exportPath = "/jtppm/zc_mkt_campaign/600104";

    private String campaignList = "28,1570,1576,1579,1584,1589,1836,1863,1870,1879,1973,2019,2704,2872,2873,2874,2876,3192,3352,3424,3567,3753,3968,3972,4876,5050,5488,5563,6067,6126,6407,6408,6409,6410,6688,6869,6873,6976,7227,7295,7362,7384,7385,7386,7524,7653,7735,7929,8186,8569,8706,8797,8939,9110,9285,9411,9527,9528,9530,9531,9537,9539,9540,9548,9549,9550,9551,9592,9799,9859,10033,10136,10227,10299,10338,10349,10361,10373,10594,10596,10597,10598,10600,10601,10602,10603,10604,10686,10724,10733,11077,11156,11209,11248,11293,11434,11464,11488,11489,11491,11505,11565,11581,11588,11617,11679,11722,11780,11863,11988,12001,12010,12135,12180,12192,12219,12419,12495,12570,12701,12711,12712,12718,12720,12721,12723,12724,12725,12736,12737,12747,12750,12753,12759,12764,12783,12790,12792,12795,12798,12800,12806,12807,12808,12811,12815,12817,12820,12823,12824,12826,12827,12829,12830,12831,12833,12840,12842,12845,12846,12847,12848,12849,12850,12853,12854,12855,12856,12858,12861,12862,12864,12865,12867,12868,12869,12871,12872,12873,12877,12878,12879,12883,12884,12892,12894,12896,12897,12899,12901,12907,12908,12909,12910,12911,12912,12913,12914,12916,12917,12922,12926,12929,12931,12933,12937,12938,12939,12940,12941,12942,12945,12946,12949,12950,12953,12956,12958,12965,12966,12967,12968,12969,12971,12973,12974,12976,12977,12978,12979,12980,12981,12983,12984,12985,12987,12989,12990,12991,12992,12993,12995,12997,12998,13001,13002,13003,13006,13009,13010,13015,13016,13017,13018,13021,13024,13027,13028,13029,13030,13033,13038,13050,13053,13066,13067,13068,13069,13070,13072,13074,13075,13076,13077,13083,13090,13093,13097,13103,13105,13106,13107,13108,13112,13113,13114,13115,13116,13117,13119,13121,13122,13123,13125,13127,13130,13131,13134,13139,13140,13141,13142,13143,13144,13145,13148,13149,13150,13153,13155,13156,13158,13159,13160,13162,13163,13164,13168,13169,13170,13171,13173,13175,13176,13178,13179,13180,13184,13186,13188,13190,13193,13196,13199,13201,13205,13209,13213,13215,13216,13217,13218,13237,13239,13240,13245,13246,13249,13252,13253,13254,13255,13257,13258,13259,13260,13261,13262,13263,13265,13266,13267,13268,13269,13271,13275,13277,13278,13279,13280,13281,13282,13283,13284,13288,13289,13290,13291,13292,13293,13296,13297,13299,13302,13305,13307,13308,13310,13313,13318,13319,13320,13322,13323,13325,13326,13329,13330,13332,13333,13334,13335,13336,13337,13338,13339,13340,13342,13343,13344,13345,13346,13349,13350,13352,13354,13356,13357,13359,13361,13362,13364,13365,13366,13367,13368,13369,13373,13374,13375,13376,13377,13379,13382,13383,13384,13385,13386,13389,13390,13391,13393,13394,13395,13396,13399,13400,13401,13402,13403,13404,13405,13409,13410,13411,13412,13413,13414,13419,13432,13433,13435,13436,13438,13444,13446,13465,13466,13467,13468,13469,13473,13475,13476,13477,13478,13479,13496,13502,13504,13508,13524,13526,13528,13533,13535,13536,13537,13538,13539,13552,13554,13555,13556,13559,13573,13593,13595,13604,13606,13609,13610,13611,13612,13613,13614,13615,13616,13617,13618,13619,13620,13621,13625,13628,13630,13631,13632,13634,13638,13639,13641,13642,13643,13644,13645,13646,13648,13649,13652,13654,13655,13656,13657,13662,13663,13664,13665,13666,13669,13670,13672,13673,13674,13675,13676,13679,13680,13682,13687,13689,13690,13691,13692,13693,13696,13697,13698,13699,13700,13701,13702,13703,13707,13709,13711,13712,13713,13715,13716,13717,13719,13721,13722,13725,13726,13730,13731,13733,13734,13735,13737,13739,13740,13744,13745,13747,13750,13751,13752,13754,13759,13761,13762,13763,13764,13770,13775,13776,13778,13779,13781,13782,13784,13785,13787,13788,13790,13792,13793,13794,13795,13796,13797,13798,13800,13802,13813,13817,13820,13822,13826,13827,13829,13831,13836,13838,13839,13842,13843,13845,13846,13849,13852,13853,13856,13857,13858,13860,13861,13863,13864,13866,13869,13871,13872,13877,13879,13881,13882,13883,13884,13888,13891,13892,13895,13897,13900,13901,13902,13903,13904,13905,13907,13908,13909,13911,13913,13915,13916,13918,13919,13921,13923,13925,13930,13931,13933,13934,13935,13936,13937,13938,13939,13945,13950,13951,13952,13953,13954,13955,13957,13960,13961,13962,13963,13966,13969,13970,13971,13972,13973,13975,13977,13979,13980,13982,13983,13984,13985,13986,13987,13988,13989,13990,13991,13992,13993,13994,13996,13997,13999,14001,14002,14003,14005,14006,14007,14008,14009,14010,14012,14013,14014,14016,14018,14021,14022,14023,14024,14025,14026,14027,14028,14033,14035,14038,14039,14040,14041,14043,14045,14046,14047,14048,14049,14050,14051,14052,14053,14056,14057,14058,14060,14061,14062,14063,14064,14066,14067,14068,14069,14070,14071,14072,14073,14074,14077,14078,14079,14081,14083,14084,14085,14089,14091,14092,14093,14094,14096,14097,14099,14100,14104,14106,14108,14109,14110,14111,14112,14113,14116,14117,14118,14120,14121,14122,14123,14124,14129,14131,14132,14133,14139,14141,14143,14144,14145,14146,14147,14149,14152,14154,14155,14156,14158,14160,14161,14163,14165,14166,14168,14170,14171,14172,14173,14174,14175,14176,14177,14178,14179,14180,14181,14182,14184,14185,14186,14187,14188,14199,14203,14204,14208,14214,14215,14216,14217,14218,14220,14221,14222,14223,14224,14237,14239,14241,14243,14244,14251,14252,14253,14254,14255,14258,14259,14261,14262,14264,14266,14268,14270,14271,14274,14275,14276,14277,14279,14280,14281,14282,14283,14285,14287,14289,14292,14293,14295,14297,14299,14300,14301,14302,14305,14307,14311,14312,14314,14315,14316,14317,14318,14319,14324,14325,14326,14327,14328,14329,14330,14331,14333,14334,14335,14337,14338,14341,14343,14344,14348,14349,14350,14351,14352,14354,14355,14356,14366,14367,14368,14370,14371,14372,14373,14374,14376,14377,14378,14379,14404,14406,14408,14409,14410,14411,14412,14414,14415,14417,14418,14420,14422,14423,14424,14426,14427,14428,14430,14432,14435,14438,14439,14440,14444,14445,14446,14448,14451,14452,14453,14454,14455,14457,14458,14459,14460,14461,14462,14463,14464,14465,14466,14467,14469,14470,14471,14472,14474,14475,14476,14477,14478,14479,14480,14482,14483,14485,14486,14489,14493,14494,14495,14498,14499,14500,14501,14504,14506,14507,14508,14509,14513,14514,14518,14519,14520,14521,14523,14525,14526,14527,14528,14530,14531,14532,14534,14535,14536,14537,14538,14540,14541,14543,14548,14551,14552,14553,14554,14555,14556,14558,14559,14560,14563,14565,14566,14568,14569,14570,14571,14572,14573,14574,14575,14577,14578,14580,14581,14584,14590,14591,14593,14594,14595,14597,14598,14603,14604,14609,14610,14612,14613,14614,14616,14617,14618,14619,14620,14622,14623,14626,14627,14628,14629,14630,14634,14635,14636,14641,14647,14648,14649,14651,14652,14653,14654,14657,14658,14662,14664,14669,14673,14675,14676,14678,14679,14681,14682,14684,14685,14691,14693,14694,14695,14698,14699,14701,14702,14703,14704,14706,14707,14708,14709,14711,14712,14714,14716,14717,14720,14721,14723,14724,14725,14726,14727,14728,14731,14732,14734,14736,14737,14743,14752,14753,14769,14771,14772,14773,14775,14776,14782,14783,14784,14785,14786,14787,14789,14790,14791,14794,14795,14796,14797,14799,14801,14803,14804,14805,14807,14808,14809,14812,14813,14814,14817,14818,14821,14822,14823,14824,14828,14829,14836,14840,14841,14842,14844,14845,14846,14847,14849,14850,14851,14852,14853,14854,14855,14856,14857,14858,14859,14860,14862,14863,14864,14865,14868,14869,14870,14871,14872,14873,14875,14876,14877,14878,14879,14880,14881,14883,14884,14885,14887,14888,14889,14890,14891,14892,14893,14895,14896,14898,14899,14905,14906,14909,14911,14912,14914,14915,14916,14917,14918,14919,14920,14922,14923,14925,14926,14927,14929,14931,14932,14933,14934,14938,14943,14948,14951,14953,14956,14957,14958,14960,14964,14966,14967,14971,14972,14973,14974,14975,14976,14978,14979,14981,14982,14984,14985,14986,14987,14988,14989,14990,14993,14994,14995,14996,14997,14998,14999,15001,15002,15003,15004,15008,15011,15012,15014,15016,15017,15018,15019,15020,15021,15023,15026,15027,15028,15030,15032,15037,15041,15044,15045,15047,15048,15052,15053,15054,15055,15056,15057,15058,15059,15062,15065,15066,15070,15071,15072,15073,15076,15077,15079,15080,15081,15083,15084,15087,15088,15094,15095,15096,15098,15102,15105,15107,15109,15111,15112,15114,15116,15118,15122,15123,15125,15126,15129,15130,15131,15132,15133,15134,15135,15137,15138,15139,15141,15142,15156,15157,15161,15162,15163,15164,15166,15170,15173,15175,15178,15179,15180,15185,15186,15189,15190,15191,15193,15195,15196,15197,15200,15201,15202,15203,15204,15205,15206,15208,15210,15212,15213,15214,15215,15217,15218,15220,15221,15223,15224,15225,15226,15227,15229,15230,15231,15235,15236,15237,15239,15240,15241,15242,15245,15246,15247,15250,15251,15252,15253,15254,15255,15256,15257,15261,15262,15265,15266,15267,15268,15269,15271,15275,15276,15277,15278,15279,15280,15282,15285,15286,15289,15290,15292,15293,15294,15295,15296,15297,15299,15301,15302,15307,15308,15309,15310,15313,15314,15315,15318,15321,15322,15323,15324,15325,15326,15329,15332,15333,15334,15335,15337,15338,15344,15346,15348,15350,15351,15352,15355,15357,15360,15362,15367,15368,15371,15372,15373,15375,15376,15377,15379,15381,15385,15391,15392,15393,15394,15396,15401,15402,15404,15405,15406,15407,15411,15412,15413,15414,15415,15416,15418,15423,15424,15425,15433,15434,15439,15440,15443,15444,15445,15446,15449,15453,15456,15457,15459,15460,15463,15465,15467,15468,15470,15473,15476,15477,15478,15481,15486,15489,15493,15494,15495,15496,15498,15508,15510,15511,15512,15513,15514,15515,15517,15521,15522,15524,15530,15531,15532,15534,15535,15540,15541,15542,15553,15556,15557,15558,15559,15563,15566,15567,15568,15570,15571,15573,15577,15579,15581,15582,15585,15586,15587,15589,15592,15594,15595,15597,15599,15600,15601,15604,15605,15611,15613,15614,15617,15618,15619,15622,15623,15624,15625,15627,15630,15632,15633,15634,15636,15639,15641,15642,15647,15648,15651,15653,15654,15655,15656,15658,15663,15670,15671,15672,15673,15676,15677,15681,15683,15686,15689,15691,15692,15693,15695,15696,15697,15698,15700,15701,15703,15705,15706,15709,15710,15711,15713,15714,15715,15716,15717,15718,15719,15720,15721,15722,15723,15725,15726,15727,15729,15730,15733,15734,15738,15740,15742,15747,15748,15749,15756,15757,15758,15759,15761,15763,15764,15766,15767,15768,15769,15770,15772,15773,15774,15775,15776,15777,15778,15779,15782,15784,15785,15787,15790,15792,15793,15794,15796,15799,15802,15805,15806,15808,15810,15813,15814,15815,15818,15820,15825,15826,15827,15830,15831,15832,15833,15835,15836,15837,15839,15840,15841,15843,15844,15848,15849,15850,15851,15853,15854,15855,15856,15857,15858,15859,15863,15867,15868,15869,15870,15877,15878,15880,15881,15882,15883,15885,15887,15894,15895,15897,15899,15900,15901,15902,15903,15904,15905,15907,15909,15911,15915,15917,15918,15919,15921,15923,15924,15925,15927,15931,15932,15934,15937,15938,15939,15942,15943,15944,15945,15952,15953,15957,15958,15959,15963,15967,15968,15969,15972,15974,15976,15977,15978,15979,15982,15983,15985,15986,15987,15989,15990,15995,15997,15999,16003,16004,16005,16006,16007,16011,16012,16013,16014,16015,16016,16018,16019,16021,16022,16023,16024,16026,16027,16028,16029,16031,16034,16036,16037,16038,16039,16040,16044,16046,16047,16048,16049,16050,16052,16053,16060,16061,16062,16064,16065,16067,16068,16069,16072,16073,16074,16075,16076,16077,16078,16080,16081,16082,16083,16084,16085,16088,16090,16092,16093,16094,16095,16096,16099,16101,16102,16104,16105,16106,16107,16108,16109,16110,16111,16112,16115,16116,16117,16119,16120,16121,16123,16124,16125,16126,16127,16128,16129,16130,16131,16133,16134,16135,16141,16142,16143,16144,16146,16149,16150,16151,16152,16156,16157,16158,16159,16162,16163,16166,16168,16169,16170,16171,16172,16174,16175,16177,16178,16186,16188,16189,16190,16191,16194,16198,16199,16200,16203,16204,16205,16206,16207,16208,16209,16210,16211,16212,16213,16216,16218,16219,16222,16223,16224,16226,16228,16229,16232,16235,16237,16238,16239,16240,16241,16243,16244,16245,16248,16249,16250,16252,16253,16254,16255,16258,16259,16260,16261,16262,16263,16265,16267,16271,16272,16275,16277,16279,16280,16281,16286,16290,16296,16297,16299,16300,16301,16302,16303,16304,16305,16306,16307,16309,16314,16316,16317,16318,16319,16320,16321,16323,16326,16327,16328,16330,16331,16332,16334,16335,16337,16339,16341,16342,16344,16346,16348,16349,16350,16351,16352,16355,16356,16358,16361,16362,16364,16367,16368,16369,16370,16371,16372,16373,16376,16380,16383,16388,16389,16391,16393,16395,16396,16397,16398,16399,16407,16408,16410,16414,16417,16418,16419,16420,16424,16426,16427,16428,16430,16431,16443,16444,16447,16448,16449,16451,16455,16456,16458,16468,16469,16470,16472,16475,16476,16477,16479,16480,16482,16484,16485,16486,16487,16488,16489,16492,16493,16494,16496,16499,16500,16508,16510,16511,16512,16513,16514,16515,16517,16519,16522,16523,16524,16525,16527,16528,16529,16533,16534,16537,16548,16554,16555,16557,16564,16571,16574,16575,16576,16578,16580,16581,16582,16583,16584,16585,16586,16587,16589,16590,16594,16595,16596,16600,16601,16602,16603,16604,16605,16606,16609,16615,16617,16618,16619,16622,16623,16625,16626,16627,16628,16631,16640,16642,16643,16644,16645,16648,16649,16650,16652,16654,16656,16659,16660,16661,16663,16666,16668,16669,16673,16674,16682,16683,16684,16687,16693,16694,16695,16696,16697,16698,16700,16701,16704,16707,16708,16710,16711,16712,16714,16715,16716,16721,16723,16724,16725,16726,16728,16732,16733,16736,16737,16742,16745,16746,16749,16751,16755,16758,16759,16760,16762,16764,16765,16766,16767,16768,16769,16770,16771,16772,16774,16775,16776,16778,16779,16780,16782,16783,16787,16788,16789,16790,16791,16793,16794,16795,16796,16799,16800,16801,16802,16803,16806,16808,16809,16814,16815,16817,16818,16820,16821,16823,16825,16826,16827,16828,16829,16831,16832,16834,16835,16836,16837,16842,16844,16845,16848,16849,16851,16853,16855,16856,16857,16858,16859,16860,16863,16864,16865,16866,16868,16869,16870,16871,16872,16873,16874,16876,16880,16882,16884,16885,16886,16887,16888,16890,16892,16894,16896,16897,16898,16901,16904,16906,16912,16916,16917,16918,16919,16921,16922,16923,16924,16925,16927,16930,16931,16932,16935,16938,16939,16942,16944,16945,16946,16947,16951,16952,16954,16955,16956,16957,16958,16965,16966,16967,16968,16969,16970,16971,16972,16973,16974,16975,16976,16977,16978,16979,16981,16982,16983,16985,16986,16987,16988,16989,16990,16993,16994,16995,16996,16998,16999,17000,17001,17002,17003,17005,17006,17010,17011,17013,17016,17018,17021,17023,17031,17032,17034,17035,17036,17037,17041,17044,17046,17047,17048,17049,17051,17053,17054,17056,17057,17093,17101,17102,17111,17112,17128,17153,17179,17194,17216,17217,17218,17219,17220,17222,17223,17227,17228,17241,17243,17244,17245,17246,17248,17249,17287,17300,17323,17352,17357,17359,17373,17379,17380,17406,17407,17427,17471,17485,17504,17505,17569,17605,17615,17622,17624,17625,17626,17628,17631,17633,17637,17638,17640,17641,17643,17648,17651,17652,17654,17655,17657,17662,17663,17668,17672,17674,17675,17676,17681,17682,17684,17688,17689,17693,17695,17696,17697,17699,17700,17701,17702,17710,17711,17712,17713,17715,17716,17717,17720,17721,17725,17728,17729,17730,17732,17733,17736,17738,17739,17740,17743,17745,17746,17749,17750,17755,17761,17762,17767,17768,17772,17776,17777,17778,17780,17781,17782,17784,17787,17788,17789,17790,17798,17799,17804,17807,17808,17809,17810,17811,17814,17816,17819,17826,17827,17828,17829,17830,17833,17836,17837,17838,17839,17840,17842,17843,17847,17851,17852,17854,17855,17857,17858,17860,17861,17863,17864,17865,17866,17871,17873,17875,17880,17884,17891,17892,17893,17894,17902,17903,17904,17905,17906,17908,17909,17910,17911,17917,17918,17921,17922,17925,17929,17931,17932,17933,17937,17944,17946,17950,17951,17952,17953,17954,17955,17957,17958,17959,17962,17964,17965,17966,17967,17969,17970,17972,17973,17974,17977,17978,17982,17983,17985,17986,17987,17990,17996,18000,18002,18004,18005,18007,18008,18009,18010,18011,18013,18016,18017,18019,18022,18024,18025,18027,18030,18032,18040,18041,18042,18044,18045,18046,18047,18048,18049,18051,18052,18054,18055,18056,18058,18059,18060,18061,18063,18064,18065,18069,18071,18074,18075,18076,18077,18078,18083,18084,18085,18086,18087,18089,18091,18092,18093,18094,18096,18097,18098,18099,18100,18102,18106,18122,18124,18126,18128,18129,18130,18135,18136,18138,18143,18146,18147,18148,18149,18150,18151,18152,18153,18154,18157,18161,18162,18163,18165,18167,18169,18170,18177,18181,18183,18184,18186,18192,18193,18194,18198,18199,18202";

    private String camList = "17747";

    private String increamentList = "13690,15573,13785,16082,14603,14987,16121,16356,16048,13281,14028,16316,13795,15495,16129,14918,14877,17903,14014,14354,15190,16389,13663,15905,16618,16038,16044,13275,14302,15542,17711,16016,16092,15094,16053,17969,16026,15989,16444,15995,13955,16006,15026,13731,16005,14040,16130,14252,15867,16259,14409,15931,13682,14438,15530,15997,13744,13790,6519,16817,16124,16660,16107,16011,14035,16004,17838,13266,16248,16019,14064,13904,13707,14618,16027,15982,13673,13740,16174,13279,13761,15808,16007,13860,16047,15793,16021,13001,13730,13861,15987,13864,14113,15139,13655,17216,16837,16866,17003,14208,16134,14455,17810,14888,13793,15855,13038,14077,13148,15204,13916,16128,15468,14905,14498,16776,16701,16039,14604,17858,16120,15301,16146,16116,16072,16470,15719,14161,16695,13813,16049,16099,13638,16095,14110,16013,15856,10724,16944,16988,17000,15057,16244,14860,16123,13966,14411,14073,16111,16673,15494,14262,13193,17828,14704,13396,16060,13278,16358,14081,14003,6519,13901,14885,18154,12931,13282,15077,15268,15986,14796,13595,14472,14404,13796,13010,16306,15014,14012,14084,15252,15979,14325,15423,1973,16028,14337,14352,14475,14817,14435,14708,13969,17842,15080,13918,14199,13973,15470,15907,15083,13763,16077,13216,13634,13693,15229,16110,17837,16938,16958,13796,14123,10724,16018,13528,14243,15999,16300,16115,15231,15957,14657,15985,14069,17837,16080,15081,13593,15236,14165,15235,18153,13936,17684,17804,15904,14259,15566,15911,12856,16052,14237,14951,16012,15185,15164,16265,13975,14878,13002,14324,14124,16062,16064,16081,15517,16050,15055,13751,16024,16061,16110,16119,18147,14033,16074,17833,17112,16803,16918,16836,17006,15506,16524,13787,14452,13913,13909,14453,16125,15201,13712,15251,16096,17712,15250,16085,15677,16112,16290,13905,16003,15581,13979,14027,16775,16029,16320,14083,13375,15990,15556,15162,12840,16023,14506,16424,14370,14507,16131,16015,13831,15254,15116,14120,14208,13716,16631,14627,16987,17747,13800,17111,17001,16302,13983,14156,16127,15344,15449,12992,17657,16084,14368,15351,13721,13697,12933,13935,14406,12855,13119,13997,15030,15983,13402,16576,16141,14039,16069,13945,15792,17641,16143,16133,16046,14149,15863,15269,16031,13733,14318,13852,15326,13800,13639,15671,18146,13009,15440,16022,14500,13792,16989,15544,16848,16947,16826,16834,13266,13794,16606,15978,15439,14570,13985,17782,14328,13648,16108,14187,15541,14791,15189,15118,13930,16806,17651,17747,13006,15072,15360,16525,14078,16068,16094,16093,17615,15255,16430,16040,13003,16073,15114,13435,15163,15253,16190,16443,16802,16482,16361,15129,18150,16484,14706,15805,13536,16240,16499,14009,14836,14160,16075,16034,16865,15507,16956,16853,16978";

    @Autowired
     private  OpenCampaignScheService openCampaignScheService;
    @Autowired
    private MktCampaignMapper campaignMapper;



    @Override
    public Map<String, Object> openCampaignScheForDay() {
        Map<String ,Object> result = new HashMap<>();
        Date start = DateUtil.getStartTime();
        Date end = DateUtil.getnowEndTime();
        System.out.println("start"+DateUtil.date2StringDate(start));
        System.out.println("end"+DateUtil.date2StringDate(end));
        List<MktCampaignDO> campaignDOS = campaignMapper.listByCreateDate(start, end);
        for (int i = 0 ; i< campaignDOS.size();i++){
            try {
                Map<String ,Object> dataMap = openCampaignScheService.openCampaignScheForDay(campaignDOS.get(i).getMktCampaignId());
                OpenCampaignScheEntity campaign = new OpenCampaignScheEntity();
                if (dataMap.get("code").toString().equals("200")){
                    campaign = (OpenCampaignScheEntity) dataMap.get("data");
                    String num = getNum(i);
                    Map<String,Object>  map = new HashMap<>();
                    List<OpenCampaignScheEntity> campaignScheEntities = new ArrayList<>();
                    campaignScheEntities.add(campaign);
                    map.put("totalCount",campaignDOS.size());
                    map.put("mktCampaignDetails",campaignScheEntities);
                    exportFile(map,"A",num,i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }




    @Override
    public Map<String, Object> openCampaignScheForMonth() {
        Integer lastMonth = DateUtil.getLastMonth();
        Date firstDayOfMonth = DateUtil.getFirstDayOfMonth(lastMonth);
        Date lastDayOfMonth = DateUtil.getLastDayOfMonth(lastMonth);
        Date date = DateUtil.string2DateTime4Day("2020-01-01");
        System.out.println("start"+DateUtil.date2StringDate(firstDayOfMonth));
        System.out.println("end"+DateUtil.date2StringDate(new Date()));
        List<MktCampaignDO> campaignDOS = campaignMapper.listByCreateDate(date,lastDayOfMonth);

        List<String> stringList = ChannelUtil.StringToList(increamentList);

        Map<String ,Object> result = new HashMap<>();
        Map<String,Object>  map = new HashMap<>();
        List<OpenCampaignScheEntity> campaignScheEntities = new ArrayList<>();
        List<List<String>> lists = ChannelUtil.averageAssign(stringList, 1);
        int a = 1;
        for (List<String> list : lists) {
            final int  aaa = a;
            new Thread(){
                public void run(){
                    List<String> idList = new ArrayList<>();
                    for (int i = 0 ; i< list.size();i++){
                        if (idList.contains(list.get(i))){
                            continue;
                        }
                        idList.add(list.get(i));
                        try {
                            Map<String ,Object> dataMap = openCampaignScheService.openCampaignScheForDay(Long.valueOf(list.get(i)));
                            OpenCampaignScheEntity campaign = new OpenCampaignScheEntity();
                            if (dataMap.get("code").toString().equals("200")){
                                campaign = (OpenCampaignScheEntity) dataMap.get("data");
                                campaignScheEntities.add(campaign);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            log.info("活动文件上传失败:"+Long.valueOf(list.get(i)));
                        }
                    }
                    map.put("totalCount",list.size());
                    map.put("mktCampaignDetails",campaignScheEntities);
                    String num = getNum(aaa);
                    exportFile(map,"A",num,0);
                }
            }.start();
            a++;
        }
        return result;
    }

    public String  getNum(int num) {
        num = num+1;
        String i = "";
        if (num < 10) {
            i = "00" + num;
        } else if (num >= 100) {
            i = String.valueOf(num);
        } else {
            i = "0" + num;
        }
        return i;
    }

    /**
     * 文件名定义：
     * 文件的前10位是发起方系统编码:6001040005
     * 第11到20位是落地方系统编码:1000000038
     * 第21到28位，填写当前业务功能编码:BUS63001
     * @param campaign
     * @return
     */

    private Map<String, Object> exportFile(Map<String,Object> campaign,String flg,String intNum,int i) {
        System.out.println(JSON.toJSONString(campaign));
        Map<String, Object> resultMap = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYYMMdd");
        String Date = dateFormat.format(new Date());
        String dataFileName = "6001040005"+"1000000038"+ "BUS63001"+ Date + flg + intNum + ".json";     //文件路径+名称+文件类型
        File dataFile = new File(dataFileName);
        SftpUtils sftpUtils = new SftpUtils();
        final FTPClient ftp = sftpUtils.ftpConnect(ftpAddress, ftpPort, ftpName, ftpPassword);
        boolean uploadResult = false;
        if (!dataFile.exists()) {
            // 如果文件不存在，则创建新的文件
            try {
                dataFile.createNewFile();
                sftpUtils.writeFileContent(dataFile.getName(), JSON.toJSONString(campaign));
                log.info("ftp已获得连接");
                sftpUtils.ftpUploadFile(ftp,exportPath, dataFile.getName(), new FileInputStream(dataFile));
                sftpUtils.ftpDisConnect(ftp);
                resultMap.put("resultMsg", "success");
            } catch (Exception e) {
                log.error("活动文件上传失败！Expection = ", e);
                resultMap.put("resultMsg", "faile");
            } finally {
                if (uploadResult) {
                    log.info("上传成功，开始删除本地文件！");
                }
                boolean b1 = dataFile.delete();
                if (b1) {
                    log.info("删除本地文件成功！"+dataFileName);
                }
                sftpUtils.ftpDisConnect(ftp);
            }
        } else {
            log.info(dataFileName + "文件已存在！");
        }
        return resultMap;
    }




}
