package ch.bbw.m183.vulnerapp.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.bbw.m183.vulnerapp.datamodel.UserEntity;
import ch.bbw.m183.vulnerapp.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	@GetMapping("/whoami")
	public UserEntity whoami(Authentication authentication) {
		return userService.whoami(authentication.getName());
	}

	@PostMapping("/fakelogin")
	public UserEntity fakelogin(@RequestParam String username, @RequestParam String password) {
		return userService.whoami(username, password);
	}

	@GetMapping("/fakelogout")
	public void fakelogout() {
		// does absolutely nothing
	}
}
