package ru.javarush.klimovich;

import org.hibernate.SessionFactory;
import ru.javarush.klimovich.entity.DataFromDB;
import ru.javarush.klimovich.entity.DataToRedis;
import ru.javarush.klimovich.entity.City;
import ru.javarush.klimovich.redis.CityCountry;
import ru.javarush.klimovich.repository.CityRepository;
import ru.javarush.klimovich.repository.CountryRepository;
import ru.javarush.klimovich.session_provider.PropertiesSessionFactoryProvider;
import ru.javarush.klimovich.session_provider.SessionProvider;

import java.util.List;


public class Main {
    private final SessionFactory sessionFactory;
    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;
    private static List<CityCountry> preparedData;
    private final DataFromDB dataDB;
    private final DataToRedis dataToRedis;
    private final SessionProvider sessionProvider;

    public Main() {
        sessionProvider = new PropertiesSessionFactoryProvider();
        sessionFactory = sessionProvider.getSessionFactory();
        cityRepository = new CityRepository(sessionFactory);
        countryRepository = new CountryRepository(sessionFactory);
        dataDB = new DataFromDB();
        dataToRedis = new DataToRedis();
    }

    public static void main(String[] args) {
        Main main = new Main();
        List<City> allCities = main.dataDB.fetchData(main.dataDB);

        List<CityCountry> preparedData = main.dataDB.transformData(allCities);
        main.dataToRedis.pushToRedis(preparedData);

        main.sessionFactory.getCurrentSession().close();

        List<Integer> ids = List.of(3, 2545, 123, 4, 189, 89, 3458, 1189, 10, 102);

        long startRedis = System.currentTimeMillis();
        main.dataToRedis.testRedisData(ids);
        long stopRedis = System.currentTimeMillis();

        long startMysql = System.currentTimeMillis();
        main.dataDB.testMysqlData(ids);
        long stopMysql = System.currentTimeMillis();

        System.out.printf("%s:\t%d ms\n", "Redis", (stopRedis - startRedis));
        System.out.printf("%s:\t%d ms\n", "MySQL", (stopMysql - startMysql));

        main.dataToRedis.shutdown();
    }
}