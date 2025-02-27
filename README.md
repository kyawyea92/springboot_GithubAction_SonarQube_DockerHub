# Spring Boot Project with GitHub Actions, SonarCloud and DockerHub

## Overview
This project is a Spring Boot application built with Java 21. It utilizes GitHub Actions for continuous integration and deployment (CI/CD) with three jobs:

- **Build**: Compiles the application and ensures dependencies are resolved.
- **Test**: Runs unit tests and performs static code analysis using SonarCloud.
- **Deploy**: Pushes the Docker image to DockerHub upon successful completion of previous jobs.

Either process can be adapted based on project requirements.

## Prerequisites
Before running the project, ensure you have the following installed:

- Java 21
- Docker
- Maven
- GitHub account with repository access
- SonarCloud account (if code analysis is enabled)
- DockerHub account

## GitHub Actions Workflow
The GitHub Actions workflow (`.github/workflows/sonarqube_dockerhub.yml`) includes three jobs:

### 1. Build Job
- Checks out the repository
- Sets up JDK 21
- Builds the application using Maven

### 2. Test Job (Including SonarCloud)
- Runs unit tests with Maven
- Uploads test results as artifacts
- Performs static code analysis with SonarCloud

### 3. Deploy Job
- Builds a Docker image
- Pushes the image to DockerHub

## Setup and Configuration
### SonarCloud Integration
Ensure you have a SonarCloud token:
1. Create a SonarCloud account.
2. Generate a token under "My Account > Security".
3. Add the token as a GitHub secret (`SONAR_TOKEN`).
4. Configure `sonar-project.properties` with:
   ```properties
   sonar.organization=your_organization
   sonar.host.url=https://sonarcloud.io
   ```

### DockerHub Integration
Ensure you have a DockerHub account:
1. Create a repository on DockerHub.
2. Add your DockerHub credentials as GitHub secrets:
   - `DOCKER_USERNAME`
   - `DOCKER_PASSWORD`

### GitHub Actions Workflow File Example
```yaml
name: SpringBoot_GitHubAction_SonarQube_DockerHub
on:
 push:
  branches: ["main","dev"]
 workflow_dispatch:
jobs:
 build:
  runs-on: ubuntu-latest
  steps:
    - name: Checkout Code
      uses: actions/checkout@v4.2.2
      with:
          fetch-depth: 0

    - name: Setup JDK 21
      uses: actions/setup-java@v4.7.0
      with:
        java-version: 21
        distribution: "temurin"
        cache: maven
    
    - name: Build maven
      run: mvn clean install -DskipTests=true
      working-directory: SpringBootSonarQube
     
 test:
  runs-on: ubuntu-latest
  needs: build
  steps:
    - name: Checkout from Repository
      uses: actions/checkout@v4.2.2
    - name: Setup JDK 21
      uses: actions/setup-java@v4.7.0
      with:
        java-version: 21
        distribution: "temurin"
        cache: maven
        
    - name: Build maven
      run: mvn clean install -DskipTests=true
      working-directory: SpringBootSonarQube
      
    - name: Build and analyze
      env:
        SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
      run: mvn -B verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.projectKey=kyawyea92_springboot_GithubAction_SonarQube_DockerHub
      working-directory: SpringBootSonarQube
      
    - name: Check SonarQube Quality Gate
      id: sonar
      run: |
          sleep 10
          STATUS=$(curl -s -u ${{ secrets.SONAR_TOKEN }}: "https://sonarcloud.io/api/qualitygates/project_status?projectKey=kyawyea92_springboot_GithubAction_SonarQube_DockerHub" | jq -r .projectStatus.status)
          echo "SonarQube Quality Gate status: $STATUS"
          if [[ "$STATUS" != "OK" ]]; then
            echo "Quality gate failed"
            exit 1
          fi
          
 deploy:
    needs: test
    if: github.ref == 'refs/heads/main'
    runs-on: ubuntu-latest
    steps:
      - name: Checkout for production
        uses: actions/checkout@v4.2.2
        
      - name: Setup JDK 21
        uses: actions/setup-java@v4.7.0
        with:
          java-version: 21
          distribution: "temurin"
          cache: maven
          
      - name: Build With Maven
        run: mvn clean install -DskipTests=true
        working-directory: SpringBootSonarQube
        
      - name: Package with Maven
        run: mvn package -DskipTests=true
        working-directory: SpringBootSonarQube

      - name: Build Docker images
        run: docker build ./SpringBootSonarQube/ -t kyawyealwin/springboot_sonarqube:v1.0.0
      
      - name: Push to DockerHub
        run: |
          docker login -u kyawyealwin -p ${{secrets.DOCKER_PASSWORD}}
          docker push kyawyealwin/springboot_sonarqube:v1.0.0
```

## Running Locally
To run the project locally:
```sh
mvn spring-boot:run
```

## Contributing
Contributions are welcome! Feel free to open issues or submit pull requests.

## License
This project is licensed under the MIT License.

