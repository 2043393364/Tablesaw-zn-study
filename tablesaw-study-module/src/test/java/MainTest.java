import org.junit.Test;
import tech.tablesaw.api.*;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.selection.Selection;

import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.function.DoublePredicate;
import java.util.function.IntPredicate;
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
    @Test
    public void getDataDemo() throws Exception{

        CsvReadOptions.Builder builder =
                CsvReadOptions.builder("src/test/resources/cancer patient data sets.csv")
                        .separator(',');										// table is tab-delimited

        CsvReadOptions options = builder.build();
        // 创建一个表
        Table table = Table.read().usingOptions(options);
//        String print = table.print();
//        System.out.println(print);

        // 1. 创建一个名为 test1 的列 ---> 该列只有名，没有数据
        DateColumn dateColumn = DateColumn.create("test1");

        // 2. 创建一个数据为 { 1, 3, 4, 3.14, 10.8 } 名为 test2 的列
        double values[] = { 1, 3, 4, 3.14, 10.8 };
        DoubleColumn doubleColumn = DoubleColumn.create("test2", values);

        // 3. 创建一个名为Test的表,并将test2列加至该表中
        Table test = Table.create("Test");
        test.addColumns(doubleColumn);
        System.out.println(test.print());
    }

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
        System.out.println(doubleColumn.print());

        // 4. 将dateColumn和doubleColumn添加至Test表中 ---> 注意每列的长度要一致才能合并在一起
        test.addColumns(dateColumn).addColumns(doubleColumn);
//        System.out.println(test.print());
    }
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

        // 编写Predicate类 实现对数值数据的筛选 ---> 筛选出double_value中大于1.5的数据
        DoublePredicate doublePredicate = new DoublePredicate() {

            @Override
            public boolean test(double value) {
                return value > 1.5;
            }
        };
        Table table_filtered_double = table.where(doubleColumn.eval(doublePredicate));
//        System.out.println(table_filtered_double);

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

    /**
     * 对表格的操作
     */
    @Test
    public void operationAboutTable(){
        // 1. 读取数据
        Table table = Table.read().csv("src/test/resources/cancer patient data sets.csv");

        // 2. 输出数据
        String printData1 = table.print();
//        System.out.println(printData1);

        // 3. 截取数据
        Table first30 = table.first(30);// 截取前30行
        Table last20 = table.last(20); // 最去后20行
//        System.out.println(first30.print());
//        System.out.println(last20.print());

        // 4. 获取表格信息

        // 4.1 获取表格名 --- 默认为文件名
        String tableName = table.name();
//        System.out.println(tableName);
        // 可以使用setName()对表格名进行设置
        table.setName("myData");
//        System.out.println(table.name());

        // 4.2 获取表头名称
        List<String> strings = table.columnNames();
//        System.out.println(strings);

        // 4.3 获取表头结构和数据类型
        Table structure = table.structure();
//        System.out.println(structure.print());

        // 4.4 获取表格的行列信息
        String shape = table.shape();
//        System.out.println(shape);

        // 5 向表格中添加列
        DoubleColumn doubleColumn = DoubleColumn.create("double_value",1000);
        doubleColumn.fillWith(3.6);
        table.addColumns(doubleColumn);
//        System.out.println(table.print());

        // 6 从表格中删除列
        table.removeColumns("double_value");
//        System.out.println(table.print());

        // 7 从表中选择某一列的数据
        Table table_select = table.selectColumns("Age", "Level");
//        System.out.println(table_select.print());

        // 8 根据筛选器删除行
        DoublePredicate doublePredicate = new DoublePredicate() {

            @Override
            public boolean test(double value) {
                return value >= 18 && value < 35;
            }
        };
        Table age = table.where(table.intColumn("Age").eval(doublePredicate));
        System.out.println(age.print());

        // 9. 导出表格
        table.write().csv("myTable.csv");

    }

    /**
     * 对行操作
     */
    @Test
    public void operationAboutRow(){
        // 1. 读取数据
        Table table = Table.read().csv("src/test/resources/cancer patient data sets.csv");
//        System.out.println(table.print());

        // 2. 对某列迭代行数
        for(Row row : table){
            String s = row.getString("Patient Id");
            System.out.println(s);
        }

        // 3. 通过索引迭代
        for (int i = 0; i < table.rowCount(); i++) {
            String s = table.stringColumn("Patient Id").get(i);
            System.out.println(s);
        }
    }
}
