package com.sparta.delivery.enums;

public class Enums {
	public enum UserRole { CUSTOMER, OWNER, MANAGER, MASTER }
	public enum OrderType { ONLINE }
	public enum OrderStatus { CREATED, ACCEPTED, COOKING, COOKED, DELIVERING, DELIVERED, COMPLETED, CANCELLED }
	public enum PaymentMethod { CARD }
	public enum PaymentStatus { READY, PAID, FAILED, CANCELLED, REFUNDED }
	public enum RegionType { SIDO, SIGUNGU, DONG }
}
