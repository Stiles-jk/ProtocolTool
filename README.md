# 协议解析器开发和使用文档

​	协议解析器目前以jar包的形式提供给各软件使用，其jar包名称为ProtocolTool-1.0.jar。当前解析器的项目结构如下所示：

![](../地测软件/img/图片/解析器项目结构.png)

## 使用文档

### 解析器初始化流程

![](/Users/jiangkeyuan/Desktop/JKY_Daily/地测软件/img/图片/解析器工作流程.png)

#### 对应API调用

* 获取FrameManager对象

```java
FrameManager FM = FrameManager.getInstance();
```

* 加载解析文件

```java
FrameManager FM = FrameManager.getInstance();
FM.createFramesList(String path);
```

### 解析器解析API

​	当前解析器对外只提供一个doParse(byte[] data)方法，用于解析。

```java
public List<ParsedVar> doParse(byte[] data) throws ParsedException, CRCException {

  Frame frame = null;
  for (Frame f : frameList) {
    boolean flag = f.getFlag().checkStarter(data);
    if (flag) frame = f;
  }
  if (frame == null) throw new ParsedException("can't much any farme: " + ByteArrayUtils.printAsHex(data));
  //开始解析
  List<ParsedVar> parsedVars = new ArrayList<>();
  if (frame != null) {
    frame.parse(data, parsedVars, OESwap);
  }
  return parsedVars;
}
```

### 解析结果

​	当前解析对数据流解析完成后，会按照解析文件中的seg标签的顺序结构；生成一个数据类型为ParsedVar的链表。以下介绍ParsedVar对象中的成员变量；

|  成员名称  |  类型   |            含义            |
| :--------: | :-----: | :------------------------: |
| frameName  | String  |          协议名称          |
| frameAddr  | String  | 1553专用，协议对应的子地址 |
| blockName  | String  |           块名称           |
|   parsed   | boolean |    当前数据是否经过解析    |
| valueType  | String  |          数据类型          |
|   value    | Object  |           解析值           |
| deviceName | String  |          设备名称          |
|  deviceId  |  byte   |           设备ID           |
|   buffer   | byte[]  |     解析值对应的数据流     |
|   random   | boolean |     是否为可变长数据块     |

​	ParsedVar中的所有成员属性为public，所以可以直接调用查看和使用（注意，不要修改ParsedVar中变量的值，以免对原数据造成变化）。

#### 解析文件XML

​	解析文件xml的根节点为：frames

```xml
<frames protoMarkIndex="10" OESwap="true">
```

​	属性protoMarkIndex可以自定义，OESwap用于奇偶交换；true表示开启奇偶交换；false表示关闭奇偶交换；例如

```tex
OESwap="true" # 开启奇偶交换
原数据：0x01 0x02 0x03 0x04
交换后数据：0x02 0x01 0x04 0x03
```

##### frame节点

​	frames节点的子节点 frame 节点表示一个协议；

```xml
<frame name="request" id="30">
  <starter var="0x0a,0x00"/>
  <block pass="01" name="state" length="6">
    <seg pass="01" name="innerInfo" type="byte" typesize="1" unit="byte" endian="00"/>
  </block>
  <block pass="00" name="state" length="1">
    <seg pass="00" name="errorCode" type="byte" typesize="1" unit="byte" endian="00"/>
  </block>
  <block pass="01" name="invilidData" length="13">
    <seg pass="01" name="var" type="byte" typesize="1" unit="byte" endian="00"/>
  </block>
  <block pass="00" name="broadcast" length="6">
    <seg pass="00" name="broadcast1" type="short" typesize="2" unit="byte" endian="00"/>
    <seg pass="00" name="broadcast2" type="short" typesize="2" unit="byte" endian="00"/>
    <seg pass="00" name="broadcast3" type="short" typesize="2" unit="byte" endian="00"/>
  </block>
</frame>
```

​	属性：

* name ：协议名称。
* id：协议id，自定义即可。

###### starter节点

协议帧头，属性var的值由16进制表示；当帧头的byte个数大于2，则使用逗号分隔。

###### block节点

