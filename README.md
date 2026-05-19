# CapyGlide 电动滑板车租赁系统

一个基于 Vue + Spring Boot 的电动滑板车租赁管理系统。

## 系统架构

- **前端**：Vue 3 + Element Plus + AMap (高德地图)
- **后端**：Spring Boot + MyBatis
- **数据库**：MySQL

## 环境要求

- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Maven 3.8+

## 快速部署

### 1. 数据库配置

```bash
# 登录 MySQL
mysql -u root -p

# 创建数据库
CREATE DATABASE capyglide CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 使用数据库
USE capyglide;

# 执行初始化脚本
SOURCE database.sql;
```

### 2. 后端部署

```bash
cd demo

# 修改配置文件中的数据库连接信息
# 编辑 src/main/resources/application.yml

# 打包
mvn clean package -DskipTests

# 运行
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

后端运行地址：`http://localhost:8081`

### 3. 前端部署

```bash
cd frontend

# 安装依赖
npm install

# 开发模式
npm run dev

# 生产打包
npm run build
```

前端运行地址：`http://localhost:5173`

## 默认账号

### 管理员账号
- 用户名：`admin`
- 密码：`admin123`

### 普通用户
在数据库 `users` 表中查看，或通过注册功能创建。

## 功能模块

### 用户端
- [x] 用户注册/登录
- [x] 地图找车
- [x] 扫码租车
- [x] 行程管理
- [x] 支付功能
- [x] 个人信息管理

### 管理端
- [x] 数据统计面板
- [x] 用户管理
- [x] 滑板车管理
- [x] 问题反馈管理
- [x] 定价配置
- [x] 超时费用配置

## 项目结构

```
├── demo/                    # 后端 Spring Boot 项目
│   ├── src/main/java/     # Java 源代码
│   ├── src/main/resources/ # 配置文件
│   └── pom.xml            # Maven 配置
│
├── frontend/               # 前端 Vue 项目
│   ├── src/              # Vue 源代码
│   ├── public/            # 静态资源
│   └── package.json      # NPM 配置
│
└── database.sql           # 数据库初始化脚本
```

## API 端口说明

| 服务 | 端口 | 说明 |
|------|------|------|
| 后端 API | 8081 | Spring Boot 服务 |
| 前端开发 | 5173 | Vite 开发服务器 |
| MySQL | 3306 | 数据库服务 |

## 技术栈

### 前端
- Vue 3 + Composition API
- Vue Router 4
- Pinia 状态管理
- Element Plus UI
- Axios HTTP 客户端
- AMap Web API

### 后端
- Spring Boot 3.x
- Spring MVC
- MyBatis
- MySQL Connector
- JWT 认证

## 注意事项

1. 首次运行需要先初始化数据库
2. 高德地图 API Key 需要在 `frontend/src/utils/amap.js` 中配置
3. 生产环境部署时请修改默认密码
