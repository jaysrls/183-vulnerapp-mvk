package ch.bbw.m183.vulnerapp.datamodel;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "blogs")
public class BlogEntity {

	@Id
	UUID id;

	@Column
	@CreationTimestamp
	LocalDateTime createdAt;

	@Column(columnDefinition = "text")
	@NotBlank(message = "Title cannot be blank")
	@Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
	String title;

	@Column(columnDefinition = "text")
	@NotBlank(message = "Body cannot be blank")
	@Size(min = 10, max = 10000, message = "Body must be between 10 and 10000 characters")
	String body;
}
