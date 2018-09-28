package xs.spider.base.util;

import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
import xs.spider.base.config.ConfigProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Created by xiaozhujun on 16/1/31.
 */
public class RedisConnector {
    private static Logger logger = LogUtil.getLogger(RedisConnector.class);

    private static int expire;
    private static JedisPool pool;

    static {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(Integer.valueOf(ConfigProvider.get("redis.pool.maxTotal")));
            config.setMaxIdle(Integer.valueOf(ConfigProvider.get("redis.pool.maxIdle")));
            config.setTestOnBorrow(Boolean.valueOf(ConfigProvider.get("redis.pool.testOnBorrow")));
            config.setTestOnReturn(Boolean.valueOf(ConfigProvider.get("redis.pool.testOnReturn")));

            if (ConfigProvider.get("redis.pool.timeout") != null) {
                pool = new JedisPool(config,
                        ConfigProvider.get("redis.host"),
                        Integer.valueOf(ConfigProvider.get("redis.port")),
                        Integer.valueOf(ConfigProvider.get("redis.pool.timeout")));
            } else {
                pool = new JedisPool(config,
                        ConfigProvider.get("redis.host"),
                        Integer.valueOf(ConfigProvider.get("redis.port")));
            }
            logger.info("redis.host: " + ConfigProvider.get("redis.host"));
            expire = Integer.valueOf(ConfigProvider.get("redis.expire"));
        } catch (Exception e) {
            logger.error("redis初始化失败", e);
        }
    }

    public RedisConnector() {
    }

    public static Jedis getJedis() {
        return pool.getResource();
    }

    public int getExpire() {
        return expire;
    }

    public static double incrByFloat(String key,double value) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.incrByFloat(key,value);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            jedis.close();
        }
        return -1L;
    }

    public static double incrBy(String key,long value) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.incrBy(key,value);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            jedis.close();
        }
        return -1L;
    }

    public static boolean setex(String key,int extime, String value) {
        Jedis jedis = pool.getResource();
        try {
            String result = jedis.setex(key, extime, value);
            if (result.equals("OK")) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return false;
    }


    public static boolean set(String key, String value) {
        Jedis jedis = pool.getResource();
        try {
            String result = jedis.set(key, value);
            if (result.equals("OK")) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return false;
    }

    public static boolean set(byte[] key, byte[] value) {
        Jedis jedis = pool.getResource();
        try {
            String result = jedis.set(key, value);
            if (result.equals("OK")) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return false;
    }

    public static boolean set(String key, int expire, String value) {
        Jedis jedis = pool.getResource();
        try {
            String result = jedis.setex(key, expire, value);
            if (result.equals("OK")) {
                return true;
            }
        } catch (Exception e) {
            logger.error(e);
        } finally {
            jedis.close();
        }
        return false;
    }


    public static String get(String key) {
        Jedis jedis = pool.getResource();
        String result = null;
        try {
            result = jedis.get(key);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            jedis.close();
        }
        return result;
    }

    public static byte[] get(byte[] key) {
        Jedis jedis = pool.getResource();
        byte[] result = null;
        try {
            result = jedis.get(key);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            jedis.close();
        }
        return result;
    }

    public static Set<String> keys(String pattern) {
        Jedis jedis = pool.getResource();
        Set<String> result = null;
        try {
            result = jedis.keys(pattern);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            jedis.close();
        }
        return result;
    }


    public static long del(String key) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.del(key);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            jedis.close();
        }
        return 0;
    }

    public static long del(byte[] key) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.del(key);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            jedis.close();
        }
        return 0;
    }


    public static boolean exists(String key) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.exists(key);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            jedis.close();
        }
        return false;
    }

    public static boolean hexists(String key,String field) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.hexists(key,field);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            jedis.close();
        }
        return false;
    }

    /**
     * 头入队列
     *
     * @param key
     * @param value
     * @return
     */
    public static Long lpush(String key, String value) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.lpush(key, value);
        } catch (Exception e) {
            logger.error(e);
            return null;
        } finally {
            jedis.close();
        }
    }

    /**
     * 未入队列
     *
     * @param key
     * @param value
     * @return
     */
    public static Long rpush(String key, String value) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.rpush(key, value);
        } catch (Exception e) {
            logger.error(e);
            return null;
        } finally {
            jedis.close();
        }
    }

    /**
     * 从左出队列
     *
     * @param key
     * @return
     */
    public static String lpop(String key) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.lpop(key);
        } catch (Exception e) {
            logger.error(e);
            return null;
        } finally {
            jedis.close();
        }
    }

    /**
     * 从右出队列
     *
     * @param key
     * @return
     */
    public static String rpop(String key) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.rpop(key);
        } catch (Exception e) {
            logger.error(e);
            return null;
        } finally {
            jedis.close();
        }
    }

    /**
     * Return the length of the list stored
     *
     * @param key
     * @return
     */
    public static Long llen(String key) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.llen(key);
        } catch (Exception e) {
            logger.error(e);
            return null;
        } finally {
            jedis.close();
        }
    }

    /**
     * 有序集合
     *
     * @param key
     * @param value
     * @return
     */
    public static void addSet(String key, String value) {
        Jedis jedis = pool.getResource();
        try {
            Long score = jedis.zcard(key);
            jedis.zadd(key, ++score, value);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            jedis.close();
        }
    }
    /**
     * 有序集合
     *
     * @param key
     * @param value
     * @return
     */
    public static void addSet(String key, Long score, String value) {
        Jedis jedis = pool.getResource();
        try {
            jedis.zadd(key, score, value);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            jedis.close();
        }
    }
    /**
     * 得到有序集合
     *
     * @param key
     * @return
     */
    public static Set<String> getSet(String key) {
        Jedis jedis = pool.getResource();
        try {

            Set<String> sets = jedis.zrange(key, 0, -1);
            return sets;
        } catch (Exception e) {
            logger.error(e);
            return null;
        } finally {
            jedis.close();
        }
    }

    public static String loadScript(String script) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.scriptLoad(script);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            jedis.close();
        }
        return null;
    }

    /**
     * 分页得到有序集合
     *
     * @param key
     * @param size
     * @return
     */
    public static Set<String> zrange(String key, int size) {
        Jedis jedis = pool.getResource();
        try {
            Set<String> sets = jedis.zrange(key, 0, size);
            return sets;
        } catch (Exception e) {
            logger.error(e);
            return null;
        } finally {
            jedis.close();
        }
    }
    /**
     * 分页得到有序集合
     *
     * @param key
     * @param size
     * @return
     */
    public static Set<String> zrange(String key, long start, int size) {
        Jedis jedis = pool.getResource();
        try {
            Set<String> sets = jedis.zrange(key, start, size);
            return sets;
        } catch (Exception e) {
            logger.error(e);
            return null;
        } finally {
            jedis.close();
        }
    }

    /**
     * 分页得到有序集合(索引倒序)
     *
     * @param key
     * @param size
     * @return
     */
    public static Set<String> zrevrange(String key, long start, int size) {
        Jedis jedis = pool.getResource();
        try {
            Set<String> sets = jedis.zrevrange(key, start, size);
            return sets;
        } catch (Exception e) {
            logger.error(e);
            return null;
        } finally {
            jedis.close();
        }
    }
    /**
     * 分页得到有序集合(score倒序)
     *
     * @param key
     * @param size
     * @return
     */
    public static Set<String> zrevrangeByScore(String key, int start, int size) {
        Jedis jedis = pool.getResource();
        try {
            Set<String> sets = jedis.zrevrangeByScore(key, Double.MAX_VALUE, Double.MIN_VALUE, start, size);
            return sets;
        } catch (Exception e) {
            logger.error(e);
            return null;
        } finally {
            jedis.close();
        }
    }
    public static Long zcard(String key) {
        Jedis jedis = pool.getResource();
        try {
            Long total = jedis.zcard(key);
            return total;
        } catch (Exception e) {
            logger.error(e);
            return 0L;
        } finally {
            jedis.close();
        }
    }
    /**
     * 获取成员分数
     * @param key
     * @param member
     * @return
     */
    public static Double zscore(String key, String member) {
        Jedis jedis =null;
        try{
            jedis=pool.getResource();
            Double score = jedis.zscore(key, member);
            return score;
        }catch (Exception e){
            logger.error(e);
            return 0.0;
        }finally {
            if(jedis!=null)
                jedis.close();
        }
    }
    /**
     * 删除指定的元素
     *
     * @param key
     * @param values
     * @return
     */
    public static Long zrem(String key, String... values) {
        Jedis jedis = pool.getResource();
        try {
            Long count = jedis.zrem(key, values);
            return count;
        } catch (Exception e) {
            logger.error(e);
            return 0l;
        } finally {
            jedis.close();
        }
    }
    /**
     * 统计set中的数据个数
     *
     * @param key
     * @return
     */
    public static Long zcount(String key, Double min, Double max) {
        Jedis jedis = pool.getResource();
        try {
            Long cnt = jedis.zcount(key, min, max);
            return cnt;
        } catch (Exception e) {
            logger.error(e);
            return null;
        } finally {
            jedis.close();
        }
    }

    public static Map<String, String> hget(String key) {
        Jedis jedis = pool.getResource();

        Map<String, String> map = null;
        try {
            map = jedis.hgetAll(key);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            jedis.close();
        }
        return map;
    }

    public static void hmset(String key,Map<String,String> values) {
        Jedis jedis = pool.getResource();
        try {
            jedis.hmset(key, values);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            jedis.close();
        }
    }

    /**
     *
     * @param key
     * @param values
     * @param expireSeconds 过期时间 (单位: 秒)
     */
    public static void hmset(String key,Map<String,String> values,int expireSeconds) {
        Jedis jedis = pool.getResource();
        try {
            jedis.hmset(key, values);
            jedis.expire(key, expireSeconds);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            jedis.close();
        }
    }



    public static boolean hset(String key, String field, String fieldVal) {
        Jedis jedis = pool.getResource();
        try {
            jedis.hset(key, field, fieldVal);
            return true;
        } catch (Exception e) {
            logger.error(e);
        } finally {
            jedis.close();
        }
        return false;
    }

    public static boolean hset(String key, Map<String, String> map) {
        Jedis jedis = pool.getResource();
        try {
            for (Entry<String, String> e : map.entrySet()) {
                jedis.hset(key, e.getKey(), e.getValue());
            }
            return true;
        } catch (Exception e) {
            logger.error(e);
        } finally {
            jedis.close();
        }
        return false;
    }

    public static boolean hset(String key, Map<String, String> map, int exprise) {
        Jedis jedis = pool.getResource();
        try {
            for (Entry<String, String> e : map.entrySet()) {
                jedis.hset(key, e.getKey(), e.getValue());
            }
            jedis.expire(key, exprise);
            return true;
        } catch (Exception e) {
            logger.error(e);
        } finally {
            jedis.close();
        }
        return false;
    }

    /**
     * 批量更新redis
     *
     * @param keys
     * @param maps
     * @return
     */
    public static boolean batchHset(List<String> keys, List<Map<String, String>> maps) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            Pipeline pipeline = jedis.pipelined();
            int index = 0;
            for (String key : keys) {
                pipeline.hmset(key, maps.get(index++)); //key -map
            }
            if (index % keys.size() == 0) {
                pipeline.sync();
            }
            return true;
        } catch (Exception e) {
            logger.error(e);
        } finally {
            if (jedis != null) {
                jedis.disconnect();
                jedis.close();
            }
        }
        return false;
    }

    public static void main(String[] args) {

        int count = 10;
        Jedis jedis = RedisConnector.getJedis();
        Pipeline p = jedis.pipelined();
        Map<String, String> maps = new HashMap<>();
        for (int i = 0; i < count; i++) {
            maps.clear();
            maps.put("k_" + i, "v_" + i);
            p.hmset("k_" + i, maps);
        }
        p.sync();
        jedis.close();
    }

    public static long incr(String key) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.incr(key);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            jedis.close();
        }
        return -1L;
    }

    public static long hincr(String key,String field){
        Jedis jedis = pool.getResource();
        try {
            return jedis.hincrBy(key,field,1);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            jedis.close();
        }
        return -1L;
    }

    public static long hincrby(String key,String field,int num){
        Jedis jedis = pool.getResource();
        try {
            return jedis.hincrBy(key,field,num);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            jedis.close();
        }
        return -1L;
    }

    public static long decr(String key) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.decr(key);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            jedis.close();
        }
        return -1L;
    }

    public static long decrby(String key,Long value) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.decrBy(key,value);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            jedis.close();
        }
        return -1L;
    }

    public static long setnx(String key) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.setnx(key, "1");
        } catch (Exception e) {
            logger.error(e);
        } finally {
            jedis.close();
        }
        return -1L;
    }

    public static long setnx(String key,String value) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.setnx(key, value);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            jedis.close();
        }
        return -1L;
    }

    public static long setnx(String key, int expire, String value) {
        Jedis jedis = pool.getResource();
        try {

            if ("OK".equals(jedis.set(key, value, "NX", "EX", expire))) {
                return 1;
            }
            return 0;

        } catch (Exception e) {
            logger.error("redis异常:", e);
        } finally {
            jedis.close();
        }
        return -1L;
    }

    public static void expire(String key,int second) {
        Jedis jedis = pool.getResource();
        try {
            jedis.expire(key, second);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            jedis.close();
        }
    }

    public static long sadd(String key, String... members) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.sadd(key, members);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            jedis.close();
        }
        return -1L;
    }

    public static long scard(String key) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.scard(key);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            jedis.close();
        }
        return -1L;
    }

    public static Set<String> smembers(String key) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.smembers(key);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            jedis.close();
        }
        return null;
    }

    public static String spop(String key){
        Jedis jedis = pool.getResource();
        try {
            return jedis.spop(key);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            jedis.close();
        }
        return null;
    }

    /**
     *
     * @param key
     * @param count 返回个数
     * @return
     */
    public static Set<String> spop(String key,int count){
        Jedis jedis = pool.getResource();
        try {
            return jedis.spop(key,count);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            jedis.close();
        }
        return null;
    }

    public static long srem(String redisKey, String... strings) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.srem(redisKey, strings);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            jedis.close();
        }
        return -1l;
    }

    public static List<String> hmget(String key,String... fields){
        Jedis jedis=pool.getResource();
        try{
            return jedis.hmget(key,fields);
        }catch (Exception e){
            logger.error(e);
            return null;
        }finally {
            jedis.close();
        }
    }

    public static String hget(String key,String field){
        Jedis jedis=pool.getResource();
        try{
            return jedis.hget(key,field);
        }catch (Exception e){
            logger.error(e);
            return null;
        }finally {
            jedis.close();
        }
    }
    public static Long ttl(String key){
        Jedis jedis=pool.getResource();
        try{
            return jedis.ttl(key);
        }catch (Exception e){
            logger.error(e);
            return -2L;
        }finally {
            jedis.close();
        }
    }

    public static List<String> lrange(String key,Long start,Long end){
        Jedis jedis = pool.getResource();
        try {
            return jedis.lrange(key,start,end);
        }catch (Exception e){
            logger.error(e);
            return null;
        }finally {
            jedis.close();
        }
    }


    public static long ldel(String key, String value) {
        Jedis jedis=pool.getResource();
        try{
            return jedis.lrem(key,0,value);
        }catch (Exception e){
            logger.error(e);
            return 0L;
        }finally {
            jedis.close();
        }
    }

    public static Object eval(String script,List<String> keys,List<String> args){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.eval(script,keys,args);
        }catch (Exception e){
            logger.error(e);
        }finally {
            if(jedis != null){
                jedis.close();
            }

        }
        return null;
    }

    public static Long hlen(String mapKey) {
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.hlen(mapKey);
        }catch (Exception e){
            logger.error(e);
        }finally {
            if(jedis != null){
                jedis.close();
            }

        }
        return null;
    }
    public static String srandMember(String key) {
        List<String> list = srandMember(key, 1);
        if (list == null || list.isEmpty()) return null;
        else return list.get(1);
    }
    public static List<String> srandMember(String key, int count) {
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.srandmember(key, count);
        }catch (Exception e){
            logger.error(e);
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return null;
    }

    public static Object evalsha(String sha,List<String> keys,List<String> args){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.evalsha(sha, keys, args);
        }catch (Exception e){
            logger.error(e);
        }finally {
            if(jedis != null){
                jedis.close();
            }

        }
        return null;
    }

    /**
     * 查看value值是否存在
     */
    public static boolean sismember(String key, String member){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.sismember(key,member);
        }catch (Exception e){
            logger.error(e);
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return false;
    }

    /**
     * Hdel long.
     * Hash: delete a field
     * @param key
     *         the key
     * @param field
     *         the field
     * @return the long
     * @author cx
     * @version 2017 -06-20 20:22:37
     */
    public static long hdel(String key, String field){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.hdel(key,field);
        }catch (Exception e){
            logger.error(e);
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return 0;
    }

}
