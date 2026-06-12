-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS `tools` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `tools`;

-- 考勤打卡记录表
CREATE TABLE IF NOT EXISTS `attendance_record` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '主键',
    `work_date`    DATE         NOT NULL                 COMMENT '日期',
    `clock_in`     TIME         NOT NULL                 COMMENT '上班时间',
    `clock_out`    TIME         NOT NULL                 COMMENT '下班时间',
    `is_leave`     TINYINT(1)   NOT NULL DEFAULT 0       COMMENT '是否请假(0-否,1-是)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_attendance_work_date` (`work_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考勤打卡记录';

-- 加班配置表
CREATE TABLE IF NOT EXISTS `overtime_settings` (
    `id`                   BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '主键',
    `work_start_time`      TIME         NOT NULL                 COMMENT '标准上班时间',
    `work_end_time`        TIME         NOT NULL                 COMMENT '标准下班时间',
    `lunch_start_time`     TIME         DEFAULT NULL             COMMENT '午休开始时间',
    `lunch_end_time`       TIME         DEFAULT NULL             COMMENT '午休结束时间',
    `standard_work_minutes` INT         NOT NULL                 COMMENT '标准工时(分钟)',
    `calculation_mode`     VARCHAR(32)  NOT NULL                 COMMENT '加班计算模式(EXCLUDE_STANDARD/INCLUDE_STANDARD)',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='加班配置';
