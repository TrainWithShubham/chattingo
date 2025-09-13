# Chattingo - Production-Ready Chat Application

A full-stack real-time chat application built with React, Spring Boot, MySQL, and WebSocket, deployed with Docker, Nginx, SSL, and Jenkins CI/CD pipeline.

## 🚀 Live Demo

- **Application**: https://mini.awscloudshop.online
- **Jenkins CI/CD**: https://jenkins.awscloudshop.online

## 🏗️ Architecture

```
Frontend (React) → Nginx Reverse Proxy → Backend (Spring Boot) → MySQL Database
                                    ↓
                            Jenkins CI/CD Pipeline
                                    ↓
                    Docker Registry + S3 Security Scans + Shared Library
```

## 🛠️ Tech Stack

- **Frontend**: React 18, WebSocket, Tailwind CSS
- **Backend**: Spring Boot, JWT Authentication, WebSocket, Maven
- **Database**: MySQL 8.0
- **Infrastructure**: Docker, Docker Compose, Nginx
- **CI/CD**: Jenkins with Shared Libraries
- **Security**: Trivy vulnerability scanning, SSL/TLS
- **Cloud**: AWS S3, IAM
- **Server**: Ubuntu VPS (Hostinger)

## 📋 Prerequisites

- Ubuntu VPS with root access
- Domain name with DNS configured
- Docker & Docker Compose
- Git
- Node.js 18+ (for local development)
- Java 17+ (for local development)

## 🚀 Quick Start

### Clone Repository
```bash
git clone https://github.com/dushyantkumark/chattingo.git
cd chattingo
```

### Environment Setup
```bash
# Copy environment templates
cp frontend/.env.example frontend/.env
cp backend/.env.example backend/.env

# Generate JWT secret
openssl rand -base64 64

# Update .env files with your configurations
```

### Local Development
```bash
# Start with Docker Compose
docker-compose up -d

# Or run individually
cd backend && ./mvnw spring-boot:run
cd frontend && npm start
```

### Production Deployment
```bash
# Build and deploy
docker-compose build
docker-compose up -d
```

## 🔧 Configuration

### Frontend Environment (.env)
```env
# Development
REACT_APP_API_URL=http://localhost:8080

# Production
REACT_APP_API_URL=https://mini.awscloudshop.online
```

### Backend Environment (.env)
```env
# JWT Configuration
JWT_SECRET=your-generated-jwt-secret-here

# Database Configuration
SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/chattingo_db?createDatabaseIfNotExist=true
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=securepassword123

# CORS Configuration
CORS_ALLOWED_ORIGINS=https://mini.awscloudshop.online,http://localhost:8080,http://localhost:3000
CORS_ALLOWED_METHODS=GET,POST,PUT,DELETE,OPTIONS
CORS_ALLOWED_HEADERS=*

# Application Configuration
SPRING_PROFILES_ACTIVE=production
SERVER_PORT=8080

# MySQL Configuration
MYSQL_ROOT_PASSWORD=securepassword123
MYSQL_DATABASE=chattingo_db
MYSQL_USER=chattingo
MYSQL_PASSWORD=chatpassword123
```

## 🐳 Docker Configuration

### Frontend Dockerfile
```dockerfile
# Multi-stage build for React
FROM node:18-alpine AS build-env
WORKDIR /app
COPY package*.json ./
RUN npm install --only=production

FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM node:18-alpine AS runtime
WORKDIR /app
COPY --from=build /app/build ./build
RUN npm install -g serve
EXPOSE 3000
CMD ["serve", "-s", "build", "-l", "3000"]
```

### Backend Dockerfile
```dockerfile
# Multi-stage build for Spring Boot
FROM maven:3.9.6-eclipse-temurin-17 AS build-env
WORKDIR /app
COPY pom.xml ./
RUN mvn dependency:go-offline

FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml ./
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
```

### Docker Compose
```yaml
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: securepassword123
      MYSQL_DATABASE: chattingo_db
      MYSQL_USER: chattingo
      MYSQL_PASSWORD: chatpassword123
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    networks:
      - chattingo-network
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      timeout: 20s
      retries: 10

  backend:
    image: dushyantkumark/chattingo-app-backend:latest
    env_file:
      - ./backend/.env
    depends_on:
      mysql:
        condition: service_healthy
    ports:
      - "8080:8080"
    networks:
      - chattingo-network
    restart: unless-stopped

  frontend:
    image: dushyantkumark/chattingo-app-frontend:latest
    env_file:
      - ./frontend/.env
    ports:
      - "3000:3000"
    networks:
      - chattingo-network
    restart: unless-stopped

volumes:
  mysql_data:

networks:
  chattingo-network:
    driver: bridge
```

