package nl.fontys.kwetter.controllers;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import nl.fontys.kwetter.exceptions.LoginException;
import nl.fontys.kwetter.models.dto.CredentialsDTO;
import nl.fontys.kwetter.models.dto.JwtTokenDTO;
import nl.fontys.kwetter.models.entity.Credentials;
import nl.fontys.kwetter.models.entity.User;
import nl.fontys.kwetter.service.IEmailService;
import nl.fontys.kwetter.service.IHateoasService;
import nl.fontys.kwetter.service.ILoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping(path = "/api/", produces = MediaType.APPLICATION_JSON_VALUE)
public class LoginController {

    private final Logger logger;
    private final ILoginService loginService;
    private final IHateoasService hateoasService;
    private final IEmailService emailService;

    @Autowired
    public LoginController(ILoginService loginService, IHateoasService hateoasService, IEmailService emailService) {
        this.logger = LoggerFactory.getLogger(LoginController.class);
        this.loginService = loginService;
        this.hateoasService = hateoasService;
        this.emailService = emailService;
    }

    @PostMapping("login")
    public ResponseEntity<JwtTokenDTO> login(@RequestBody CredentialsDTO credentials) {

        if (logger.isDebugEnabled()) {
            logger.info(String.format("%s is trying to login", credentials.getEmail()));
        }

        User userLoggedIn = loginService.login(credentials);
        Credentials userCredentials = userLoggedIn.getCredentials();

        String jwtToken = buildJwtToken(userLoggedIn.getCredentials().getEmail());
        JwtTokenDTO jwtTokenDTO = new JwtTokenDTO(jwtToken, hateoasService.getUserDTOWithLinks(userLoggedIn));

        if (!userCredentials.isVerified()) {
            emailService.sendMessage(userCredentials.getEmail(), "Kwetter Verification", jwtToken, userLoggedIn.getId().toString());
            throw new LoginException("User is not verified, an verification email has been sent");
        }

        if (logger.isDebugEnabled()) {
            logger.info(String.format("%s logged in successfully", credentials.getEmail()));
        }

        return ResponseEntity.ok(jwtTokenDTO);
    }

    private String buildJwtToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .claim("roles", "user")
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, "secretkey")
                .compact();
    }
}
