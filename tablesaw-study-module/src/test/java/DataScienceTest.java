import org.junit.Test;
import smile.clustering.KMeans;
import smile.data.formula.Formula;
import smile.regression.LinearModel;
import smile.regression.OLS;
import tech.tablesaw.api.*;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.LinePlot;
import tech.tablesaw.plotly.api.ScatterPlot;

import javax.swing.text.TabableView;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * ClassName: DataScienceTest
 * Package: PACKAGE_NAME
 * Description:
 *
 * @Author: Ning
 * @Create: 2023/1/5 - 0:47
 */
public class DataScienceTest {

    @Test
    public void regressionTest(){
        // 1. 导入数据
        Table baseball = Table.read().csv("src/test/resources/baseball.csv");

        // 2. 过滤数据，只选取2002年以前的
        Table moneyball = baseball.where(
                baseball.intColumn("year").isLessThan(2002)
        );

        // 3. 散点图可视化
        NumericColumn<?> wins = moneyball.nCol("W");
        NumericColumn<?> year = moneyball.nCol("Year");
        Column<?> playoffs = moneyball.column("Playoffs");

        Plot.show(
                ScatterPlot.create(
                        "Regular season wins by years",
                        moneyball,
                        "W",
                        "year",
                        "playoffs"
                )
        );

        // 4. 使用交叉表进行量化。交叉表计算属于不同组的观测值的数量或百分比。
        BooleanColumn ninetyFivePlus = BooleanColumn.create("95+ Wins", wins.isGreaterThanOrEqualTo(95), wins.size());
        moneyball.addColumns(ninetyFivePlus);
        Table xtab95 = moneyball.xTabColumnPercents("Playoffs", "95+ Wins");

        for(Object ea: xtab95.columnsOfType(ColumnType.DOUBLE)){
            ((NumberColumn)ea).setPrintFormatter(NumberColumnFormatter.percent(1));
        }

        NumberColumn RS = (NumberColumn) moneyball.numberColumn("RS");
        NumberColumn RA = (NumberColumn) moneyball.numberColumn("RA");

        NumberColumn runDifference = RS.subtract(RA).setName("RD");
        moneyball.addColumns(runDifference);

        Plot.show(
                ScatterPlot.create(
                        "Run Difference x Wins",
                        moneyball,
                        "RD",
                        "W"
                )
        );

        // 5. 使用OLS最小二乘法进行回归建模
        LinearModel winModel = OLS.fit(Formula.lhs("W"), moneyball.selectColumns("RD", "W").smile().toDataFrame());
        System.out.println(winModel);

        // 6. 预测
        double[] runDifferential = {135};
        double predict = winModel.predict(runDifferential);
        System.out.println(predict);
    }

    @Test
    public void crossTableTest(){
        Table table = Table.read().csv("src/test/resources/bush.csv");
        StringColumn month = table.dateColumn("date").month();
        month.setName("month");
        table.addColumns(month);
        // 执行交叉表操作
        Table counts = table.xTabCounts("month", "who");
        System.out.println(counts.print());

        Table percents = table.xTabTablePercents("month", "who");
        percents
                .columnsOfType(ColumnType.DOUBLE)
                .forEach(
                        x->((NumberColumn) x).setPrintFormatter(NumberColumnFormatter.percent(1))
                );
        System.out.println(percents);
    }

    @Test
    public void kMeansTest(){
        Table pickups = Table.read().csv("src/test/resources/uber-pickups-apr14.csv");

        // 筛选 --- 删除lat大于40.91 小于40.50的数据（保留40.50到40.91的数据）
        pickups = pickups.dropWhere(
                pickups.doubleColumn("lat").isGreaterThan(40.91)
        );
        pickups = pickups.dropWhere(
                pickups.doubleColumn("lat").isLessThan(40.50)
        );
        // 筛选 --- 删除lon大于-73.8 小于-74.05的数据（保留-74.05到-73.8的数据）
        pickups = pickups.dropWhere(
                pickups.doubleColumn("lon").isGreaterThan(-73.8)
        );
        pickups = pickups.dropWhere(
                pickups.doubleColumn("lon").isLessThan(-74.05)
        );

//        System.out.println(pickups);

        // 随机选择10万条数据进行处理
        pickups = pickups.sampleN(100000);

        // 将 Date/time 列分为两个新列，一个LocalDateTimeColumn，一个localtime
        List<String> dateTimes = pickups.textColumn("Date/Time").asList();
        DateTimeColumn dateTimesAsLocalDateTime = DateTimeColumn.create("localDateTime");
        TimeColumn timeAsTimeColumn = TimeColumn.create("localTime");

        for(String dt: dateTimes){
            // 格式转换
            dateTimesAsLocalDateTime.append(
                    LocalDateTime.parse(dt, DateTimeFormatter.ofPattern("M/d/yyyy H:m"))
            );
            timeAsTimeColumn.append(
                    LocalDateTime.parse(dt,DateTimeFormatter.ofPattern("M/d/yyyy H:m")).toLocalTime()
            );
        }
        // 替换列
        pickups = pickups.replaceColumn("Date/Time", dateTimesAsLocalDateTime);
        // 添加列
        pickups.addColumns(timeAsTimeColumn);

//        System.out.println(pickups);

        // 使用KMeans进行聚类
        KMeans model = KMeans.fit(pickups.as().doubleMatrix(), 3);
        Table plot_data = pickups.copy();
        plot_data.addColumns(IntColumn.create("cluster", model.y));

        // 可视化
        Plot.show(
                ScatterPlot.create("K=3",plot_data,"lon","lat","cluster")
        );

        // 观察参数k与distortion的关系
        Table elbowTable = Table.create("Elbow", DoubleColumn.create("Distortion", 10));
        elbowTable.addColumns(IntColumn.create("k",10));

        for (int i = 2; i < 10; i++) {
            KMeans model2 = KMeans.fit(pickups.as().doubleMatrix(), i);
            elbowTable.doubleColumn("Distortion").set(i,model2.distortion);
            elbowTable.intColumn("K").set(i,i);
        }

        Plot.show(
                LinePlot.create("Distortion vs K",elbowTable,"k","distortion")
        );

    }


}