​	block：协议分块区域，单位为byte；一个block可以由一个或多个byte组成；

​		属性：

* pass："01"表示解析时当前block可以跳过不解析；"00"表示当前block需要解析，默认为"00";
* name：当前分块区域的名称，自定义即可
* length：当前block的字节个数，单位为byte；

###### loop节点

​	表示对某一个或以上的seg标签进行重复解析操作

​		属性：

* time：重复次数，表示需要重复的次数

被loop节点包裹的seg节点的name回从1开始递增，例如：

```xml
<block pass="00" name="dataBuffera" length="32">
  <loop time="16">
    <seg pass="00" name="var" type="short" typesize="2" unit="byte" endian="01"/>
  </loop>
</block>
```

​		此时 var会从 var1 递增到 var16；

###### random节点

​	子节点：length,表示某个可变长度区域，表示有效数据长度的字段。解析时会根据length节点解析出的长度信息，去循环解析seg标签，这点和loop节点类似。

​	length标签的属性和seg标签一致，所代表的含义也一致，使用length标签，只是为了指明该标签为random的长度专属节点表示。

```xml
<frame name="数据遥测" id="12">
  <starter var="0x0a,0x12"/>
  <block pass="00" name="state" length="1">
    <seg pass="00" name="errorCode" type="byte" typesize="1" unit="byte" endian="00"/>
  </block>
  <block pass="00" name="dataBuffera" length="32">
    <loop time="16">
      <seg pass="00" name="var" type="short" typesize="2" unit="byte" endian="01"/>
    </loop>
	</block>
  <block pass="00" name="dataBuffera" length="32" shouldCheck="true">
    <seg pass="00" name="同步字" type="string" typesize="2" unit="byte" endian="01"/>
    <seg pass="00" name="apid" type="string" typesize="11" preBits="0" unit="bit" endian="01"/>
    <random pass="00" name="sjq">
      <length pass="00" name="len" type="short" typesize="5" preBits="11" end="true" unit="bit" endian="01"/>
      <seg pass="00" name="var" type="short" typesize="2" unit="byte" endian="01"/>
    </random>
  </block>
</frame>
```

​

###### seg节点

​	seg节点是block，random，loop节点的子节点；是协议解析器的最小解析单位。一个seg节点对应协议中的一个待解析数据。

​	当前支持解析的数据包括java中的基本数据类型，string类型，则会将数据专为16进制数展示。

seg节点的属性配置如下表所示

| 属性名称 | 含义                                                         | 可填写内容                                       | 是否必须 |
| -------- | ------------------------------------------------------------ | ------------------------------------------------ | -------- |
| name     | seg节点的名称                                                | 自定义                                           | 是       |
| dev      | 数据对应的设备名称                                           | 和协议设备对应，若该数据不对应设备，则不需要编写 | 否       |
| devId    | 设备ID                                                       | 和协议设备对应，若该数据不对应设备，则不需要编写 | 否       |
| type     | 数据对应的类型                                               | java中的基本数据类型                             | 是       |
| typesize | 解析为type类型的数据需要多少个字节（byte）或位（bit）        | 数字的十进制形式                                 | 是       |
| unit     | typesize的单位，默认为byte                                   | "byte"或"bit"                                    | 是       |
| endian   | 数据解析时的大小端，默认为big                                | 大端为"big"，小端为"little"                      | 是       |
| pass     | 解析时是否跳过该seg，默认为01，跳过                          | 不跳过填写"00",跳过填写"01"                      | 是       |
| preBits  | 该属性只有unit为bit时才可以填写，表示按位解析时前面有多少个bit已经被使用了。 | 填写前面多少个位被使用了的十进制表示即可；       | 否       |
| end      | 当位解析结束后填写，属性值为true，表示位解析结束             | true                                             | 否       |

​	由于在协议中位解析比较特殊，现做举例说明；

例如解析如下协议：

![](../地测软件/img/图片/位解析.png)

​	可以看到从第二个byte开始，一共有16bit长度的数据需要做位解析；之后又有1byte长度的需要做字节解析；因此可以编写如下xml文件

