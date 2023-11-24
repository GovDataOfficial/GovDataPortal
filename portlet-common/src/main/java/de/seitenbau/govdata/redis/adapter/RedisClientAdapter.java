package de.seitenbau.govdata.redis.adapter;

import java.time.Duration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import de.seitenbau.govdata.exception.RedisNotAvailableException;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import lombok.extern.slf4j.Slf4j;

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

  private StatefulRedisConnection<String, String> connection;

  /**
   * Initialize client connection.
   */
  @PostConstruct
  public void init()
  {
    final String method = "init() : ";
    log.trace(method + "Start");

    connectToRedisClient();

    checkAvailability();

    log.trace(method + "End");
  }

  /**
   * Shutdown client connection.
   */
  @PreDestroy
  public void destroy()
  {
    final String method = "destroy() : ";
    log.trace(method + "Start");

    if (connection != null)
    {
      log.info("Closing redis connection...");
      connection.close();
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
  public StatefulRedisConnection<String, String> getConnection() throws RedisNotAvailableException
  {
    return connection;
  }

  private void connectToRedisClient()
  {
    log.info("Connecting to redis database...");
    redisClient =
        RedisClient.create(RedisURI.Builder.redis(redisHost, redisPort).withDatabase(redisDatabase)
            .withTimeout(Duration.ofSeconds(REDIS_CONNECTION_TIMEOUT)).build());
    connection = redisClient.connect();
  }

  private void checkAvailability()
  {
    final String method = "checkAvailability() : ";
    log.trace(method + "Start");

    StatefulRedisConnection<String, String> connection = null;
    try
    {
      connection = redisClient.connect();
      String pingResponse = connection.sync().ping();
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
