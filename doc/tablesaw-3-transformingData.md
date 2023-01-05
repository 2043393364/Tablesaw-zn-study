# 数据操作
> - 对列操作
> - 对数据表操作
> - 对行操作

## 1. 对列操作
每一列的数据类型都是一致的，因此处理每一列的元素是十分重要的。Tablesaw 提供了以下数据类型的处理类.

|      类名       |            作用             |
|:-------------:|:-------------------------:|
| BooleanColumn |      拥有true和false两种值      |
| StringColumn  |   用于在列中多次出现的类别名。例如："北京"   |
|  TextColumn   | 用于文本值的处理。例如："Hello World" |
|NumberColumn|         处理数值类型的接口         |
|ShortColumn|          用于小整数值           |
|IntColumn|       大多数情况下处理整型的类        |
|LongColumn|          用于大整数值           |
|FloatColumn|         用于单精度浮点数          |
|DoubleColumn| 用于双精度浮点数(大多数处理浮点类型的最好选择)  |
|DateColumn|   处理没有时区的日期。如：2023年1月1日   |
|DateTimeColumn|  处理日期和时间。如：2023年1月1日0时0分  |
|TimeColumn|     处理时分秒。如：12:47:03      |
|InstantColumn|处理不参考时区的单个时间点|

> 所有数学运算均返回 *DoubleColumn* 类型。

### 1.1 创建一个列

示例代码
```java
DateColumn dateColumn = DateColumn.create("date");
```


> 创建一个名为date的DateColumn的列。该列暂时没有值。DoubleColumn、IntColumn类似。

### 1.2 向列中添加数据

#### 1.2.1 在创建列的时候添加数据
示例代码
```java
// 创建一个名为double的DoubleColumn的列，并向该列中添加一些数据
        double values[] = {1.4, 5.6, 7.5};
        DoubleColumn doubleColumn = DoubleColumn.create("double",values);
```
输出
```java
Column: double
1.4
5.6
7.5
```

#### 1.2.2 在创建列之后，再添加数据
示例代码
```java
// 创建一个名为date的DateColumn列，并向该列中添加一些时间
        DateColumn dateColumn = DateColumn.create("date");
        dateColumn.append(LocalDate.of(2022,12,31));
        dateColumn.append(LocalDate.of(2023,1,1));
        dateColumn.append(LocalDate.of(2023,1,2));
```
输出
```java
Column: date
2022-12-31
2023-01-01
2023-01-02
```

### 1.3 修改列中某一个数据

示例代码
```java
double values[] = {1.4, 5.6, 7.5};
DoubleColumn doubleColumn = DoubleColumn.create("double",values);
// 更改第2行的数据为3.14 ---> 注：下标是从0开始的
doubleColumn.set(1, 3.14);
```
输出
```java
Column: double
1.4
3.14
7.5
```


### 1.4 删除列中某一数据

示例代码
```java
double values[] = {1.4, 5.6, 7.5};
DoubleColumn doubleColumn = DoubleColumn.create("double",values);
// 删除第2行数据 ---> 这里的删除并不是从表中完全去除掉该数据所在的空间，而是将需要删除的数据设置为缺失值。
doubleColumn.setMissing(1);
```
输出
```java
Column: double
1.4

7.5
```

### 1.5 综合
```java
import org.junit.Test;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.io.csv.CsvReader;

import java.time.LocalDate;

/**
 * ClassName: MainTest
 * Package: PACKAGE_NAME
 * Description:
 *
 * @Author: Ning
 * @Create: 2023/1/1 - 17:43
 */
public class MainTest {
    /**
     * 对每列数据进行增、删、改操作
     */
    @Test
    public void operationAboutDate(){
        // 1. 创建一个名为Test空表
        Table test = Table.create("Test");
//        System.out.println(test.print());

        // 2. 创建一个名为date的DateColumn列，并向该列中添加一些时间
        DateColumn dateColumn = DateColumn.create("date");
        dateColumn.append(LocalDate.of(2022,12,31));
        dateColumn.append(LocalDate.of(2023,1,1));
        dateColumn.append(LocalDate.of(2023,1,2));
//        System.out.println(dateColumn.print());

        // 3. 创建一个名为double的DoubleColumn的列，并向该列中添加一些数据
        double values[] = {1.4, 5.6, 7.5};
        DoubleColumn doubleColumn = DoubleColumn.create("double",values);
//        System.out.println(doubleColumn.print());

        // 3.1 更改第2行的数据为3.14 ---> 注：下标是从0开始的
        doubleColumn.set(1,3.14);
//        System.out.println(doubleColumn.print());

        // 3.2 删除第2行数据 ---> 这里的删除并不是从表中完全去除掉该数据所在的空间，而是将需要删除的数据设置为缺失值。
        doubleColumn.setMissing(1);
//        System.out.println(doubleColumn.print());

        // 4. 将dateColumn和doubleColumn添加至Test表中 ---> 注意每列的长度要一致才能合并在一起
        test.addColumns(dateColumn).addColumns(doubleColumn);
//        System.out.println(test.print());
    }
}

```

