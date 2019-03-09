package nl.fontys.kwetter.configuration;

import nl.fontys.kwetter.models.Credentials;
import nl.fontys.kwetter.models.Kwetter;
import nl.fontys.kwetter.models.Role;
import nl.fontys.kwetter.models.User;
import nl.fontys.kwetter.repository.ICredentialsRepository;
import nl.fontys.kwetter.repository.IKwetterRepository;
import nl.fontys.kwetter.repository.IUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;

@TestConfiguration
public class DataLoaderTestConfiguration {

    @Bean
    public CommandLineRunner loadData(IKwetterRepository kwetterRepository, IUserRepository userRepository, ICredentialsRepository credentialsRepository) {
        return (args) -> {
            userRepository.deleteAll();
            kwetterRepository.deleteAll();
            credentialsRepository.deleteAll();

            Collection<User> presetUsers = new ArrayList<>();
            Collection<Credentials> presetCredentials = new ArrayList<>();
            Collection<Kwetter> presetKwetters = new ArrayList<>();

            //Create 10 allUsers
            for (int i = 1; i < 11; i++) {
                User user = new User(Role.USER);
                user.setName(i + "Test");

                Credentials credentials = new Credentials(i + "@test.nl", "test", user);

                presetCredentials.add(credentials);
                presetUsers.add(user);

                userRepository.save(user);
                credentialsRepository.save(credentials);
            }

            //Follow everyone via the first user
            Iterator<User> userIterator = presetUsers.iterator();
            User user = userIterator.next();
            while (userIterator.hasNext()) {
                user.follow(userIterator.next());
            }
            userRepository.save(user);

            //Create Kwetters for the first user
            Calendar calendar = Calendar.getInstance();
            for (int i = 1; i < 11; i++) {
                Kwetter kwetter = new Kwetter(i + "Test", null, null, user, calendar.getTime());

                presetKwetters.add(kwetter);

                kwetterRepository.save(kwetter);
            }
        };
    }
}
