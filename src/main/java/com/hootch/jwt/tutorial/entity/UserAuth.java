package com.hootch.jwt.tutorial.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAuth {

	@Id
	@Column(name = "id")
	private String userid;

	private String auth1;

	private String auth2;

}
