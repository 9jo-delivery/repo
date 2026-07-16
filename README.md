# 🚴‍♂️ Delivery Project (배달 서비스 프로젝트)

> 모놀리식 아키텍쳐를 가진 배달 애플리케이션 서비스 프로젝트입니다. 
> 효율적인 가용성과 확장성을 고려하여 설계되었으며, AI 기능을 접목하여 점주와 고객 모두에게 편리한 경험을 제공합니다.

---

## 📌 프로젝트 목적 및 상세
본 프로젝트는 현대적인 배달 플랫폼의 핵심 비즈니스 로직을 마이크로서비스 아키텍처(MSA) 관점에서 설계하고 구현하는 것을 목표로 합니다.
* **사용자 및 보안**: JWT 기반의 안전한 인증/인가 처리를 통해 사용자 정보를 보호합니다.
* **가게 및 메뉴**: 카테고리별 가게 관리와 더불어 다채로운 메뉴 옵션 구성을 지원합니다.
* **주문 및 배송**: 실시간 주문 흐름과 사용자 배송지 관리를 통해 원활한 배달 경험을 제공합니다.
* **AI 연동 및 소통**: AI(Gemini)를 활용한 메뉴 설명 자동 생성 기능을 제공하며, 신뢰할 수 있는 리뷰 시스템을 구축합니다.

---

## 👥 팀원 역할 분담

각 팀원은 담당 도메인을 맡아 책임 개발을 진행하였습니다.

| 이름 | 담당 도메인 그룹 | 개발 상세 내용 |
| :--- | :--- | :--- |
| **정한길** | **회원 및 보안 그룹** | • Auth (인증/인가)<br>• Users (사용자 관리) |
| **엄태윤** | **지역 및 배송 그룹** | • Regions (지역 관리)<br>• DeliveryAddresses (배송지 관리) |
| **손유진** | **가게 관리 그룹** | • Restaurants (가게 관리)<br>• RestaurantCategories (가게 카테고리) |
| **이재형** | **메뉴 및 옵션 그룹** | • Menus (메뉴 관리)<br>• MenuOptionGroup (메뉴 옵션 그룹)<br>• MenuOption (메뉴 옵션) |
| **백승환** | **주문 및 결제 그룹** | • Orders (주문 관리)<br>• OrderItemOptions (주문 선택 옵션)<br>• Payments (결제 관리) |
| **최유준** | **고객 소통 및 AI 로그 그룹** | • Reviews (리뷰/평점)<br>• AiDescriptionLogs (AI 설명 생성 로그) |

---

## 🛠️ 기술 스택

### Backend
* **Language**: Java 17
* **Framework**: Spring Boot 3.x
* **Database**: PostgreSQL / JPA (Hibernate)
* **Caching & Concurrency**: Redis

### DevOps & Infrastructure
* **Build Tool**: Gradle
* **Environment**: Docker, Git / GitHub

---

## 💾 ERD (Entity Relationship Diagram)

> *프로젝트의 데이터베이스 테이블 구조 설계도입니다.*


<img width="2266" height="2382" alt="Image" src="https://github.com/user-attachments/assets/aa2ea48a-5efd-4e40-b8c7-d5efecf8006a" />

---

## 🚀 서비스 구성 및 실행 방법

### 1. 사전 요구사항
프로젝트를 실행하기 전, 아래 소프트웨어가 설치되어 있어야 합니다.
* Docker & Docker Compose
* Java 17 SDK

### 2. 환경 설정 (.env)
루트 디렉토리에 `.env` 파일을 생성하고 필요한 환경 변수를 설정합니다.
```env
DATABASE_URL=jdbc:postgresql://localhost:5432/delivery_db
DATABASE_USERNAME=your_username
DATABASE_PASSWORD=your_password
REDIS_HOST=localhost
REDIS_PORT=6379
GEMINI_API_KEY=your_gemini_api_key
