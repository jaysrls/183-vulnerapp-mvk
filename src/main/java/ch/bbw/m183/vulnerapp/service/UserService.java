package ch.bbw.m183.vulnerapp.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import ch.bbw.m183.vulnerapp.datamodel.UserEntity;
import ch.bbw.m183.vulnerapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.StandardException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserEntity whoami(String username, String password) {
		var user = userRepository.findById(username)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
		if (passwordEncoder.matches(password, user.getPassword())) {
			return user;
		}
		throw new InvalidPasswordException("invalid password for user " + user.getUsername());
	}

	public UserEntity whoami(String username) {
		var user = userRepository.findById(username)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
		return user;
	}

	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@StandardException
	public static class InvalidPasswordException extends RuntimeException {

	}
}
