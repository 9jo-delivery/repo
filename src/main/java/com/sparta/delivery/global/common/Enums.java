package com.sparta.delivery.global.common;

public class Enums {
	public enum UserRole {
		CUSTOMER(Authority.CUSTOMER),
		OWNER(Authority.OWNER),
		MANAGER(Authority.MANAGER),
		MASTER(Authority.MASTER);

		private final String authority;

        UserRole(String authority) {
            this.authority = authority;
        }

		public String getAuthority() {
			return this.authority;
		}

		public static class Authority {
			public static final String CUSTOMER = "ROLE_CUSTOMER";
			public static final String OWNER = "ROLE_OWNER";
			public static final String MANAGER = "ROLE_MANAGER";
			public static final String MASTER = "ROLE_MASTER";
		}
    }
	public enum OrderType { ONLINE }
	public enum OrderStatus { CREATED, ACCEPTED, COOKING, COOKED, DELIVERING, DELIVERED, COMPLETED, CANCELLED }
	public enum PaymentMethod { CARD }
	public enum PaymentStatus { READY, PAID, FAILED, CANCELLED, REFUNDED }
	public enum RegionType { SIDO, SIGUNGU, DONG }
}
