## serial
- serial-distribute-inf 			dubbo接口包
- serial-distribute-respository 数据库dao层接口
- serial-distribute-service 		核心服务,提供获取序列号服务接口
- serial-distribute-manager 		核心服务,供后台应用程序生成各自业务key使用
- serial-distribute-client 		包含一个测试类,用于向序列号服务管理器注册key的示例代码


## 各个项目模块采用目录结构说明：
- src/main/java:源代码目录
- src/main/resources:配置文件目录
- src/test/java:单元测试用例目录
- src/test/resources:测试配置文件目录
- src/main/filters:环境隔离配置文件目录
- bin:启动脚本目录，appctl.sh/appctl-pre.sh/appctl-test.sh分别对应线上/测试环境启动脚本
- resources:资源目录,存放数据库建表脚本,系统设计架构图 

## 项目打包方式:
- mvn clean package -Pdev/test/pre/prod -U -DskipTests=true
- dev test pre prod分别对应对应的环境参数,根据对应的环境任选其一即可