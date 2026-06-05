-- --------------------------------------------------------
-- ホスト:                          127.0.0.1
-- サーバーのバージョン:                   11.8.6-MariaDB - MariaDB Server
-- サーバー OS:                      Win64
-- HeidiSQL バージョン:               12.16.0.7229
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- simpleappapp2 のデータベース構造をダンプしています
CREATE DATABASE IF NOT EXISTS `simpleappapp2` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_uca1400_ai_ci */;
USE `simpleappapp2`;

--  テーブル simpleappapp2.app_delete_histories の構造をダンプしています
DROP TABLE IF EXISTS `app_delete_histories`;
CREATE TABLE IF NOT EXISTS `app_delete_histories` (
  `history_id` varchar(20) NOT NULL,
  `apct_id` varchar(20) NOT NULL,
  `emp_id` varchar(20) NOT NULL,
  `delete_date` datetime NOT NULL,
  PRIMARY KEY (`history_id`),
  KEY `apct_id` (`apct_id`),
  KEY `emp_id` (`emp_id`),
  CONSTRAINT `FK_app_delete_histories_applications` FOREIGN KEY (`apct_id`) REFERENCES `applications` (`apct_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_app_delete_histories_employees` FOREIGN KEY (`emp_id`) REFERENCES `employees` (`emp_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- エクスポートするデータが選択されていません

--  テーブル simpleappapp2.applications の構造をダンプしています
DROP TABLE IF EXISTS `applications`;
CREATE TABLE IF NOT EXISTS `applications` (
  `apct_id` varchar(20) NOT NULL,
  `emp_id` varchar(20) NOT NULL,
  `content` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `method` varchar(20) NOT NULL,
  `amount` int(10) NOT NULL DEFAULT 0,
  `reason` varchar(200) NOT NULL,
  `remark` varchar(200) DEFAULT NULL,
  `urgent` varchar(20) NOT NULL,
  `status` varchar(20) NOT NULL,
  `create_date` datetime NOT NULL,
  `update_date` datetime NOT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`apct_id`),
  KEY `emp_id` (`emp_id`),
  CONSTRAINT `f_emp_id` FOREIGN KEY (`emp_id`) REFERENCES `employees` (`emp_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- エクスポートするデータが選択されていません

--  テーブル simpleappapp2.approvals の構造をダンプしています
DROP TABLE IF EXISTS `approvals`;
CREATE TABLE IF NOT EXISTS `approvals` (
  `approval_id` varchar(20) NOT NULL,
  `apct_id` varchar(20) NOT NULL,
  `emp_id` varchar(20) NOT NULL,
  `apct_type` varchar(20) NOT NULL,
  `comment` varchar(200) DEFAULT NULL,
  `time` datetime NOT NULL,
  PRIMARY KEY (`approval_id`),
  KEY `apct_id` (`apct_id`),
  KEY `emp_id` (`emp_id`),
  CONSTRAINT `FK_approvals_applications` FOREIGN KEY (`apct_id`) REFERENCES `applications` (`apct_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_approvals_employees` FOREIGN KEY (`emp_id`) REFERENCES `employees` (`emp_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- エクスポートするデータが選択されていません

--  テーブル simpleappapp2.departments の構造をダンプしています
DROP TABLE IF EXISTS `departments`;
CREATE TABLE IF NOT EXISTS `departments` (
  `dpt_id` int(3) NOT NULL,
  `dpt_name` varchar(50) NOT NULL DEFAULT '',
  PRIMARY KEY (`dpt_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- エクスポートするデータが選択されていません

--  テーブル simpleappapp2.emp_delete_histories の構造をダンプしています
DROP TABLE IF EXISTS `emp_delete_histories`;
CREATE TABLE IF NOT EXISTS `emp_delete_histories` (
  `history_id` varchar(20) NOT NULL,
  `emp_del_id` varchar(20) NOT NULL,
  `emp_id` varchar(20) NOT NULL,
  `delete_date` datetime NOT NULL,
  PRIMARY KEY (`history_id`),
  KEY `emp_del_id` (`emp_del_id`),
  KEY `emp_id` (`emp_id`),
  CONSTRAINT `FK_emp_delete_histories_employees` FOREIGN KEY (`emp_del_id`) REFERENCES `employees` (`emp_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_emp_delete_histories_employees_2` FOREIGN KEY (`emp_id`) REFERENCES `employees` (`emp_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- エクスポートするデータが選択されていません

--  テーブル simpleappapp2.employees の構造をダンプしています
DROP TABLE IF EXISTS `employees`;
CREATE TABLE IF NOT EXISTS `employees` (
  `emp_id` varchar(20) NOT NULL,
  `emp_name` varchar(100) NOT NULL,
  `email` varchar(200) NOT NULL,
  `password` varchar(200) NOT NULL,
  `dpt_id` int(3) NOT NULL,
  `pos_id` int(3) NOT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`emp_id`),
  KEY `dpt_id` (`dpt_id`),
  KEY `pos_id` (`pos_id`),
  CONSTRAINT `dpt_id` FOREIGN KEY (`dpt_id`) REFERENCES `departments` (`dpt_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `pos_id` FOREIGN KEY (`pos_id`) REFERENCES `positions` (`pos_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- エクスポートするデータが選択されていません

--  テーブル simpleappapp2.positions の構造をダンプしています
DROP TABLE IF EXISTS `positions`;
CREATE TABLE IF NOT EXISTS `positions` (
  `pos_id` int(3) NOT NULL,
  `pos_name` varchar(50) NOT NULL DEFAULT '',
  `pos_amount` int(10) NOT NULL DEFAULT 0,
  PRIMARY KEY (`pos_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- エクスポートするデータが選択されていません

--  テーブル simpleappapp2.security_quiz の構造をダンプしています
DROP TABLE IF EXISTS `security_quiz`;
CREATE TABLE IF NOT EXISTS `security_quiz` (
  `sq_id` varchar(20) NOT NULL,
  `emp_id` varchar(20) NOT NULL,
  `quiz` varchar(200) NOT NULL,
  `answer` varchar(200) NOT NULL,
  PRIMARY KEY (`sq_id`),
  KEY `emp_id` (`emp_id`),
  CONSTRAINT `emp_id` FOREIGN KEY (`emp_id`) REFERENCES `employees` (`emp_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- エクスポートするデータが選択されていません

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
