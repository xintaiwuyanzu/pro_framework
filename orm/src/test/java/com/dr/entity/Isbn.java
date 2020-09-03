package com.dr.entity;

import com.dr.framework.common.entity.BaseCreateInfoEntity;
import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;

/**
 * ISBN基本信息，对应CNMARC内容，id是ISBN规则号码，指令为010 a字段
 * 详情查看
 * <a href='https://mubu.com/doc/explore/10995'>幕布个人博客</a>
 * <a href='https://baike.baidu.com/item/CNMARC'>百度百科</a>
 * <a href='http://lhml.calis.edu.cn/calis/lhml/kejian/1306/%E7%AC%AC2%E8%AE%B2%20CNMARC%E6%A0%BC%E5%BC%8F%E4%BB%8B%E7%BB%8D201305.pdf'>CNLIS联机合作编目中心</a>
 * <a href='http://www.nlc.cn/newgtcb/gtcbywyt/bmgz/dyjwxbmgz/201106/t20110629_45686.htm'>国家采编网</a>
 *
 * @see com.dr.tushu.jyg.entity.ilas.ZBilios 暂时使用ilas的isbn数据库
 * @deprecated
 */
//@Table(name = "ISBN", comment = "ISBN基本信息", module = "tushu")
@Deprecated
public class Isbn extends BaseCreateInfoEntity {
    /**
     * =======================
     * 【005】 记录版次标识
     * =======================
     */
    @Column(name = "sourceDate", comment = "ISBN数据源处理时间，指令为005", type = ColumnType.DATE)
    private long sourceDate;
    @Column(name = "price", comment = "图书价格 指令为010  d字段", length = 100)
    private String price;
    /**
     * ==========
     * 035 系统控制号码
     * ==========
     */
    @Column(name = "sysControlNumber", comment = "系统控制号码 指令为035 a字段", length = 100)
    private String sysControlNumber;
    //TODO 049是你妈的啥

    /**
     * ================
     * 100 通用处理数据
     * 本字段包含用于记录任何媒体资料的固定长数据。
     * ================
     */

    /**
     * $a子字段固定长数据元素表
     * 数据元素名称	               	字符数	            字符位置
     * (1)记录生成时间(必备)	         8                  0-7
     * (2)出版时间类型	             1                  8
     * (3)出版年1               	 4                  9-12
     * (4)出版年2	                4	                13-16
     * (5)阅读对象代码	            3	                17-19
     * (6)政府出版物代码            1                   20
     * (7)修改记录时间(必备)	        1                   21
     * (8)编目语种代码(必备)	        3                   22-24
     * (9)音译代码	                1                   25
     * (10)字符集(必备)	           4	                26-29
     * (11)补充字符集	           4	                30-33
     * (12)题名语系代码	           2                   34-35
     */
    @Column(name = "commonInfo", comment = "通用处理数据  指令为100 a字段", length = 100)
    private String commonInfo;
    /**
     * ======================================
     * 101 作品语种
     * 本字段包含作品实体的整体、部分和题名的语种代码，以及该作品为译作时，其原作的语言标识。
     * 凡是有语言文字的作品均为必备字段, 不可重复。
     * 指示符1：翻译指示符。表明作品是否是译文或含译文。
     * 0－作品为原文
     * 1－作品为译文
     * 2－包含译文(摘要除外)
     * 指示符2：未定义，填空格。
     * 每一子字段用三个字符位置标识语种代码。在未修订出ISO标准之前，暂用“美国国会图书馆语言和语言代码修订表”如下：
     * 美国国会图书馆语言的语言代码修订表
     * 阿尔巴尼亚语	alb	冰岛语		ice
     * 波斯语(近代) 	per	阿拉伯语	    ara
     * 印度语       inc	波兰语		pol
     * 孟加拉语     	ben	印尼语		ind
     * 葡萄牙语	    por	保加利亚语	bul
     * 意大利语	    ita	罗马尼亚语	rum
     * 缅甸语		bur 日语		    jpn
     * 俄语		    rus	高棉语		cam
     * 哈萨克语	    kaz	梵语		    san
     * 汉语		    chi	吉尔吉斯语	kir
     * 斯洛伐克语	slo	捷克语		cze
     * 刚果语		kon	西班牙语    	spa
     * 丹麦语		dan	朝鲜语		kor
     * 瑞典语		swe	荷兰语		dut
     * 寮国语		lao	叙利亚语	    syr
     * 埃及语		egy	拉丁语		lat
     * 泰语		    tha	英语		    eng
     * 马来语		may	藏语		    tib
     * 世界语		esp	马尔他语    	mlt
     * 土耳其语	    tur	法语		    fre
     * 蒙古语		mon	维吾尔语	    uig
     * 德语		    ger	匈牙利语    	hun
     * 越南语		vie	西腊语(近代)	gre
     * 尼泊尔语	    nep	瑶族语		yao
     * 希伯来语	    heb	挪威语		nor
     * 犹太语		yid	多种语言    	mul
     * ──  子字段标识符、内容、可否重复 ──
     * a  正文语种                           可重复
     * b  中间语种(当作品不是直接译自原作时)   可重复
     * c  原作语种                           可重复
     * d  提要或文摘语种                     可重复
     * e  目次页语种(与正文页语种不同时)      可重复
     * f  题名页语种(与正文语种不同时)        可重复
     * g  正题名语种(与正文语种不同时)       不重复
     * h  歌词语种(与正文语种不同时)         可重复
     * i  附件语种(与正文语种不同时)         可重复
     * j  字幕语种(与正文语种不同时)         可重复
     * 当一个子字段重复出现时，语种编码的排列次序将反映该语种在作品中使用的程度和重要性。如果对这种区别不能作出判断，则按照语种的代码排列。如果在某个子字段要记录很多个语种，也可使用代码“mul”。
     * ======================================
     */
    @Column(name = "textLanguage", comment = "正文语种  指令为101  a字段")
    private String textLanguage;

