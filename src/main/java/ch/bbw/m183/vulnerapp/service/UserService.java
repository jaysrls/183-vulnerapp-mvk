package ch.bbw.m183.vulnerapp.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import ch.bbw.m183.vulnerapp.datamodel.UserEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.experimental.StandardException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

	private final EntityManager entityManager;

	public UserEntity whoami(String username, String password) {
		// native queries are more performant!!1 :P
		var user = (UserEntity) entityManager.createNativeQuery("SELECT * from users where username='" + username + "'", UserEntity.class)
			.getSingleResult();
		if (password.equals(user.getPassword())) {
			return user;
		}
		throw new InvalidPasswordException("invalid password for user " + user.getUsername());
	}

	public UserEntity whoami(String username) {
		var user = entityManager.find(UserEntity.class, username);
		if (user == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
		}
		return user;
	}

	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@StandardException
	public static class InvalidPasswordException extends RuntimeException {

	}
}
