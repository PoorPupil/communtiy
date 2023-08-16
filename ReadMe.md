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
