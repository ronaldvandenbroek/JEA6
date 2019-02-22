package nl.fontys.kwetter.service;

import nl.fontys.kwetter.exceptions.CannotLoginException;
import nl.fontys.kwetter.models.Credentials;
import nl.fontys.kwetter.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testing the Login Service")
class LoginServiceTest {

    private LoginService loginService;

    @BeforeEach
    void setUp(){
        loginService = new LoginService();
    }

    @Test
    @DisplayName("Valid login")
    void login() {
        String email = "1@test.nl";
        String password = "test";
        String username = "1Test";

        try {
            User user = loginService.login(email, password);
            assertNotNull(user);
            assertEquals(email, user.getCredentials().getEmail());
            assertEquals(password, user.getCredentials().getPassword());
            assertEquals(username, user.getName());
        } catch (CannotLoginException e) {
            fail("This exception should not have been thrown");
        }
    }

    @Test
    @DisplayName("Invalid user")
    void invalidUser() {
        String email = "WrongEmail@test.nl";
        String password = "test";

        assertThrows(CannotLoginException.class, () -> loginService.login(email, password));
    }

    @Test
    @DisplayName("Invalid email")
    void invalidEmail() {

        new Credentials(null, null);

        String email = null;
        String password = "test";

        assertThrows(CannotLoginException.class, () -> loginService.login(email, password));
    }
}