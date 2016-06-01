package de.seitenbau.govdata.redis.adapter;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;
import com.lambdaworks.redis.RedisConnectionPool;
import com.lambdaworks.redis.RedisURI;

import de.seitenbau.govdata.exception.RedisNotAvailableException;

@Slf4j
@Repository
public class RedisClientAdapter
{
  private static final int REDIS_CONNECTION_TIMEOUT = 5;

  @Value("${ckanext.govdata.validators.redis.host}")
  private String redisHost;

  @Value("${ckanext.govdata.validators.redis.port}")
  private Integer redisPort;

  @Value("${ckanext.govdata.validators.redis.database}")
  private Integer redisDatabase;

  private RedisClient redisClient;

  private RedisConnectionPool<RedisConnection<String, String>> connectionPool;

  @PostConstruct
  public void init()
  {
    final String method = "init() : ";
    log.trace(method + "Start");

    connectToRedisClient();

    checkAvailability();

    log.trace(method + "End");
  }

  @PreDestroy
  public void destroy()
  {
    final String method = "destroy() : ";
    log.trace(method + "Start");

    if (connectionPool != null)
    {
      log.info("Closing redis connection pool...");
      connectionPool.close();
    }
    if (redisClient != null)
    {
      log.info("Shutting down redis client...");
      redisClient.shutdown();
    }

    log.info("Connection to Redis closed and Redis client shutdowned");
    log.trace(method + "End");
  }

  /**
   * Liefert eine Verbindung aus dem Verbindungs-Pool.<br>
   * <b>HINWEIS:</b> Wenn die Verbindung nicht mehr gebraucht wird, diese mit dem Aufruf von
   * {@link #freeConnection(RedisConnection)} wieder an den Pool zurückgeben.
   * 
   * @return die Verbindung aus dem Verbindungs-Pool.
   * @throws RedisNotAvailableException
   */
  public RedisConnection<String, String> getPooledConnection() throws RedisNotAvailableException
  {
    RedisConnection<String, String> connection;
    try
    {
      connection = connectionPool.allocateConnection();
    }
    catch (Exception e)
    {
      log.warn("Could not allocate connection from Redis database!");
      throw new RedisNotAvailableException(e);
    }
    return connection;
  }

  /**
   * Gibt die übergebene Verbindung zur weiteren Verwendung wieder an den Verbindungs-Pool zurück.
   * 
   * @param connection die freizugebende Verbindung.
   */
  public void freeConnection(RedisConnection<String, String> connection)
  {
    if (connection != null)
    {
      connectionPool.freeConnection(connection);
    }
  }

  private void connectToRedisClient()
  {
    log.info("Connecting to redis database...");
    redisClient =
        new RedisClient(RedisURI.Builder.redis(redisHost, redisPort).withDatabase(redisDatabase)
            .withTimeout(REDIS_CONNECTION_TIMEOUT, TimeUnit.SECONDS).build());
    connectionPool = redisClient.pool();
  }

  private void checkAvailability()
  {
    final String method = "checkAvailability() : ";
    log.trace(method + "Start");

    RedisConnection<String, String> connection = null;
    try
    {
      connection = redisClient.connect();
      String pingResponse = connection.ping();
      log.debug(method + "ping response: {}", pingResponse);
      if (StringUtils.equalsIgnoreCase(pingResponse, "pong"))
      {
        log.info(method + "Redis database is available.");
      }
      else
      {
        log.warn(method + "Ping from Redis database works, but response string is not as expected!");
      }
    }
    catch (Exception e)
    {
      log.error(method + "Redis database not available!", e);
    }
    finally
    {
      if (connection != null)
      {
        connection.close();
      }
    }

    log.trace(method + "End");
  }

}
