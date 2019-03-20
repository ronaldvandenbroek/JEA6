package nl.fontys.kwetter.service.implementation;

import nl.fontys.kwetter.exceptions.CannotLoginException;
import nl.fontys.kwetter.exceptions.InvalidModelException;
import nl.fontys.kwetter.models.Credentials;
import nl.fontys.kwetter.models.User;
import nl.fontys.kwetter.repository.IUserRepository;
import nl.fontys.kwetter.service.ILoginService;
import nl.fontys.kwetter.service.IValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Service for handling model operations regarding the jsfLogin process.
 */
@Service
public class LoginService implements ILoginService {

    private IUserRepository userRepository;
    private IValidatorService validator;

    @Autowired
    public LoginService(IValidatorService validator, IUserRepository userRepository) {
        this.validator = validator;
        this.userRepository = userRepository;
    }

    /**
     * Login
     *
     * @param credentials The users credentials
     * @return The logged in user
     * @throws CannotLoginException  Thrown if the credentials are not correct.
     * @throws InvalidModelException Thrown if the credentials are not valid.
     */
    @Override
    public User login(Credentials credentials) throws CannotLoginException, InvalidModelException {
        validator.validate(credentials);

        User user = userRepository.findByCredentials(credentials);
        if (user == null) {
            throw new CannotLoginException("No account found matching the credentials");
        }
        return user;
    }

    /**
     * Create a new account
     *
     * @param email    The users email
     * @param password The users password
     * @return The new user
     */
    @Override
    public User createAccount(String email, String password) {
        throw new NotImplementedException();
    }
}
