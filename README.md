# AI 工具箱

基于 Java 17 + Spring Boot 3 + LangChain4j 的多工具集合项目，采用阿里编码规范与 DDD 充血模型。

## 功能

- **工具箱首页**：展示所有可用小工具
- **加班计算器**：
  - 配置标准上下班、午休、标准工时
  - 两种加班计算模式（不包含/包含标准工时）
  - 每日打卡录入与月度汇总
  - 周六全天自动算作加班
  - 请假/半天请假标记（红色/黄色标识）
  - 列表视图与日历视图双模式查看
  - Excel 批量导入打卡记录
  - AI 自然语言解析打卡、AI 月度分析报告
- **业务名词库**：
  - 多系统 Tab 切换管理
  - 名词模糊搜索
  - 新增、编辑、删除名词
  - 字段级变更历史追溯
  - Excel 批量导入（序号、指标、解释）
  - 重复名词自动检测与跳过
  - 预置 29 条股票金融名词

## 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+（数据库名：`tools`）
- （可选）Ollama 本地大模型：`ollama pull qwen2.5:7b`
- （可选）阿里云 DashScope API Key

## 快速启动

### 1. 初始化数据库

在 MySQL 中执行建表脚本：

```bash
mysql -u root -p < data/init-tools.sql
mysql -u root -p < data/init-glossary.sql
```

或保持 `spring.jpa.hibernate.ddl-auto=update`，Hibernate 自动建表后手动导入默认数据。

### 2. 修改数据库配置

编辑 `ai-start/src/main/resources/application.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/tools?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: 你的密码
```

### 3. 启动应用

```bash
cd D:\work\AI
mvn clean install
mvn spring-boot:run -pl ai-start
```

浏览器访问：http://localhost:8082

## AI 配置

默认使用 Ollama（`ai-start/src/main/resources/application.yml`）：

```yaml
ai:
  provider: ollama

langchain4j:
  ollama:
    chat-model:
      base-url: http://localhost:11434
      model-name: qwen2.5:7b
```

切换至 DashScope（通义千问）：

```bash
set DASHSCOPE_API_KEY=your_api_key
mvn spring-boot:run -pl ai-start -Dspring-boot.run.profiles=dashscope
```

或在 `application.yml` 中设置 `ai.provider: dashscope` 并配置 `langchain4j.community.dashscope.chat-model.api-key`。

> 核心加班计算不依赖 AI；AI 不可用时仍可正常使用计算功能。

## 项目结构

```
ai-toolbox/
├── ai-common/         # 公共模块（Result、错误码、工具枚举）
├── ai-domain/         # 领域层（充血模型）
├── ai-application/    # 应用服务编排
├── ai-infrastructure/ # 持久化、LangChain4j 对接
└── ai-start/          # 启动模块、Controller、静态页面

data/
├── init-tools.sql     # 数据库建表脚本
└── init-glossary.sql  # 业务名词库建表+默认数据
```

## API 接口

### 加班计算器

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/tools` | 工具列表 |
| POST | `/api/overtime/settings` | 保存加班配置 |
| GET | `/api/overtime/settings` | 获取加班配置 |
| POST | `/api/overtime/records` | 录入打卡 |
| POST | `/api/overtime/records/leave` | 切换请假标记 |
| GET | `/api/overtime/summary?year=&month=` | 月汇总 |
| POST | `/api/overtime/import` | Excel 导入打卡（multipart） |
| POST | `/api/overtime/ai/parse` | AI 解析自然语言 |
| POST | `/api/overtime/ai/analyze` | AI 分析月报 |

### 业务名词库

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/glossary/systems` | 系统列表 |
| POST | `/api/glossary/systems` | 新增系统 |
| DELETE | `/api/glossary/systems/{id}` | 删除系统 |
| GET | `/api/glossary/terms?systemId=&keyword=` | 名词列表（可选模糊搜索） |
| GET | `/api/glossary/terms/{id}` | 名词详情 |
| POST | `/api/glossary/terms` | 新增名词 |
| PUT | `/api/glossary/terms/{id}` | 编辑名词 |
| DELETE | `/api/glossary/terms/{id}` | 删除名词 |
| POST | `/api/glossary/terms/import` | Excel 导入名词（multipart） |
| GET | `/api/glossary/terms/{id}/history` | 变更历史 |

## Excel 导入格式

### 加班打卡

| 列 | 内容 | 示例 |
|---|------|------|
| A | 日期 | 2026-06-08 |
| B | 上班时间 | 09:00 |
| C | 下班时间 | 18:00 |

### 业务名词

| 列 | 内容 | 示例 |
|---|------|------|
| A | 序号 | 1 |
| B | 指标（名词名称） | 市盈率（PE） |
| C | 解释说明 | 股票价格除以每股收益的比率 |

> 第 1 行为表头，从第 2 行开始读取数据。

## 扩展新工具

1. 在 `ai-common/.../tool/ToolType` 枚举中新增工具
2. 在 `ai-domain` 创建领域模型与仓库接口
3. 在 `ai-application` 添加应用服务与 DTO
4. 在 `ai-infrastructure` 添加 JPA 实体与仓库实现
5. 在 `ai-start` 添加 Controller 与静态页面（`static/tools/`）
