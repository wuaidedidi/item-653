# 图书管理系统

商业级图书管理系统，支持图书管理、用户管理、借阅管理、弹幕互动等功能。

## 🚀 技术栈

| 层级 | 技术 |
|------|------|
| **前端** | Vue 3.4 + Element Plus 2.4 + Pinia + Vue Router 4 |
| **后端** | Java 17 + Spring Boot 2.7.18 + Spring Security |
| **ORM** | MyBatis 2.3.1 (XML Mapper) |
| **数据库** | MySQL 8.0 |
| **认证** | JWT (jjwt 0.11.5) |
| **部署** | 单 Dockerfile 容器化运行 |

## 📦 项目结构

```
├── backend/                    # Spring Boot 后端
│   ├── src/main/java/com/library/
│   │   ├── controller/         # REST API 控制器
│   │   ├── service/            # 业务逻辑层
│   │   ├── mapper/             # MyBatis Mapper 接口
│   │   ├── entity/             # 实体类
│   │   ├── dto/                # 数据传输对象
│   │   ├── config/             # 配置类 (JWT, Security, CORS)
│   │   └── util/               # 工具类
│   └── src/main/resources/
│       ├── mapper/             # MyBatis XML 映射文件
│       └── application.yml     # 配置文件
├── frontend/                   # Vue 3 前端
│   ├── src/
│   │   ├── views/              # 页面组件
│   │   ├── api/                # API 请求模块
│   │   ├── stores/             # Pinia 状态管理
│   │   ├── router/             # Vue Router 路由
│   │   └── components/         # 公共组件
│   └── nginx.conf
├── db/init.sql                 # 数据库初始化脚本
└── README.md
```

## 🔧 快速启动

### 使用外层 Dockerfile

```bash
# 在交付目录中构建镜像，Dockerfile 与 repo 目录同级
docker build -t item-653 .

# 启动前端、后端和 MySQL
docker run --rm -p 3000:80 -p 8000:8080 --name item-653 item-653
```

### 服务地址

| 服务 | 地址 | 说明 |
|------|------|------|
| 前端 | http://localhost:3000 | Vue 3 应用 |
| 后端 API | http://localhost:8000/api | Spring Boot REST API |
| 数据库 | 容器内 127.0.0.1:3306 | MySQL (root/root) |

## 🔐 测试账号

| 角色 | 用户名 | 密码 | 权限说明 |
|------|--------|------|----------|
| 管理员 | admin | 123456 | 图书CRUD、用户管理、借阅管理、弹幕管理 |
| 普通用户 | user | 123456 | 借阅图书、发送弹幕、个人信息修改 |

## ✨ 功能特性

### 📚 图书管理
- 图书信息 CRUD（ISBN、书名、作者、出版社等）
- 分类管理（10个预设分类）
- 库存管理（库存总量、可借数量）
- 多条件搜索筛选（关键词、分类、状态）
- 封面图片支持

### 👥 用户管理
- 用户登录/注册
- JWT Token 认证
- 个人信息修改（昵称、邮箱、手机号）
- 密码修改
- 权限控制（管理员/普通用户）
- 用户状态管理（启用/禁用）

### 📖 借阅管理
- 借书/还书操作
- 续借功能（延长30天）
- 借阅记录查询
- 借阅状态跟踪（借阅中/已归还/已逾期）
- 逾期检测

### 🎯 弹幕功能
- 弹幕墙页面 (`/barrage`) - 全局弹幕展示
- 图书详情页弹幕 - 针对特定图书的评论
- 弹幕颜色自定义（颜色选择器）
- CSS3 动画滚动效果
- 最新弹幕列表

### 📊 数据仪表盘
- 图书总数统计
- 用户总数统计
- 借阅总数统计
- 今日新增数据
- 分类统计图表

## 🗄️ 数据库设计

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| users | 用户表 | id, username, password, nickname, role, status |
| categories | 分类表 | id, name, description, sort_order |
| books | 图书表 | id, isbn, title, author, category_id, stock, available |
| borrow_records | 借阅记录 | id, user_id, book_id, borrow_date, due_date, status |
| barrages | 弹幕表 | id, user_id, book_id, content, color |

## 🔧 环境变量配置

项目支持通过环境变量自定义配置，详见 `.env.example`。

| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| DB_HOST | localhost | 数据库主机 |
| DB_PORT | 3306 | 数据库端口 |
| DB_NAME | library | 数据库名称 |
| DB_USERNAME | root | 数据库用户名 |
| DB_PASSWORD | root | 数据库密码 |
| JWT_SECRET | (内置默认值) | JWT 密钥（生产环境务必更换） |

## 🛠 本地开发

### 后端开发

```bash
cd backend
# 使用 Maven Wrapper 编译运行（无需全局安装 Maven）
./mvnw spring-boot:run       # Linux/Mac
mvnw.cmd spring-boot:run     # Windows

# 或者打包后运行
./mvnw package -DskipTests
java -jar target/*.jar
```

### 前端开发

```bash
cd frontend
# 安装依赖
npm install
# 启动开发服务器（默认端口 3000，代理后端 8080）
npm run dev
# 生产构建
npm run build
```

## 📝 API 接口

### 认证接口
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/auth/login | 用户登录 |
| POST | /api/auth/register | 用户注册 |
| POST | /api/auth/logout | 用户登出 |

### 用户接口
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/users/me | 获取当前用户信息 |
| PUT | /api/users/me | 更新当前用户信息 |
| PUT | /api/users/me/password | 修改密码 |
| GET | /api/users | 获取用户列表（管理员） |

### 图书接口
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/books | 获取图书列表（分页） |
| GET | /api/books/all | 获取所有图书 |
| GET | /api/books/{id} | 获取图书详情 |
| POST | /api/books | 添加图书（管理员） |
| PUT | /api/books/{id} | 更新图书（管理员） |
| DELETE | /api/books/{id} | 删除图书（管理员） |

### 分类接口
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/categories | 获取分类列表 |
| POST | /api/categories | 添加分类（管理员） |
| PUT | /api/categories/{id} | 更新分类（管理员） |
| DELETE | /api/categories/{id} | 删除分类（管理员） |

### 借阅接口
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/borrows | 获取借阅记录 |
| GET | /api/borrows/my | 获取我的借阅记录 |
| POST | /api/borrows/borrow | 借书 |
| POST | /api/borrows/return/{id} | 还书 |
| POST | /api/borrows/renew/{id} | 续借 |

### 弹幕接口
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/barrages/public/book/{bookId} | 获取图书弹幕 |
| GET | /api/barrages/public/latest | 获取最新弹幕 |
| POST | /api/barrages | 发送弹幕 |

### 仪表盘接口
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/dashboard/stats | 获取统计数据 |

## 🐛 常见问题

### 容器名称冲突
```bash
# 先清理旧容器
docker rm -f item-653
docker run --rm -p 3000:80 -p 8000:8080 --name item-653 item-653
```

### 数据库连接失败
等待容器内 MySQL 完全启动（约 30 秒），启动脚本会在数据库可用后再拉起后端。

### 前端页面空白
检查浏览器控制台错误，确保后端 API 正常运行。

## 📄 License

MIT License
