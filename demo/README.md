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

### 3. API 接口（前后端协作约定）

**说明**：以下接口为 Sprint 1 统一约定，前端按此对接；如有新增或变更会在此文档更新并同步。

- **基础地址**：`http://localhost:8080`
- **统一响应格式**：所有接口返回 JSON，格式为 `{ "code": 200, "message": "success", "data": { ... } }`；失败时 `code` 非 200，`message` 为错误说明。
- **需登录接口**：请求头携带 `Authorization: Bearer <token>`（登录后返回的 token）。

---

#### 3.1 认证（用户与管理员）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/auth/register` | 用户注册 |
| POST | `/auth/login` | 用户登录（顾客） |
| POST | `/auth/admin/login` | 管理员登录 |

- **注册** `POST /auth/register`：请求体 `{ "username", "password", "email", ... }`（按实际用户字段）；成功返回 `data` 中为注册后的用户信息（不含密码）。
- **登录** `POST /auth/login`、`POST /auth/admin/login`：请求体 `{ "username", "password" }`；成功返回 `data: { "token": "...", "user": { ... } }`，前端存 token 用于后续请求。

**测试账号**（执行 `init.sql` 后可用，供前端/联调使用）：

| 角色 | 用户名 | 密码 | 登录接口 |
|------|--------|------|----------|
| 管理员 | `admin` | `admin123` | POST `/auth/admin/login` |
| 普通用户 | `testuser` | `user123` | POST `/auth/login` |

---

#### 3.2 用户

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/users` | 获取所有用户（管理端） |
| GET | `/api/users/{id}` | 获取指定用户 |
| POST | `/api/users` | 创建用户（可与注册二选一或共用） |
| PUT | `/api/users/{id}` | 更新用户 |
| DELETE | `/api/users/{id}` | 删除用户 |

- 管理端查看全部客户、维护账户时使用上述接口；需管理员 token。

---

#### 3.3 电动车（列表、状态、位置、配置）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/scooters` | 获取电动车列表；可选 query：`?available=true` 仅可用，`?location=1` 按点位筛选（按需） |
| GET | `/api/scooters/{id}` | 获取指定电动车（含状态、位置） |
| POST | `/api/scooters` | 添加电动车（管理端） |
| PUT | `/api/scooters/{id}` | 更新电动车信息 |
| PUT | `/api/scooters/{id}/status` | 更新状态：available / unavailable（员工/管理端） |
| DELETE | `/api/scooters/{id}` | 删除电动车 |

- 列表与详情中的 `availability`、`location` 等字段供前端做「可用性/地图展示」使用。

---

#### 3.4 租用选项与价格

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/pricing` | 获取租用选项与价格（1hr / 4hr / 1day / 1week 等） |

- 返回 `data` 为租期选项列表及对应价格，供预订页展示与选时使用。

---

#### 3.5 预订

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/bookings` | 创建预订（选 scooterId + 租用选项/时长） |
| GET | `/api/bookings` | 当前用户的预订列表（我的预订） |
| GET | `/api/bookings/{id}` | 预订详情（含确认信息） |
| PUT | `/api/bookings/{id}/extend` | 延长当前预订时长 |
| POST | `/api/bookings/{id}/cancel` | 取消预订 |

- 创建预订请求体示例：`{ "scooterId": 1, "hireOption": "4hr" }` 或按你们约定的枚举/时长字段。
- 需要用户登录；管理端「代客预订」见 3.9。

---

#### 3.6 支付（模拟）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/bookings/{id}/pay` | 对指定预订进行模拟支付 |

- 请求体可包含模拟卡信息（如 `{ "cardLast4", "amount" }`）；成功则预订状态变为已支付，并可触发「预订确认」逻辑（如 ID 7、8）。

---

#### 3.7 预订确认与展示

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/bookings/{id}/confirmation` | 获取预订确认信息（用于「按需展示」） |

- 与「存储预订确认」（backlog ID 8）对应；发送邮件（ID 7）由后端在支付或预订创建时内部处理，前端仅需调展示接口。

---

#### 3.8 顾客端：统计与反馈

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/users/me/stats` | 当前用户的使用统计（时长、费用等） |
| POST | `/api/feedback` | 提交故障/问题反馈（短文本） |

- `stats` 返回结构可包含：总时长、总费用、预订次数等，便于前端做简单统计展示。

---

#### 3.9 管理端

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/admin/bookings` | 代客预订（为未注册用户创建预订，关联 ID 7） |
| GET | `/api/admin/feedback` | 反馈/问题列表（可 query：priority=high） |
| GET | `/api/admin/issues` | 高优先级问题列表（或与上合并，用 priority 筛选） |
| PUT | `/api/admin/feedback/{id}` | 处理反馈：更新状态、优先级（prioritise/resolve） |
| GET | `/api/admin/reports/income/weekly` | 按租期统计周收入（1hr/4hr/day/week） |
| GET | `/api/admin/reports/income/daily` | 按天汇总一周收入（含各租期与周折扣） |
| GET | `/api/admin/pricing` | 获取当前价格/折扣配置 |
| PUT | `/api/admin/pricing` | 配置价格与折扣（租期价格、常客/学生/长者等） |

- 以上管理端接口均需**管理员 token**。
- 图表（ID 21）由前端根据 weekly/daily 接口数据自行绘图。

---

#### 3.10 可选：银行卡信息（ID 2）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/users/me/cards` | 当前用户已存卡列表（脱敏） |
| POST | `/api/users/me/cards` | 添加卡信息（模拟存储） |
| DELETE | `/api/users/me/cards/{id}` | 删除已存卡 |

