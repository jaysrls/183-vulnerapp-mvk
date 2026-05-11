package ch.bbw.m183.vulnerapp.service;

import java.util.stream.Stream;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.bbw.m183.vulnerapp.datamodel.Role;
import ch.bbw.m183.vulnerapp.datamodel.UserEntity;
import ch.bbw.m183.vulnerapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

	private final UserRepository userRepository;

	public UserEntity createUser(UserEntity newUser) {
		if (newUser.getRole() == null) {
			newUser.setRole(Role.USER);
		}
		return userRepository.save(newUser);
	}

	public Page<UserEntity> getUsers(Pageable pageable) {
		return userRepository.findAll(pageable);
	}

	public void deleteUser(String username) {
		userRepository.deleteById(username);
	}

	@EventListener(ContextRefreshedEvent.class)
	public void loadTestUsers() {
		Stream.of(
				new UserEntity().setUsername("admin").setFullname("Super Admin").setPassword("{noop}super5ecret").setRole(Role.ADMIN),
				new UserEntity().setUsername("fuu").setFullname("Johanna Doe").setPassword("{noop}bar").setRole(Role.USER)
			)
				.forEach(this::createUser);
	}
}
