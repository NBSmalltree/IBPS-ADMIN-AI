# 超级网(IBPS)管理控制台

超级网(IBPS)管理控制台，用于银行间支付系统的运维管理，涵盖连接管理、自由格式报文、数据导入、证书热更新及交易追踪等功能。

## 技术栈

**后端**

- Java 8 / Spring Boot 2.0.8 / MyBatis
- OceanBase (Oracle 模式)
- RocketMQ
- Spring WebSocket / Spring Actuator

**前端**

- Vue 3 / Vue Router / Pinia
- Element Plus
- Vite 5 / Axios

## 项目结构

```
ibps-admin-ai/
├── ibps-admin-service/        # 后端服务 (Java / Spring Boot)
│   └── src/main/java/com/actuary/ibps/
│       ├── api/               # Controller、DTO 校验
│       ├── application/       # 应用服务、MQ 消费入口
│       ├── domain/            # 领域核心（实体、领域服务）
│       └── infrastructure/    # MyBatis Mapper、DB 实体、MQ 生产者
│
└── ibps-admin-view/           # 前端应用 (Vue 3)
    └── src/
        ├── views/ibps/
        │   ├── sysManage/     # 签到签退、自由报文、数据导入、证书维护
        │   ├── config/        # 通讯录管理
        │   └── trace/         # 仪表盘、交易追踪图
        └── api/ibps.js        # Axios 接口层
```

## 快速开始

### 后端

```bash
cd ibps-admin-service

# 构建
mvn clean package -DskipTests

# 本地启动
mvn spring-boot:run
```

后端默认运行在 `http://localhost:8080/ibps-admin`。

### 前端

```bash
cd ibps-admin-view

# 安装依赖
npm install

# 开发模式
npm run dev

# 生产构建
npm run build
```

前端开发服务器默认运行在 `http://localhost:3000`，已配置代理转发至后端。

## 环境变量

| 变量名 | 说明 |
|---|---|
| `IBPS_DB_PASSWORD` | 数据库密码 |
| `ROCKETMQ_NAMESRV` | RocketMQ NameServer 地址 |
| `CCPC_URL` | CCPC 系统连接地址 |

## 核心功能

- **连接管理** — 登录/登出 CCPC 系统
- **自由格式报文** — XML 报文发送与校验
- **数据导入** — 异步解析 IBPSODT* 文件，线程池处理，WebSocket 实时推送进度，批量插入（每次 1000 条）
- **证书热更新** — CC00/CC02 证书变更通过 MQ 广播至所有节点，无需重启
- **交易追踪** — 交易生命周期状态机，毫秒级耗时分析，超过 800ms 的链路自动高亮告警