## 🌐 Nginx Configuration

### VPS Setup Commands
```bash
# Update system and install Nginx + Certbot
sudo apt update && sudo apt install -y nginx certbot python3-certbot-nginx

# Create Nginx configuration
sudo nano /etc/nginx/sites-available/chattingo
```

### Nginx Virtual Host
```nginx
server {
    listen 80;
    server_name mini.awscloudshop.online;

    # Block access to hidden files
    location ~ /\.(?!well-known) {
        deny all;
    }

    # Frontend
    location / {
        proxy_pass http://127.0.0.1:3000;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
    }

    # Backend API
    location /api/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Auth endpoints
    location /auth/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # WebSocket endpoints
    location /ws/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_read_timeout 86400;
    }
}
```

### SSL Setup
```bash
# Enable site and test configuration
sudo ln -s /etc/nginx/sites-available/chattingo /etc/nginx/sites-enabled/
sudo nginx -t && sudo systemctl reload nginx

# Obtain SSL certificate
sudo certbot --nginx -d mini.awscloudshop.online --non-interactive --agree-tos -m your-email@example.com

# Test auto-renewal
sudo certbot renew --dry-run
```

## 🔄 Jenkins CI/CD Pipeline

### Jenkins Installation
```bash
# Install Java and Jenkins
sudo apt update && sudo apt install -y openjdk-17-jdk wget
curl -fsSL https://pkg.jenkins.io/debian/jenkins.io-2023.key | sudo tee /usr/share/keyrings/jenkins-keyring.asc
echo deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] https://pkg.jenkins.io/debian binary/ | sudo tee /etc/apt/sources.list.d/jenkins.list
sudo apt update && sudo apt install -y jenkins

# Change Jenkins port (backend uses 8080)
sudo nano /etc/default/jenkins
# Set HTTP_PORT=8000
sudo systemctl restart jenkins

# Get initial admin password
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

# Create Nginx configuration for Jenkins
sudo nano /etc/nginx/sites-available/jenkins
```

### Nginx Virtual Host
```nginx
server {
    listen 80;
    server_name jenkins.awscloudshop.online;

    # Block access to hidden files
    location ~ /\.(?!well-known) {
        deny all;
    }

    # Frontend
    location / {
        proxy_pass http://127.0.0.1:8000;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
    }
}
```

### SSL Setup
```bash
# Enable site and test configuration
sudo ln -s /etc/nginx/sites-available/jenkins /etc/nginx/sites-enabled/
sudo nginx -t && sudo systemctl reload nginx

# Obtain SSL certificate
sudo certbot --nginx -d jenkins.awscloudshop.online --non-interactive --agree-tos -m your-email@example.com

# Test auto-renewal
sudo certbot renew --dry-run
```

### Jenkins Shared Library

The project uses a Jenkins Shared Library for reusable pipeline functions:

**Repository**: https://github.com/dushyantkumark/jenkins-shared-library.git
**Branch**: `feat/library`

**Functions Available**:
- `buildImages()` - Build Docker images for frontend and backend
- `scanFilesystem()` - Run Trivy filesystem vulnerability scan
- `scanImages()` - Run Trivy Docker image vulnerability scans
- `uploadScansToS3()` - Upload scan results to S3 with pre-signed URLs
- `pushImages()` - Push Docker images to registry
- `updateCompose()` - Update docker-compose.yml with new image tags
- `deployApp()` - Deploy application using docker-compose
- `cleanupResources()` - Clean up Docker resources and artifacts

### Jenkins Shared Library Setup

**Configure Global Pipeline Libraries**:
- Go to **Manage Jenkins** → **Configure System** → **Global Pipeline Libraries**
- Click **Add** and configure:
  - **Name**: `jenkins-shared-library`
  - **Default Version**: `feat/library`
  - **Retrieval Method**: Modern SCM → Git
  - **Project Repository**: `https://github.com/dushyantkumark/jenkins-shared-library.git`
  - **Credentials**: (if private repo, otherwise leave blank)

**Library Structure**:
```
jenkins-shared-library/
├── vars/
│   ├── buildImages.groovy
│   ├── scanFilesystem.groovy
│   ├── scanImages.groovy
│   ├── uploadScansToS3.groovy
│   ├── pushImages.groovy
│   ├── updateCompose.groovy
│   ├── deployApp.groovy
│   └── cleanupResources.groovy
└── README.md
```

### Pipeline Configuration

