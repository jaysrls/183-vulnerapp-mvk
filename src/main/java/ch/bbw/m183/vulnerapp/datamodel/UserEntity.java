package ch.bbw.m183.vulnerapp.datamodel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "users")
public class UserEntity {

	@Id
	@NotBlank(message = "Username cannot be blank")
	@Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
	String username;

	@Column
	@NotBlank(message = "Fullname cannot be blank")
	@Size(min = 2, max = 100, message = "Fullname must be between 2 and 100 characters")
	String fullname;

	@Column
	@NotBlank(message = "Password cannot be blank")
	@Size(min = 8, message = "Password must be at least 8 characters")
	String password;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	Role role = Role.USER;

}
