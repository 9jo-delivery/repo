package com.sparta.delivery.domain.user.entity;

import com.sparta.delivery.domain.user.dto.request.AdminUpdateUserReqDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.sparta.delivery.global.common.BaseEntity;
import com.sparta.delivery.global.common.Enums;

@Entity
@Table(name = "p_users")
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

	public void updateUser(String password, String phone) {
		this.password = password;
		this.phone = phone;
	}

	public void adminUpdateUser(AdminUpdateUserReqDto reqDto, String encodedPassword) {
		this.username = reqDto.getUsername();
		this.password = encodedPassword;
		this.name = reqDto.getName();
		this.phone = reqDto.getPhone();
		this.role = reqDto.getRole();
	}
}
