package ch.bbw.m183.vulnerapp.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
	private final AuthenticationManager authenticationManager;

	@PostMapping("/login")
	public UserEntity login(@RequestParam String username, @RequestParam String password, HttpServletRequest request) {
		var authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(username, password));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		request.getSession(true);
		return userService.whoami(authentication.getName());
	}

	@PostMapping("/logout")
	public void logout(HttpServletRequest request) throws ServletException {
		request.logout();
	}

	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	@GetMapping("/whoami")
	public UserEntity whoami(Authentication authentication) {
		return userService.whoami(authentication.getName());
	}
}
