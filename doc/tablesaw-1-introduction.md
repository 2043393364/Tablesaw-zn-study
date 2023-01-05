# Tablesaw的相关介绍
Tablesaw 结合了用于处理表格和列的工具以及创建统计模型和可视化的能力。换句话说，它是一个具有附加功能的Dataframe。

## 1. DataFrame
DataFrame是一个表格型的数据结构，它含有一组有序的列，其中每一列都包含一种数据类型，而每行可以包含多种类型。 
Tablesaw为DataFrame提供以下操作：
> - 从文件或数据库中导入和导出数据
> - 添加和删除某列
> - 排序
> - 过滤或筛选
> - 通过对现有列的映射函数来创建一个新列
> - 汇总列或表
> - 通过追加或连接来合并表格
> - 计算统计量
> - 添加、修改和删除某行

## 2. 支持可视化
> - 可以绘制数据

## 3. 准备工作

Maven配置
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>tablesaw-study</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <dependency>
            <groupId>tech.tablesaw</groupId>
            <artifactId>tablesaw-core</artifactId>
            <version>0.43.1</version>
            <scope>compile</scope>
        </dependency>

        <dependency>
            <groupId>tech.tablesaw</groupId>
            <artifactId>tablesaw-jsplot</artifactId>
            <version>LATEST</version>
            <scope>compile</scope>
        </dependency>
        
        <!-- https://mvnrepository.com/artifact/junit/junit -->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.13.2</version>
            <scope>test</scope>
        </dependency>


    </dependencies>

</project>
```
