# 仿牛客网论坛

## 2023.08.13、个人主页

完成点击头像进入个人主页以及总点赞数的展示（这里还是使用redis实现，使用的方法是另外一个字段存储而非每次展示的时候查询）

需要重构点赞方法，每次点赞的时候顺便将该用户的总点赞数进行相应的改变，并且需要用 redis 的事务

在LikeService中重构方法 like,为了速度，选择直接传入参数 entityUserId ,而非使用旧参数进行查询

```java
    // 点赞
    public void like(int userId, int entityType, int entityId, int entityUserId) {
//        // 获取 key
//        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
//        // 判断是否已经点赞
//        boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
//        if(isMember) {
//            // 取消点赞
//            redisTemplate.opsForSet().remove(entityLikeKey,userId);
//        }else{
//            // 点赞
//            redisTemplate.opsForSet().add(entityLikeKey, userId);
//        }
        // redis 事务
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                // 生成 key
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);

                //判断是否已经点赞，查询需要放在事务外才能立即查询
                boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
                // 开启事务
                operations.multi();

                if(isMember){
                    operations.opsForSet().remove(entityLikeKey, userId);
                    operations.opsForValue().decrement(userLikeKey);
                }else {
                    operations.opsForSet().add(entityLikeKey, userId);
                    operations.opsForValue().increment(userLikeKey);
                }
                // 执行事务
                return operations.exec();
            }
        });
    }
```

### 修改实体类属性名错误

另外发现实体类 User 的属性名 cteateTime 字段名错误，这里这里建议直接使用 "Shift + F6" 修改属性名即可，idea 会自动帮你修改其他关联的地方，避免错误的出现

个人主页的就比较简单了，需要注意的是我们为了防止恶意攻击，使用不存在的用户id访问，这里需要做出判断查询用户是否为空并返回异常。

## 2023.08.13、个人主页 关注和取消关注

使用的方法和点赞一样都是用redis，一次关注需要两个操作，

1、一用户为中心，记录用户关注了或取消关注了什么实体

2、以被关注的主体为中心，记录了这个主体被谁关注了

在这里出现的意外是 报错——说前端模板的问题，一直以为是前端哪里粗心写错了，最后发现是controller没有注解json。

## 2023.08.16、 使用kafka实现系统通知模块



# 如何在 SpringBoot 的官方文档找到自己需要的文档内容

说明：

在我使用 springboot 3.1.2 作为核心 学习项目的时候，我需要根据 版本依赖关系确定其他 组件工具的版本。

在使用到 elasticsearch 的时候， 3.1.x 对应的 es 版本是 8.7.1 ，是目前 SpringBoot 官方整合 es 最新稳定版，网上资料资料较少，但是根据版本依赖的表，跟 es 8.7.0 之前是不一样的，这时候需要 在官网找到相关信息 进行快速入门的自我学习（根据官网学习）

所以才有这个记录。

## 一、需要知道去哪看文档