**Jenkinsfile** (using shared library):
```groovy
@Library('jenkins-shared-library@feat/library') _

pipeline {
    agent any

    environment {
        DOCKER_HUB_CREDENTIALS = credentials('docker-hub-credentials')
        AWS_CREDENTIALS        = credentials('aws-cred')
        FRONTEND_REPO          = credentials('frontend-repo')
        BACKEND_REPO           = credentials('backend-repo')
        IMAGE_TAG              = "${BUILD_NUMBER}"
        AWS_DEFAULT_REGION     = credentials('my-region')
        S3_BUCKET              = credentials('bucket')
    }

    stages {
        stage('Git Clone') {
            steps {
                git branch: 'mini-hackathon', url: 'https://github.com/dushyantkumark/chattingo.git'
            }
        }

        stage('Build Images') {
            steps {
                script {
                    buildImages([
                        frontendRepo: env.FRONTEND_REPO,
                        backendRepo: env.BACKEND_REPO,
                        imageTag: env.IMAGE_TAG
                    ])
                }
            }
        }

        stage('Scan Filesystem') {
            steps {
                script {
                    scanFilesystem()
                }
            }
        }

        stage('Scan Images') {
            steps {
                script {
                    scanImages([
                        frontendRepo: env.FRONTEND_REPO,
                        backendRepo: env.BACKEND_REPO,
                        imageTag: env.IMAGE_TAG
                    ])
                }
            }
        }

        stage('Upload Scans to S3') {
            steps {
                script {
                    uploadScansToS3([
                        awsCredentialsId: 'aws-cred',
                        awsRegion: env.AWS_DEFAULT_REGION,
                        s3Bucket: env.S3_BUCKET
                    ])
                }
            }
        }

        stage('Push Images') {
            steps {
                script {
                    pushImages([
                        frontendRepo: env.FRONTEND_REPO,
                        backendRepo: env.BACKEND_REPO,
                        imageTag: env.IMAGE_TAG,
                        dockerCredentialsId: 'docker-hub-credentials'
                    ])
                }
            }
        }

        stage('Update Compose') {
            steps {
                script {
                    updateCompose([
                        frontendRepo: env.FRONTEND_REPO,
                        backendRepo: env.BACKEND_REPO,
                        imageTag: env.IMAGE_TAG
                    ])
                }
            }
        }

        stage('Deploy Application') {
            steps {
                script {
                    deployApp([
                        deploymentPath: '/home/ubuntu/mini/chattingo/'
                    ])
                }
            }
        }
    }

    post {
        always {
            script {
                cleanupResources([
                    imageTag: env.IMAGE_TAG
                ])
            }
            archiveArtifacts artifacts: 'filesystem-scan-results.txt,docker-scan-results.txt,scan-urls.txt', allowEmptyArchive: true
        }
    }
}
```

### Required Jenkins Credentials
- `docker-hub-credentials`: Docker Hub username/password
- `aws-cred`: AWS Access Key/Secret for S3 uploads
- `frontend-repo`: Docker repository name for frontend (e.g., `dushyantkumark/chattingo-app-frontend`)
- `backend-repo`: Docker repository name for backend (e.g., `dushyantkumark/chattingo-app-backend`)
- `my-region`: AWS region (e.g., `ap-south-1`)
- `bucket`: S3 bucket name for scan reports (e.g., `chattingo-app`)

## 🔒 Security Features

### Vulnerability Scanning with Trivy
```bash
# Install Trivy
wget https://github.com/aquasecurity/trivy/releases/latest/download/trivy_0.55.0_Linux-64bit.deb
sudo dpkg -i trivy_0.55.0_Linux-64bit.deb

# Scan filesystem
trivy fs .

# Scan Docker images
trivy image your-image:tag
```

### AWS IAM Configuration

**IAM User Policy**:
```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "AllowListBucket",
            "Effect": "Allow",
            "Action": "s3:ListBucket",
            "Resource": "arn:aws:s3:::chattingo-app"
        },
        {
            "Sid": "AllowReadWriteObjects",
            "Effect": "Allow",
            "Action": [
                "s3:GetObject",
                "s3:PutObject",
                "s3:DeleteObject"
            ],
            "Resource": "arn:aws:s3:::chattingo-app/*"
        }
    ]
}
```

**S3 Bucket Policy**:
```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "AllowJenkinsUserAccess",
            "Effect": "Allow",
            "Principal": {
                "AWS": "arn:aws:iam::YOUR-ACCOUNT-ID:user/chattingo-app-user"
            },
            "Action": [
                "s3:GetObject",
                "s3:PutObject"
            ],
            "Resource": "arn:aws:s3:::chattingo-app/jenkins-chattingo-app-scanning/*"
        }
    ]
}
```

