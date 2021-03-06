package nl.fontys.kwetter.service;

import nl.fontys.kwetter.configuration.InMemoryTestConfiguration;
import nl.fontys.kwetter.exceptions.LoginException;
import nl.fontys.kwetter.exceptions.ModelInvalidException;
import nl.fontys.kwetter.exceptions.ModelNotFoundException;
import nl.fontys.kwetter.exceptions.UsernameAlreadyExistsException;
import nl.fontys.kwetter.models.dto.CredentialsDTO;
import nl.fontys.kwetter.models.dto.UserDTO;
import nl.fontys.kwetter.models.entity.User;
import nl.fontys.kwetter.repository.memory.implementation.data.manager.IInMemoryDatabaseManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testing the ProfileService")
@DataJpaTest
@Import(InMemoryTestConfiguration.class)
@Transactional
class ProfileServiceIntegrationTest {
    private static final String TEST_STRING = "This string is longer that the max length allowed for the name bio language website and location. The bio is still a bit longer but this should be enough.";

    private User testUser;

    @Autowired
    private IProfileService profileService;

    @Autowired
    private ILoginService loginService;

    @Autowired
    private IInMemoryDatabaseManager inMemoryDatabaseManager;

    @BeforeEach
    void setUp() {
        inMemoryDatabaseManager.reset();

        String email = "1@test.nl";
        String password = "test";

        try {
            testUser = loginService.login(new CredentialsDTO(email, password));
        } catch (LoginException | ModelInvalidException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("User can update the bio")
    void updateUser() {
        String bio = "This is a new test bio";
        String location = "This is a new test location";
        String website = "This is a new test website";
        String language = "Nederlands";

        UserDTO newUser = new UserDTO();
        newUser.setUuid(testUser.getId());
        newUser.setLocation(location);
        newUser.setWebsite(website);
        newUser.setBio(bio);
        newUser.setLanguage(language);

        try {
            User user = profileService.updateUser(newUser);
            assertNotNull(user);
            assertEquals(bio, user.getBio());
            assertEquals(location, user.getLocation());
            assertEquals(website, user.getWebsite());
            assertEquals(language, user.getLanguage());
        } catch (ModelInvalidException | ModelNotFoundException e) {
            fail("This exception should not have been thrown");
        }
    }

    @Test
    @DisplayName("User cant update the bio with too long entries")
    void tooLongUserUpdates() {
        UserDTO newUser = new UserDTO();
        newUser.setUuid(testUser.getId());
        newUser.setLocation(TEST_STRING);
        newUser.setWebsite(TEST_STRING);
        newUser.setBio(TEST_STRING);
        newUser.setLanguage(TEST_STRING);

        assertThrows(ModelInvalidException.class, () -> profileService.updateUser(newUser));

        try {
            User user = profileService.getFullProfile(testUser.getId());
            assertNull(user.getLocation());
            assertNull(user.getWebsite());
            assertNull(user.getBio());
            assertNull(user.getLanguage());
        } catch (ModelNotFoundException e) {
            fail("This exception should not have been thrown");
        }
    }

    @Test
    @DisplayName("User can update its name")
    void updateName() {
        String name = "newTest";

        UserDTO newUser = new UserDTO();
        newUser.setUuid(testUser.getId());
        newUser.setName(name);

        try {
            User user = profileService.updateName(newUser);
            assertNotNull(user);
            assertEquals(name, user.getName());
        } catch (ModelInvalidException | UsernameAlreadyExistsException | ModelNotFoundException e) {
            fail("This exception should not have been thrown");
        }
    }

    @Test
    @DisplayName("User cant update its name when it is too long")
    void tooLongName() {
        UserDTO newUser = new UserDTO();
        newUser.setUuid(testUser.getId());
        newUser.setName(TEST_STRING);

        assertThrows(ModelInvalidException.class, () -> profileService.updateName(newUser));

        try {
            User user = profileService.getFullProfile(testUser.getId());
            assertEquals("1Test", user.getName());
        } catch (ModelNotFoundException e) {
            fail("This exception should not have been thrown");
        }
    }

    @Test
    @DisplayName("User cant update its name when it is already taken")
    void nameAlreadyExists() {
        String name = "1Test";

        UserDTO newUser = new UserDTO();
        newUser.setUuid(testUser.getId());
        newUser.setName(name);

        assertThrows(UsernameAlreadyExistsException.class, () -> profileService.updateName(newUser));

        try {
            User user = profileService.getFullProfile(testUser.getId());
            assertEquals("1Test", user.getName());
        } catch (ModelNotFoundException e) {
            fail("This exception should not have been thrown");
        }
    }

    @Test
    @DisplayName("User can get a list of who it is following")
    void getFollowers() {
        try {
            List<User> followers = profileService.getFollowing(testUser.getId());
            assertNotNull(followers);
            assertEquals(9, followers.size());
        } catch (ModelNotFoundException e) {
            fail("This exception should not have been thrown");
        }
    }

    @Test
    @DisplayName("User can get a list of who it is following it")
    void getFollowing() {
        try {
            List<User> followers = profileService.getFollowers(testUser.getId());
            assertNotNull(followers);
            assertEquals(1, followers.size());
        } catch (ModelNotFoundException e) {
            fail("This exception should not have been thrown");
        }
    }

    @Test
    @DisplayName("User can get its full profile")
    void getFullProfile() {
        try {
            User user = profileService.getFullProfile(testUser.getId());
            assertNotNull(user);
        } catch (ModelNotFoundException e) {
            fail("This exception should not have been thrown");
        }
    }

    @Test
    @DisplayName("A user can follow and unfollow another user")
    void followAndUnfollow() {
        try {
            User user2 = loginService.login(new CredentialsDTO("2@test.nl", "test"));
            User user3 = loginService.login(new CredentialsDTO("3@test.nl", "test"));

            profileService.followUser(user2.getId(), user3.getId());
            List<User> following2 = profileService.getFollowing(user2.getId());
            List<User> followers3 = profileService.getFollowers(user3.getId());
            assertEquals(2, following2.size());
            assertEquals(2, followers3.size());

            profileService.unFollowUser(user2.getId(), user3.getId());
            List<User> following2afterUnfollow = profileService.getFollowing(user2.getId());
            List<User> followers3afterUnfollow = profileService.getFollowers(user3.getId());
            assertEquals(1, following2afterUnfollow.size());
            assertEquals(1, followers3afterUnfollow.size());

        } catch (LoginException | ModelInvalidException | ModelNotFoundException e) {
            fail("This exception should not have been thrown");
        }
    }
}