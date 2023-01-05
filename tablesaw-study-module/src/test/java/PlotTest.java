import org.junit.Test;
import tech.tablesaw.aggregate.Summarizer;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.numbers.IntColumnType;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.*;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.BarTrace;


import static tech.tablesaw.aggregate.AggregateFunctions.mean;
import static tech.tablesaw.aggregate.AggregateFunctions.sum;

/**
 * ClassName: PlotTest
 * Package: PACKAGE_NAME
 * Description:
 *
 * @Author: Ning
 * @Create: 2023/1/2 - 11:36
 */
public class PlotTest {

    @Test
    public void horizontalBarPlot(){
        // 1. 获取数据集
        Table tornadoes = Table.read().csv("src/test/resources/tornadoes_1950-2014.csv");
        System.out.println(tornadoes.shape());

        // 2. 获取scale列
        IntColumn scale = tornadoes.intColumn("scale");
//        System.out.println(scale.print());

        // 3. 在该列中，将值为-9的值设置为缺失值
        scale.set(scale.isEqualTo(-9), IntColumnType.missingValueIndicator());
//        System.out.println(scale.print());

        // 4. 安装scale进行分组，并对每个组求死亡人数之和
        Table fatalities1 = tornadoes.summarize("fatalities", sum).by("scale");
        System.out.println(fatalities1.print());

        // 5. 绘制横向直方图
        Plot.show(
               HorizontalBarPlot.create(
                       "龙卷风程度与死亡人数",
                       fatalities1,
                       "Scale",
                       "Sum [Fatalities]"
               )
        );

        Table t2 = tornadoes.summarize("fatalities", sum).by("State");

        // 自定义绘制条形图
        t2 = t2.sortDescendingOn(t2.column(1).name());
        Layout layout = Layout.builder().title("Tornado Fatalities by State").build();
        BarTrace trace = BarTrace.builder(t2.categoricalColumn(0), t2.numberColumn(1)).build();
        Plot.show(new Figure(layout, trace));
    }

    @Test
    public void piePlotTest(){
        // 1. 读取数据
        Table cancer = Table.read().csv("src/test/resources/cancer patient data sets.csv");

        // 2. 按年龄进行求和
        Table age_sum = cancer.summarize("Age", sum).by("Age");

        for(Row row:age_sum){
            double sum = row.getDouble("Sum [Age]");
            int age = row.getInt("Age");
            row.setDouble("Sum [Age]", sum/age);
        }
//        System.out.println(age_sum.print());

        Plot.show(
                PiePlot.create(
                        "年龄人数",
                        age_sum,
                        "Age",
                        "Sum [Age]"
                )
        );

    }

    @Test
    public void histogramsPlotTest(){
        // 1. 数据读取
        Table property = Table.read().csv("src/test/resources/sacramento_real_estate_transactions.csv");
        // 2. 绘制直方图
        Plot.show(
                Histogram.create(
                        "Distribution of prices",// 图像标题
                        property,// 数据表
                        "price"// 数字特征所在列的列名
                )
        );
    }

    @Test
    public void histograms2DPlotTest(){
        // 1. 数据读取
        Table property = Table.read().csv("src/test/resources/sacramento_real_estate_transactions.csv");
        // 2. 绘制直方图
        Plot.show(
                Histogram2D.create(
                        "Distribution of prices",// 图像标题
                        property,// 数据表
                        "price",// x轴所在的列名
                        "sq__ft"// y轴所在的列名

                )
        );
    }

    @Test
    public void boxPlotTest(){
        Table property = Table.read().csv("src/test/resources/sacramento_real_estate_transactions.csv");
        Plot.show(
                BoxPlot.create(
                        "Prices by property type",
                        property,
                        "type",
                        "price"
                )
        );
    }

    @Test
    public void scatterPlotTest(){
        Table property = Table.read().csv("src/test/resources/sacramento_real_estate_transactions.csv");
        Plot.show(
                ScatterPlot.create(
                        "price and latitude ",
                        property,
                        "latitude",
                        "price"
                )
        );
    }

    @Test
    public void scatter3DPlotTest(){
        Table property = Table.read().csv("src/test/resources/sacramento_real_estate_transactions.csv");
        Plot.show(
                Scatter3DPlot.create(
                        "price and latitude and sq__ft",
                        property,
                        "latitude",
                        "price",
                        "sq__ft"
                )
        );
    }

    @Test
    public void  bubblePlotTest(){
        Table property = Table.read().csv("src/test/resources/cancer patient data sets.csv");
        Plot.show(
                BubblePlot.create(
                        "Alcohol use and Coughing of Blood and Age",
                        property,
                        "Alcohol use",
                        "Coughing of Blood",
                        "Age"
                )
        );
    }

    @Test
    public void  timeSeriesPlotTest(){
        Table property = Table.read().csv("src/test/resources/bush.csv");
        Plot.show(
                TimeSeriesPlot.create(
                        "George W. Bush approval",
                        property,
                        "date",
                        "approval",
                        "who"
                )
        );
    }

    @Test
    public void  linePlotTest(){
        Table property = Table.read().csv("src/test/resources/boston-robberies.csv");
        Plot.show(//Record,Robberies
                LinePlot.create(
                        "Monthly Boston Robberies: Jan 1966-Oct 1975",
                        property,
                        "Record",
                        "Robberies"
                )
        );
    }

}