1. 去spring官网[Spring | Home](https://spring.io/)

   ![image-20230819165355327](C:\Users\27922\AppData\Roaming\Typora\typora-user-images\image-20230819165355327.png)

   然后进入 SpringBoot 部分

   ![image-20230819165436978](C:\Users\27922\AppData\Roaming\Typora\typora-user-images\image-20230819165436978.png)

2. 查找自己需要的 工具组件在哪个地方

   - Ctrl + F 按钮 开启页面查找功能

     然后在这里我 查找的功能是 elasticsearch ，emmm，没找到

   - 然后根据我需要的 工具组件是属于什么分类去对应板块去查找

     elasticsearch 是 SpringData 的部分，然后就找到了

     ![image-20230819165852973](C:\Users\27922\AppData\Roaming\Typora\typora-user-images\image-20230819165852973.png)

3. 选择需要的版本

   1. 我需要的是当前最新稳定版

      点击 LEARN

      ![image-20230819170036283](C:\Users\27922\AppData\Roaming\Typora\typora-user-images\image-20230819170036283.png)

      ![image-20230819170116810](C:\Users\27922\AppData\Roaming\Typora\typora-user-images\image-20230819170116810.png)

   2. 点击 Reference Docment  进入对应版本的文档

   3. 进行学习

## 核心：Repository

1. 讲述了 用于 crud 的 CrudRepository[CrudRepository (Spring Data Core 3.1.3 API)](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/CrudRepository.html)

    和 [ListCrudRepository (spring.io)](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/ListCrudRepository.html)

   

   ```java
   public interface CrudRepository<T, ID> extends Repository<T, ID> {
   
     <S extends T> S save(S entity);      
   
     Optional<T> findById(ID primaryKey); 
   
     Iterable<T> findAll();               
   
     long count();                        
   
     void delete(T entity);               
   
     boolean existsById(ID primaryKey);   
   
     // … more functionality omitted.
   }
   
   Saves the given entity.
   Returns the entity identified by the given ID.
   Returns all entities.
   Returns the number of entities.
   Deletes the given entity.
   Indicates whether an entity with the given ID exists.
   
   ```

2.  然后是分页

   [PagingAndSortingRepository (spring.io)](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/PagingAndSortingRepository.html)

3. 然后是一些乱七八糟的东西，略

## 正文：

demo

1. 声明一个接口，继承 `Repository` 或其子接口之一，并将其类型化为该接口应该处理的域类和 ID 类型。示例如下：这个接口一般放在 dao 层的 elasticsearch 包下

   ```java
   interface PersonRepository extends Repository<Person, Long> { … }
   
   ```

2. 在接口上声明查询方法，如下所示：

   ```java
   interface PersonRepository extends Repository<Person, Long> {
     List<Person> findByLastname(String lastname);
   }
   
   ```

3. 配置 Spring 来创建这些接口的代理实例，可以使用 JavaConfig 或 XML 配置。示例中使用了 JPA 命名空间，如果您使用其他存储库模块的存储库抽象，需要将其更改为相应存储库模块的适当命名空间声明。

   ```java
   @EnableJpaRepositories
   class Config { … }
   ```

4. 注入存储库实例并使用它，如下所示：

   ```java
   class SomeClient {
   
     private final PersonRepository repository;
   
     SomeClient(PersonRepository repository) {
       this.repository = repository;
     }
   
     void doSomething() {
       List<Person> persons = repository.findByLastname("Matthews");
     }
   }
   
   ```

上面是官方简单的例子，接下来是较为详细的内容

1. Repository[org.springframework.data.repository (Spring Data Core 3.1.3 API)](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/package-summary.html)

   ```java
   /*
    * Copyright 2011-2023 the original author or authors.
    *
    * Licensed under the Apache License, Version 2.0 (the "License");
    * you may not use this file except in compliance with the License.
    * You may obtain a copy of the License at
    *
    *      https://www.apache.org/licenses/LICENSE-2.0
    *
    * Unless required by applicable law or agreed to in writing, software
    * distributed under the License is distributed on an "AS IS" BASIS,
    * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    * See the License for the specific language governing permissions and
    * limitations under the License.
    */
   package org.springframework.data.repository;
   
   import org.springframework.stereotype.Indexed;
   
   /**
    * Central repository marker interface. Captures the domain type to manage as well as the domain type's id type. General
    * purpose is to hold type information as well as being able to discover interfaces that extend this one during
    * classpath scanning for easy Spring bean creation.
    * <p>
    * Domain repositories extending this interface can selectively expose CRUD methods by simply declaring methods of the
    * same signature as those declared in {@link CrudRepository}.
    * 
    * @see CrudRepository
    * @param <T> the domain type the repository manages
    * @param <ID> the type of the id of the entity the repository manages
    * @author Oliver Gierke
    */
   @Indexed
   public interface Repository<T, ID> {
   
   }
   ```

总的来说，这个文件定义了一个核心接口`Repository`，用于表示Spring Data库中的数据仓库。这个接口提供了用于处理数据访问的通用方法，可以被其他接口继承，实现不同的数据访问需求。

我们一般要用的是实现他的接口

子类：

[CrudRepository](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/CrudRepository.html)<T,ID>	：特定类型的存储库上的通用 CRUD 操作的接口。

[ListCrudRepository](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/ListCrudRepository.html)<T,ID>	：特定类型的存储库上的通用 CRUD 操作的接口。

[ListPagingAndSortingRepository](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/ListPagingAndSortingRepository.html)<T,ID>	：存储库片段，提供使用分页和排序抽象检索实体的方法。

[NoRepositoryBean](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/NoRepositoryBean.html)	：注释以排除存储库接口被拾取，从而获得实例 创建。

[PagingAndSortingRepository](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/PagingAndSortingRepository.html)<T,ID>	：存储库片段，提供使用分页和排序抽象检索实体的方法。

[Repository](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/Repository.html)<T,ID>	：中央存储库标记界面。

[RepositoryDefinition](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/RepositoryDefinition.html)	：应为其创建存储库代理的分隔接口的注释。



子接口：

[CrudRepository](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/CrudRepository.html)<T,ID>

特定类型的存储库上的通用 CRUD 操作的接口。

[ListCrudRepository](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/ListCrudRepository.html)<T,ID>

特定类型的存储库上的通用 CRUD 操作的接口。

[ListPagingAndSortingRepository](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/ListPagingAndSortingRepository.html)<T,ID>

存储库片段，提供使用分页和排序抽象检索实体的方法。

[PagingAndSortingRepository](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/PagingAndSortingRepository.html)<T,ID>

存储库片段，提供使用分页和排序抽象检索实体的方法。

[Repository](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/Repository.html)<T,ID>

中央存储库标记界面。



这些接口实现的功能有所不同，我们用接口继承就行（Repository是空的，没有方法）

crud 用 CrudRepository 接口

返回的类型不同的话可以用 ListCrudRepository 接口

分页功能 需要 PagingAndSortingRepository

具体需要用什么功能建议百度一下有没有你需要的（因为我也刚学，不过一般都需要分页吧）



以上内容以及足够我们将信息进行 crud 了，接下来我们要学会怎么 优雅的 搜索，而不是查询



# 好东西

### 5.1. 命令式 REST 客户端

要使用命令式（非反应式）客户机，必须按如下方式配置配置 Bean：

```java
@Configuration
public class MyClientConfig extends ElasticsearchConfiguration {

	@Override
	public ClientConfiguration clientConfiguration() {
		return ClientConfiguration.builder()           
			.connectedTo("localhost:9200")
			.build();
	}
}
```

|      | 有关生成器方法的详细说明，请参阅[客户端配置](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#elasticsearch.clients.configuration) |
| ---- | ------------------------------------------------------------ |
|      |                                                              |



#### 6.1.1. 映射标注概述

使用元数据来驱动对象到文档的映射。 元数据取自可以注释的实体属性。`MappingElasticsearchConverter`

以下注释可用：

- `@Document`：在类级别应用，以指示此类是映射到数据库的候选项。 最重要的属性是（查看 API 文档以获取完整的属性列表）：
  - `indexName`：要在其中存储此实体的索引的名称。 这可以包含一个 SpEL 模板表达式，例如`"log-#{T(java.time.LocalDate).now().toString()}"`
  - `createIndex`：标记是否在存储库引导时创建索引。 默认值为 *true*。 请参阅[使用相应映射自动创建索引](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#elasticsearch.repositories.autocreation)
- `@Id`：在字段级别应用，以标记用于标识目的的字段。
- `@Transient`， ， ： 有关详细信息，请参阅以下控制写入[和读取哪些属性一节。](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#elasticsearch.mapping.meta-model.annotations.read-write)`@ReadOnlyProperty``@WriteOnlyProperty`
- `@PersistenceConstructor`：标记给定的构造函数 - 甚至是受包保护的构造函数 - 以便在从数据库中实例化对象时使用。 构造函数参数按名称映射到检索到的文档中的键值。
- `@Field`：应用于字段级别并定义字段的属性，大多数属性映射到相应的 [Elasticsearch 映射](https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping.html)定义（以下列表不完整，请查看注释 Javadoc 以获取完整参考）：
  - `name`：将在 Elasticsearch 文档中表示的字段名称，如果未设置，则使用 Java 字段名称。
  - `type`：字段类型可以是文本、关键字、长整型、*整数、短整型、字节、双精度、浮点型、Half_Float型、Scaled_Float、日期、Date_Nanos、布尔值、二进制、Integer_Range、Float_Range、Long_Range、Double_Range、Date_Range、Ip_Range、对象、嵌套、IP、令牌计数、渗滤器、扁平化、Search_As_You_Type*之一。 请参阅 [Elasticsearch 映射类型](https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-types.html)。 如果未指定字段类型，则默认为 。 这意味着，不会为该属性写入映射条目，并且 Elasticsearch 将在存储此属性的第一个数据时动态添加映射条目（查看 Elasticsearch 文档以了解动态映射规则）。`FieldType.Auto`
  - `format`：一种或多种内置日期格式，请参阅下一节[日期格式映射](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#elasticsearch.mapping.meta-model.annotations.date-formats)。
  - `pattern`：一种或多种自定义日期格式，请参阅下一节[日期格式映射](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#elasticsearch.mapping.meta-model.annotations.date-formats)。
  - `store`：标记原始字段值是否应存储在 Elasticsearch 中，默认值为 *false*。
  - `analyzer`， ， ，用于指定自定义分析器和规范化器。`searchAnalyzer``normalizer`
- `@GeoPoint`：将字段标记为*geo_point*数据类型。 如果字段是类的实例，则可以省略。`GeoPoint`
- `@ValueConverter`定义用于转换给定属性的类。 与注册的 Spring 不同，这只转换带注释的属性，而不是给定类型的每个属性。`Converter`

映射元数据基础设施在一个单独的 spring-data-commons 项目中定义，该项目与技术无关。

# 强烈呼吁同学们从参考文档开始看起，前面的内容low一眼就行了，不用看的太仔细QAQ

[Spring 数据弹性搜索 - 参考文档](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#core.extensions)

就是这个

后面看起来就头不会太晕了@……@

**直接实现 Repository 接口来实现 crud 这个设定很有意思**

进入 Repository 源码是没有方法的，但是 官方提供了一系列 词条来触发

如：

```java
interface BookRepository extends Repository<Book, String> {
  List<Book> findByNameAndPrice(String name, Integer price);
}
```

结果就是会自动 翻译成：

```
{
    "query": {
        "bool" : {
            "must" : [
                { "query_string" : { "query" : "?", "fields" : [ "name" ] } },
                { "query_string" : { "query" : "?", "fields" : [ "price" ] } }
            ]
        }
    }
}
```



他会通过 扫描 方法名，观察是否有 关键词

**还可以用注解实现**



## 实现了 es 的搜索功能以及 添加文章时自动将内容同步到 es 库的功能

这里借助 kafka 实现异步实现

