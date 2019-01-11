**this is a springboot elasticsearch demo**
#Docker 下elasticsearch 集群配置及简单demo
```docker
docker run -d -p 9200:9200 -p 9300:9300 --name elasticsearch001 -h elasticsearch001\
 -e cluster.name=lookout-es -e ES_JAVA_OPTS="-Xms512m -Xmx512m" -e xpack.security.enabled=false\
  docker.elastic.co/elasticsearch/elasticsearch:5.3.2
```
### 参数说明
-d 设置后台运行容器。
-p [宿主机端口]:[容器内端口]。
--name 设置容器别名。
-h 设置容器的主机名。
-e 设置环境变量。这里关闭 x-pack 的安全校验功能，防止访问认证。
```java
{
  "name" : "p6P2kbx",
  "cluster_name" : "elasticsearch",
  "cluster_uuid" : "bqQE1oqaQ3y9F_F0eC6x6g",
  "version" : {
    "number" : "5.3.2",
    "build_hash" : "3068195",
    "build_date" : "2017-04-24T16:15:59.481Z",
    "build_snapshot" : false,
    "lucene_version" : "6.4.2"
  },
  "tagline" : "You Know, for Search"
}
```
###继续执行节点2的添加
```docker
docker run -d -p 9211:9200 -p9311:9300 --link elasticsearch001 --name elasticsearch002 -e cluster.name=lookout_es -e ES_JAVA_OPTS="-Xms512m -Xmx512m" -e xpack.security.enabled=false -e discovery.zen.ping.unicast.hosts=elasticsearch001 elasticsearch:5.3.2
```
####命令浅析如下：
    
    -d 设置后台运行容器。
    -p [宿主机端口]:[容器内端口]，这边指定新的端口，和实例 elasticsearch001 区别开。
    --link [其他容器名]:[在该容器中的别名] 添加链接到另一个容器, 在本容器 hosts 文件中加入关联容器的记录。
    --name 设置容器别名。
    -h 设置容器的主机名。
    -e 设置环境变量。这里额外指定了 ES 集群的 cluster.name、ES 集群节点淡泊配置 discovery.zen.ping.unicast.hosts 设置为实例 elasticsearch001。
###稍后再使用 health 命令检查下 ES 的集群状态：
    curl http://localhost:9200/_cat/health\?v
###为了更好的监控集群, 我们安装一个kibana
```docker
docker run -d --name kibana001  --link elasticsearch001 -e ELASTICSEARCH_URL=http://elasticsearch001:9200 -p 5601:5601\
 docker.elastic.co/kibana/kibana:5.3.2
```
##问题
```docker
max virtual memory areas vm.max_map_count [65530] is too low, increase to at least [262144]
```
解决方案

步骤1：修改服务器vm.max_map_count参数

root>sysctl -w vm.max_map_count=262144
root>sysctl -p
步骤2：

通过--umlmit参数修改配置（docker 1.6以后有效）
##使用docker-compose 创建集群
```docker
version: '2.2'
services:
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:6.5.4
    container_name: elasticsearch
    environment:
      - cluster.name=docker-cluster
      - bootstrap.memory_lock=true
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
    ulimits:
      memlock:
        soft: -1
        hard: -1
    volumes:
      - esdata1:/usr/share/elasticsearch/data
    ports:
      - 9200:9200
    networks:
      - esnet
  elasticsearch2:
    image: docker.elastic.co/elasticsearch/elasticsearch:6.5.4
    container_name: elasticsearch2
    environment:
      - cluster.name=docker-cluster
      - bootstrap.memory_lock=true
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
      - "discovery.zen.ping.unicast.hosts=elasticsearch"
    ulimits:
      memlock:
        soft: -1
        hard: -1
    volumes:
      - esdata2:/usr/share/elasticsearch/data
    networks:
      - esnet

volumes:
  esdata1:
    driver: local
  esdata2:
    driver: local

networks:
  esnet:
```
```html
If you are bind-mounting a local directory or file, ensure it is readable by this user, while the data and log dirs additionally require write access. A good strategy is to grant group access to gid 1000 or 0 for the local directory. As an example, to prepare a local directory for storing data through a bind-mount:

mkdir esdatadir
chmod g+rwx esdatadir
chgrp 1000 esdatadir
```
挂载绑定了宿主机的目录, 需要给予相应的权限

#通过挂载外部配置文件开启多借点集群
```docker
 docker run --name es001 -p 9200:9200 -p 9300:9300 -e ES_JAVA_OPTS="-Xms512m -Xmx512m" -v /usr/es/node-1.yml:/usr/share/elasticsearch/config/elasticsearch.yml elasticsearch:6.5.4
  docker run --name es002 -p 9201:9201 -p 9301:9301 -e ES_JAVA_OPTS="-Xms512m -Xmx512m" -v /usr/es/node-2.yml:/usr/share/elasticsearch/config/elasticsearch.yml elasticsearch:6.5.4

```
#kibana监控
```docker
docker run --name kibana001 -p 5601:5601 --link es001 -e ELASTICSEARCH_URL=http://es001:9200 kibana:6.5.4
```
###坑
```java
拉取镜像的时候, 一定要把版本号带上, 否则默认拉取的最新版镜像是最小版本,不是完整包,不包含插件

docker.io/kibana          latest              a674d23325b0        3 months ago        388 MB
docker.io/elasticsearch   latest              5acf0e8da90b        3 months ago        486 MB

docker.io/kibana                                6.5.4               3c8a8603d365        3 weeks ago         735 MB
docker.io/elasticsearch                         6.5.4               93109ce1d590        3 weeks ago         774 MB
上图比较
```
