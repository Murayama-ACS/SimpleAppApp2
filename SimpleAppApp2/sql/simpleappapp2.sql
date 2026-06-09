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

-- テーブル simpleappapp2.app_delete_histories: ~0 rows (約) のデータをダンプしています
DELETE FROM `app_delete_histories`;

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
  `urgent` varchar(50) NOT NULL DEFAULT '0',
  `status_id` int(10) NOT NULL,
  `create_date` datetime NOT NULL,
  `update_date` datetime NOT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`apct_id`),
  KEY `emp_id` (`emp_id`),
  KEY `status_id` (`status_id`),
  CONSTRAINT `FK_applications_employees` FOREIGN KEY (`emp_id`) REFERENCES `employees` (`emp_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_applications_status` FOREIGN KEY (`status_id`) REFERENCES `status` (`status_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- テーブル simpleappapp2.applications: ~0 rows (約) のデータをダンプしています
DELETE FROM `applications`;

--  テーブル simpleappapp2.approvals の構造をダンプしています
DROP TABLE IF EXISTS `approvals`;
CREATE TABLE IF NOT EXISTS `approvals` (
  `approval_id` varchar(20) NOT NULL,
  `apct_id` varchar(20) NOT NULL,
  `emp_id` varchar(20) NOT NULL,
  `apct_type` varchar(20) NOT NULL,
  `comment` varchar(200) DEFAULT NULL,
  `time` datetime NOT NULL,
  `status_id` int(10) NOT NULL,
  PRIMARY KEY (`approval_id`),
  KEY `apct_id` (`apct_id`),
  KEY `emp_id` (`emp_id`),
  KEY `status` (`status_id`) USING BTREE,
  CONSTRAINT `FK_approvals_applications` FOREIGN KEY (`apct_id`) REFERENCES `applications` (`apct_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_approvals_employees` FOREIGN KEY (`emp_id`) REFERENCES `employees` (`emp_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_approvals_status` FOREIGN KEY (`status_id`) REFERENCES `status` (`status_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- テーブル simpleappapp2.approvals: ~0 rows (約) のデータをダンプしています
DELETE FROM `approvals`;

--  テーブル simpleappapp2.departments の構造をダンプしています
DROP TABLE IF EXISTS `departments`;
CREATE TABLE IF NOT EXISTS `departments` (
  `dpt_id` char(10) NOT NULL DEFAULT '',
  `dpt_name` varchar(50) NOT NULL DEFAULT '',
  PRIMARY KEY (`dpt_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- テーブル simpleappapp2.departments: ~23 rows (約) のデータをダンプしています
DELETE FROM `departments`;
INSERT INTO `departments` (`dpt_id`, `dpt_name`) VALUES
	('D000', '経営企画部'),
	('D100', '管理部'),
	('D200', '経理部'),
	('D300', '総務部'),
	('D400', '人事部'),
	('D410', '人事部A課'),
	('D420', '人事部B課'),
	('D500', '開発部'),
	('D510', '開発部A課'),
	('D520', '開発部B課'),
	('D530', '開発部C課'),
	('D540', '開発部D課'),
	('D600', '営業部'),
	('D610', '営業部A課'),
	('D620', '営業部B課'),
	('D630', '営業部C課'),
	('D700', '情報システム部'),
	('D710', '情報システム部A課'),
	('D712', '情報システム部A課B課'),
	('D720', '情報システム部B課'),
	('D730', '情報システム部C課'),
	('D734', '情報システム部C課D課'),
	('D740', '情報システム部A課');

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

-- テーブル simpleappapp2.emp_delete_histories: ~0 rows (約) のデータをダンプしています
DELETE FROM `emp_delete_histories`;

--  テーブル simpleappapp2.employees の構造をダンプしています
DROP TABLE IF EXISTS `employees`;
CREATE TABLE IF NOT EXISTS `employees` (
  `emp_id` varchar(20) NOT NULL,
  `emp_name` varchar(100) NOT NULL,
  `email` varchar(200) NOT NULL,
  `password` varchar(200) NOT NULL,
  `dpt_id` char(10) NOT NULL DEFAULT '',
  `pos_id` char(10) NOT NULL DEFAULT '',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`emp_id`),
  KEY `dpt_id` (`dpt_id`),
  KEY `pos_id` (`pos_id`),
  CONSTRAINT `FK_employees_departments` FOREIGN KEY (`dpt_id`) REFERENCES `departments` (`dpt_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_employees_positions` FOREIGN KEY (`pos_id`) REFERENCES `positions` (`pos_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- テーブル simpleappapp2.employees: ~6 rows (約) のデータをダンプしています
DELETE FROM `employees`;
INSERT INTO `employees` (`emp_id`, `emp_name`, `email`, `password`, `dpt_id`, `pos_id`, `is_deleted`) VALUES
	('A00000001', '山田 太一', 'ceo@example.com', 'aaaaaaaaaa', 'D000', 'E04', 0),
	('A20160108', '山田 真央', 'user64@example.com', '11111111', 'D710', 'E01', 0),
	('A20180926', '鈴木 健', 'user117@example.com', '1111', 'D700', 'E03', 0),
	('A20190103', '渡辺 一郎', 'user2@example.com', '1111', 'D100', 'E02', 0),
	('A20190524', '加藤 健', 'user119@example.com', '1111', 'D740', 'E00', 0),
	('A20221203', '佐藤 直樹', 'user1@example.com', '1111', 'D410', 'E00', 0);

--  テーブル simpleappapp2.positions の構造をダンプしています
DROP TABLE IF EXISTS `positions`;
CREATE TABLE IF NOT EXISTS `positions` (
  `pos_id` char(10) NOT NULL DEFAULT '',
  `pos_name` varchar(50) NOT NULL DEFAULT '',
  `pos_amount` int(10) DEFAULT 0,
  PRIMARY KEY (`pos_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- テーブル simpleappapp2.positions: ~5 rows (約) のデータをダンプしています
DELETE FROM `positions`;
INSERT INTO `positions` (`pos_id`, `pos_name`, `pos_amount`) VALUES
	('E00', '一般社員', 30000),
	('E01', '課長', 100000),
	('E02', '部長', 300000),
	('E03', '本部長', NULL),
	('E04', '社長', NULL);

--  テーブル simpleappapp2.security_quiz の構造をダンプしています
DROP TABLE IF EXISTS `security_quiz`;
CREATE TABLE IF NOT EXISTS `security_quiz` (
  `sq_id` int(11) NOT NULL AUTO_INCREMENT,
  `emp_id` varchar(20) NOT NULL,
  `quiz` varchar(200) NOT NULL,
  `answer` varchar(200) NOT NULL,
  PRIMARY KEY (`sq_id`) USING BTREE,
  KEY `emp_id` (`emp_id`),
  CONSTRAINT `emp_id` FOREIGN KEY (`emp_id`) REFERENCES `employees` (`emp_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- テーブル simpleappapp2.security_quiz: ~6 rows (約) のデータをダンプしています
DELETE FROM `security_quiz`;
INSERT INTO `security_quiz` (`sq_id`, `emp_id`, `quiz`, `answer`) VALUES
	(4, 'A00000001', '初めて飼ったペットの名前は？', 'cat'),
	(5, 'A00000001', '幼少期によく遊んだ公園の呼び名や特徴は？', 'park'),
	(6, 'A00000001', '自分で最初に作った料理の名前は？', 'rice'),
	(7, 'A20160108', '自分で最初に作った料理の名前は？', '123'),
	(8, 'A20160108', '幼少期によく遊んだ公園の呼び名や特徴は？', '123'),
	(9, 'A20160108', '初めて飼ったペットの名前は？', '123');

--  テーブル simpleappapp2.status の構造をダンプしています
DROP TABLE IF EXISTS `status`;
CREATE TABLE IF NOT EXISTS `status` (
  `status_id` int(10) NOT NULL,
  `status_name` varchar(20) NOT NULL,
  PRIMARY KEY (`status_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- テーブル simpleappapp2.status: ~6 rows (約) のデータをダンプしています
DELETE FROM `status`;
INSERT INTO `status` (`status_id`, `status_name`) VALUES
	(1, '未承認'),
	(2, '上長承認'),
	(3, '管理部承認'),
	(4, '完了'),
	(5, '却下'),
	(6, '削除');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
