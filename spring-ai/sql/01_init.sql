CREATE
DATABASE IF NOT EXISTS `ai-lab` DEFAULT CHARACTER SET utf8mb4;

USE
`ai-lab`;

-- 订单表
CREATE TABLE `order_info`
(
    `id`                 VARCHAR(32)    NOT NULL COMMENT '订单号，如 ORD202401001',
    `user_id`            BIGINT         NOT NULL COMMENT '用户ID',
    `total_amount`       DECIMAL(10, 2) NOT NULL COMMENT '订单金额',
    `status`             VARCHAR(20)    NOT NULL COMMENT '状态：PENDING/SHIPPED/DELIVERED/CANCELLED',
    `tracking_number`    VARCHAR(64)             DEFAULT NULL COMMENT '物流单号',
    `estimated_delivery` DATE                    DEFAULT NULL COMMENT '预计到达日期',
    `created_at`         DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`         DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY                  `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 商品表
CREATE TABLE `product`
(
    `id`          BIGINT         NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(128)   NOT NULL COMMENT '商品名称',
    `price`       DECIMAL(10, 2) NOT NULL COMMENT '价格',
    `stock`       INT            NOT NULL DEFAULT 0 COMMENT '库存',
    `rating`      DECIMAL(2, 1)  NOT NULL DEFAULT 5.0 COMMENT '评分 0-5',
    `description` VARCHAR(512)            DEFAULT NULL COMMENT '商品描述',
    `created_at`  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FULLTEXT KEY `ft_name_desc` (`name`, `description`) -- 支持 MATCH AGAINST 模糊搜索
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 测试数据
INSERT INTO `order_info`
VALUES ('ORD001', 1001, 599.00, 'SHIPPED', 'SF1234567890', '2025-03-10', NOW(), NOW()),
       ('ORD002', 1001, 1299.00, 'PENDING', NULL, NULL, NOW(), NOW()),
       ('ORD003', 1002, 299.00, 'DELIVERED', 'YT9876543210', '2025-03-05', NOW(), NOW());

INSERT INTO `product`
VALUES (1, 'iPhone 16 Pro 256G', 8999.00, 23, 4.8, '苹果最新旗舰，A18 Pro芯片', NOW()),
       (2, 'iPhone 16 Pro 512G', 9999.00, 5, 4.8, '苹果最新旗舰，大存储版本', NOW()),
       (3, '华为 Mate 70 Pro', 6999.00, 12, 4.7, '华为旗舰，麒麟芯片', NOW()),
       (4, 'AirPods Pro 2', 1799.00, 50, 4.6, '苹果降噪耳机', NOW());