## 📊 Application Features

### Core Functionality
- **Real-time Chat**: WebSocket-based messaging
- **User Authentication**: JWT-based login/signup
- **User Management**: Profile management
- **Message History**: Persistent chat storage
- **Responsive Design**: Mobile-friendly UI

### Technical Features
- **Multi-stage Docker builds** for optimized images
- **Health checks** for database connectivity
- **Graceful shutdowns** and restart policies
- **Environment-based configuration**
- **CORS handling** for cross-origin requests
- **WebSocket proxy** configuration

## 📁 Project Structure

```
chattingo/
├── frontend/                    # React application
│   ├── src/
│   │   ├── Components/         # React components
│   │   ├── Redux/             # State management
│   │   └── config/            # Configuration files
│   ├── public/                # Static assets
│   ├── Dockerfile             # Frontend container
│   ├── package.json           # Dependencies
│   └── .env                   # Environment variables
├── backend/                    # Spring Boot application
│   ├── src/
│   │   ├── main/java/        # Java source code
│   │   └── test/             # Test files
│   ├── Dockerfile            # Backend container
│   ├── pom.xml              # Maven dependencies
│   └── .env                 # Environment variales
├── docker-compose.yml       # Container orchestration
├── Jenkinsfile             # CI/CD pipeline
├── .gitignore              # Git ignore rules
├── CONTRIBUTING.md         # Development guide
└── README.md              # This file
```

## 🚀 Deployment Commands

### Development
```bash
# Start all services
docker compose up -d

# View logs
docker compose logs -f

# Rebuild after changes
docker compose build && docker compose up -d

# Stop services
docker compose down
```

### Production
```bash
# Deploy with specific tags
docker compose pull
docker compose up -d

# Update single service
docker compose up -d --no-deps backend

# Check service status
docker compose ps
```

## 🔧 Troubleshooting

### Common Issues

**CORS Errors**
- Update `CORS_ALLOWED_ORIGINS` in backend .env
- Restart backend container

**WebSocket Connection Failed**
- Check Nginx WebSocket proxy configuration
- Verify `/ws/` endpoint routing

**JWT Authentication Issues**
- Ensure JWT_SECRET is properly set
- Check token expiration settings

**Database Connection Errors**
- Verify MySQL container is healthy
- Check database credentials in .env

**Build Failures**
- Clear Docker build cache: `docker system prune -a`
- Check Dockerfile syntax

**Jenkins Shared Library Issues**
- Verify library configuration in Jenkins
- Check branch name: `feat/library`
- Ensure repository is accessible

### Debug Commands
```bash
# Check container status
docker compose ps

# View container logs
docker compose logs backend
docker compose logs frontend
docker compose logs mysql

# Test database connection
docker compose exec mysql mysql -u root -p

# Check Nginx configuration
sudo nginx -t

# View SSL certificate status
sudo certbot certificates

# Check Jenkins logs
sudo journalctl -u jenkins -f
```

## 📊 Monitoring & Logs

### Application Monitoring
- **Container Health**: Docker health checks
- **Application Logs**: Centralized logging via Docker
- **SSL Certificate**: Auto-renewal with Certbot
- **Security Scans**: Automated Trivy reports uploaded to S3

### Log Locations
- **Application Logs**: `docker-compose logs`
- **Nginx Logs**: `/var/log/nginx/`
- **Jenkins Logs**: `/var/log/jenkins/`
- **System Logs**: `journalctl`
- **Security Scan Reports**: S3 bucket with pre-signed URLs

## 🤝 Contributing

- Fork the repository
- Create feature branch (`git checkout -b feature/amazing-feature`)
- Commit changes (`git commit -m 'Add amazing feature'`)
- Push to branch (`git push origin feature/amazing-feature`)
- Open Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Built during the Chattingo Mini Hackathon
- Hostinger VPS for infrastructure hosting
- AWS for cloud services and S3 storage
- Jenkins community for CI/CD automation tools
- Docker for containerization platform
- Jenkins Shared Library: https://github.com/dushyantkumark/jenkins-shared-library.git

---

**🌐 Live Application**: https://mini.awscloudshop.online
**🔧 Jenkins Pipeline**: https://jenkins.awscloudshop.online
**📚 Chattingo Application**: https://github.com/dushyantkumark/chattingo.git (branch: feat/jenkinsLibrary)
**📚 Jenkins Shared Library**: https://github.com/dushyantkumark/jenkins-shared-library.git (branch: feat/library)
**📊 Security Reports**: Available via S3 pre-signed URLs in Jenkins builds.