```xml
<frame name="数据遥测" id="12">  <starter var="0x01,0x00"/>  		<block pass="00" name="test" length="5">        <seg pass="00" name="a" type="string" typesize="1" unit="byte" endian="01"/>				<seg pass="00" name="b" type="string" typesize="1" unit="byte" endian="01"/>        <seg pass="00" name="bit1" dev="standby" type="byte" typesize="3" preBits="0" unit="bit" endian="01"/>				<seg pass="00" name="bit2" dev="SF" type="byte" typesize="6" preBits="3" unit="bit" endian="01"/>				<seg pass="00" name="bit3" dev="GPD" type="byte" typesize="7" preBits="9" unit="bit" end="true" endian="01"/>  		</block></frame>
```

​	preBits从0开始计数，直到end="true";preBits的大小为从最近处preBits为0的seg标签的typesize数到当前seg标签的前一个标签的typesize数之和。

#### ParsedVar中属性和解析文件中属性的对应关系

​	frame标签和ParsedVar中的属性具有如下关系

* ParsedVar.frameName = name
* ParsedVar.frameAddr = id

​	block标签和ParsedVar中的属性具有如下关系

* ParsedVar.blockName = name
* ParsedVar.parsed = pass

​	seg标签和ParsedVar中的属性具有如下关系：

* ParsedVar.valueName = name
* ParsedVar.valueType = type
* ParsedVar.value = seg解析出来的值
* ParsedVar.deviceName = dev
* ParsedVar.deviceId = devId
* ParsedVar.buffer = 当前seg解析出value时使用的byte数组

## 开发文档

​	协议解析器的设计分为两部分：

* 协议加载封装
* 解析流程

### 协议加载封装

​	协议加载封装其目的是将解析文件XML中的内容封装为java类；其映射关系如下

* SegNode -----> seg节点
* LoopNode -----> loop节点
* RandomNode -----> random节点
* Block -----> block节点
* Frame -----> frame节点

​     解析器类结构如下图所示

![](../地测软件/img/图片/解析器结构.png)

​	协议封装流程

​		FrameManager::createFramesList(String path);之后会实例化Frame对象，Frame对象中有一个Block类型的链表，按xml文件中的block节点顺序保存Block对象；每个Block对象中保存了ParseableNode类型的链表，其中保存了ParseableNode的不同子类对象（SegNode,LoopNode,RandomNode,TimeNode）。由此封装了该解析文件。

```java
public void createFramesList(String path) throws IOException, JDOMException, NodeAttributeNotFoundException, ProtoNodeNotFoundException {  if (path == null) throw new RuntimeException("path is null");  File xmlFile = new File(path);  if (xmlFile.exists() == false || xmlFile.canRead() == false)    throw new FileNotFoundException("can't find xml file" + path);  Element root = XMLUtils.getRoot(xmlFile);  //奇偶交换  OESwap = Boolean.parseBoolean(root.getAttributeValue("OESwap"));  System.out.println("OESwap: " + OESwap);  List<Element> frames = root.getChildren("frame");  for (Element frame : frames) {    //通过创建Frame对象，封装xml文件    this.frameList.add(new Frame(frame));  }}
```

### 解析过程

​	解析器的解析入口为doParse函数；该函数的作用很简单就是匹配协议，匹配到就使用对应的Frame对象解析，否则报错。

```java
public List<ParsedVar> doParse(byte[] data) throws ParsedException, CRCException {  Frame frame = null;  for (Frame f : frameList) {    boolean flag = f.getFlag().checkStarter(data);    if (flag) frame = f;  }  if (frame == null) throw new ParsedException("can't much any farme: " + ByteArrayUtils.printAsHex(data));  //开始解析  List<ParsedVar> parsedVars = new ArrayList<>();  if (frame != null) {    frame.parse(data, parsedVars, OESwap);  }  return parsedVars;}
```

​	frame对象的parse函数，先进行奇偶交换判断，再通过遍历其Block链表，一个Block，一个Block的去解析数据。并对其进行CRC校验。

​	allFrameData会将pass不为1的Block中的数据组合起来，并保存在ParsedVar链表的最后；

​	crcCheckBytes会组合所有Block中shouldCheck属性为true的数据，最后进行crc校验。

