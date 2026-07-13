package com.sparta.delivery.domain.user.entity;

import com.sparta.delivery.global.common.BaseEntity;
import com.sparta.delivery.global.common.Enums;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "p_users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE p_users SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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

	public void updateUser(String username, String password, String name, String phone) {
		if (username != null && !username.isBlank()) this.username = username;
		if (password != null && !password.isBlank()) this.password = password;
		if (name != null && !name.isBlank()) this.name = name;
		if (phone != null && !phone.isBlank()) this.phone = phone;
	}

    public void updateRole(Enums.UserRole role) {
		this.role = role;
    }
}