### 1.6 其它常用操作
|          方法名          |            返回值             |
|:---------------------:|:--------------------------:|
|        name()         |           返回列的名称           |
|        type()         | 返回 ColumnType，例如LOCAL_DATE |
|        size()         |           返回元素个数           |
|       isEmpty()       |   如果列没有数据，则返回 true；否则为假    |
| first(n) and last(n)  |      返回第一个和最后一个 n 个元素      |
|     max() 和 min()     |         返回最大和最小元素          |
| top(n) and bottom(n)  |        返回n个最大和最小的元素        |
|        print()        |        返回列的字符串表示形式         |
|        copy()         |          返回列的深拷贝           |
|      emptyCopy()      |    返回一个相同类型和名称的列，但没有数据     |
|       unique()        |         返回只有唯一值的列          |
|     countUnique()     |          返回唯一值的个数          |
|        asSet()        |     将唯一值作为 java Set 返回     |
|       summary()       |        返回特定类型的数据摘要         |
| void sortAscending()  |          对列进行升序排序          |
| void sortDescending() |          对列进行升序排序          |
|     append(value)     |          将单个值附加到列          |
|  appendCell(string)   |     将字符串转换为正确的类型并追加结果      |
|  append(otherColumn)  |       将其他列中的数据附加到这一列       |
|    removeMissing()    |        返回删除所有缺失值的列         |

### 1.7 过滤器或筛选器
过滤方式共有两种：
> - 编写Predicates类来过滤列中的数据
> - 使用内嵌的Predicates类

#### 1.7.1 编写Predicates类来过滤列中的数据

示例代码
```java
import com.sun.management.VMOption;
import org.junit.Test;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.io.csv.CsvReader;

import java.time.LocalDate;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;

/**
 * ClassName: MainTest
 * Package: PACKAGE_NAME
 * Description:
 *
 * @Author: Ning
 * @Create: 2023/1/1 - 17:43
 */
public class MainTest {
    /**
     * 数据过滤
     */
    @Test
    public void filteringTest(){
        Table table = Table.create("test");
        double values[] = {1.3, 1.5, 1.6, 4.3};
        DoubleColumn doubleColumn = DoubleColumn.create("double_value", values);

        LocalDate dates[] = {
                LocalDate.of(2018,6,10),
                LocalDate.of(2020,7,9),
                LocalDate.of(2022,10,6),
                LocalDate.of(2023,10,31),
        };
        DateColumn dateColumn = DateColumn.create("date_value",dates);

        table.addColumns(doubleColumn).addColumns(dateColumn);

        // 编写Predicates类 实现对日期进行筛选 ---> 筛选出闰年的数据
        Predicate<LocalDate> leapYear = new Predicate<>() {
            @Override
            public boolean test(LocalDate localDate) {
                int year = localDate.getYear();
                if (year % 400 == 0 || year % 4 == 0 && year % 100 != 0) {
                    return true;
                } else {
                    return false;
                }
            }
        };
        // 对整张表进行筛选
        Table table_filtered_date = table.where(dateColumn.eval(leapYear));
        // 对该列进行筛选
        DateColumn dateColumn_filtered_date = dateColumn.where(dateColumn.eval(leapYear));

//        System.out.println(table_filtered_date.print());
//        System.out.println(dateColumn_filtered_date.print());

        // 编写DoublePredicate类 实现对double类型的数据筛选 ---> 筛选出double_value中大于1.5的数据
        DoublePredicate doublePredicate = new DoublePredicate() {

            @Override
            public boolean test(double value) {
                return value > 1.5;
            }
        };
        Table table_filtered_double = table.where(doubleColumn.eval(doublePredicate));
        System.out.println(table_filtered_double);


    }
}
```

#### 1.7.2 使用内置的Predicate对数据进行筛选