​	调用Block对象的解析函数前，需要将解析数据从原数据中copy一份放到Block的dataBuffer中。

​	Block对象的解析函数为parseData(List\<ParsedVar> res);

```java
public void parse(byte[] recvBytes, List<ParsedVar> parsedVars, boolean OESwap) throws ParsedException, CRCException {  byte[] data = ByteArrayUtils.copySubArray(recvBytes, flag.length, -1);  System.out.println("before swap: " + ByteArrayUtils.printAsHex(data));  data = Odd_EvenSwap(data, OESwap);  System.out.println("after swap: " + ByteArrayUtils.printAsHex(data));  //计算CRC校验  byte[] allFrameData = new byte[0];  byte[] crcCheckBytes = new byte[0];  for (Block block : blocks) {    int length = block.getLength();    try {      byte[] blockBytes = ByteArrayUtils.copySubArray(data, offset, length);      if (block.getShouldCheck()) {        crcCheckBytes = ByteArrayUtils.mergeBytes(crcCheckBytes, blockBytes);      }      if (block.getPass() != 1)        allFrameData = ByteArrayUtils.mergeBytes(allFrameData, blockBytes);      block.setBuffer(blockBytes);//拷贝数据      block.parseData(parsedVars);    } catch (ArrayIndexOutOfBoundsException e) {      System.out.println(block.getName() + "parse error");      System.out.println(block.getLength());    }    offset += length;  }  //CRC校验  int checkSum = CRCUtils.CRC_MPEG_2(crcCheckBytes);  if (checkSum != 0) {    throw new CRCException(checkSum + "");  }  //将整帧数据添加到最后一个ParsedVar中  addAllReceive(parsedVars, data);  System.out.println(ByteArrayUtils.printAsHex(allFrameData));  offset = 0;}
```

​	调用block.parseData(parsedVars)，该函数的主要功能就是遍历Block中包含的ParseableNode链表；并调用ParseableNode的parse方法来解析数据；当然会做判断，如果当前block的pass==1则直接将该区域的数据封装到ParsedVar中，如果当前block到type类型包含了-array则将这一区域的数据解析为对应类型的数组，否则调用ParseableNode的parse方法去解析该数据。ParseableNode的parse方法为每个子类重写的方法；因此以下对不同子类对象的parse方法进行介绍。

```java
public void parseData(List<ParsedVar> parsedVars) throws ParsedException {        //pass current block        if (this.pass == 1) {            ParsedVar var = new ParsedVar();            var.blockName = name;            var.frameName = this.frameName;            var.frameAddr = this.frameAddr;            var.parsed = false;            var.buffer = buffer;            parsedVars.add(var);            return;        }        if (this.pass != 1 && type != null && type.contains("-array") && typesize != -1) {            Object array = parseToPrimaryArray(buffer, type, typesize, endian);            ParsedVar var = new ParsedVar();            var.blockName = name;            var.frameName = this.frameName;            var.frameAddr = this.frameAddr;            var.parsed = true;            var.buffer = buffer;            var.value = array;            var.valueType = type;            var.valueName = name;            parsedVars.add(var);        } else {            for (ParseableNode p : parseableNodes) {                offset = p.parse(buffer, offset, parsedVars);            }        }        //清空buffer中的数据        for (int i = 0; i < buffer.length; i++) {            buffer[i] = 0;        }        offset = 0;    }
```

#### SegNode

1. 创建一个ParsedVar对象
2. 判断当前unit的值：
    * bit按照位解析的方式，函数：parseAsBits
        * 需要将解析需要使用的bit从byte数组中提取出来，并专为long类型来操作
    * byte则按照字节解析的方式，函数：parseAsBytes
3. 以上函数需要对应代码查看

