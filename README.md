TBJMap
======

JMap Enhanced Edition

基于Serviceability Agent,对JMap做了增强,可以方便的输出JVM堆中,每一个分区的对象实例个数和大小的Histogram图

使用方法:

`java -classpath /home/admin/tbjmap.jar:/java/lib/sa-jdi.jar sun.jvm.hotspot.tools.TBJMap -histo PID > jmap_output.log`
`java -classpath /home/admin/tbjmap.jar:/java/lib/sa-jdi.jar sun.jvm.hotspot.tools.TBJMap -oldhisto PID > jmap_oldoutput.log`
`java -classpath /home/admin/tbjmap.jar:/java/lib/sa-jdi.jar sun.jvm.hotspot.tools.TBJMap -permhisto PID > jmap_permoutput.log`

`java -classpath /home/admin/tbjmap.jar:/java/lib/sa-jdi.jar sun.jvm.hotspot.tools.TBDumpClassURL PID > class_url.log`

`java -classpath /home/admin/tbjmap.jar:/java/lib/sa-jdi.jar sun.jvm.hotspot.tools.TBDirectMemorySize PID`

执行完毕会输出如下的信息:
> 
> Old Object Histogram:
> 
> num	all instances/bytes	old instances/bytes	Class description
> -----------------------------------------------------------------------------------
> 1:	1831111/70874072	1798386/68395648	char[]

> 2:	1871467/59886944	1840369/58891808	java.lang.String

> 3:	1224464/39182848	1224384/39180288	java.util.HashMap$Entry

> 4:	188390/26556456	188317/26547800	java.util.HashMap$Entry[]

> 5:	395298/25299072	395298/25299072	forest.dataobject.impl.DefaultPropertyValueDO

> 6:	1046319/25111656	1046181/25108344	java.lang.Long

> 7:	332206/23910336	329739/23821816	java.lang.Object[]

> 8:	117127/19404056	88705/15361808	long[]

> 9:	147064/17362472	134052/15619640	byte[]

> 10:	156284/17022448	136050/15396256	int[]

> 11:	412608/16504320	412402/16496080	java.util.LinkedHashMap$Entry

> 12:	325472/10415104	325472/10415104	forest.dataobject.impl.DefaultValueDO

> 13:	304452/9742464	304452/9742464	forest.dataobject.impl.FeatureDO

> 14:	190344/9136512	190344/9136512	forest.dataobject.impl.IntegerMapSupportArrayList

> 15:	188028/9025344	187971/9022608	java.util.HashMap

> 16:	88013/7041040	88013/7041040	forest.dataobject.impl.FileStdCategoryPropertyDO

**线上使用建议先offline应用**