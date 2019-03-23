package nl.fontys.kwetter.service;

import nl.fontys.kwetter.exceptions.CannotLoginException;
import nl.fontys.kwetter.exceptions.InvalidModelException;
import nl.fontys.kwetter.models.Credentials;
import nl.fontys.kwetter.models.User;

public interface ILoginService {
    User autoLogin() throws CannotLoginException, InvalidModelException;

    User login(Credentials credentials) throws CannotLoginException, InvalidModelException;

    User createAccount(String email, String password);
}
