# 🚀 Chattingo - Mini Hackathon Challenge

A full-stack real-time chat application built with React, Spring Boot, and WebSocket technology. **Your mission**: Containerize this application using Docker and deploy it to Hostinger VPS using Jenkins CI/CD pipeline.

## 🚨 **IMPORTANT: Registration Required**

### **📝 [REGISTER NOW](https://forms.gle/NgNJNg8yQvPaA1Vz9)**

**Deadline: September 5, 2025**

**What you need to register:**

- Your Name
- Email ID
- LinkedIn Profile URL
- GitHub Profile URL

### **📤 [SUBMISSION FORM](https://forms.gle/ww3vPN29JTNRqzM27)**

**Deadline: September 10, 2025 (11:59 PM)**

### **📂 Repository Access**

**Repository URL**: https://github.com/iemafzalhassan/chattingo

- **Currently**: Private repository (registration phase)
- **Will be public**: After registration closes (Sept 6)
- **Action Required**: Fork & star the repository once it becomes public

---

## 🎯 **Hackathon Challenge**

Transform this vanilla application into a production-ready, containerized system with automated deployment!

## 📋 Table of Contents

- [Hackathon Overview](#-hackathon-overview)
- [Architecture Overview](#️-architecture-overview)
- [Technology Stack](#️-technology-stack)
- [Quick Start](#-quick-start)
- [Your Tasks](#-your-tasks)
- [Application Features](#-application-features)
- [Project Structure](#-project-structure)
- [Submission Requirements](#-submission-requirements)
- [Support & Resources](#-support--resources)
- [Judging Criteria](#-judging-criteria)

## 🏆 Hackathon Overview

### 🎯 Project Goals

- **Build & Deploy**: Create Dockerfiles and containerize the application
- **CI/CD Pipeline**: Implement Jenkins automated deployment
- **VPS Deployment**: Deploy on Hostinger VPS using modern DevOps practices

### 📅 Timeline & Registration

#### **Registration Phase (Sept 3-5)**

📝 **[REGISTER HERE](https://forms.gle/NgNJNg8yQvPaA1Vz9)** - Complete by Sept 5

**Registration Form Requirements:**

- Name
- Email ID
- LinkedIn Profile
- GitHub Profile

#### **Event Schedule**

- **Sept 6**: Kickoff session (VPS setup, Docker + Jenkins basics)
- **Sept 7-10**: Build period (3 days)
- **Sept 10**: Submissions close at 11:59 PM
- **Sept 11-12**: Judging & reviews
- **Sept 13**: Winners announced

## 🏗️ Architecture Overview

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Backend       │    │   Database      │
│   (React)       │◄──►│   (Spring Boot) │◄──►│   (MySQL)       │
│   Port: 80      │    │   Port: 8080    │    │   Port: 3306    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │
         └────── WebSocket ──────┘
```

## 🛠️ Technology Stack

### Frontend

- **React 18** - Modern UI framework
- **Redux Toolkit** - State management
- **Material-UI** - Component library
- **Tailwind CSS** - Utility-first CSS
- **WebSocket (SockJS + STOMP)** - Real-time messaging
- **React Router** - Client-side routing

### Backend

- **Spring Boot 3.3.1** - Java framework
- **Spring Security** - Authentication & authorization
- **Spring Data JPA** - Database operations
- **Spring WebSocket** - Real-time communication
- **JWT** - Token-based authentication
- **MySQL** - Database

### DevOps (Your Tasks)

- **Docker** - Containerization (YOU BUILD)
- **Docker Compose** - Multi-container orchestration (YOU BUILD)
- **Jenkins** - CI/CD pipeline (YOU BUILD)
- **Nginx** - Web server & reverse proxy (YOU BUILD)

## 🚀 Quick Start

### **Just Registered? Start Here!**

#### **Step 1: Fork & Clone**

```bash
# Fork this repository on GitHub: https://github.com/iemafzalhassan/chattingo
# Then clone your fork
git clone https://github.com/YOUR_USERNAME/chattingo.git
cd chattingo
```

#### **Step 2: Join Discord**

- **[🗓┃Events-Announcement](https://discord.gg/jYeffuxs)**: Stay updated
- **[📝┃Events-Chat](https://discord.gg/bHVKCYj4)**: Get technical support

#### **Step 3: Local Development Setup**

Follow **[CONTRIBUTING.md](CONTRIBUTING.md)** for detailed setup instructions.

#### **Step 4: Follow the Timeline**

- **Sept 7-10**: Build your implementation
- **Sept 10**: Submit before 11:59 PM

### **Reference Guides**

- **Detailed Setup & Deployment**: **[CONTRIBUTING.md](CONTRIBUTING.md)**

## 🔧 **YOUR TASKS**

### **Task 1: Docker Implementation (5 Marks)**

You need to create these files from scratch:

#### **Frontend Dockerfile** (3-stage build)

- Stage 1: Node.js build environment
- Stage 2: Build React application
- Stage 3: Nginx runtime server

#### **Backend Dockerfile** (3-stage build)

- Stage 1: Maven build environment
- Stage 2: Build Spring Boot application
- Stage 3: JRE runtimee

#### **Docker Compose** (Root level)

Create `docker-compose.yml` to orchestrate all services.

**Scoring**: Single Stage (2), Two Stage (4), Multi Stage (5)

### **Task 2: Jenkins CI/CD Pipeline (17 Marks)**

Create a `Jenkinsfile` with these stages:

```groovy
pipeline {
    agent any

    stages {
        stage('Git Clone') {
            // Clone repository from GitHub (2 Marks)
        }
        stage('Image Build') {
            // Build Docker images for frontend & backend (2 Marks)
        }
        stage('Filesystem Scan') {
            // Security scan of source code (2 Marks)
        }
        stage('Image Scan') {
            // Vulnerability scan of Docker images (2 Marks)
        }
        stage('Push to Registry') {
            // Push images to Docker Hub/Registry (2 Marks)
        }
        stage('Update Compose') {
            // Update docker-compose with new image tags (2 Marks)
        }
        stage('Deploy') {
            // Deploy to Hostinger VPS (5 Marks)
        }
    }
}
```

### Additional Requirements

- **Jenkins Shared Library**: 3 Marks
- **Active Engagement**: 2 Marks
- **Creativity**: 2 Marks
- **Quality Storytelling**: 10 Marks
  - README (Compulsory): 3 Marks
  - Blog (Optional): 2 Marks
  - Video (Compulsory): 5 Marks

### **Task 3: VPS Deployment**

- **Hostinger VPS Setup**: Ubuntu 22.04 LTS, 2GB RAM
- **Domain Configuration**: Setup your domain with DNS
- **SSL Certificate**: Configure HTTPS with Let's Encrypt
- **Production Deployment**: Automated deployment via Jenkins

**Detailed Instructions**: See **[CONTRIBUTING.md](CONTRIBUTING.md)**

## 📱 Application Features

### Core Functionality

- ✅ User authentication (JWT)
- ✅ Real-time messaging (WebSocket)
- ✅ Group chat creation
- ✅ User profile management
- ✅ Message timestamps
- ✅ Responsive design

### API Endpoints

```
POST   /api/auth/register    - User registration
POST   /api/auth/login       - User login
GET    /api/users            - Get users
POST   /api/chats/create     - Create chat
GET    /api/chats            - Get user chats
POST   /api/messages/create  - Send message
GET    /api/messages/{chatId} - Get chat messages
WS     /ws                   - WebSocket endpoint
```

## 📊 Project Structure

```
chattingo/
├── backend/                 # Spring Boot application
│   ├── src/main/java/
│   │   └── com/chattingo/
│   │       ├── Controller/  # REST APIs
│   │       ├── Service/     # Business logic
│   │       ├── Model/       # JPA entities
│   │       └── config/      # Configuration
│   ├── src/main/resources/
│   │   └── application.properties
│   ├── .env                 # Environment variables
│   └── pom.xml
├── frontend/               # React application
│   ├── src/
│   │   ├── Components/     # React components
│   │   ├── Redux/          # State management
│   │   └── config/         # API configuration
│   ├── .env                # Environment variables
│   └── package.json
├── CONTRIBUTING.md         # Detailed setup & deployment guide
└── README.md              # This file
```

## 🎥 **Submission Requirements**

### **📤 Submission Form: [Submit Here](https://forms.gle/ww3vPN29JTNRqzM27)**

**Deadline: Sept 10, 11:59 PM**

### **Required Submission Fields**

1. **Name** - Your full name
2. **Email ID** - Contact email
3. **GitHub Repository URL** - Your forked and implemented project
4. **Video Demo URL** - 3-minute demo video (YouTube/Drive link)
5. **Live Application URL** - Your deployed application on VPS
6. **Blog URL** - Technical writeup (Optional but recommended)
7. **README URL** - Link to your updated README file

### **Required Deliverables**

1. **GitHub Repository** with your implementation

   - ✅ Dockerfiles (Backend & Frontend - 3-stage builds)
   - ✅ docker-compose.yml (Root level orchestration)
   - ✅ Jenkinsfile (Complete CI/CD pipeline)
   - ✅ nginx.conf (Frontend configuration)
   - ✅ Environment configurations
   - ✅ Updated README with deployment instructions

2. **Live Application** deployed on Hostinger VPS

   - ✅ Working chat application with HTTPS
   - ✅ SSL certificate configured
   - ✅ Domain properly configured
   - ✅ All features functional (registration, login, messaging)

3. **Video Demo** (3 minutes max) showing:
   - ✅ Local Docker setup demonstration
   - ✅ Jenkins pipeline execution
   - ✅ Live application walkthrough on VPS
   - ✅ Key features demonstration

### **Bonus Points**

1. **Blog Post** - Technical writeup of your implementation (2 marks)
2. **Additional Features** - Enhancements to the chat app
3. **Monitoring** - Application monitoring and logging
4. **Security** - Additional security measures

## 🚀 **Getting Started with the Challenge**

### **Implementation Flow** (Following Hackathon Timeline)

#### **Phase 1: Registration (Sept 3-5)**

1. **[Register Here](https://forms.gle/NgNJNg8yQvPaA1Vz9)** with your details
2. **Fork this repository**: https://github.com/iemafzalhassan/chattingo
3. **Join Discord** channels for updates and support

#### **Phase 2: Kickoff Session (Sept 6)**

- **Attend intro session** - VPS setup guide, Docker & Jenkins basics
- **Get your VPS** access and domain setup
- **Ask questions** and clarify requirements

#### **Phase 3: Build Period (Sept 7-10)**

- **Day 1**: Local development setup → **[CONTRIBUTING.md](CONTRIBUTING.md)**
- **Day 2**: Docker & Jenkins implementation
- **Day 3**: VPS deployment → **[CONTRIBUTING.md](CONTRIBUTING.md)**

#### **Phase 4: Submission (Sept 10)**

📤 **[SUBMIT HERE](https://forms.gle/ww3vPN29JTNRqzM27)** before 11:59 PM

#### **Phase 5: Results (Sept 11-13)**

- **Sept 11-12**: Judging & reviews
- **Sept 13**: Winners announced

## 📞 Support & Resources

### Discord Channels

- **[🗓┃𝖤𝗏𝖾𝗇𝗍𝗌-𝖠𝗇𝗇𝗈𝗎𝗇𝖼𝖾𝗆𝖾𝗇𝗍](https://discord.gg/jYeffuxs)**: Stay Active in the Announcement channel for Hackathon Update.
- **[📝┃𝖤𝗏𝖾𝗇𝗍𝗌-𝖢𝗁𝖺𝗍](https://discord.gg/bHVKCYj4)**: Technical support.

### Reference Links

- [Hackathon Repository](https://github.com/iemafzalhassan/chattingo)
- [Docker Documentation](https://docs.docker.com/)
- [Jenkins Documentation](https://www.jenkins.io/doc/)
- [Hostinger VPS Guide](https://www.hostinger.com/tutorials/vps)

## 🏅 Judging Criteria

| Component      | Marks | Description                    |
| -------------- | ----- | ------------------------------ |
| Dockerfile     | 5     | Multi-stage implementation     |
| Jenkinsfile    | 17    | Complete CI/CD pipeline        |
| Shared Library | 3     | Reusable Jenkins components    |
| Engagement     | 2     | Active participation           |
| Creativity     | 2     | Unique features/implementation |
| Documentation  | 10    | README, blog, video            |

---

**Good luck with your hackathon project! 🚀**
