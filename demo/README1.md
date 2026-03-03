# 电动车租赁系统 - 后端项目

## 技术栈

| 类别 | 技术/工具 |
|------|-----------|
| 框架 | Spring Boot 3.2.0 |
| 数据库 | MySQL 8.x |
| ORM | MyBatis 3.0.3 |
| Java | JDK 17 |
| 构建工具 | Maven |
| 开发工具 | IntelliJ IDEA |

---

## 已完成内容

### 1. 项目架构 (三层架构)

```
com.example.demo/
├── controller/          # 控制层
│   ├── UserController.java
│   └── ScooterController.java
├── service/             # 业务层
│   ├── UserService.java
│   ├── ScooterService.java
│   └── impl/
├── mapper/              # 数据访问层
│   ├── UserMapper.java
│   └── ScooterMapper.java
├── entity/              # 实体类
│   ├── User.java
│   └── Scooter.java
└── common/              # 公共类
    └── Result.java
```

### 2. 数据库

- 数据库名: `scooter_rental`
- 已创建表: `users`, `scooters`
- 初始化脚本: `src/main/resources/sql/init.sql`

### 3. API接口

#### 用户接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/users` | 获取所有用户 |
| GET | `/api/users/{id}` | 获取指定用户 |
| POST | `/api/users` | 创建用户 |
| PUT | `/api/users/{id}` | 更新用户 |
| DELETE | `/api/users/{id}` | 删除用户 |

#### 电动车接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/scooters` | 获取所有电动车 |
| GET | `/api/scooters/{id}` | 获取指定电动车 |
| POST | `/api/scooters` | 添加电动车 |
| PUT | `/api/scooters/{id}` | 更新电动车信息 |
| DELETE | `/api/scooters/{id}` | 删除电动车 |

### 4. 配置文件

- 端口: `8080`
- 数据库连接: `root/root` (本地MySQL)

---

## 如何运行

```powershell
cd E:\SEP2\SEProject\demo
.\mvnw.cmd spring-boot:run
```

启动成功后访问: http://localhost:8080

---

## 对接说明

### 前端工程师

1. **API基础地址**: `http://localhost:8080`
2. **接口文档**: 见上方API接口表格
3. **数据格式**: 返回JSON，统一格式如下：

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

**测试示例**:
- 获取所有用户: GET http://localhost:8080/api/users
- 获取所有电动车: GET http://localhost:8080/api/scooters

### 后端工程师 (继续开发)

1. **添加新功能**: 在对应模块的 `controller/service/mapper/entity` 目录添加代码
2. **数据库操作**: 使用 `src/main/resources/sql/init.sql` 初始化数据库
3. **配置修改**: 编辑 `src/main/resources/application.yml`
4. **接口规范**: 返回值使用 `Result` 包装类，保持统一风格

### 数据库连接

- 主机: `localhost:3306`
- 数据库名: `scooter_rental`
- 用户名: `root`
- 密码: `root`

如需修改，编辑 `application.yml` 中的数据库配置。

---

## 待完成

- [ ] 用户登录/注册功能
- [ ] 订单管理模块
- [ ] 租金计算逻辑
- [ ] 车辆状态管理
- [ ] 前端页面 (待前端工程师开发)

---

## 新手入门指南 (从零开始配置)

### 第一步：安装必要软件

#### 1. 安装 JDK 17

1. 访问 Oracle 官网或 Adoptium 下载 JDK 17:
   - https://adoptium.net/temurin/releases/?version=17
2. 下载 Windows x64 MSI 安装包
3. 双击安装，**记得勾选"Set JAVA_HOME variable"**
4. 安装完成后验证：

```powershell
java -version
```

如果显示 `java version "17.xxx` 说明安装成功。

#### 2. 安装 MySQL 8.x

1. 下载 MySQL Installer:
   - https://dev.mysql.com/downloads/installer/
2. 选择 "Full" 安装类型
3. 设置 root 密码为: `root` (或者记住你设置的密码，后面要改配置)
4. 完成后验证：

```powershell
mysql --version
```

#### 3. 安装 IntelliJ IDEA

1. 下载 IntelliJ IDEA Ultimate (教育版可以申请免费 license):
   - https://www.jetbrains.com/idea/download/
2. 安装后打开项目

---

### 第二步：配置数据库

#### 1. 启动 MySQL 服务

Windows + R，输入 `services.msc`，找到 MySQL 服务，右键启动。

或者用命令行：

```powershell
net start mysql
```

#### 2. 创建数据库

打开 MySQL 命令行或 Navicat (如果安装了)，执行：

```sql
CREATE DATABASE scooter_rental CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 3. 导入初始化数据

找到项目中的 `src/main/resources/sql/init.sql` 文件，在 MySQL 中执行：

```sql
USE scooter_rental;
SOURCE E:/SEP2/SEProject/demo/src/main/resources/sql/init.sql;
```

或者直接复制 init.sql 里面的内容到 MySQL 执行。

---

### 第三步：从 GitHub 克隆项目

#### 1. 安装 Git

下载 Git for Windows:
- https://git-scm.com/download/win

安装时选项全部默认即可。

#### 2. 克隆项目

打开命令行，进入你想存放项目的目录：

```powershell
cd E:\SEP2\SEProject
git clone [你的GitHub仓库地址]
```

例如：

```powershell
git clone https://github.com/你的用户名/scooter-rental-system.git
```

#### 3. 用 IDEA 打开项目

1. 打开 IntelliJ IDEA
2. File -> Open -> 选择 demo 文件夹
3. 等待 Maven 自动下载依赖 (首次可能需要 3-5 分钟)

---

### 第四步：修改数据库配置

如果你的 MySQL 密码不是 `root`，需要修改配置文件：

1. 打开 `src/main/resources/application.yml`
2. 修改以下内容：

```yaml
spring:
  datasource:
    password: 你的实际密码  # 把 root 改成你的密码
```

---

### 第五步：运行项目

#### 方式一：用 IDEA 运行

1. 找到 `DemoApplication.java` 文件
2. 右键 -> Run 'DemoApplication'
3. 看到 "Started DemoApplication" 即启动成功

#### 方式二：用命令行运行

```powershell
cd E:\SEP2\SEProject\demo
mvnw.cmd spring-boot:run
```

---

### 第六步：测试接口

项目启动成功后，可以测试以下地址：

| 测试内容 | 地址 |
|---------|------|
| 获取所有用户 | http://localhost:8080/api/users |
| 获取所有电动车 | http://localhost:8080/api/scooters |

你可以在浏览器直接打开，或者用 Postman / Apifox 等工具测试。

---

### 常见问题

#### Q1: 端口 8080 被占用？

```powershell
# 查找占用端口的进程
netstat -ano | findstr :8080

# 关闭该进程 (假设 PID 是 12345)
taskkill /PID 12345 /F
```

或者修改 `application.yml` 中的端口为其他数字 (如 8081)。

#### Q2: 数据库连接失败？

1. 确认 MySQL 服务已启动
2. 确认数据库名、用户名、密码正确
3. 确认 MySQL 是否允许远程连接

#### Q3: Maven 下载依赖很慢？

可以配置阿里云镜像，在 `pom.xml` 或 Maven 设置中添加镜像源。

#### Q4: 乱码问题？

确保数据库和表的字符集是 `utf8mb4`，连接 URL 中要加 `characterEncoding=utf8`。

---

### 接下来要做什么？

1. **前端工程师**: 参考 "对接说明" 中的 API 文档，开始开发前端页面
2. **后端工程师**: 参考 "待完成" 列表，分配任务继续开发
3. **全组**: 确保每个人都能成功运行项目，进行接口联调
