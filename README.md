# Electric Scooter Rental System - 电动车租赁系统

A full-stack web application for renting electric scooters in the City Centre.

## Project Overview

This project involves the development of an application for hiring electric scooters in the City Centre. The system consists of:

- **Backend**: Spring Boot 3.2.0 REST API
- **Frontend**: Vue 3 + Vite SPA
- **Database**: MySQL 8.x

---

## Tech Stack | 技术栈

### Backend (后端)

| Category | Technology |
|----------|------------|
| Framework | Spring Boot 3.2.0 |
| Database | MySQL 8.x |
| ORM | MyBatis 3.0.3 |
| Java | JDK 17 |
| Build Tool | Maven |
| IDE | IntelliJ IDEA |

### Frontend (前端)

| Category | Technology |
|----------|------------|
| Framework | Vue 3 (Composition API) |
| Build Tool | Vite 7.x |
| UI Library | Element Plus 2.x |
| State Management | Pinia 3.x |
| Router | Vue Router 5.x |
| HTTP Client | Axios |
| Charts | ECharts 6.x |
| Map | AMap (高德地图) |

---

## Project Structure | 项目结构

```
SEProject/
├── backend/              # 后端代码 (Spring Boot)
│   ├── src/main/java/
│   │   └── com.example.demo/
│   │       ├── controller/    # 控制层
│   │       ├── service/       # 业务层
│   │       ├── mapper/        # 数据访问层
│   │       ├── entity/         # 实体类
│   │       └── common/         # 公共类
│   └── src/main/resources/
│       ├── mapper/            # MyBatis XML
│       └── sql/               # 数据库脚本
├── frontend/             # 前端代码 (Vue 3)
│   ├── src/
│   │   ├── api/              # API 接口封装
│   │   ├── components/       # 公共组件
│   │   ├── router/           # 路由配置
│   │   ├── stores/           # Pinia 状态管理
│   │   ├── views/            # 页面组件
│   │   └── main.js           # 入口文件
│   └── package.json
└── doc/                  # 项目文档
```

---

## Features | 功能特性

### User Features | 用户功能

- [x] User Registration & Login (用户注册登录)
- [x] Browse Available Scooters (浏览可用车辆)
- [x] View Scooters on Map (地图视图查看车辆)
- [x] Book a Scooter (预约租车)
- [x] View Booking History (查看预约记录)
- [x] Fault Reporting (故障上报)
- [x] View Statistics Dashboard (使用统计图表)

---

## Prerequisites | 环境要求

| Software | Version | Download |
|----------|---------|----------|
| JDK | 17+ | [Download](https://adoptium.net/temurin/releases/?version=17) |
| MySQL | 8.x | [Download](https://dev.mysql.com/downloads/installer/) |
| Node.js | 18+ | [Download](https://nodejs.org/) |
| Maven | 3.6+ | [Download](https://maven.apache.org/download.cgi) |

### Recommended IDEs

- **IntelliJ IDEA**: For backend development
- **VS Code**: For frontend development (with Volar extension)
- **Navicat**: For database management (optional)

---

## Installation & Setup | 安装与配置

### Step 1: Clone the Project | 克隆项目

```powershell
cd E:\SEP2\SEProject
git clone <your-repo-url>
cd SEProject
```

### Step 2: Setup Database | 数据库配置

#### 2.1 Start MySQL Service | 启动 MySQL 服务

```powershell
# Windows - Start MySQL service
net start mysql
```

#### 2.2 Create Database | 创建数据库

```powershell
# Login to MySQL
mysql -u root -p
```

```sql
-- Create database
CREATE DATABASE scooter_rental CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 2.3 Initialize Database | 初始化数据库

Find and execute the SQL script in `backend/src/main/resources/sql/init.sql`:

```sql
USE scooter_rental;
SOURCE E:/SEP2/SEProject/SEProject/backend/src/main/resources/sql/init.sql;
```

### Step 3: Configure Backend | 后端配置

Open `backend/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/scooter_rental?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: root  # 修改为你的 MySQL 密码
```

### Step 4: Configure Frontend | 前端配置

```powershell
cd frontend
npm install
```

---

## Running the Project | 运行项目

### Start Backend | 启动后端

#### Option 1: Using Maven Command Line

```powershell
cd backend
mvnw.cmd spring-boot:run
```

#### Option 2: Using IntelliJ IDEA

1. Open IntelliJ IDEA
2. File -> Open -> Select `backend` folder
3. Wait for Maven to download dependencies
4. Find `DemoApplication.java` -> Right-click -> Run

**Backend will start at**: http://localhost:8080

---

### Start Frontend | 启动前端

```powershell
cd frontend
npm run dev
```

**Frontend will start at**: http://localhost:5173

---

## Pages | 页面

| Page | Route | Description |
|------|-------|-------------|
| Home | `/` | 首页 |
| Login | `/login` | 登录 |
| Register | `/register` | 注册 |
| Scooter List | `/scooters` | 车辆列表 |
| Map View | `/map` | 地图视图 |
| Booking | `/booking/:id` | 预约租车 |
| Profile | `/profile` | 个人中心 |
| Stats | `/stats` | 使用统计 |

---

## API Endpoints | API 接口

### User | 用户

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users/login` | 登录 |
| POST | `/api/users/register` | 注册 |

### Scooter | 电动车

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/scooters` | 获取所有车辆 |
| GET | `/api/scooters/available` | 获取可用车辆 |

### Booking | 预约

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/bookings` | 获取所有预约 |
| GET | `/api/bookings/{id}` | 获取预约详情 |
| POST | `/api/bookings` | 创建预约 |
| PUT | `/api/bookings/{id}` | 更新预约 |
| DELETE | `/api/bookings/{id}` | 取消预约 |

### Statistics | 统计

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/stats/consumption` | 消费统计 |
| GET | `/api/stats/usage` | 使用统计 |

### Response Format | 响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

---

## Quick Start | 快速启动

```powershell
# 1. 启动 MySQL 数据库

# 2. 启动后端 (Terminal 1)
cd backend
mvnw.cmd spring-boot:run

# 3. 启动前端 (Terminal 2)
cd frontend
npm install  # 首次运行需要
npm run dev

# 4. 打开浏览器访问
# http://localhost:5173
```

---

## Common Issues | 常见问题

### Q1: 端口 8080 被占用?

```powershell
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### Q2: 数据库连接失败?

1. 确认 MySQL 服务已启动: `net start mysql`
2. 确认数据库名、用户名、密码正确
3. 检查 `application.yml` 配置

### Q3: 前端无法请求后端 API?

1. 确认后端运行在 localhost:8080
2. 检查 `frontend/src/utils/request.js` 中的 baseURL 配置
3. 确保后端已启动

### Q4: 地图无法加载?

1. 检查高德地图 API Key 是否有效
2. 检查网络连接
3. 查看浏览器控制台错误信息

---

## License

This project is for educational purposes as part of the Software Engineering course.