示例代码
```java
import com.sun.management.VMOption;
import org.junit.Test;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.io.csv.CsvReader;
import tech.tablesaw.selection.Selection;

import java.time.LocalDate;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;

/**
 * ClassName: MainTest
 * Package: PACKAGE_NAME
 * Description:
 *
 * @Author: Ning
 * @Create: 2023/1/1 - 17:43
 */
public class MainTest {
    /**
     * 数据过滤
     */
    @Test
    public void filteringTest(){
        Table table = Table.create("test");
        double values[] = {1.3, 1.5, 1.6, 4.3};
        DoubleColumn doubleColumn = DoubleColumn.create("double_value", values);

        LocalDate dates[] = {
                LocalDate.of(2018,6,10),
                LocalDate.of(2020,7,9),
                LocalDate.of(2022,10,6),
                LocalDate.of(2023,10,31),
        };
        DateColumn dateColumn = DateColumn.create("date_value",dates);

        table.addColumns(doubleColumn).addColumns(dateColumn);


        LocalDate date1 = LocalDate.of(2023,10,31);
        LocalDate date2 = LocalDate.of(2022,10,6);

        // 筛选出当天是当月的最后一天的数据
        Selection lastDayOfMonth = dateColumn.isLastDayOfMonth();
        DateColumn where1 = dateColumn.where(lastDayOfMonth);
        System.out.println(where1.print());

        // 筛选出当天是星期天的数据
        Selection sunday = dateColumn.isSunday();
        DateColumn where2 = dateColumn.where(sunday);
        System.out.println(where2.print());

        // 筛选出时间处于第二季度的数据
        Selection inQ2 = dateColumn.isInQ2();
        DateColumn where3 = dateColumn.where(inQ2);
        System.out.println(where3.print());
        
    }
}
```

其它内置的Predicate可参考<a href="https://www.javadoc.io/static/tech.tablesaw/tablesaw-core/0.43.1/tech/tablesaw/filtering/DeferredDateColumn.html#isMonday--">官方文档</a>


## 2. 对表操作

### 2.1 创建一个表
> 创建一个名为test的表格

```java
Table t = Table.create("test");
```
> 在创建表格的同时添加每一列数据
```java
Table t = Table.create("name", column1, column2, ...)
```

### 2.2 导入表格数据

> *Table.read().csv("路径名")*

```java
Table t = Table.read().csv("myFile.csv");
```

### 2.3 输出表格数据
> *table.print()* : 返回数据的字符串形式 ---> 默认返回前10行和最后10行
```java

String printData = t.print();
System.out.println(printData);

```


### 2.4 截取表格数据
> *table.first(int number)* 和 *table.last(int number)* ---> 截取前number行或后number行的数据
```java
// 截取表格table中前30行数据
Table t = table.first(30);
```

### 2.5 获取表格信息

#### 2.5.1 获取表格名称
> - *table.name()*: 获取表格名称。默认为文件名    
> - *table.setName(String name)*: 对表格名进行重命名
```java
String tableName = table.name();
System.out.println(tableName);
        
table.setName("myData");
```

#### 2.5.2 获取表头名称
> *table.columnNames*: 返回一个字符串列表，列表中的每个元素表示每一列的表头。
```java
List<String> strings = table.columnNames();
System.out.println(strings);
```

#### 2.5.3 获取表头结构和数据类型
> *table.structure()*: 返回一个Table对象，数据为每列的名称和每列的数据类型。
```java
Table structure = table.structure();
System.out.println(structure.print());
```

#### 2.5.4 获取行列信息
> *table.shape()*: 返回一个字符串，该字符串描述的是表格的行列信息。
```java
String shape = table.shape();
System.out.println(shape);
```

> *rowCount()* 和 *columnCount()* 可以单独获取行列信息。

### 2.6 增加或删除表格中的某一列
> *table.addColumns(Column<?>... cols)*: 向表格中追加列
```java
DoubleColumn doubleColumn = DoubleColumn.create("double_value",1000);
doubleColumn.fillWith(3.6);
// 追加列
table.addColumns(doubleColumn);
System.out.println(table.print());
```

> *table.removeColumns(columnName,...)* : 依据列名删除数据。
```java
table.removeColumns("double_value");
System.out.println(table.print());
```

### 2.7 选择列数据
> *table.selectColumns(columnName,...)*: 返回一个Table对象，依据列名进行选择,当然也可以根据索引或列的数据类型进行筛选。
```java
Table table_select = table.selectColumns("Age", "Level");
System.out.println(table_select.print());
```

### 2.8 合并两个表格
> *table1.append(table2)*: 返回合并后的Table对象。
```java
Table table = table1.append(table2);
System.out.pringln(table.print());
```

### 2.9 添加或删除行

> 利用过滤器来删除行
> 以"Age"那一列的数据为标准，筛选大于等于18小于35的数据
```java
DoublePredicate doublePredicate = new DoublePredicate() {
       @Override
       public boolean test(double value) {
            return value >= 18 && value < 35;
      }
};

Table age = table.where(table.intColumn("Age").eval(doublePredicate));
System.out.println(age.print());
```

### 2.10 导出表格
```java
table.write().csv("filename.csv");
```

## 3. 行操作
Tablesaw 的行操作是根据列迭代选择进行的。

```java
//  通过索引迭代 ---> 从列名为Patient Id的对象进行索引
for (int i = 0; i < table.rowCount(); i++) {
    String s = table.stringColumn("Patient Id").get(i);
    System.out.println(s);
}
```

Tablesaw 为表的行操作提供了Row类。
```java
// 2. 对某列迭代行数
for(Row row : table){
    String s = row.getString("Patient Id");
    System.out.println(s);
}
```











