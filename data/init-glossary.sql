USE `tools`;

-- 业务系统表
CREATE TABLE IF NOT EXISTS `glossary_system` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`       VARCHAR(100) NOT NULL                 COMMENT '系统名称',
    `sort_order` INT          NOT NULL DEFAULT 0       COMMENT '排序号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_system_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='业务系统';

-- 业务名词表
CREATE TABLE IF NOT EXISTS `glossary_term` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `system_id`   BIGINT       NOT NULL                 COMMENT '所属系统ID',
    `name`        VARCHAR(200) NOT NULL                 COMMENT '名词名称',
    `description` TEXT                                   COMMENT '名词解释',
    `category`    VARCHAR(100) DEFAULT NULL              COMMENT '分类',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_system_id` (`system_id`),
    KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='业务名词';

-- 变更历史表
CREATE TABLE IF NOT EXISTS `glossary_change_history` (
    `id`         BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `term_id`    BIGINT      NOT NULL                 COMMENT '名词ID',
    `field_name` VARCHAR(50) NOT NULL                 COMMENT '变更字段',
    `old_value`  TEXT                                  COMMENT '旧值',
    `new_value`  TEXT                                  COMMENT '新值',
    `changed_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '变更时间',
    PRIMARY KEY (`id`),
    KEY `idx_term_id` (`term_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='变更历史';

-- 默认系统
INSERT IGNORE INTO `glossary_system` (`id`, `name`, `sort_order`) VALUES (1, '股票金融', 1);

-- 股票金融默认名词
INSERT IGNORE INTO `glossary_term` (`id`, `system_id`, `name`, `description`, `category`) VALUES
(1, 1, 'A股', '人民币普通股票，由中国境内公司发行，在境内交易所上市，以人民币认购和交易。', '基本概念'),
(2, 1, 'B股', '人民币特种股票，以人民币标明面值，以外币认购和买卖，在境内交易所上市。', '基本概念'),
(3, 1, 'H股', '注册地在内地、上市地在香港的外资股。', '基本概念'),
(4, 1, '涨停', '证券交易中当日的最高价格限制，涨幅达到上限时停止交易。A股主板涨停板为10%。', '交易规则'),
(5, 1, '跌停', '证券交易中当日的最低价格限制，跌幅达到下限时停止交易。A股主板跌停板为10%。', '交易规则'),
(6, 1, '市盈率（PE）', '股票价格除以每股收益的比率，衡量股票是否被高估或低估的指标。', '估值指标'),
(7, 1, '市净率（PB）', '股票价格除以每股净资产的比率，用于衡量公司资产价值。', '估值指标'),
(8, 1, '净资产收益率（ROE）', '净利润除以平均净资产的比率，衡量公司运用自有资本的效率。', '财务指标'),
(9, 1, '每股收益（EPS）', '净利润除以总股本，反映每股创造的税后利润。', '财务指标'),
(10, 1, '股息率', '每股股息除以股票价格，衡量股息收益水平的指标。', '财务指标'),
(11, 1, '流通市值', '在证券交易所上市交易的可流通股票的总市值。', '市值指标'),
(12, 1, '总市值', '公司全部已发行股票的总市场价值，等于股价乘以总股本。', '市值指标'),
(13, 1, '换手率', '一定时间内股票转手买卖的频率，反映股票流通性强弱。', '技术指标'),
(14, 1, '成交量', '一定时间内股票交易的数量，反映市场活跃程度。', '交易指标'),
(15, 1, '成交额', '一定时间内股票交易的金额总量。', '交易指标'),
(16, 1, '主力资金', '大额资金进出情况，通常指机构投资者的大单交易资金。', '资金面'),
(17, 1, '北向资金', '通过沪港通、深港通从香港市场流入A股的资金。', '资金面'),
(18, 1, '融资融券', '投资者向证券公司借入资金买入证券（融资）或借入证券卖出（融券）的业务。', '交易规则'),
(19, 1, 'K线', '将一定时间内的开盘价、收盘价、最高价、最低价以图形方式展示的图表。', '技术分析'),
(20, 1, '均线（MA）', '一定时间内股票收盘价的算术平均值连线，用于判断趋势方向。', '技术分析'),
(21, 1, 'MACD', '指数平滑异同移动平均线，由快慢均线及其离散值构成，用于判断买卖时机。', '技术分析'),
(22, 1, 'KDJ', '随机指标，通过计算一定周期内的最高价、最低价和收盘价来预测趋势。', '技术分析'),
(23, 1, 'RSI', '相对强弱指标，通过比较一定时期内平均收盘涨数和平均收盘跌数来判断市场强弱。', '技术分析'),
(24, 1, 'BOLL', '布林线，由中轨（均线）、上轨和下轨三条线组成，衡量股价波动范围。', '技术分析'),
(25, 1, '除权除息', '上市公司分红送股后，对股价进行相应调整的行为。', '交易规则'),
(26, 1, '复权', '对股价进行修复，消除除权除息对历史价格的影响，分为前复权和后复权。', '技术分析'),
(27, 1, 'IPO', '首次公开发行，公司首次向公众出售股票并在交易所上市的过程。', '基本概念'),
(28, 1, '再融资', '上市公司通过增发、配股等方式再次筹集资金的行为。', '基本概念'),
(29, 1, '并购重组', '公司通过收购、合并、资产置换等方式实现资源整合的行为。', '基本概念');
