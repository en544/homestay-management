# 民宿预订与房态管理系统

Java EE 期末大作业，对应选题 **AU. 16B 民宿预订与房态管理：房源 / 房态 / 订单 / 评价**。

项目使用 Spring Boot 3、Spring MVC、Spring Data JPA、Thymeleaf 和 H2 数据库，包含可直接演示的经营后台页面与初始化数据，不依赖前端框架或云服务。

## 功能模块

### 1. 经营概览

- 在营门店数、可售房量、今日入住率、今日到店、累计营收
- 未来七天入住率图表
- 今日接待清单、最近订单、房态速览

### 2. 门店房源

- 门店列表与名称/城市搜索
- 新增、编辑门店档案
- 查看门店详情、图片、特色标签与在售房型

### 3. 房态管理

- 房型新增与编辑
- 设置价格、容量、库存与房态
- 支持可预订、保洁中、维护中三种状态
- 非可预订房型自动退出新预订的可选列表

### 4. 订单中心

- 新建预订
- 按日期计算可售房量与预计房费
- 待确认、已确认、已入住、已完成、已取消五种状态
- 确认、入住、完成、取消的状态流转
- 取消订单后自动释放对应日期库存
- 日期重叠时累加占房数量

### 5. 住客评价

- 已完成订单录入评分与评价
- 商家回复
- 公开/隐藏评价
- 平均评分与评价列表

### 6. 登录与接口

- Session 管理员登录
- 健康检查接口 `/api/health`
- 经营看板 JSON 接口 `/api/dashboard`

## 运行方式

### 环境要求

- JDK 17 或更高版本
- 已安装 Maven 3.9+，或使用项目内的 Maven Wrapper

### 从源码启动

```bash
mvn spring-boot:run
```

Windows 也可以使用：

```powershell
.\mvnw.cmd spring-boot:run
```

### 运行已打包 JAR

```bash
java -jar target/homestay-management-1.0.0.jar
```

浏览器访问：

```text
http://localhost:8080
```

演示账号：

```text
账号：admin
密码：123456
```

## 数据库

默认使用文件型 H2 数据库，首次启动自动建表并写入演示数据：

```text
./data/homestay.mv.db
```

H2 控制台：

```text
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:file:./data/homestay
用户名: sa
密码: 留空
```

### 切换到 MySQL

1. 创建数据库：

```sql
CREATE DATABASE homestay_management
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

2. 修改 `src/main/resources/application-mysql.yml` 中的用户名和密码。

3. 使用 MySQL 配置启动：

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

## 核心表结构

| 表名 | 说明 |
| --- | --- |
| `homestays` | 门店档案、地址、介绍、封面和营业状态 |
| `rooms` | 房型、价格、容量、库存、房态和所属门店 |
| `booking_orders` | 住客、日期、间数、金额、订单状态 |
| `reviews` | 评分、评价正文、商家回复和可见状态 |

订单占房采用日期重叠规则：

```text
已有订单.checkIn < 新订单.checkOut
且
已有订单.checkOut > 新订单.checkIn
```

满足条件的待确认、已确认、已入住订单会累加占用房量。

## 关键路由

| 路由 | 功能 |
| --- | --- |
| `/` | 经营概览 |
| `/homestays` | 门店列表 |
| `/rooms` | 房态管理 |
| `/orders` | 订单列表 |
| `/bookings/new` | 新建预订 |
| `/reviews` | 住客评价 |
| `/api/health` | 健康检查 |
| `/api/dashboard` | 看板数据 |

## 测试

项目包含无端口 MockMvc 集成测试，覆盖登录拦截、后台页面渲染、初始化数据、下单金额计算和订单状态流转。

```bash
mvn test
```

## 部署

IDEA 本地运行、JAR 独立部署、Linux 服务器部署和 MySQL 切换说明见：

```text
docs/部署说明.md
```

## 项目结构

```text
src/main/java/com/example/homestay
├── config      登录拦截、Web 配置、演示数据
├── domain      门店、房型、订单、评价实体
├── repository  Spring Data JPA 数据访问
├── service     预订、房态与经营统计业务
└── web         页面控制器、表单与异常处理

src/main/resources
├── static      本地 CSS、JavaScript 和图片
├── templates   Thymeleaf 页面
└── application*.yml
```

## 说明

- 页面图片、Lucide 图标库均已本地化，演示时无需访问外网。
- 密码为课程演示配置，真实部署时应改用 Spring Security、密码哈希与数据库用户表。
- 本项目为教学演示，未接入支付、短信、发票等第三方服务。
