package ru.javarush.klimovich.entity;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.javarush.klimovich.redis.CityCountry;
import ru.javarush.klimovich.redis.Language;
import ru.javarush.klimovich.repository.CityRepository;
import ru.javarush.klimovich.repository.CountryRepository;
import ru.javarush.klimovich.session_provider.PropertiesSessionFactoryProvider;
import ru.javarush.klimovich.session_provider.SessionProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DataFromDB {
    private final SessionFactory sessionFactory;
    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;

    public DataFromDB() {
        SessionProvider sessionProvider = new PropertiesSessionFactoryProvider();
        sessionFactory = sessionProvider.getSessionFactory();
        cityRepository = new CityRepository(sessionFactory);
        countryRepository = new CountryRepository(sessionFactory);
    }

    public List<City> fetchData(DataFromDB dataDB) {
        try (Session session = sessionFactory.getCurrentSession()) {
            List<City> allCities = new ArrayList<>();
            session.beginTransaction();

            List<Country> countries = dataDB.countryRepository.getAll();

            int totalCount = dataDB.cityRepository.getTotalCount();
            int step = 500;
            for (int i = 0; i < totalCount; i += step) {
                allCities.addAll(dataDB.cityRepository.getItems(i, step));
            }

            List<CityCountry> preparedData = dataDB.transformData(allCities);

            session.getTransaction().commit();
            return allCities;
        }
    }

    public List<CityCountry> transformData(List<City> cities) {
        return cities.stream().map(city -> {
            CityCountry res = new CityCountry();
            res.setId(city.getId());
            res.setName(city.getName());
            res.setPopulation(city.getPopulation());
            res.setDistrict(city.getDistrict());

            Country country = city.getCountry();
            res.setAlternativeCountryCode(country.getAlternativeCode());
            res.setContinent(country.getContinent());
            res.setCountryCode(country.getCode());
            res.setCountryName(country.getName());
            res.setCountryPopulation(country.getPopulation());
            res.setCountryRegion(country.getRegion());
            res.setCountrySurfaceArea(country.getSurfaceArea());
            Set<CountryLanguage> countryLanguages = country.getLanguages();
            Set<Language> languages = countryLanguages.stream().map(cl -> {
                Language language = new Language();
                language.setLanguage(cl.getLanguage());
                language.setOfficial(cl.getOfficial());
                language.setPercentage(cl.getPercentage());
                return language;
            }).collect(Collectors.toSet());
            res.setLanguages(languages);

            return res;
        }).collect(Collectors.toList());
    }

    public void testMysqlData(List<Integer> ids) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            for (Integer id : ids) {
                City city = cityRepository.getById(id);
                Set<CountryLanguage> languages = city.getCountry().getLanguages();
            }
            session.getTransaction().commit();
        }
    }
}
