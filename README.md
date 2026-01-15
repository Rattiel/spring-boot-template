# Spring Boot with Kotlin

[![Spring Boot](https://img.shields.io/maven-central/v/org.springframework.boot/spring-boot.svg?label=Spring%20Boot&filter=3*&logo=spring-boot)](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot)
[![Kotlin](https://img.shields.io/badge/kotlin-2.0.0-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![ci](https://github.com/Rattiel/spring-boot-template/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/Rattiel/spring-boot-template/actions/workflows/ci.yml)
[![codecov](https://codecov.io/gh/Rattiel/spring-boot-template/graph/badge.svg?token=Y5903aP5R4)](https://codecov.io/gh/Rattiel/spring-boot-template)
[![License](https://img.shields.io/badge/license-MIT-green.svg?style=flat)](https://opensource.org/licenses/MIT)

English | [한국어](./README-ko_kr.md)

This is a project template for building applications using Spring Boot and Kotlin, structured with a Gradle Multi-Module setup.

### **1. Codecov Integration**

To automatically track and visualize code coverage for your commits and pull requests, you need to set up the Codecov integration. This requires adding a specific `CODECOV_TOKEN` to your GitHub repository's secrets, which allows the CI workflow to securely authenticate and upload coverage reports to Codecov.

#### **Adding the `CODECOV_TOKEN` to GitHub Secrets**

1. **Locate your Token**: First, go to your repository's page on the [Codecov website](https://codecov.io/) and find the upload token in the settings section.

2. **Navigate to Repository Settings**: In your GitHub repository, click on the **Settings** tab.

3. **Access Secrets**: In the left sidebar, go to the **Secrets and variables** menu and select **Actions**.

4. **Add the New Secret**:
    * Click the **New repository secret** button.
    * For the **Name**, enter `CODECOV_TOKEN`.
    * In the **Secret** field, paste the token you copied from the Codecov website.
    * Click **Add secret** to save.

This setup ensures that your token remains secure while enabling your CI pipeline to communicate with Codecov.

![Screenshot of the GitHub interface for adding a new repository secret](https://app.codecov.io/assets/repo_secret_light.CMxxRs9TMwzJw3_ZDi9-E.png)

### **2. Enabling the Dependency Graph**

The dependency graph is a feature that shows a list of external libraries (dependencies) your project uses. This feature is essential for submitting dependency information to GitHub from your CI pipeline.

**How to Set Up**

1. Go to the **Settings** tab of the repository.

2. In the left menu, click on **Code security and analysis**.

3. Find the **Dependency graph** section and click the **Enable** button to complete the process.

### **3. Update README.md Badges**

To display your project's CI status and code coverage, you need to update the badges in your `README.md` file to point to your own repository.

Update the following Markdown snippet:

```markdown
[![ci](https://github.com/Rattiel/spring-boot-template/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/Rattiel/spring-boot-template/actions/workflows/ci.yml)
[![codecov](https://codecov.io/gh/Rattiel/spring-boot-template/graph/badge.svg?token=Y5903aP5R4)](https://codecov.io/gh/Rattiel/spring-boot-template)
```

Replace the placeholder values as shown below.

```markdown
[![ci](https://github.com/<YOUR_USERNAME>/<YOUR_REPOSITORY>/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/<YOUR_USERNAME>/<YOUR_REPOSITORY>/actions/workflows/ci.yml)
[![codecov](https://codecov.io/gh/<YOUR_USERNAME>/<YOUR_REPOSITORY>/graph/badge.svg?token=<YOUR_CODECOV_TOKEN>)](https://codecov.io/gh/<YOUR_USERNAME>/<YOUR_REPOSITORY>)
```

**What to change:**

* `<YOUR_USERNAME>`: Replace this with your GitHub username or the organization name that owns the repository.
* `<YOUR_REPOSITORY>`: Replace this with the name of your repository.
* `<YOUR_CODECOV_TOKEN>`: Replace this with the Codecov token you obtained for your repository. You can find this on the settings page of your repository on the Codecov website.

## Requirements

* **Containerization**: Docker & Docker Compose
* **Java Runtime**: JDK 21 or higher

## Setting Up a Monitoring Environment

This template includes a pre-configured monitoring stack using Docker Compose, providing a comprehensive observability solution out of the box.

To launch the entire stack, run the following command:

```bash
docker compose up -d
```

This will start the following services:

* **Grafana:** [localhost:3000](http://localhost:3000) - A comprehensive dashboard for visualizing metrics and logs. You can log in with the default credentials: username `admin` and password `password`.
* **Prometheus:** [localhost:9090](http://localhost:9090) - The data source that collects metrics from your application.
* **Loki:** [localhost:3100](http://localhost:3100) - A log aggregation system designed to be highly scalable and efficient.
* **Tempo:** [localhost:3200](http://localhost:3200) - A high-scale, minimal-dependency distributed tracing backend.
* **Otel Collector:** [localhost:4317,4318](http://localhost:4317,4318) - A vendor-agnostic proxy to receive, process, and export telemetry data (metrics, traces, and logs).

## Project Structure

```
spring-boot-template/
├───application/        # The main module that starts the Spring Boot application
├───dependencies/       # Manages all dependency versions for the project
├───domain/             # Contains core business logic and data models (JPA entities)
├───observability/      # Provides monitoring capabilities (metrics, logs, traces)
├───web/                # Handles web requests, REST APIs, and security
├───build.gradle.kts    # Root build configuration for all modules
└───settings.gradle.kts # Defines the multi-module project structure
```

*   **`application`**: This is the final executable module. It assembles the other modules (`domain`, `web`, `observability`) into a runnable Spring Boot application.
*   **`dependencies`**: A special `java-platform` module that centralizes dependency management. It ensures that all other modules use consistent versions of libraries.
*   **`domain`**: The core of the application. It contains the business logic, data entities (JPA), and repositories. This module is designed to be independent of the web layer.
*   **`observability`**: Contains all the necessary configurations for monitoring the application. It collects and processes metrics, distributed traces, and logs via OTLP.
*   **`web`**: Implements the web layer of the application. It includes REST controllers, handles HTTP requests, and manages security (using Spring Security with OAuth2).
