USE `tools`;

-- 工作分类表
CREATE TABLE IF NOT EXISTS `work_category` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`       VARCHAR(100) NOT NULL                 COMMENT '分类名称',
    `color`      VARCHAR(20)  NOT NULL DEFAULT '#3b82f6' COMMENT '显示颜色',
    `sort_order` INT          NOT NULL DEFAULT 0       COMMENT '排序号',
    `is_default` BIT(1)       NOT NULL DEFAULT 0       COMMENT '是否系统预置',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_category_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工作分类';

-- 工作记录表
CREATE TABLE IF NOT EXISTS `work_record` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `category_id` BIGINT       NOT NULL                 COMMENT '分类ID',
    `title`       VARCHAR(300) NOT NULL                 COMMENT '工作标题',
    `description` TEXT                                   COMMENT '工作内容',
    `record_date` DATE         NOT NULL                 COMMENT '开始日期',
    `end_date`    DATE         NOT NULL                 COMMENT '结束日期',
    `date_type`   VARCHAR(10)  NOT NULL DEFAULT 'DAY'   COMMENT '粒度：DAY/WEEK/MONTH/CUSTOM',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_record_date` (`record_date`),
    KEY `idx_end_date` (`end_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工作记录';

-- 默认分类（需求、开发任务、日常工作）
INSERT IGNORE INTO `work_category` (`id`, `name`, `color`, `sort_order`, `is_default`) VALUES
(1, '需求', '#3b82f6', 1, 1),
(2, '开发任务', '#22c55e', 2, 1),
(3, '日常工作', '#eab308', 3, 1);
