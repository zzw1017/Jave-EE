# 班级考勤管理系统

## 一、项目简介

班级考勤管理系统是一个基于 Spring Boot 开发的 Web 应用，主要用于班级日常考勤管理。系统支持学生、教师和管理员三类角色，不同角色拥有不同的功能权限。

学生可以进行课程考勤打卡、扫码打卡和提交请假申请；教师和管理员可以进行学生管理、课程管理、二维码考勤、考勤记录查询、考勤记录导出以及请假管理。

本系统适用于课程考勤、班级点名、学生信息维护和请假审批等教学管理场景。

---

## 二、技术栈

| 类型       | 技术                  |
| -------- | ------------------- |
| 后端框架     | Spring Boot         |
| 模板引擎     | Thymeleaf           |
| 数据访问     | Spring Data JPA     |
| 数据库      | MySQL               |
| 前端技术     | HTML、CSS、JavaScript |
| 二维码生成    | ZXing               |
| Excel 处理 | Apache POI          |
| 构建工具     | Maven               |
| 开发工具     | IntelliJ IDEA       |

---

## 三、功能模块

### 1. 用户登录与权限管理

* 用户登录
* 用户注册
* 学生、教师、管理员角色区分
* 不同角色显示不同功能菜单
* 未登录用户自动跳转登录页
* 无权限访问时返回系统首页

### 2. 学生信息管理

* 学生信息列表展示
* 新增学生信息
* 编辑学生信息
* 删除学生信息
* 批量删除学生信息
* 按姓名或学号搜索学生
* 学生信息分页显示
* 学生信息排序
* Excel 批量导入学生信息

### 3. 课程管理

* 课程列表展示
* 新增课程
* 编辑课程
* 删除课程
* 设置课程开始时间和结束时间

### 4. 考勤管理

* 学生课程打卡
* 教师端生成二维码考勤
* 学生扫码完成考勤打卡
* 二维码有效期控制
* 考勤记录查询
* 按课程筛选考勤记录
* 按时间筛选考勤记录
* 考勤记录 Excel 导出
* 记录打卡时间、签退时间、打卡 IP 和考勤状态

### 5. 请假管理

* 学生提交请假申请
* 教师和管理员查看请假记录
* 请假申请审核
* 请假状态管理

---

## 四、角色权限说明

| 角色  | 功能权限                      |
| --- | ------------------------- |
| 学生  | 考勤打卡、二维码扫码打卡、提交请假申请       |
| 教师  | 学生管理、课程管理、二维码考勤、考勤记录、请假管理 |
| 管理员 | 拥有教师端全部功能                 |

---

## 五、项目目录结构

```text
attendance
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.example.attendance
│   │   │       ├── config
│   │   │       ├── controller
│   │   │       ├── dto
│   │   │       ├── entity
│   │   │       ├── Repository
│   │   │       ├── result
│   │   │       └── service
│   │   └── resources
│   │       ├── static
│   │       │   └── css
│   │       │       └── style.css
│   │       ├── templates
│   │       │   ├── login.html
│   │       │   ├── register.html
│   │       │   ├── dashboard.html
│   │       │   ├── student-list.html
│   │       │   ├── student-form.html
│   │       │   ├── student-import.html
│   │       │   ├── course-list.html
│   │       │   ├── course-form.html
│   │       │   ├── checkin.html
│   │       │   ├── attendance-list.html
│   │       │   ├── attendance-qrcode.html
│   │       │   └── result.html
│   │       └── application.properties
│   └── test
├── pom.xml
└── README.md
```

---

## 六、环境要求

| 环境    | 版本要求                       |
| ----- | -------------------------- |
| JDK   | 17 或以上                     |
| Maven | 3.6 或以上                    |
| MySQL | 8.0 或以上                    |
| 浏览器   | Chrome、Edge、Firefox 等现代浏览器 |

---

## 七、快速开始

### 1. 创建数据库

```sql
CREATE DATABASE attendance_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

---

### 2. 修改配置文件

配置文件路径：

```text
src/main/resources/application.properties
```

参考配置：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/attendance_system?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=你的数据库密码
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=50MB

file.upload.path=D:/uploads/

server.address=0.0.0.0
server.port=8080

app.base-url=http://你的电脑IPv4地址:8080
```