- 若 Sprint 1 不做「存卡」，可后续再对接；安全（ID 3）由后端统一处理，前端不传明文完整卡号。

---

**接口汇总表（便于快速对照）**

| 分类 | 方法 | 路径 |
|------|------|------|
| 认证 | POST | `/auth/register`, `/auth/login`, `/auth/admin/login` |
| 用户 | GET/POST/PUT/DELETE | `/api/users`, `/api/users/{id}` |
| 电动车 | GET/POST/PUT/DELETE | `/api/scooters`, `/api/scooters/{id}`, `/api/scooters/{id}/status` |
| 价格 | GET | `/api/pricing` |
| 预订 | POST/GET/PUT | `/api/bookings`, `/api/bookings/{id}`, `/api/bookings/{id}/extend`, `/api/bookings/{id}/cancel`, `/api/bookings/{id}/pay`, `/api/bookings/{id}/confirmation` |
| 顾客 | GET/POST | `/api/users/me/stats`, `/api/feedback` |
| 管理 | GET/POST/PUT | `/api/admin/bookings`, `/api/admin/feedback`, `/api/admin/issues`, `/api/admin/feedback/{id}`, `/api/admin/reports/income/weekly`, `/api/admin/reports/income/daily`, `/api/admin/pricing` |
| 可选 | GET/POST/DELETE | `/api/users/me/cards`, `/api/users/me/cards/{id}` |

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

## 对接说明（以协作为主）

### 前端工程师

1. **API 基础地址**：`http://localhost:8080`
2. **接口文档**：以本文档 **「3. API 接口（前后端协作约定）」** 为准，所有路径、方法、请求/响应约定均按该节实现；后端会保持与此文档一致，如有变更会在此更新并同步。
3. **统一数据格式**：所有接口返回 JSON，格式如下：

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

- 失败时 `code` 非 200（如 400、401、500），`message` 为错误说明。
- 需登录的接口请在请求头携带：`Authorization: Bearer <token>`（登录接口返回的 token）。

4. **测试账号**（需先执行 `init.sql` 初始化数据库）：
   - 管理员：用户名 `admin`，密码 `admin123`，登录用 POST `/auth/admin/login`
   - 普通用户：用户名 `testuser`，密码 `user123`，登录用 POST `/auth/login`
5. **测试示例**（基础）：
   - 获取租用选项：GET http://localhost:8080/api/pricing
   - 获取电动车列表：GET http://localhost:8080/api/scooters
   - 用户登录：POST http://localhost:8080/auth/login，body `{ "username": "testuser", "password": "user123" }`，返回 `data: { "token", "user" }`

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

## 后端 Backlog 待办（未实现项）

以下按 Project Backlog ID 列出**后端尚未实现**的项，前端/非功能项（如 F24 响应式、F25 可访问性）不在此列。

| ID | 描述 | 优先级 | 说明 |
|----|------|--------|------|
| **2** | 存储客户银行卡信息（便于快速预订） | 2 | 需实现 `GET/POST/DELETE /api/users/me/cards`，README 3.10 可选接口 |
| **3** | 若做 ID2：用户账户与卡信息安全 | 2 | 非功能；ID2 实现后需保证卡号不明文存储、传输加密等 |
| **7** | ~~预订确认通过邮件发送~~ ✅ 已完成 | 2 | 支付成功或预订创建后发邮件；需接入邮件服务（如 JavaMail） |
| **10** | ID5：预订/支付后把电动车状态改为不可用 | 2 | `payBooking` 时将该 scooter 状态更新为 IN_USE；取消/结束后改回 AVAILABLE |
| **12** | 取消预订时释放车辆 | 1 | 已有取消接口；需在取消时把对应 scooter 状态改回 AVAILABLE |
| **22** | 常客(8+hr/周)、学生、长者折扣 | 2 | 需用户标签/统计 + 价格计算时应用折扣（或 pricing 表扩展） |
| **23** | 支持多用户同时使用 | 2 | 一般由框架并发处理；若有压测/锁需求再补 |

**其他待完善（无单独 Backlog ID）：**

- [ ] **GET /api/users/me/stats** 返回真实数据：从 booking 表统计 `totalBookings`、`totalDuration`、`totalCost`，当前为写死的 0。

---

## 待完成（Sprint 1）

- [ ] 完成上表后端 Backlog 待办（ID 2、7、10、12、22 及 me/stats）
- [ ] 前端页面按接口文档对接（顾客端 + 管理端）

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
