# ChoSim
### Gamified Habit Tracking & Social Motivation App

---

## 프로젝트 소개

ChoSim은 사용자의 습관 형성과 자기관리를 돕기 위해  
**루틴 관리 + 캐릭터 성장 + 친구 소셜 기능**을 결합한  
게임화(Gamification) 기반 Android 애플리케이션입니다.

사용자는 루틴을 수행하며 캐릭터를 성장시키고,  
친구와 진행 상황을 공유하여 지속적인 동기부여를 받을 수 있습니다.

---

## 프로젝트 목표

- 습관 형성 및 자기관리 지원  
- 게임 요소를 통한 지속적인 동기 유도  
- 소셜 기능을 통한 사용자 참여도 향상  

---

## 주요 기능

### ✅ 루틴 관리 시스템
- 루틴 생성 / 수정 / 삭제  
- 반복 일정 설정  
- 캘린더 기반 완료 체크  

### ✅ 수행 평가 & 통계
- 루틴 수행률 분석  
- 자기관리 피드백 제공  

### ✅ 캐릭터 성장 시스템
- 루틴 수행 → 캐릭터 성장  
- 사용자 아바타 커스터마이징  

### ✅ 소셜 기능
- 친구 추가  
- 친구 루틴 진행 상황 확인  
- 상호 동기부여  

### ✅ 보상 & 커스터마이징
- 배경 테마 및 꾸미기 아이템 구매  
- 성취 기반 보상 시스템  

### ✅ 알림 기능
- 루틴 시간 알림  
- 기기 재부팅 후 자동 재등록  

---

## 앱 구조

```
ChoSim/
├── activities/              # 메인 화면 및 UI Activity
├── fragments/               # 홈, 루틴, 친구, 캐릭터 화면
├── adapters/                # RecyclerView 어댑터
├── models/                  # 데이터 모델
├── utils/                   # 공통 유틸리티
├── notifications/           # 알림 처리
├── resources/
│   ├── drawable/            # 캐릭터 및 UI 이미지
│   ├── layout/              # 화면 레이아웃
│   └── values/              # 스타일 및 문자열
└── manifest                 # 앱 권한 및 설정
```

---

## 시스템 구성

### 🔹 UI 구조
- Bottom Navigation 기반 화면 전환  
- ViewPager & Fragment 구조  

### 🔹 데이터 관리
- 루틴 및 사용자 데이터 관리  
- 로컬 저장소 또는 서버 연동 구조  

### 🔹 백그라운드 기능
- AlarmManager 기반 루틴 알림  
- 재부팅 시 알림 자동 복원  

---

## 기술 스택

### Mobile
- Android (Java/Kotlin)  
- Android Jetpack Components  
- RecyclerView  
- ViewPager & Fragment  

### Architecture
- MVVM 패턴  
- LiveData 기반 상태 관리  

### System Features
- AlarmManager (루틴 알림)  
- Background Service  

### UX 설계
- Gamification 기반 사용자 경험 설계  

---

## 주요 구현 포인트

✔ 루틴 관리 CRUD 기능 구현  
✔ 알림 시스템 및 백그라운드 처리  
✔ 캐릭터 성장 기반 동기부여 UX 설계  
✔ 친구 기능을 통한 소셜 동기부여  
✔ 생산성과 재미를 결합한 앱 설계  

---

## 개발자

| 이름 | 담당 |
|------|------|
| 김형규 | 앱 설계 및 개발, 루틴 관리 기능 구현, 알림 시스템 구현, 캐릭터 성장 UI 설계, UI 구성 및 사용자 경험 개선 |
| 김창섭 | 앱 설계 및 개발, 루틴 관리 기능 구현,  데이터 관리 기능 보조 구현, 테스트 및 안정화 |
---

## 향후 개선 방향

- Firebase 기반 클라우드 동기화  
- 사용자 통계 시각화 강화  
- 경쟁 및 랭킹 시스템 추가  
- AI 기반 습관 추천 기능  

---

## ⭐ 한 줄 설명

Gamified habit tracking Android app with social motivation and character growth system.
