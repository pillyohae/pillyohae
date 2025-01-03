# trello

---

## 🛠️ Tools :  <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> <img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=github&logoColor=Green"> <img alt="Java" src ="https://img.shields.io/badge/Java-007396.svg?&style=for-the-badge&logo=Java&logoColor=white"/>  <img alt="Java" src ="https://img.shields.io/badge/intellijidea-000000.svg?&style=for-the-badge&logo=intellijidea&logoColor=white"/>

---
## 👨‍💻 Period : 2024/12/23 ~ 2024/12/31

---
## 👨‍💻 ERD
![ERD](https://github.com/user-attachments/assets/5bd69a31-5cbd-4ccb-be91-95346e58da63)


---
## 👨‍💻 API명세서
https://documenter.getpostman.com/view/39378739/2sAYJ6E1Xu

---
## 👨‍💻 About Project
⚠️ : 예외처리 

- ### 회원가입/로그인
  - 회원가입 시 ID는 이메일 형식 비밀번호는 대소문자 포함 영문 + 숫자 + 특수문자 최소 1글자 포함한 8글자 이상이어야 한다. 
  - 권한에 따라 User/Admin으로 가입하며 권한에 따라 사용 가능한 기능이 다르다. 
  - 회원 탈퇴 시 비밀번호 확인 후 탈퇴, 탈퇴한 아이디는 재사용, 복구가 불가능합니다.  
  - ⚠️ 중복된 아이디 및 형식 불일치 시, Id, password 불일치 및 이미 탈퇴한 Id 

- ### 워크스페이스/멤버
    - ADMIN 권한의 유저만 생성 가능하며 이름, 설명을 설정하고 생성 시 생성자를 WORKSPACE 멤버로 설정 여러 개의 보드를 포함할 수 있다. 
    - WORKSPACE 역할을 가진 멤버는 멤버를 초대할 수 있으며 초대는 이메일을 통해 이루어진다. 
    - 유저가 멤버로 가입된 워크스페이스 목록을 볼 수 있다. 
    - 수정 및 삭제는 워크스페이스 역할의 멤버만 할 수 있고 삭제 시 워크스페이스 내의 모든 보드와 데이터도 삭제된다. 
    - ⚠️  이메일이 조잰하지 않거나 워크스페이스 역할인 멤버가 아닐 시, 역할이 워크스페이스가 아닐 시 

- ### 보드 
  - 멤버는 워크스페이스 내에 보드를 생성할 수 있고 제목, 배경색 or 이미지를 설정할 수 있다. 
  - 멤버는 자신이 속한 워크스페이스의 보드 조회 가능, 단 건 조회 시 해당 보드의 리스트와 카드도 볼 수 있다. 
  - 삭제 시 보드 내의 모든 리스트와 데이터 삭제 
  - ⚠️ 로그인하지 않은 멤버가 보드를 생성 시, 제목이 비어 있을 시, 읽기 전용 역할을 가진 멤버가 보드 생성/수정 시 ,읽기 전용 멤버가 보드 삭제 시
  
- ### 리스트 
  - 보드 내에 리스트 생성/수정 가능 제목을 갖고 보드 내에 순서 변경이 가능하다. 
  - 리스트 삭제 시 해당 리스트의 모든 카드와 데이터도 삭제된다. 
  - ⚠️ 읽기 전용 역할을 가진 멤버가 리스트 생성/수정/삭제 시 

- ### 카드 
  - 리스트 내에서 카드 생성/수정이 가능 하며 제목, 설명, 마감일, 담당자 멤버를 추가할 수 있다. 
  - 단 건 조회 시 상세 정보 조회 가능하며 활동 내역, 댓글 등을 확인할 수 있다. 
  - 카드 삭제 시 해당 카드와 데이터도 삭제된다. 
  - ⚠️ 읽기 전용 역할의 멤버가 리스트 생성/수정/삭제 시 

- ### 댓글 
  - 카드 내에 댓글 작성 가능하며 텍스트와 이모지 포함할 수 있다. 
  - 댓글 작성자는 자신의 댓글을 수정/삭제가 가능하다. 
  - ⚠️ 읽기 전용 역할을 가진 멤버가 댓글을 생성하려는 경우, 댓글 작성자가 아닌 멤버가 수정/삭제하려는 경우 

- ### 검색 
  - 카드의 제목, 내용, 마감일, 담당자 이름 등을 기준으로 페이징하여 검색한다. 
  - 특정 보드에 속한 모든 카드를 검색할 수 있다. 

---
## 🥵 Trouble Shooting & 🚀 Refactoring

- 회원가입 
  - 현재 유저에 대한 정보를 얻기 위해 session.setAttribute(GlobalConstants.USER_AUTH, authentication);를 통해 authentication에서 Id 값을 받아 사용할 수 있도록 했다.  


- 워크스페이스 
  - 멤버 초대시 현재 유저에 대한 멤버의 역할을 검증하기 위해 member 객체를 생성해야 하는데 현재 유저가 어느 곳에도 member로 속해 있지 않을 때 null이 저장되어 Objects.isNull(member)을 통해 if문에서 처리할 수 있도록 하였다. 


---
## 😭 아쉬운점
- 이전 과제를 다시 제출해야하는 상황으로 인해 온전히 집중할 수 없어서 도전 기능까지 해볼 시간이 조금 부족했던 것 같다.  