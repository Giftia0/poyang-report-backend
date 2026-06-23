# Poyang Report Backend

鄱阳湖线索/举报系统后端服务，面向公众上报、线索受理、办理跟踪和数据统计等业务场景。服务以 Spring Boot 为核心，提供验证码登录、JWT 鉴权、实名认证、线索工单、办理状态、图片材料和统计看板等接口能力，可作为移动端公众入口和处理端工作台的数据服务。

## 项目定位

本项目承担系统的数据和业务流转层，主要处理三类对象：

- 用户：验证码登录、身份信息读取、实名认证记录、认证材料上传。
- 工单：举报、咨询、反馈等内容的创建、查询、筛选和详情聚合。
- 办理：待办加入/移除、状态流转、处理材料上传、回复和统计聚合。

前端配套项目为 `poyang-report-view`，默认通过 HTTP 接口访问本服务。

## 技术栈

- Java 17
- Spring Boot 3.2.3
- Spring Web / Validation / Redis
- MyBatis / MyBatis Plus
- MySQL
- Druid 数据源
- JWT
- Redis 缓存
- 腾讯云 COS SDK
- UniSMS SDK
- OpenCV 本地依赖

## 功能模块

### 认证与用户

- 手机号验证码发送与校验。
- 登录成功后签发 JWT，前端通过 `Authorization` 请求头携带令牌。
- 拦截器统一解析用户身份，将当前用户信息提供给业务接口。
- 查询当前用户资料。
- 上传身份证正反面并写入认证记录。
- 查询实名认证状态和认证详情。
- 支持清空当前认证记录，便于重新提交。

### 线索上报

- 支持举报、咨询、反馈三类内容。
- 上报内容包含标题、描述、类型、分类、经纬度、地址、辅助地点、行政区划等信息。
- 支持线索图片分批上传，图片与线索 ID 关联保存。
- 上报后自动生成初始办理状态，便于前端展示时间线。

### 线索查询

- 查询“我的上报”，支持按分类、状态、时间范围和滚动分页游标筛选。
- 查询全部线索列表，供数据页或管理视图使用。
- 查询待办列表，供处理人员工作台使用。
- 获取单条线索详情，聚合上报内容、图片、最新状态和办理信息。

### 办理流转

- 将线索加入待办。
- 移除待办并记录处理说明。
- 新增办理状态，状态包含处理中、已解决、无法处理、已结束、待评价等业务节点。
- 上传状态材料图片。
- 查询状态时间线和最新状态。
- 对咨询、反馈类内容进行回复。

### 数据统计

- 首页看板数据：总量、历史累计、已处理数量、无法处理数量、按时办结率、满意率等。
- 饼图数据：按线索类型统计占比。
- 柱状图/趋势数据：按日、月、年等维度聚合线索数量。
- 统计接口支持时间范围参数，前端可用于区间筛选。

## 接口概览

| 模块 | 方法 | 路径 | 说明 |
| --- | --- | --- | --- |
| visitor | GET | `/visitor/getCheckCode/{phone}` | 获取手机验证码 |
| visitor | POST | `/visitor/loginByCheckCode` | 验证码登录 |
| user | GET | `/user/getUserInfo` | 查询当前用户 |
| user | GET | `/user/checkAuthRecord` | 查询实名认证状态 |
| user | POST | `/user/uploadIdcard` | 上传身份证材料 |
| user | GET | `/user/getAuthInfo` | 获取认证详情 |
| report | POST | `/report/addReport` | 新增线索/咨询/反馈 |
| report | POST | `/report/addReportImg` | 上传上报图片 |
| report | GET | `/report/getMyReport` | 查询我的上报 |
| report | GET | `/report/getToDoReport` | 查询待办列表 |
| report | GET | `/report/getReportList` | 查询线索列表 |
| report | GET | `/report/getReportDetail` | 查询线索详情 |
| report | GET | `/report/addToDo` | 加入待办 |
| report | GET | `/report/removeToDo` | 移除待办 |
| report | POST | `/report/addStatus` | 新增办理状态 |
| report | POST | `/report/addStatusImg` | 上传办理材料 |
| report | POST | `/report/replyReport` | 回复咨询/反馈 |
| report | GET | `/report/getReportStatusList` | 查询状态时间线 |
| report | GET | `/report/getLatestStatus` | 查询最新状态 |
| report | GET | `/report/getReportDataView` | 获取看板指标 |
| report | GET | `/report/getPieChartData` | 获取类型占比 |
| report | GET | `/report/getBarChartData` | 获取趋势统计 |

## 数据对象

核心实体位于 `src/main/java/com/example/poyangreportbackend/domain`：

- `User`：用户身份、手机号、头像、角色等信息。
- `AuthRecord`：实名认证记录和识别结果。
- `ReportForm`：线索/咨询/反馈主体信息。
- `ReportImg`：上报图片材料。
- `ReportStatus`：办理节点。
- `ReportStatusDTO`：状态节点与图片、操作者等展示信息。
- `StatusImg`：办理状态图片。
- `ReportDTO`：线索列表和详情展示对象。

Mapper XML 位于 `src/main/resources/mapper`，用于列表筛选、状态聚合和统计查询。

## 目录结构

```text
src/main/java/com/example/poyangreportbackend
├── Interceptor/       # JWT 用户拦截器
├── common/            # Result、状态码、全局异常、正则规则
├── config/            # Redis、Jackson、RestTemplate、拦截器配置
├── controller/        # HTTP 接口
├── domain/            # 实体与 DTO
├── mapper/            # MyBatis Mapper
├── service/           # 业务服务
└── util/              # JWT、短信、OCR、文件、对象存储等工具

src/main/resources
├── application.yml    # 服务、数据库、Redis 配置
└── mapper/            # MyBatis XML
```

## 本地运行

### 环境要求

- JDK 17
- Maven 3.8+
- MySQL 8.x
- Redis 6.x+

### 配置数据库和 Redis

修改 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://<DB_HOST>:<DB_PORT>/<DB_NAME>?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8
    username: <DB_USERNAME>
    password: <DB_PASSWORD>
  data:
    redis:
      host: <REDIS_HOST>
      port: 6379
      password: <REDIS_PASSWORD>
```

如果需要启用短信、OCR 或对象存储能力，请在对应工具类中补充服务配置。

### 启动服务

```bash
./mvnw spring-boot:run
```

Windows：

```powershell
.\mvnw.cmd spring-boot:run
```

服务默认监听 `8080` 端口。

## 业务流程

1. 用户在移动端输入手机号，获取验证码。
2. 登录成功后，前端保存 JWT 并在后续请求中携带。
3. 用户可进行实名认证，上传身份证材料并查看认证状态。
4. 用户提交举报、咨询或反馈，填写文字、位置和图片材料。
5. 系统保存线索，并生成初始办理状态。
6. 处理人员在待办列表中接收线索，补充办理状态和处理图片。
7. 用户在详情页查看进度、回复和状态时间线。
8. 数据页根据统计接口展示类型占比、趋势和办理指标。

## 配套前端

推荐与 `poyang-report-view` 一起使用。前端包含移动端上报入口、线索数据页、我的上报、工作台、详情页、状态时间线和统计图表等页面。

## 应用场景

项目适用于湖区生态巡查、城市治理线索收集、热线工单流转、公众反馈办理、基层事件上报等系统建设场景。
