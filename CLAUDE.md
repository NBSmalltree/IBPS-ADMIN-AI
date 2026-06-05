# CLAUDE.md - Super Net (IBPS) Management Console AI Guide

This document defines the development standards, commands, architecture patterns, and critical business rules for the independent IBPS management project. AI agents must strictly adhere to these instructions.

---

## 1. Core Tech Stack & Constraints
* **Backend:** Java 8 (STRICT: Do NOT use Java 9+ features like `var`, text blocks, Switch Expressions, or modern Stream enhancements)
* **Framework:** Spring Boot 2.0.8.RELEASE / MyBatis
* **Database:** OceanBase (Oracle Mode) - use Oracle-compatible DDL/SQL (e.g., `VARCHAR2`, `TIMESTAMP(3)`)
* **Messaging:** RocketMQ
* **Frontend:** Vue 3 (开发环境) / Vue 2 兼容风格 / Element Plus / Axios
* **Chart Library:** ECharts (STRICT: Do NOT use Highcharts — any interactive chart requirement must use ECharts instead)

---

## 2. Common Build & Run Commands

### Backend (`ibps-admin-service`)
* **Build Project:** `mvn clean package -DskipTests`
* **Run Local Application:** `mvn spring-boot:run`
* **Run Single Test:** `mvn test -Dtest=ClassName#methodName`

### Frontend (`ibps-admin-view`)
* **Install Dependencies:** `npm install`
* **Run Dev Server:** `npm run dev`
* **Build Production:** `npm run build`

---

## 3. Architecture & Directory Guidelines

AI must place new files strictly within the following simplified DDD and Modular Front-End structure:

```text
ibps-admin-service/ (Backend)
└── src/main/java/com/actuary/ibps/
    ├── api/                  # Controller, DTO validation
    ├── application/          # Application Services, MQ Consumer Entry
    ├── domain/               # Domain Core (Entities, Domain Services)
    │   ├── connection/       # Login/Logout/CCPC Connection domain
    │   ├── message/          # Free text message, File parsing domain
    │   ├── certificate/      # Hot-reload certificate domain
    │   └── trace/            # Lifecycle status machine, calculation domain
    └── infrastructure/       # MyBatis Mappers, DB Entities, MQ Producer, External Call Adapters

ibps-admin-view/ (Frontend)
└── src/
    ├── views/ibps/
    │   ├── sysManage/        # Sign-in/out, Free XML Msg, Data Import, Cert Maintenence
    │   ├── config/           # Contact Grid
    │   └── trace/            # Dashboard, Trace Graph View
    └── scripts/api/ibps.js   # $http 安装 + WebSocket 工具函数
```

---

## 4. Coding Styles & Patterns

Backend (Java 8 / DDD / Spring Boot 2.0.8)
No Modern Java: Stick to Java 8 standard features. Use Lambda and basic Streams safely.

DDD Isolation: * domain must not depend on infrastructure or api.

Use infrastructure implementation to fulfill interfaces defined in domain.

Database Compatibility: Ensure all XML mappers or SQL providers comply with OceanBase-Oracle mode. Use Oracle syntax (e.g., sequence handling, Oracle specific functions if necessary).

DTO Validation: Always use javax.validation.constraints (e.g., @NotNull, @Size) in Controller layer entrypoints.

Frontend (Vue 2 兼容风格 / Element Plus)
Options API: 使用 `<script>` + `export default { data(), computed, methods, mounted() }` 选项式语法，禁止使用 `<script setup>` 或 Composition API（ref, computed, onMounted, useRoute 等）。

组件标签: 使用 `h-*` 前缀（如 `<h-table>`, `<h-card>`, `<h-button>`），禁止使用 `el-*` 标签。Element Plus 组件已通过 main.js 全局注册为 h-* 名称。

接口调用: 使用 `this.$http.get/post/put/delete()` 调用接口，禁止导入独立的 API 函数。API 模块位于 `src/scripts/api/ibps.js`，仅导出 `installHttp` 和 `connectImportProgress`。

消息提示: 使用 `this.$hMessage.success/error/warning()` 替代 ElMessage；使用 `this.$hConfirm()` 替代 ElMessageBox.confirm。

Vue 2 语法: 分页组件使用 `:current-page.sync` / `:page-size.sync`；对话框使用 `:visible.sync`；Radio 使用 `:label` 绑定值。

Component Styling: Use Element Plus responsive layout features.

Encapsulation: Keep everything inside src/views/ibps/ self-contained so it can be easily copied out later.

---

## 5. Critical Business & Logic Rules
When generating code for specific features, AI must enforce these invariants:

Transaction Tracing Mutual Exclusivity (CRITICAL):

In TRX_BUSI_TRACE_LOG processing, a single transaction (BUSI_SERIAL) cannot have both RECV_ANSW_601 (Success) and RECV_ANSW_900 (Fail) steps.

If both steps are detected for the same transaction during RocketMQ consumption, log an Infrastructure-level Alert immediately.

Date Picker Limitations:

Any query filtering by date (e.g., Free Message Management, Diagnostics) must enforce a maximum range of 7 days on both front-end validations and back-end constraints.

Data Import Architecture:

File parsing (IBPSODT* files) must be executed asynchronously via thread pools (do not block the Tomcat container threads).

Utilize MyBatis Batch inserts with a chunk size of 1000 records per commit.

Provide parsing progress through real-time push (WebSocket).

Certificate Hot Reload:

Any changes to certificates (CC00/CC02) must trigger an internal event or lightweight MQ broadcast to all running core nodes to invoke a hot reload without restart.

Trace Millisecond Threshold:

When compiling the trace timeline for front-end visual mapping, calculate the exact millisecond delta between STEP_n and STEP_n-1. If delta >= 800ms, flag the link node for front-end rendering alert (highlighting performance bottleneck).