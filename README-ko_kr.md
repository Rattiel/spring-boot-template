# Spring Boot와 Kotlin

[![Spring Boot](https://img.shields.io/maven-central/v/org.springframework.boot/spring-boot.svg?label=Spring%20Boot)](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot)
[![Kotlin](https://img.shields.io/badge/kotlin-2.0.0-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![ci](https://github.com/Rattiel/spring-boot-template/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/Rattiel/spring-boot-template/actions/workflows/ci.yml)
[![codecov](https://codecov.io/gh/Rattiel/spring-boot-template/graph/badge.svg?token=Y5903aP5R4)](https://codecov.io/gh/Rattiel/spring-boot-template)
[![License](https://img.shields.io/badge/license-MIT-green.svg?style=flat)](https://opensource.org/licenses/MIT)

[English](./README.md) | 한국어

이 프로젝트는 Spring Boot와 Kotlin을 사용하여 애플리케이션을 빌드하기 위한 템플릿으로, Gradle 멀티 모듈 구조로 구성되어 있어요.

### **1. Codecov 연동하기**

커밋과 풀 리퀘스트에 대한 코드 커버리지를 자동으로 추적하고 시각화하려면 Codecov 연동을 설정해야 해요. 이를 위해서는 GitHub 리포지토리의 시크릿에 특정 `CODECOV_TOKEN`을 추가해야 하며, 이를 통해 CI 워크플로우가 Codecov에 안전하게 인증하고 커버리지 리포트를 업로드할 수 있어요.

#### **GitHub 시크릿에 `CODECOV_TOKEN` 추가하기**

1. **토큰 찾기**: 먼저 [Codecov 웹사이트](https://codecov.io/)의 리포지토리 페이지로 이동하여 설정 섹션에서 업로드 토큰을 찾아요.

2. **리포지토리 설정으로 이동하기**: GitHub 리포지토리에서 **Settings** 탭을 클릭해요.

3. **시크릿 접근하기**: 왼쪽 사이드바에서 **Secrets and variables** 메뉴로 이동한 후 **Actions**를 선택해요.

4. **새 시크릿 추가하기**:
    * **New repository secret** 버튼을 클릭해요.
    * **Name**에는 `CODECOV_TOKEN`을 입력하요.
    * **Secret** 필드에는 Codecov 웹사이트에서 복사한 토큰을 붙여넣어요.
    * **Add secret**을 클릭하여 저장해요.

이 설정은 토큰을 안전하게 유지하면서 CI 파이프라인이 Codecov와 통신할 수 있도록 보장해요.

![새로운 리포지토리 시크릿을 추가하기 위한 GitHub 인터페이스 스크린샷](https://app.codecov.io/assets/repo_secret_light.CMxxRs9TMwzJw3_ZDi9-E.png)

### **2. 디펜던시 그래프 활성화하기**

디펜던시 그래프는 프로젝트가 사용하는 외부 라이브러리(의존성)의 목록을 보여주는 기능이에요. CI 파이프라인에서 의존성 정보를 GitHub에 제출하기 위해 이 기능이 꼭 필요해요.

**설정 방법**

1. 해당 저장소의 **Settings** 탭으로 이동해요.

2. 왼쪽 메뉴에서 **Advanced Security**를 클릭해요.

3. **Dependency graph** 항목을 찾아 **Enable** 버튼을 눌러주면 완료돼요.

### **3. README.md 배지 업데이트하기**

프로젝트의 CI 상태와 코드 커버리지를 표시하려면 `README.md` 파일의 배지가 자신의 리포지토리를 가리키도록 업데이트해야 해요.

아래의 마크다운 스니펫을 업데이트해요:

```markdown
[![ci](https://github.com/Rattiel/spring-boot-template/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/Rattiel/spring-boot-template/actions/workflows/ci.yml)
[![codecov](https://codecov.io/gh/Rattiel/spring-boot-template/graph/badge.svg?token=Y5903aP5R4)](https://codecov.io/gh/Rattiel/spring-boot-template)
```

아래와 같이 플레이스홀더 값을 교체해요.

```markdown
[![ci](https://github.com/<YOUR_USERNAME>/<YOUR_REPOSITORY>/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/<YOUR_USERNAME>/<YOUR_REPOSITORY>/actions/workflows/ci.yml)
[![codecov](https://codecov.io/gh/<YOUR_USERNAME>/<YOUR_REPOSITORY>/graph/badge.svg?token=<YOUR_CODECOV_TOKEN>)](https://codecov.io/gh/<YOUR_USERNAME>/<YOUR_REPOSITORY>)
```

**변경할 내용:**

* `<YOUR_USERNAME>`: 여러분의 GitHub 사용자 이름 또는 리포지토리를 소유한 조직의 이름으로 바꾸어요.
* `<YOUR_REPOSITORY>`: 여러분의 리포지토리 이름으로 바꾸어요.
* `<YOUR_CODECOV_TOKEN>`: 여러분의 리포지토리를 위해 발급받은 Codecov 토큰으로 바꾸어요. 이 토큰은 Codecov 웹사이트의 리포지토리 설정 페이지에서 찾을 수 있어요.

## 요구사항

* **컨테이너화**: Docker & Docker Compose
* **자바 런타임**: JDK 21 또는 이상

## 모니터링 환경 설정하기

이 템플릿에는 Docker Compose를 사용하여 미리 구성된 모니터링 스택이 포함되어 있어, 즉시 포괄적인 관측 가능성 솔루션을 제공해요.

전체 스택을 시작하려면 다음 명령어를 실행해요:

```bash
docker compose up -d
```

이 명령어를 실행하면 다음 서비스들이 시작돼요:

* **Grafana:** [localhost:3000](http://localhost:3000) - 메트릭과 로그를 시각화하는 포괄적인 대시보드예요. 기본 자격 증명인 사용자 이름 `admin`과 비밀번호 `password`로 로그인할 수 있어요.
* **Prometheus:** [localhost:9090](http://localhost:9090) - 애플리케이션에서 메트릭을 수집하는 데이터 소스예요.
* **Loki:** [localhost:3100](http://localhost:3100) - 확장성이 뛰어나고 효율적으로 설계된 로그 집계 시스템이에요.
* **Tempo:** [localhost:3200](http://localhost:3200) - 최소한의 의존성을 가진 대규모 분산 추적 백엔드예요.
* **Otel Collector:** [localhost:4317,4318](http://localhost:4317,4318) - 원격 측정 데이터(메트릭, 트레이스, 로그)를 수신, 처리 및 내보내는 벤더 중립적인 프록시예요.

## 프로젝트 구조

```
spring-boot-template/
├───application/        # Spring Boot 애플리케이션을 시작하는 메인 모듈
├───dependencies/       # 프로젝트의 모든 의존성 버전을 관리하는 모듈
├───domain/             # 핵심 비즈니스 로직과 데이터 모델(JPA 엔티티)을 포함하는 모듈
├───observability/      # 모니터링(메트릭, 로그, 트레이스) 기능을 제공하는 모듈
├───web/                # 웹 요청, REST API 및 보안을 처리하는 모듈
├───build.gradle.kts    # 모든 모듈에 대한 루트 빌드 구성
└───settings.gradle.kts # 멀티 모듈 프로젝트 구조를 정의
```

*   **`application`**: 최종 실행 가능한 모듈이에요. 다른 모듈(`domain`, `web`, `observability`)을 조립하여 실행 가능한 Spring Boot 애플리케이션을 만들어요.
*   **`dependencies`**: 의존성 관리를 중앙에서 처리하는 특별한 `java-platform` 모듈이에요. 다른 모든 모듈이 일관된 버전의 라이브러리를 사용하도록 보장해요.
*   **`domain`**: 애플리케이션의 핵심이에요. 비즈니스 로직, 데이터 엔티티(JPA) 및 리포지토리를 포함해요. 이 모듈은 웹 계층과 독립적으로 설계되었어요.
*   **`observability`**: 애플리케이션 모니터링에 필요한 모든 구성을 포함해요. 지표, 분산 추적, 로그를 OTLP를 통해 수집하고 처리해요.
*   **`web`**: 애플리케이션의 웹 계층을 구현해요. REST 컨트롤러, HTTP 요청 처리 및 보안(Spring Security 및 OAuth2)을 포함해요.