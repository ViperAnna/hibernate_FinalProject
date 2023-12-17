package ru.javarush.klimovich.session_provider;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.javarush.klimovich.entity.City;
import ru.javarush.klimovich.entity.Country;
import ru.javarush.klimovich.entity.CountryLanguage;

public class PropertiesSessionFactoryProvider implements SessionProvider {

    @Override
    public SessionFactory getSessionFactory() {
        final SessionFactory sessionFactory;
        return new Configuration()
                .addAnnotatedClass(City.class)
                .addAnnotatedClass(Country.class)
                .addAnnotatedClass(CountryLanguage.class)
                .buildSessionFactory();

    }
}

