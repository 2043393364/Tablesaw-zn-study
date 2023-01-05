# 导入和导出数据

## 1. Tablesaw支持的数据格式

|    Format     | Import | Export |
|:-------------:|:------:|:------:|
|      CSV      |  YES   |  YES   |
|     JSON      |  YES   |  YES   |
|     HTML      |  YES   |  YES   |
|   固定宽度的文本文件   |  YES   |  YES   |
| RDBMS(来自JDBC) |  YES   |        |
|     EXCEL     |  YES   |        |

## 2. 数据的导入
> 注:JSON、HTML表格、Excel文件被打包在单独的模块中，若需要读取这类文件，需要将对应的模块导入，才能使用Table.read()方法。    

<a href="https://www.javadoc.io/doc/tech.tablesaw/tablesaw-core/0.31.0/tech/tablesaw/io/DataFrameReader.html">具体可查看官方文档</a>

### 2.1 文本文件(CSV、制表符分隔、固定宽度字段等)
从本地中读入一个CSV文件，最简单的就是使用如下方法:
```java
Table t = Table.read().file("myFile.csv");
```
> 该方法为数据中的所有内容提供了默认值。假设所导入的数据之间是用逗号分隔，并且有一个Table行，若存在默认值不正确的情况，可以使用CsvReadOptions类来自定义加载。

示例代码
```java
CsvReadOptions.Builder builder = 
	CsvReadOptions.builder("myFile.csv")
		.separator('\t')										// table is tab-delimited
		.header(false)											// no header
		.dateFormat("yyyy.MM.dd");  				// the date format to use. 

CsvReadOptions options = builder.build();

Table t1 = Table.read().usingOptions(options);
```

> - *builder*: 传入文件路径
> - *separator*: 传入数据之间的分隔标识符
> - *header*: 若文件中不存在单独的Table行，则需传入false来表示，此时文件中的所有内容均视为数据。
> - *dataFormat*: 传入文件中读取日期的格式。文件中的所有日期均会采用该格式进行读取。
> - 
在创建表格时，会根据加载的文件名设置默认名称，若想修改名称，可使用 *table.setName(aString)* 来进行设置

#### 2.1.1 列
Tablsaw 在读取文件中的每一列数据时，都会对每一列的数据类型进行适当推测。可以使用参数 *sample(false)* 以在执行数据类型的推测时考虑所有数据。    
若没有适合的数据类型，则默认以 *StringColumn* 类型进行读取。

#### 2.1.2 指定每列的数据类型
通过 *ColumnType* 对象数组传递给 *read().csv()* 方法来指定每列的数据类型。

示例代码
```java
// 对象数组
ColumnType[] types = {LOCAL_DATE, INTEGER, FLOAT, FLOAT, CATEGORY};

Table t = Table.read().usingOptions(CsvReadOptions
    .builder("myFile.csv")
    .columnTypes(types));

```
> - 通过指定每列的数据类型，可以加快数据读取的速度，因为程序不需要再花时间对每列的数据类型进行推测。
> - 在某些情况下，Tablsaw无法正确推测出每列的数据类型，此时就需要对数据类型进行指定。

当数据表每列数据很多时，手动书写每列的数据类型是非常不现实的。Tablesaw提供了一个输出每列所推断出的数据类型的方法。

示例代码
```java
CsvReadOptions.Builder builder =
        CsvReadOptions.builder("myFile.csv")
        .separator('\t')										// table is tab-delimited
        .header(false)											// no header
        .dateFormat("yyyy.MM.dd");  				// the date format to use. 
        
        CsvReadOptions options = builder.build();
        
        String types = new CsvReader().printColumnTypes(options);
        System.out.println(types);
```
输出：
```java
ColumnType[] columnTypes = {
  LOCAL_DATE, // 0 date 
  SHORT_INT,  // 1 approval 
  CATEGORY,   // 2 who 
}
```
> 我们可以直接将输出内容复制粘贴下来 用于代码中，然后只需修改某一部分即可，大大减少了工作量   
> 除此之外，你还可以通过指定每列的数据类型来跳过某一列的数据载入。   

示例代码
```java
ColumnType[] types = {SKIP, INTEGER, FLOAT, FLOAT, SKIP};

Table t = Table.read().usingOptions(CsvReadOptions
    .builder("myFile.csv")
    .columnTypes(types));
```
> 在上述代码中，第一列和最后一列将不被读入

#### 2.1.3 缺失数据
Tablesaw 有一组预定义的字符串( *"NaN"*, *" \* "*, *"NA"*, *"null"*, *""*)，在从CSV文件读取数据时，会将这组字符串解释为缺失数据。   
> 若你的数据表中的缺失数据指示符不是上述预定义的字符串中的一个，如:'-'。则可以使用如下代码进行构建：

示例代码
```java
Table t = Table.read().usingOptions(CsvReadOptions
    .builder("myFile.csv")
    .missingValueIndicator("-"));
```

#### 2.1.4 日期与时间的处理
给每个时间列确定一个精确的读入格式

示例代码
```java
Table t = Table.read().usingOptions(CsvReadOptions
    .builder("myFile.csv")
    .locale(Locale.FRENCH)
    .dateFormat("yyyy.MM.dd") // 日期格式
    .timeFormat("HH:mm:ss")    // 时间格式
    .dateTimeFormat("yyyy.MM.dd::HH:mm:ss"); // 日期时间格式
```

#### 2.1.5 从Web中读入数据
通过CSV文件的地址，从网络中读入

示例代码
```java
ColumnType[] types = {SHORT_INT, FLOAT, SHORT_INT};
String location = 
    "https://raw.githubusercontent.com/jtablesaw/tablesaw/master/data/bush.csv";
Table table = Table.read().usingOptions(CsvReadOptions.builder(new URL(location))
    .tableName("bush")
  	.columnTypes(types)));
```

#### 2.1.6 编码格式

Tablesaw的文件读取默认使用UTF-8的编码格式，若文件的编码格式不是UTF-8，则需要指定一个正确的编码。

示例代码
```java
InputStreamReader reader = new InputStreamReader(
			new FileInputStream("somefile.csv"), Charset.forName("ISO-8859-1"));

Table restaurants = Table.read()
		.usingOptions(CsvReadOptions.builder(reader, "restaurants"));
```

### 其它
Tablesaw 支持从HTML、JSON和Excel导入数据。不过需要额外添加Maven依赖项。

```xml
<dependency>
  <groupId>tech.tablesaw</groupId>
  <artifactId>tablesaw-html</artifactId>
</dependency>
```

```xml
<dependency>
  <groupId>tech.tablesaw</groupId>
  <artifactId>tablesaw-json</artifactId>
</dependency>
```

```xml
<dependency>
  <groupId>tech.tablesaw</groupId>
  <artifactId>tablesaw-excel</artifactId>
</dependency>
```

