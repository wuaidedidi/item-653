-- 图书管理系统数据库初始化脚本
-- 设置字符集
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 创建数据库
CREATE DATABASE IF NOT EXISTS library CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE library;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    nickname VARCHAR(100) COMMENT '昵称',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(500) COMMENT '头像URL',
    role TINYINT DEFAULT 0 COMMENT '角色: 0-普通用户, 1-管理员',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_role (role),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 分类表
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE COMMENT '分类名称',
    description VARCHAR(500) COMMENT '分类描述',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_name (name),
    INDEX idx_sort (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书分类表';

-- 图书表
CREATE TABLE IF NOT EXISTS books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(20) NOT NULL UNIQUE COMMENT 'ISBN',
    title VARCHAR(200) NOT NULL COMMENT '书名',
    author VARCHAR(100) NOT NULL COMMENT '作者',
    publisher VARCHAR(100) COMMENT '出版社',
    category_id BIGINT COMMENT '分类ID',
    price DECIMAL(10, 2) COMMENT '价格',
    stock INT DEFAULT 1 COMMENT '库存总量',
    available INT DEFAULT 1 COMMENT '可借数量',
    cover_url VARCHAR(500) COMMENT '封面URL',
    description TEXT COMMENT '简介',
    publish_date DATE COMMENT '出版日期',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-下架, 1-上架',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_isbn (isbn),
    INDEX idx_title (title),
    INDEX idx_author (author),
    INDEX idx_category (category_id),
    INDEX idx_status (status),
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书表';

-- 借阅记录表
CREATE TABLE IF NOT EXISTS borrow_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    book_id BIGINT NOT NULL COMMENT '图书ID',
    borrow_date DATETIME NOT NULL COMMENT '借阅日期',
    due_date DATETIME NOT NULL COMMENT '应还日期',
    return_date DATETIME COMMENT '实际归还日期',
    status TINYINT DEFAULT 0 COMMENT '状态: 0-借阅中, 1-已归还, 2-已逾期',
    remark VARCHAR(500) COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user (user_id),
    INDEX idx_book (book_id),
    INDEX idx_status (status),
    INDEX idx_borrow_date (borrow_date),
    INDEX idx_due_date (due_date),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='借阅记录表';

-- 弹幕表
CREATE TABLE IF NOT EXISTS barrages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    book_id BIGINT COMMENT '图书ID（可为空，表示全局弹幕）',
    content VARCHAR(200) NOT NULL COMMENT '弹幕内容',
    color VARCHAR(20) DEFAULT '#FFFFFF' COMMENT '弹幕颜色',
    position TINYINT DEFAULT 0 COMMENT '位置: 0-滚动, 1-顶部, 2-底部',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-隐藏, 1-显示',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user (user_id),
    INDEX idx_book (book_id),
    INDEX idx_status (status),
    INDEX idx_created (created_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='弹幕表';

-- ================================
-- 初始化数据
-- ================================

-- 初始化管理员账号 (密码: 123456，会由程序在启动时重新加密)
INSERT INTO users (username, password, nickname, email, role, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuLyPqpE9KLbtWYpNQtdkJTxw6v9Yqx6e', '系统管理员', 'admin@library.com', 1, 1);

-- 初始化普通用户账号 (密码: 123456)
INSERT INTO users (username, password, nickname, email, role, status) VALUES
('user', '$2a$10$N.zmdr9k7uOCQb376NoUnuLyPqpE9KLbtWYpNQtdkJTxw6v9Yqx6e', '普通用户', 'user@library.com', 0, 1);

-- 初始化分类数据
INSERT INTO categories (name, description, sort_order) VALUES
('文学小说', '包括国内外经典文学、现代小说、网络文学等', 1),
('历史传记', '历史事件、人物传记、回忆录等', 2),
('科技计算机', '计算机科学、编程语言、人工智能等', 3),
('经济管理', '经济学、管理学、投资理财等', 4),
('哲学心理', '哲学思想、心理学、自我提升等', 5),
('艺术设计', '绘画、音乐、建筑、设计等', 6),
('教育考试', '教材教辅、考试资料、学习方法等', 7),
('生活百科', '健康养生、美食烹饪、旅游休闲等', 8),
('儿童绘本', '儿童文学、绘本故事、科普读物等', 9),
('外语学习', '英语、日语、其他外语学习资料', 10);

-- 初始化图书数据
INSERT INTO books (isbn, title, author, publisher, category_id, price, stock, available, cover_url, description, publish_date, status) VALUES
('9787020002207', '红楼梦', '曹雪芹', '人民文学出版社', 1, 59.70, 5, 5, '/covers/1.png', '中国古代四大名著之一，描写了贾、王、史、薛四大家族的兴衰历程。', '2019-05-01', 1),
('9787020008735', '三国演义', '罗贯中', '人民文学出版社', 1, 39.50, 5, 5, '/covers/2.png', '中国古代四大名著之一，描写了东汉末年群雄割据的历史故事。', '2018-08-01', 1),
('9787020008728', '水浒传', '施耐庵', '人民文学出版社', 1, 45.00, 3, 3, '/covers/3.png', '中国古代四大名著之一，描写了北宋末年梁山好汉的故事。', '2018-06-01', 1),
('9787020008711', '西游记', '吴承恩', '人民文学出版社', 1, 42.00, 4, 4, '/covers/4.png', '中国古代四大名著之一，描写了唐僧师徒四人西天取经的故事。', '2018-07-01', 1),
('9787111213826', 'Java编程思想', 'Bruce Eckel', '机械工业出版社', 3, 108.00, 3, 3, '/covers/5.png', 'Java领域经典著作，深入讲解Java语言的核心概念和编程技巧。', '2017-06-01', 1),
('9787115417305', '深入理解Java虚拟机', '周志明', '机械工业出版社', 3, 79.00, 4, 4, '/covers/6.png', '全面讲解JVM原理与性能优化的技术书籍。', '2019-12-01', 1),
('9787121384561', 'Spring实战', 'Craig Walls', '电子工业出版社', 3, 89.00, 3, 3, '/covers/7.png', 'Spring框架权威指南，涵盖Spring Boot和Spring Cloud。', '2020-03-01', 1),
('9787115546081', 'Vue.js设计与实现', '霍春阳', '人民邮电出版社', 3, 79.00, 3, 3, '/covers/8.png', '深入解析Vue.js 3.0框架设计与实现原理。', '2022-01-01', 1),
('9787508647357', '人类简史', '尤瓦尔·赫拉利', '中信出版社', 2, 68.00, 4, 4, '/covers/9.png', '从认知革命到现代，全面解读人类历史发展进程。', '2017-02-01', 1),
('9787508672069', '未来简史', '尤瓦尔·赫拉利', '中信出版社', 2, 68.00, 3, 3, '/covers/10.png', '探讨人类的未来命运与发展方向。', '2017-01-01', 1),
('9787550280526', '活着', '余华', '北京联合出版公司', 1, 28.00, 5, 5, '/covers/11.png', '讲述一个人一生的故事，揭示了生命的意义。', '2017-08-01', 1),
('9787544270878', '百年孤独', '加西亚·马尔克斯', '南海出版公司', 1, 55.00, 3, 3, '/covers/12.png', '魔幻现实主义文学的代表作，讲述布恩迪亚家族百年的兴衰史。', '2017-08-01', 1),
('9787020104529', '围城', '钱钟书', '人民文学出版社', 1, 36.00, 4, 4, '/covers/13.png', '描写抗战时期知识分子的生活和心理状态。', '2017-11-01', 1),
('9787544291163', '追风筝的人', '卡勒德·胡赛尼', '上海人民出版社', 1, 36.00, 4, 4, '/covers/14.png', '关于友谊、背叛和救赎的动人故事。', '2018-06-01', 1),
('9787115293800', '算法导论', 'Thomas H.Cormen', '机械工业出版社', 3, 128.00, 2, 2, '/covers/15.png', '计算机算法领域的权威著作，全面讲解算法设计与分析。', '2013-01-01', 1);

-- 初始化弹幕数据
INSERT INTO barrages (user_id, book_id, content, color, position, status) VALUES
(1, 1, '经典名著，值得细细品味！', '#FF6B6B', 0, 1),
(2, 1, '红楼梦真的太好看了', '#4ECDC4', 0, 1),
(1, 5, 'Java入门必读！', '#45B7D1', 0, 1),
(2, 5, '讲解很详细，推荐！', '#96CEB4', 0, 1),
(1, NULL, '欢迎来到图书管理系统', '#FFEAA7', 0, 1),
(2, NULL, '一起来读书吧！', '#DDA0DD', 0, 1),
(1, 9, '人类简史让我重新认识了世界', '#87CEEB', 0, 1),
(2, 11, '余华的书总是那么有力量', '#FFB6C1', 0, 1);
