package ru.javarush.klimovich.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import org.hibernate.SessionFactory;
import ru.javarush.klimovich.redis.CityCountry;
import ru.javarush.klimovich.session_provider.PropertiesSessionFactoryProvider;
import ru.javarush.klimovich.session_provider.SessionProvider;

import java.util.List;

import static java.util.Objects.nonNull;

public class DataToRedis {
    private final SessionFactory sessionFactory;
    private final static Integer PORT = 6379;
    private final RedisClient redisClient;
    private final ObjectMapper mapper;

    public DataToRedis() {
        SessionProvider sessionProvider = new PropertiesSessionFactoryProvider();
        redisClient = prepareRedisClient();
        mapper = new ObjectMapper();
        sessionFactory = sessionProvider.getSessionFactory();
    }

    public RedisClient prepareRedisClient() {
        RedisClient redisClient = RedisClient.create(RedisURI.create("localhost", PORT));
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            System.out.println("\nConnected to Redis\n");
        }
        return redisClient;
    }

    public void pushToRedis(List<CityCountry> data) {
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            RedisStringCommands<String, String> sync = connection.sync();
            for (CityCountry cityCountry : data) {
                try {
                    sync.set(String.valueOf(cityCountry.getId()), mapper.writeValueAsString(cityCountry));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void RedisConnection(List<Integer> ids, RedisClient redisClient, ObjectMapper mapper) {
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            RedisStringCommands<String, String> sync = connection.sync();
            for (Integer id : ids) {
                String value = sync.get(String.valueOf(id));
                try {
                    mapper.readValue(value, CityCountry.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void testRedisData(List<Integer> ids) {
        DataToRedis.RedisConnection(ids, redisClient, mapper);
    }

    public void shutdown() {
        if (nonNull(sessionFactory)) {
            sessionFactory.close();
        }
        if (nonNull(redisClient)) {
            redisClient.shutdown();
        }
    }
}
