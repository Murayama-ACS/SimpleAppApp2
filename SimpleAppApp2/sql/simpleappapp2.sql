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
  CONSTRAINT `FK_applications_employees` FOREIGN KEY (`emp_id`) REFERENCES `employees` (`emp_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_applications_status` FOREIGN KEY (`status_id`) REFERENCES `status` (`status_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- テーブル simpleappapp2.applications: ~0 rows (約) のデータをダンプしています
DELETE FROM `applications`;

--  テーブル simpleappapp2.approvals の構造をダンプしています
DROP TABLE IF EXISTS `approvals`;
CREATE TABLE IF NOT EXISTS `approvals` (
  `approval_id` varchar(20) NOT NULL,
  `apct_id` varchar(20) NOT NULL,
  `emp_id` varchar(20) NOT NULL,
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
	('D740', '情報システム部D課');

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
  UNIQUE KEY `email` (`email`),
  KEY `dpt_id` (`dpt_id`),
  KEY `pos_id` (`pos_id`),
  CONSTRAINT `FK_employees_departments` FOREIGN KEY (`dpt_id`) REFERENCES `departments` (`dpt_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_employees_positions` FOREIGN KEY (`pos_id`) REFERENCES `positions` (`pos_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- テーブル simpleappapp2.employees: ~120 rows (約) のデータをダンプしています
DELETE FROM `employees`;
INSERT INTO `employees` (`emp_id`, `emp_name`, `email`, `password`, `dpt_id`, `pos_id`, `is_deleted`) VALUES
	('A00000001', '山田 太一', 'ceo@example.com', 'Abcd1234', 'D000', 'E04', 0),
	('A20150207', '高橋 優衣', 'user77@example.com', 'Abcd1234', 'D100', 'E00', 0),
	('A20150212', '小林 直樹', 'user113@example.com', 'Abcd1234', 'D400', 'E02', 0),
	('A20150624', '山田 彩', 'user48@example.com', 'Abcd1234', 'D730', 'E00', 0),
	('A20150629', '高橋 優衣', 'user72@example.com', 'Abcd1234', 'D000', 'E02', 0),
	('A20150807', '山田 優衣', 'user63@example.com', 'Abcd1234', 'D200', 'E00', 0),
	('A20150909', '田中 健', 'user24@example.com', 'Abcd1234', 'D710', 'E00', 0),
	('A20160108', '山田 真央', 'user64@example.com', 'Abcd1234', 'D710', 'E01', 0),
	('A20160111', '田中 太郎', 'user78@example.com', 'Abcd1234', 'D200', 'E02', 0),
	('A20160611', '渡辺 優衣', 'user19@example.com', 'Abcd1234', 'D620', 'E00', 0),
	('A20160621', '小林 一郎', 'user55@example.com', 'Abcd1234', 'D730', 'E00', 0),
	('A20160709', '鈴木 健', 'user45@example.com', 'Abcd1234', 'D720', 'E00', 0),
	('A20160813', '加藤 美咲', 'user108@example.com', 'Abcd1234', 'D530', 'E00', 0),
	('A20160828', '佐藤 優衣', 'user40@example.com', 'Abcd1234', 'D520', 'E00', 0),
	('A20161124', '小林 彩', 'user76@example.com', 'Abcd1234', 'D540', 'E00', 0),
	('A20161215', '中村 真央', 'user33@example.com', 'Abcd1234', 'D720', 'E00', 0),
	('A20161216', '山田 健', 'user67@example.com', 'Abcd1234', 'D510', 'E00', 0),
	('A20161217', '高橋 真央', 'user106@example.com', 'Abcd1234', 'D720', 'E00', 0),
	('A20170115', '加藤 花子', 'user44@example.com', 'Abcd1234', 'D530', 'E00', 0),
	('A20170330', '高橋 太郎', 'user7@example.com', 'Abcd1234', 'D520', 'E00', 0),
	('A20170407', '伊藤 大輔', 'user57@example.com', 'Abcd1234', 'D630', 'E00', 0),
	('A20170626', '渡辺 美咲', 'user68@example.com', 'Abcd1234', 'D630', 'E00', 0),
	('A20170802', '伊藤 彩', 'user82@example.com', 'Abcd1234', 'D410', 'E01', 0),
	('A20170824', '中村 大輔', 'user25@example.com', 'Abcd1234', 'D620', 'E00', 0),
	('A20171125', '高橋 健', 'user16@example.com', 'Abcd1234', 'D610', 'E00', 0),
	('A20171208', '山田 大輔', 'user14@example.com', 'Abcd1234', 'D510', 'E00', 0),
	('A20180119', '中村 直樹', 'user69@example.com', 'Abcd1234', 'D540', 'E00', 0),
	('A20180407', '渡辺 健', 'user99@example.com', 'Abcd1234', 'D730', 'E01', 0),
	('A20180504', '加藤 太郎', 'user37@example.com', 'Abcd1234', 'D730', 'E00', 0),
	('A20180707', '加藤 真央', 'user95@example.com', 'Abcd1234', 'D540', 'E01', 0),
	('A20180828', '山田 健', 'user105@example.com', 'Abcd1234', 'D740', 'E00', 0),
	('A20180906', '中村 優衣', 'user47@example.com', 'Abcd1234', 'D720', 'E00', 0),
	('A20180926', '鈴木 健', 'user117@example.com', 'Abcd1234', 'D700', 'E03', 0),
	('A20181217', '加藤 一郎', 'user94@example.com', 'Abcd1234', 'D740', 'E00', 0),
	('A20181223', '高橋 健', 'user34@example.com', 'Abcd1234', 'D540', 'E00', 0),
	('A20190103', '渡辺 一郎', 'user2@example.com', 'Abcd1234', 'D100', 'E02', 0),
	('A20190106', '小林 大輔', 'user15@example.com', 'Abcd1234', 'D540', 'E00', 0),
	('A20190113', '伊藤 健', 'user8@example.com', 'Abcd1234', 'D620', 'E00', 0),
	('A20190119', '小林 彩', 'user103@example.com', 'Abcd1234', 'D510', 'E00', 0),
	('A20190211', '伊藤 直樹', 'user66@example.com', 'Abcd1234', 'D620', 'E00', 0),
	('A20190227', '田中 花子', 'user104@example.com', 'Abcd1234', 'D540', 'E00', 0),
	('A20190305', '加藤 真央', 'user23@example.com', 'Abcd1234', 'D740', 'E00', 0),
	('A20190429', '加藤 彩', 'user83@example.com', 'Abcd1234', 'D710', 'E00', 0),
	('A20190524', '加藤 健', 'user119@example.com', 'Abcd1234', 'D630', 'E00', 0),
	('A20190725', '高橋 美咲', 'user42@example.com', 'Abcd1234', 'D420', 'E00', 0),
	('A20190814', '田中 直樹', 'user26@example.com', 'Abcd1234', 'D630', 'E00', 0),
	('A20190907', '伊藤 優衣', 'user115@example.com', 'Abcd1234', 'D500', 'E02', 0),
	('A20190908', '山田 直樹', 'user110@example.com', 'Abcd1234', 'D510', 'E00', 0),
	('A20191126', '鈴木 美咲', 'user73@example.com', 'Abcd1234', 'D720', 'E00', 0),
	('A20200207', '鈴木 健', 'user35@example.com', 'Abcd1234', 'D740', 'E00', 0),
	('A20200313', '田中 太郎', 'user87@example.com', 'Abcd1234', 'D420', 'E01', 0),
	('A20200403', '加藤 一郎', 'user58@example.com', 'Abcd1234', 'D730', 'E00', 0),
	('A20200413', '渡辺 一郎', 'user46@example.com', 'Abcd1234', 'D730', 'E00', 0),
	('A20200418', '高橋 大輔', 'user97@example.com', 'Abcd1234', 'D740', 'E00', 0),
	('A20200519', '田中 優衣', 'user54@example.com', 'Abcd1234', 'D520', 'E00', 0),
	('A20200528', '中村 一郎', 'user4@example.com', 'Abcd1234', 'D610', 'E00', 0),
	('A20200717', '佐藤 太郎', 'user80@example.com', 'Abcd1234', 'D730', 'E00', 0),
	('A20200720', '田中 優衣', 'user98@example.com', 'Abcd1234', 'D540', 'E00', 0),
	('A20200913', '高橋 健', 'user53@example.com', 'Abcd1234', 'D730', 'E00', 0),
	('A20200923', '伊藤 美咲', 'user22@example.com', 'Abcd1234', 'D520', 'E00', 0),
	('A20210330', '高橋 直樹', 'user41@example.com', 'Abcd1234', 'D420', 'E00', 0),
	('A20210409', '加藤 一郎', 'user49@example.com', 'Abcd1234', 'D630', 'E00', 0),
	('A20210613', '高橋 真央', 'user86@example.com', 'Abcd1234', 'D520', 'E00', 0),
	('A20210617', '伊藤 大輔', 'user32@example.com', 'Abcd1234', 'D720', 'E00', 0),
	('A20210720', '佐藤 優衣', 'user11@example.com', 'Abcd1234', 'D630', 'E00', 0),
	('A20210904', '伊藤 彩', 'user65@example.com', 'Abcd1234', 'D530', 'E00', 0),
	('A20210930', '高橋 優衣', 'user118@example.com', 'Abcd1234', 'D400', 'E00', 0),
	('A20211108', '田中 直樹', 'user30@example.com', 'Abcd1234', 'D410', 'E00', 0),
	('A20211116', '田中 真央', 'user101@example.com', 'Abcd1234', 'D200', 'E00', 0),
	('A20220113', '佐藤 健', 'user31@example.com', 'Abcd1234', 'D410', 'E00', 0),
	('A20220317', '鈴木 彩', 'user27@example.com', 'Abcd1234', 'D710', 'E00', 0),
	('A20220320', '田中 優衣', 'user6@example.com', 'Abcd1234', 'D530', 'E00', 0),
	('A20220414', '伊藤 太郎', 'user36@example.com', 'Abcd1234', 'D510', 'E00', 0),
	('A20220418', '鈴木 健', 'user5@example.com', 'Abcd1234', 'D620', 'E00', 0),
	('A20220613', '伊藤 太郎', 'user92@example.com', 'Abcd1234', 'D400', 'E00', 0),
	('A20220707', '高橋 美咲', 'user70@example.com', 'Abcd1234', 'D530', 'E00', 0),
	('A20220822', '田中 太郎', 'user51@example.com', 'Abcd1234', 'D610', 'E00', 0),
	('A20220908', '佐藤 美咲', 'user75@example.com', 'Abcd1234', 'D000', 'E00', 0),
	('A20220910', '小林 太郎', 'user84@example.com', 'Abcd1234', 'D520', 'E01', 0),
	('A20221023', '渡辺 真央', 'user116@example.com', 'Abcd1234', 'D000', 'E00', 0),
	('A20221203', '佐藤 直樹', 'user1@example.com', 'Abcd1234', 'D410', 'E00', 0),
	('A20221207', '伊藤 彩', 'user62@example.com', 'Abcd1234', 'D100', 'E00', 0),
	('A20221226', '渡辺 大輔', 'user114@example.com', 'Abcd1234', 'D734', 'E02', 0),
	('A20230111', '渡辺 彩', 'user13@example.com', 'Abcd1234', 'D100', 'E00', 0),
	('A2023011101', '伊藤 優衣', 'user28@example.com', 'Abcd1234', 'D740', 'E00', 0),
	('A20230225', '渡辺 彩', 'user17@example.com', 'Abcd1234', 'D710', 'E00', 0),
	('A20230226', '鈴木 美咲', 'user56@example.com', 'Abcd1234', 'D420', 'E00', 0),
	('A20230324', '高橋 大輔', 'user59@example.com', 'Abcd1234', 'D740', 'E00', 0),
	('A20230419', '小林 健', 'user111@example.com', 'Abcd1234', 'D300', 'E02', 0),
	('A20230425', '山田 美咲', 'user100@example.com', 'Abcd1234', 'D400', 'E00', 0),
	('A20230709', '高橋 優衣', 'user52@example.com', 'Abcd1234', 'D740', 'E00', 0),
	('A20230812', '山田 花子', 'user112@example.com', 'Abcd1234', 'D710', 'E00', 0),
	('A20230905', '加藤 直樹', 'user89@example.com', 'Abcd1234', 'D740', 'E01', 0),
	('A20230916', '高橋 直樹', 'user18@example.com', 'Abcd1234', 'D630', 'E00', 0),
	('A20231205', '小林 花子', 'user88@example.com', 'Abcd1234', 'D530', 'E01', 0),
	('A20231208', '鈴木 大輔', 'user29@example.com', 'Abcd1234', 'D720', 'E00', 0),
	('A20240203', '小林 優衣', 'user21@example.com', 'Abcd1234', 'D710', 'E00', 0),
	('A20240329', '加藤 優衣', 'user39@example.com', 'Abcd1234', 'D530', 'E00', 0),
	('A20240409', '佐藤 美咲', 'user12@example.com', 'Abcd1234', 'D710', 'E00', 0),
	('A20240411', '佐藤 健', 'user109@example.com', 'Abcd1234', 'D712', 'E02', 0),
	('A20240702', '山田 優衣', 'user107@example.com', 'Abcd1234', 'D740', 'E00', 0),
	('A20240704', '山田 花子', 'user71@example.com', 'Abcd1234', 'D400', 'E00', 0),
	('A20240828', '渡辺 彩', 'user3@example.com', 'Abcd1234', 'D610', 'E00', 0),
	('A20241112', '渡辺 優衣', 'user60@example.com', 'Abcd1234', 'D730', 'E00', 0),
	('A20250113', '小林 美咲', 'user91@example.com', 'Abcd1234', 'D620', 'E01', 0),
	('A20250307', '佐藤 大輔', 'user10@example.com', 'Abcd1234', 'D100', 'E00', 0),
	('A20250314', '渡辺 彩', 'user61@example.com', 'Abcd1234', 'D200', 'E00', 0),
	('A20250415', '佐藤 大輔', 'user90@example.com', 'Abcd1234', 'D400', 'E00', 0),
	('A20250516', '田中 大輔', 'user79@example.com', 'Abcd1234', 'D620', 'E00', 0),
	('A20250729', '中村 美咲', 'user93@example.com', 'Abcd1234', 'D630', 'E01', 0),
	('A20250816', '中村 太郎', 'user85@example.com', 'Abcd1234', 'D610', 'E01', 0),
	('A20251115', '渡辺 優衣', 'user9@example.com', 'Abcd1234', 'D530', 'E00', 0),
	('A20251224', '小林 健', 'user20@example.com', 'Abcd1234', 'D510', 'E00', 0),
	('A20251229', '田中 彩', 'user50@example.com', 'Abcd1234', 'D720', 'E00', 0),
	('A2025122901', '渡辺 花子', 'user102@example.com', 'Abcd1234', 'D600', 'E02', 0),
	('A20260109', '田中 優衣', 'user43@example.com', 'Abcd1234', 'D610', 'E00', 0),
	('A20260112', '高橋 美咲', 'user81@example.com', 'Abcd1234', 'D510', 'E01', 0),
	('A20260307', '小林 直樹', 'user96@example.com', 'Abcd1234', 'D720', 'E01', 0),
	('A20260315', '中村 健', 'user38@example.com', 'Abcd1234', 'D610', 'E00', 0),
	('A20260327', '伊藤 一郎', 'user74@example.com', 'Abcd1234', 'D610', 'E00', 0);

--  テーブル simpleappapp2.failed_logins の構造をダンプしています
DROP TABLE IF EXISTS `failed_logins`;
CREATE TABLE IF NOT EXISTS `failed_logins` (
  `emp_id` varchar(20) NOT NULL,
  `quiz_attempts` int(11) NOT NULL DEFAULT 0,
  `password_attempts` int(11) NOT NULL DEFAULT 0,
  `first_failed_password_at` datetime DEFAULT NULL,
  `first_failed_quiz_at` datetime DEFAULT NULL,
  `last_failed_password_at` datetime DEFAULT NULL,
  `last_failed_quiz_at` datetime DEFAULT NULL,
  `lock_count` int(11) NOT NULL DEFAULT 0,
  `locked_until` datetime DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `updated_at` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`emp_id`),
  CONSTRAINT `FK_failed_logins_employees` FOREIGN KEY (`emp_id`) REFERENCES `employees` (`emp_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- テーブル simpleappapp2.failed_logins: ~0 rows (約) のデータをダンプしています
DELETE FROM `failed_logins`;

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
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- テーブル simpleappapp2.security_quiz: ~0 rows (約) のデータをダンプしています
DELETE FROM `security_quiz`;

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
