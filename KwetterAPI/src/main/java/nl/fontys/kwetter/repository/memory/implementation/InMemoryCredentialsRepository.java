package nl.fontys.kwetter.repository.memory.implementation;

import nl.fontys.kwetter.models.Credentials;
import nl.fontys.kwetter.repository.memory.IInMemoryCredentialsRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static nl.fontys.kwetter.repository.memory.implementation.data.InMemoryDatabase.credentialsCollection;
import static nl.fontys.kwetter.repository.memory.implementation.data.InMemoryDatabase.userCollection;

@Repository
@Profile("memory")
public class InMemoryCredentialsRepository implements IInMemoryCredentialsRepository {

    @Override
    public <S extends Credentials> S save(S s) {
        credentialsCollection().removeIf(credentials -> credentials.getEmail().equals(s.getEmail()));
        credentialsCollection().add(s);
        Optional<Credentials> savedCredentials = credentialsCollection().stream().filter(credentials -> credentials.getEmail().equals(s.getEmail())).findFirst();
        return (S) savedCredentials.orElse(null);
    }

    @Override
    public <S extends Credentials> Iterable<S> saveAll(Iterable<S> iterable) {
        throw new NotImplementedException();
    }

    @Override
    public Optional<Credentials> findById(String s) {
        return credentialsCollection().stream().filter(credentials -> credentials.getEmail().equals(s)).findFirst();
    }

    @Override
    public boolean existsById(String s) {
        throw new NotImplementedException();
    }

    @Override
    public Iterable<Credentials> findAll() {
        return new ArrayList<>(credentialsCollection());
    }

    @Override
    public Iterable<Credentials> findAllById(Iterable<String> iterable) {
        throw new NotImplementedException();
    }

    @Override
    public long count() {
        return credentialsCollection().size();
    }

    @Override
    public void deleteById(String s) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(Credentials credentials) {
        credentialsCollection().remove(credentials);
    }

    @Override
    public void deleteAll(Iterable<? extends Credentials> iterable) {
        throw new NotImplementedException();
    }

    @Override
    public void deleteAll() {
        credentialsCollection().clear();
    }
}