```java
@Overridepublic int parse(byte[] buffer, int offset, List<ParsedVar> parsedVars) {  ParsedVar var = new ParsedVar();  if ("bit".equals(this.unit)) {    long segLong = BitUtils.getBitsFromByteArray(buffer, offset, preBits, typeSize, endian);    System.out.println(Long.toBinaryString(segLong));    if (pass == 1) {      var.parsed = false;      var.value = segLong;    } else {      parseAsBits(segLong, var);    }    if (end) {      offset += ((preBits + typeSize) / 8);//位操作结束，加上offset    }  } else {    byte[] segBytes = null;    try {      //                System.out.println(Arrays.toString(buffer));      //                System.out.println("length : " + length);      segBytes = ByteArrayUtils.copySubArray(buffer, offset, length);      //                System.out.println("copyData : " + ByteArrayUtils.printAsHex(segBytes));    } catch (ArrayIndexOutOfBoundsException e) {      System.out.println("error offset: " + offset);      System.out.println("error segName: " + super.nodeName);      System.out.println("error length: " + length);    }    if (pass == 1) {      var.parsed = false;      var.value = segBytes;    } else {      parseAsBytes(segBytes, var);    }    offset += length;  }  var.valueName = this.nodeName;  var.valueType = this.type;  if (super.dev != null) var.deviceName = super.dev;  if (super.devId != null) var.deviceId = Byte.parseByte(super.devId);  addBaseInfo(var);  parsedVars.add(var);  return offset;}
```

#### LoopNode

* 判断当前loopTime是否为-1，-1则表示将当前传入的buffer数组，全部解析。
* 否则就按loopTime对次数解析buffer中的数据。
* offset是buffer数组开始解析的位置。

```java
/**  * @param buffer     block中的buffer  * @param offset     block中offset的相对位置  * @param parsedVars 解析得到的数据链  * @return*/@Overridepublic int parse(byte[] buffer, int offset, List<ParsedVar> parsedVars) {  int suffix = 1;  int temp = loopTime;  if (temp == -1) {    int index = 0;    while (offset < buffer.length) {      offset = loopSegs.get(index % loopSegs.size()).parse(buffer, offset, parsedVars);      index++;    }  } else {    while (temp > 0) {      //实际上是对LoopNode中的SegNode链表遍历解析      for (SegNode seg : loopSegs) {        offset = seg.parse(buffer, offset, parsedVars,suffix+"");        suffix++;      }      temp--;    }  }  return offset;}
```

#### RandomNode

* 首先从buffer中解析出长度信息
* 再通过长度信息去解析buffer中的有效数据

```java
@Overridepublic int parse(byte[] buffer, int offset, List<ParsedVar> parsedVars) {  //获取长度信息  int lengthInfo = getLengthInfo(buffer, offset, parsedVars);  if ("bit".equals(lengthRangeUnit)) {    if (end) {      offset += (preBits + lengthRange) / 8;    }  } else {    offset += lengthRange;  }  //获取lengthInfo长度的数据  byte[] dataRange = ByteArrayUtils.copySubArray(buffer, offset, lengthInfo);  if (this.pass == 1) {    ParsedVar var = new ParsedVar();    var.buffer = dataRange;    var.value = dataRange;    var.valueName = this.name;    var.valueType = "byte-array";    var.parsed = false;    parsedVars.add(var);    offset += dataRange.length;    return offset;  }  int index = 0;  //按照长度信息解析buffer中的数据  while (lengthInfo > index) {    for (SegNode seg : segs) {      index = seg.parse(dataRange, index, parsedVars);      ParsedVar var = parsedVars.get(parsedVars.size() - 1);      var.random = true;    }  }  offset += dataRange.length;  return offset;}
```



## 测试方法

​	在TestApp中，解析器位于后面板软件，因此只能通过获取原始数据，再在ProtocolTool中测试使用；

1. 实例化FrameManager对象，并加载和TestApp后面板文件的同一个解析文件XML

2. 使用将原始数据作为byte数据传入，这里需要注意，需要在该byte数组的头部添加当前协议的RT地址和子地址；也就是解析文件中的starter节点的var属性的值；
3. 开始测试即可；

### 可能出现的问题

1. can't much any frame

   starter不对，直接查看原始数据中的RT地址和子地址是否正确

2. 解析不完全

   解析时某个block解析错误；需要查看该block中xml描述是否正确；如果描述正确符合，则查看原始数据；对比大小端，奇偶交换。







​	