说明：

```text
app.base-url 用于生成二维码扫码地址。
如果手机扫码访问，不能写 localhost，需要写电脑的局域网 IPv4 地址。
```

例如：

```properties
app.base-url=http://192.168.1.8:8080
```

---

### 3. 启动项目

在 IDEA 中运行主启动类：

```text
AttendanceApplication.java
```

或者使用 Maven 命令启动：

```bash
mvn spring-boot:run
```

---

### 4. 访问系统

电脑浏览器访问：

```text
http://localhost:8080/login
```

手机扫码访问时，需要使用电脑局域网 IP：

```text
http://电脑IPv4地址:8080/login
```

例如：

```text
http://192.168.1.8:8080/login
```

---

## 八、主要页面地址

| 页面     | 地址                         | 说明           |
| ------ | -------------------------- | ------------ |
| 登录页    | `/login`                   | 用户登录         |
| 注册页    | `/register`                | 用户注册         |
| 系统首页   | `/dashboard`               | 系统功能入口       |
| 学生管理   | `/student/page/list`       | 学生信息列表       |
| 新增学生   | `/student/page/add`        | 添加学生信息       |
| 学生导入   | `/student/page/import`     | Excel 批量导入学生 |
| 课程管理   | `/course/page/list`        | 课程信息管理       |
| 学生考勤打卡 | `/attendance/page/checkin` | 学生选择课程打卡     |
| 二维码考勤  | `/attendance/page/qrcode`  | 教师生成考勤二维码    |
| 考勤记录   | `/attendance/page/list`    | 查看考勤记录       |
| 请假申请   | `/leave/page/apply`        | 学生提交请假       |
| 请假管理   | `/leave/page/list`         | 教师或管理员审核请假   |

---

## 九、数据库表设计

### 1. user 用户表

| 字段名                  | 类型      | 说明       | 备注                        |
| -------------------- | ------- | -------- | ------------------------- |
| id                   | BIGINT  | 主键       | 自增                        |
| username             | VARCHAR | 用户名      | 唯一                        |
| password             | VARCHAR | 密码       | 登录凭证                      |
| real_name            | VARCHAR | 真实姓名     | -                         |
| role                 | VARCHAR | 角色       | ADMIN / TEACHER / STUDENT |
| must_change_password | BIT     | 是否需要修改密码 | 可选                        |

---

### 2. student 学生表

| 字段名        | 类型      | 说明   | 备注    |
| ---------- | ------- | ---- | ----- |
| id         | BIGINT  | 主键   | 自增    |
| student_id | VARCHAR | 学号   | 唯一    |
| name       | VARCHAR | 姓名   | -     |
| gender     | VARCHAR | 性别   | 男 / 女 |
| class_name | VARCHAR | 班级   | -     |
| age        | INT     | 年龄   | -     |
| birth_date | DATE    | 出生日期 | -     |
| phone      | VARCHAR | 联系方式 | -     |

---

### 3. course 课程表

| 字段名         | 类型       | 说明   | 备注 |
| ----------- | -------- | ---- | -- |
| id          | BIGINT   | 主键   | 自增 |
| course_name | VARCHAR  | 课程名称 | -  |
| start_time  | DATETIME | 开始时间 | -  |
| end_time    | DATETIME | 结束时间 | -  |

---

### 4. attendance 考勤记录表

| 字段名            | 类型       | 说明      | 备注         |
| -------------- | -------- | ------- | ---------- |
| id             | BIGINT   | 主键      | 自增         |
| user_id        | BIGINT   | 学生用户 ID | 关联用户       |
| username       | VARCHAR  | 用户名     | -          |
| real_name      | VARCHAR  | 学生姓名    | -          |
| course_id      | BIGINT   | 课程 ID   | 关联课程       |
| course_name    | VARCHAR  | 课程名称    | -          |
| check_in_time  | DATETIME | 打卡时间    | -          |
| check_out_time | DATETIME | 签退时间    | 可为空        |
| ip             | VARCHAR  | 打卡 IP   | -          |
| status         | VARCHAR  | 状态      | 已打卡 / 未签退等 |

