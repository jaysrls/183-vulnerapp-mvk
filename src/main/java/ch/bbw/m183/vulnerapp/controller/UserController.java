package ch.bbw.m183.vulnerapp.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
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

	@GetMapping("/csrf-token")
	public CsrfToken csrfToken(CsrfToken token) {
		return token;
	}

	@PostMapping("/login")
	public UserEntity login(
			@RequestParam(name = "username") @NotBlank @Size(min = 3, max = 50) String username,
			@RequestParam(name = "password") @NotBlank @Size(min = 8) String password,
			HttpServletRequest request) {
		var authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(username, password));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		var session = request.getSession(true);
		session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
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
