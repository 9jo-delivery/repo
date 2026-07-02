package com.sparta.delivery.domain.user.enitiy;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.sparta.delivery.global.common.BaseEntity;
import com.sparta.delivery.global.common.Enums;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE users SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class User extends BaseEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 10)
	private String username;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(length = 30)
	private String phone;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Enums.UserRole role;

	@Builder
	public User(String username, String password, String name, String phone, Enums.UserRole role) {
		this.username = username;
		this.password = password;
		this.name = name;
		this.phone = phone;
		this.role = role;
	}
}