---

### 5. attendance_qr_code 二维码考勤表

| 字段名          | 类型       | 说明      | 备注               |
| ------------ | -------- | ------- | ---------------- |
| id           | BIGINT   | 主键      | 自增               |
| token        | VARCHAR  | 二维码唯一标识 | 唯一               |
| course_id    | BIGINT   | 课程 ID   | 关联课程             |
| course_name  | VARCHAR  | 课程名称    | -                |
| teacher_id   | BIGINT   | 教师 ID   | 关联用户             |
| teacher_name | VARCHAR  | 教师姓名    | -                |
| create_time  | DATETIME | 创建时间    | -                |
| expire_time  | DATETIME | 过期时间    | 默认 10 分钟         |
| status       | VARCHAR  | 状态      | ACTIVE / EXPIRED |

---

### 6. leave_request 请假申请表

| 字段名          | 类型       | 说明      | 备注              |
| ------------ | -------- | ------- | --------------- |
| id           | BIGINT   | 主键      | 自增              |
| user_id      | BIGINT   | 学生用户 ID | 关联用户            |
| username     | VARCHAR  | 用户名     | -               |
| real_name    | VARCHAR  | 学生姓名    | -               |
| reason       | VARCHAR  | 请假原因    | -               |
| start_time   | DATETIME | 请假开始时间  | -               |
| end_time     | DATETIME | 请假结束时间  | -               |
| status       | VARCHAR  | 审核状态    | 待审核 / 已通过 / 已拒绝 |
| create_time  | DATETIME | 提交时间    | -               |
| audit_time   | DATETIME | 审核时间    | 可为空             |
| audit_remark | VARCHAR  | 审核备注    | 可为空             |

---

### 7. course_schedule 课程安排表

| 字段名          | 类型       | 说明    | 备注   |
| ------------ | -------- | ----- | ---- |
| id           | BIGINT   | 主键    | 自增   |
| course_id    | BIGINT   | 课程 ID | 关联课程 |
| course_name  | VARCHAR  | 课程名称  | -    |
| class_name   | VARCHAR  | 班级    | -    |
| teacher_id   | BIGINT   | 教师 ID | 关联用户 |
| teacher_name | VARCHAR  | 教师姓名  | -    |
| start_time   | DATETIME | 开始时间  | -    |
| end_time     | DATETIME | 结束时间  | -    |

---

## 十、核心业务流程

### 1. 学生普通考勤流程

```text
学生登录系统
→ 进入考勤打卡页面
→ 选择课程
→ 点击立即打卡
→ 系统保存考勤记录
→ 返回操作结果页面
```

---

### 2. 教师二维码考勤流程

```text
教师登录系统
→ 进入二维码考勤页面
→ 选择课程
→ 生成二维码
→ 学生扫码
→ 系统校验二维码是否有效
→ 系统校验学生身份
→ 系统保存考勤记录
→ 返回打卡结果
```

---

### 3. 学生信息导入流程

```text
教师或管理员登录系统
→ 进入学生管理页面
→ 点击 Excel 导入
→ 选择 Excel 文件
→ 系统解析文件内容
→ 校验学生信息格式
→ 批量保存学生数据
```

---

### 4. 考勤记录导出流程

```text
教师或管理员登录系统
→ 进入考勤记录页面
→ 选择课程或时间条件
→ 点击导出 Excel
→ 系统生成 Excel 文件
→ 浏览器下载考勤记录
```

## 十一、项目说明

本项目主要完成了班级考勤管理中的基础业务功能，包括用户权限、学生管理、课程管理、普通考勤、二维码考勤、请假管理和数据导出等功能。

通过该系统，教师可以更加方便地管理班级考勤情况，学生可以快速完成考勤打卡和请假申请，从而提高班级考勤管理效率。

---

## 十二、作者信息

* 项目名称：班级考勤管理系统
* 项目类型：Spring Boot Web 应用
* 主要用途：课程考勤、学生管理、二维码打卡、请假管理
* 作者：zzw1017