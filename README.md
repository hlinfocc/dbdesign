[![GitHub release](https://img.shields.io/github/v/tag/hlinfocc/dbdesign.svg?label=%E6%9C%80%E6%96%B0%E7%89%88%E6%9C%AC)](https://github.com/hlinfocc/dbdesign/releases)
[![GitHub release](https://img.shields.io/badge/%E7%AB%8B%E5%8D%B3%E4%B8%8B%E8%BD%BD-cf2727)](https://github.com/hlinfocc/dbdesign/releases)

# dbdesign是什么？

dbdesign是一款轻量级的从数据库导出数据库表设计的桌面软件，使用JavaFx开发。

# 特点

轻量级、快速使用、开源免费

# 支持的数据库

目前支持PostgreSQL、MySQL数据库

# 快速使用

### 1.拉取源码直接运行:

* 从git仓库拉取源码，直接运行MainApplication.java即可

>注：支持JDK 1.8及以上版本（openJDK 由于没有javaFx包，故可能不支持，请使用Oracle JDK）

### 2.下载构建好的版本

[点击此下载](https://github.com/hlinfocc/dbdesign/releases)

* dbdesign-1.0.1.jar建好的jar包，可以直接运行（前提是已经安装JDK）

> 运行：java -jar dbdesign-1.0.1.jar

* dbdesign-full-linux.zip带jre环境的Linux包（电脑上无需前提安装JDK）

> 解压后运行startup.sh

* dbdesign-full-windows.7z带jre环境的windows包（电脑上无需前提安装JDK）

> 解压后运行startup.bat

# 源码打包

1. 使用mvn clean package直接打包，生成一个可执行的jar包（包含了依赖包）
2. 使用mvn jfx:jar 打包，将会在target/jfx/app/下生成包（其中依赖包单独位于lib中）
3. 使用mvn jfx:native 创建本机启动程序(如在windows上是EXE文件，在Debian Linux上是deb)

# 许可证
MIT License 
