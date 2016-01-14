package com.commonrpc.demo.myenum;

import java.util.HashMap;
import java.util.Map;

public enum UserType {
	unknow				(Platform.unknow, 	Role.unknow),		//未知
	izhuan_user			(Platform.izhuan, 	Role.studnet),		//爱赚用户
	izhuan_merchant		(Platform.izhuan, 	Role.merchant),		//爱赚商家
	game_user			(Platform.game, 	Role.studnet),		//玩赚用户
	game_merchant		(Platform.game, 	Role.merchant);		//玩赚商家

	private static final Map<String, UserType> map = new HashMap<>();
	private Platform platform;
	private Role role;

	UserType(Platform platform, Role role) {
		this.platform = platform;
		this.role = role;
	}

	public String val() {
		return String.valueOf(ordinal());
	}

	public static UserType valOf(String value) {
		int index = 0;
		try {index = Integer.valueOf(value);}catch (Exception e){}
		if (index >= values().length || index < 0) {
			return unknow;
		}
		return values()[index];
	}

	public Platform platform() {
		return platform;
	}

	public Role role() {
		return role;
	}

	public enum Platform {
		unknow, izhuan, game;
		public String val() {
			return String.valueOf(this.ordinal());
		}
	}

	public enum Role {
		unknow, studnet, merchant;
		public String val() {
			return String.valueOf(this.ordinal());
		}
	}
}