    /**
     * ===============================
     * 102 出版或制作国别
     * 本字段包含著录实体的一个或多个出版或制作国的国别代码。
     * 本字段选择使用, 不可重复。
     * 指示符
     * 指示符1:未定义, 填空格。
     * 指示符2:未定义, 填空格。
     * 子字段
     * 子字段标识符	子字段内容		  注释
     * $a	出版或制作国别	 不可重复
     * $b	出版地区     		不可重复
     * ===============================
     */
    @Column(name = "country", comment = "出版国家  指令为102 a字段", length = 50)
    private String country;
    @Column(name = "areaCode", comment = "出版地区  指令为102 b字段", length = 50)
    private String areaCode;
    /**
     * ====================================
     * 105 文字专著
     * 本字段包含有关专著性印刷文字资料的编码数据。
     * 本字段选择使用, 不可重复。在编制印刷文字专著的机读记录中，应提供该字段。
     * 指示符
     * 指示符1:未定义, 填空格。
     * 指示符2:未定义, 填空格。
     * 子字段
     * 子字段标识符    	子字段内容		注释
     * $a	       专著编码数据	  不可重复
     * 子字段$a
     * 数据元素名称		字符数	字符位置
     * (1)图表代码		  4     0-3
     * (2)内容类型代码	  4     4-7
     * (3)会议代码	      1      8
     * (4)纪念文集代码	  1	     9
     * (5)索引指示符		  1	     10
     * (6)文学体裁代码	  1	     11
     * (7)传记代码		  1	     12
     * ====================================
     */
    @Column(name = "monographCode", comment = "专著编码  指令为105 a字段", length = 50)
    private String monographCode;
    /**
     * ====================================
     * 106 文字资料--形态特征
     * 本字段含有文字资料物理形态的编码数据。
     * 本字段选择使用, 不可重复。
     * 指示符
     * 指示符1:未定义, 填空格。
     * 指示符2:未定义, 填空格。
     * 子字段
     * $a  文字资料代码--物理媒体标志，以一位字符代码标示出版物实体的物理介质。
     * 使用下列代码：
     * d = 大型印刷品
     * e = 报纸形式
     * f = 盲文本
     * g = 微型印刷品
     * h = 手写本（抄本、手稿、手绘本）
     * i = 多种媒体（如：带有缩微平片附件的普通印刷出版物）
     * j = 小型印刷品
     * r = 普通印刷品
     * z = 其它形式
     * ====================================
     */
    @Column(name = "shape", comment = "物理形态  指令为106 a字段", length = 20)
    private String shape;
    /**
     * =============================================
     * 200 题名与责任说明
     * 本字段是必备的，不可重复。
     * 指示符
     * 指示符1：题名有无意义指示符
     * 0  	题名无意义 该题名不宜作附加款目。
     * 1	题名有意义 题名作检索点。
     * 指示符2：未定义，填空格
     * 子字段
     * 标识符        子字段内容	              	注释
     * $a            正题名			            必备, 可重复
     * $b	         一般资料标识		        可重复
     * $c	         另一著者的正题名	        可重复
     * $d	         并列题名	                可重复
     * $e	         副题名及其它题名信息	    可重复
     * $f	         第一责任说明	            可重复
     * $g            其余责任说明		        可重复
     * $h	         分辑号			            可重复
     * $i	         分辑名			            可重复
     * $z	         并列题名语种		        不重复
     * $v	         卷标识			            可重复
     * $A	         正题名汉语拼音		        可重复
     * =============================================
     */
    @Column(name = "title", comment = "正题名  指令为200 a字段", length = 200)
    private String title;
    @Column(name = "pinyin", comment = "拼音？  指令为200 9字段", length = 200)
    private String pinyin;
    @Column(name = "dataInfo", comment = "一般资料标识  指令为200 9字段", length = 200)
    private String dataInfo;
    @Column(name = "responser", comment = "第一责任说明  指令为200 f字段", length = 200)
    private String responser;
    /**
     * =============================================
     * 210 出版发行项等
     * 本字段包含出版、发行和制作极其相关日期的信息。本字段对应于《国家标准书目著录》(ISBD)的出版、发行（等）项。
     * 本字段选择使用, 不可重复。
     * 指示符
     * 指示符1:未定义, 填空格。
     * 指示符2:未定义, 填空格。
     * 子字段
     * 子字段标识符	子字段内容		      注释
     * $a		   出版、发行地		   可重复
     * $b		   出版者、发行者地址	可重复
     * $c		   出版者、发行者名称	可重复
     * $d		   出版、发行日期		可重复
     * $e		   制作地			     可重复
     * $f		   制作者地址	      可重复
     * $g		   制作者名称	      可重复
     * $h		   制作日期	      可重复
     * =============================================
     */
    @Column(name = "publishArea", comment = "出版地中文  指令为210 a字段", length = 200)
    private String publishArea;
    @Column(name = "publisherName", comment = "出版社名称  指令为210 c字段", length = 200)
    private String publisherName;
    @Column(name = "publishDate", comment = "出版日期  指令为210 d字段", length = 50)
    private String publishDate;
    /**
     * =============================================
     * 215 载体形态项
     * 本字段含有作品形态特征方面的信息。同ISBD载体形态项对应。
     * 本字段选择使用, 可重复。
     * 指示符
     * 指示符1:未定义, 填空格。
     * 指示符2:未定义, 填空格。
     * 子字段
     * 子字段标识符	    子字段内容           	    注释
     * $a	    	    页码，数量即单位	           可重复
     * $c		        其他形态细节		           不重复
     * $d		        尺寸			           可重复
     * $e		        附件			           可重复
     * =============================================
     */
    @Column(name = "pageNumber", comment = "页码  指令为215 a字段", length = 50)
    private String pageNumber;
    @Column(name = "otherShape", comment = "其他形态细节  指令为215 c字段", length = 100)
    private String otherShape;
    @Column(name = "size", comment = "尺寸  指令为215 d字段", length = 100)
    private String size;
    /**
     * =============================================
     * 330 提要、文摘或全文
     * 本字段包含所编文献的提要、文摘或全文。
     * 本字段选择使用, 如需多个语种记录提要，可重复。
     * 指示符
     * 指示符1:未定义, 填空格。
     * 指示符2:未定义, 填空格。
     * 子字段
     * $a       附注内容        	不可重复
     * =============================================
     */
    @Column(name = "summary", comment = "附注内容  指令为330 a字段", length = 1000)
    private String summary;
    /**
     * =============================================
     * 606 学科名称主题
     * 本字段包含的家族名称是所编文献的一种主题，该家族名称以检索点形式出现，附以经选择的主题附加信息。
     * 本字段选择使用, 可重复。
     * 指示符
     * 指示符1:主题词的级别
     * 0  未指定级别
     * 1  主要词
     * 2  次要词
     * b  无适用的信息
     * 指示符2:未定义，填空格。
     * 子字段
     * 子字段标识符  子字段内容			注释
     * $a	款目要素			   不重复
     * $x	学科主题复分		   可重复
     * $y	地区复分			   可重复
     * $z	年代复分			   可重复
     * $2	系统代码			   不重复
     * $3	规范记录号		   不重复
     * =============================================
     */
    @Column(name = "element", comment = "款目要素  指令为606 a字段", length = 100)
    private String element;
    @Column(name = "subject", comment = "学科主题复分  指令为606 x字段", length = 500)
    private String subject;
    @Column(name = "elementArea", comment = "地区复分  指令为606 y字段", length = 500)
    private String elementArea;
    /**
     * =============================================
     * 701 本字段包括对该作品负有等同责任的其它个人姓名(检索点形式)。若所用的编目条例没有“主要款目”概念，那么所有等同责任的个人姓名都记录在该字段。
     * 本字段可重复。
     * 指示符1：未定义，填空格。
     * 指示符2：标识名称的著录方式。
     * 0 -- 名称以直序方式著录(中国人名适用)
     * 1 -- 名称以倒序方式著录
     * ──  子字段标识符、内容、可否重复 ──
     * a  主标目(对中国人名著录于此)                                         不重复
     * b  人名的其它部分(以姓为主标目时，该字段包括其名)                      不重复
     * c  人名修饰语(不包括年代)                                             可重复
     * d  罗马数字                                                          不重复
     * f  年代(包括朝代)                                                     不重复
     * 3  规范记录号                                                         不重复
     * 4  著作责任   （采用《普通图书著录规则》10.1.5.10说明）                 可重复
     * A  主标目汉语拼音(CALIS格式使用) 不重复
     * 9  主标目汉语拼音(国图格式使用)    不重复
     * 高校馆#1，直叙，＄g   （高校：Hock  公共xx,Hock）
     * =============================================
     */
    @Column(name = "author", comment = "个人名称  指令为701 a字段", length = 50)
    private String author;
    @Column(name = "authorDate", comment = "年代(包括朝代)  指令为701 f字段", length = 50)
    private String authorDate;
    @Column(name = "authorRelate", comment = "著作责任  指令为701 4字段", length = 50)
    private String authorRelate;
    @Column(name = "authorPinYin", comment = "个人名称拼音  指令为701 9字段", length = 100)
    private String authorPinYin;
    /**
     * =============================================
     * 【702】 人名--次要责任者
     * 本字段包括对该作品负有次要责任的个人姓名。
     * 本字段可重复。
     * 指示符1：未定义，填空格。
     * 指示符2：标识名称的著录方式。
     * 0 -- 名称以直序方式著录(中国人名适用)
     * 1 -- 名称以倒序方式著录
     * ──  子字段标识符、内容、可否重复 ──
     * a  主标目(对中国人名著录于此)             不重复
     * b  人名的其它部分(以姓为主标目时，该字段包括其名)                       不重复
     * c  人名修饰语(不包括年代)                  可重复
     * d  罗马数字                                不重复
     * f  年代(包括朝代)                          不重复
     * 3  规范记录号                              不重复
     * 4  著作责任                                可重复
     * （采用《普通图书著录规则》10.1.5.10说明）
     * A  主标目汉语拼音(CALIS格式使用)  不重复
     * 9  主标目汉语拼音(国图格式使用)     不重复
     * =============================================
     */
    @Column(name = "author2", comment = "次要责任者  指令为701 a字段", length = 50)
    private String author2;
    @Column(name = "author2Date", comment = "次要责任者年代(包括朝代)  指令为701 f字段", length = 50)
    private String author2Date;
    @Column(name = "author2Relate", comment = "次要责任者著作责任  指令为701 4字段", length = 50)
    private String author2Relate;
    @Column(name = "author2PinYin", comment = "次要责任者个人名称拼音  指令为701 9字段", length = 100)
    private String author2PinYin;
    /**
     * =============================================
     * 801 记录来源
     * 本字段包含记录来源的说明，包括下述情况之一：产生数据的机构，将数据录制成机读形式的机构，更改原始记录或数据的机构以及发行现行记录的机构。
     * 本字段为必备字段。对于每个负有职责功能的机构, 本字段可重复。
     * 指示符
     * 指示符1:未定义, 填空格。
     * 指示符2:功能指示符
     * 0  原始编目机构	编制记录的数据的机构
     * 1  录制机构	将数据转换成机读形式的机构。
     * 2  更改机构	修正记录的知识内容或记录结构的机构。
     * 3  发行机构	发行记录的机构。
     * 子字段
     * $a  国家	以两个字符代码形式表示编制或发行的国家。不可重复。
     * $b  机构	目前尚无国际和国家的机构代码表，建议采用机构名称的英文缩写形式表示。也可用全名。
     * $c  处理日期	记录更改或发行的日期。数据根据ISO 8601-1989定为全数字形式，年月日之间无分隔符号，即YYYYMMDD。不可重复。
     * $g  编目规则	（著录条例）用于书目著录和见说的编目规则的缩略代码。仅用于指示符2的值为0或2的情况下。本子字段选择使用，可重复。
     * =============================================
     */
   /* @Column(name = "author2", comment = "次要责任者  指令为701 a字段", length = 50)
    private String author2;
    @Column(name = "author2Date", comment = "次要责任者年代(包括朝代)  指令为701 f字段", length = 50)
    private String author2Date;
    @Column(name = "author2Relate", comment = "次要责任者著作责任  指令为701 4字段", length = 50)
    private String author2Relate;
    @Column(name = "author2PinYin", comment = "次要责任者个人名称拼音  指令为701 9字段", length = 100)
    private String author2PinYin;*/
}
