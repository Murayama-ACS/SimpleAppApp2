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

-- テーブル simpleappapp2.applications: ~4 rows (約) のデータをダンプしています
DELETE FROM `applications`;
INSERT INTO `applications` (`apct_id`, `emp_id`, `content`, `type`, `method`, `amount`, `reason`, `remark`, `urgent`, `status_id`, `create_date`, `update_date`, `is_deleted`) VALUES
	('AP260616100155265', 'A20221203', 'test', '備品購入申請', '立替払い（現金手渡し）', 25000, 'test', 'test', '緊急', 3, '2026-06-16 10:01:55', '2026-06-16 11:00:20', 0),
	('AP260616103151826', 'A20221203', 't', '研修参加申請', '立替払い（現金手渡し）', 11, 't', NULL, '緊急', 1, '2026-06-16 10:31:51', '2026-06-16 10:31:59', 1),
	('AP260616103237011', 'A20221203', '1212', '備品購入申請', '立替払い（現金手渡し）', 1212, '1212', NULL, '緊急', 5, '2026-06-16 10:32:37', '2026-06-16 10:40:11', 0),
	('AP260616105101124', 'A20221203', 'test', '出張申請', '会社直接支払い', 10000, 'test', 'test2', '緊急', 5, '2026-06-16 10:51:01', '2026-06-16 10:54:42', 0);

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
  CONSTRAINT `FK_approvals_status` FOREIGN KEY (`status_id`) REFERENCES `status` (`status_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- テーブル simpleappapp2.approvals: ~8 rows (約) のデータをダンプしています
DELETE FROM `approvals`;
INSERT INTO `approvals` (`approval_id`, `apct_id`, `emp_id`, `comment`, `time`, `status_id`) VALUES
	('APV260616101816775', 'AP260616100155265', 'A20170802', 'テスト', '2026-06-16 10:18:16', 6),
	('APV260616103303616', 'AP260616103237011', 'A20170802', 'ok', '2026-06-16 10:33:03', 2),
	('APV260616103425702', 'AP260616103237011', 'A20190103', '問題なし', '2026-06-16 10:34:25', 3),
	('APV260616104011181', 'AP260616103237011', 'A20160111', NULL, '2026-06-16 10:40:11', 5),
	('APV260616105250182', 'AP260616105101124', 'A20170802', 'ok', '2026-06-16 10:52:50', 2),
	('APV260616105353476', 'AP260616105101124', 'A20190103', 'test', '2026-06-16 10:53:53', 3),
	('APV260616105442665', 'AP260616105101124', 'A20160111', NULL, '2026-06-16 10:54:42', 5),
	('APV260616110020194', 'AP260616100155265', 'A20170802', 'no', '2026-06-16 11:00:20', 6);

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
  `furigana` varchar(50) NOT NULL DEFAULT '0',
  `email` varchar(200) NOT NULL,
  `password` varchar(200) NOT NULL DEFAULT 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac',
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
INSERT INTO `employees` (`emp_id`, `emp_name`, `furigana`, `email`, `password`, `dpt_id`, `pos_id`, `is_deleted`) VALUES
	('A00000001', '山田 太一', 'やまだ たいち', 'ceo@example.com', 'a89980d83cc3ff0cf773f44c2c677410ea99aa2b6f29a71909965c0614942d9cbb6eeb96b323cab10f5a86caf44b929f3a0df6a14c3154e4017a603a0c493b48', 'D000', 'E04', 0),
	('A20150207', '高橋 優衣', 'たかはし ゆい', 'user77@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D100', 'E00', 0),
	('A20150212', '小林 直樹', 'こばやし なおき', 'user113@example.com', 'a89980d83cc3ff0cf773f44c2c677410ea99aa2b6f29a71909965c0614942d9cbb6eeb96b323cab10f5a86caf44b929f3a0df6a14c3154e4017a603a0c493b48', 'D400', 'E02', 0),
	('A20150624', '山田 彩', 'やまだ あや', 'user48@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D730', 'E00', 0),
	('A20150629', '高橋 優衣', 'たかはし ゆい', 'user72@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D000', 'E02', 0),
	('A20150807', '山田 優衣', 'やまだ ゆい', 'user63@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D200', 'E00', 0),
	('A20150909', '田中 健', 'たなか たける', 'user24@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D710', 'E00', 0),
	('A20160108', '山田 真央', 'やまだ まお', 'user64@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D710', 'E01', 0),
	('A20160111', '田中 太郎', 'たなか たろう', 'user78@example.com', 'a89980d83cc3ff0cf773f44c2c677410ea99aa2b6f29a71909965c0614942d9cbb6eeb96b323cab10f5a86caf44b929f3a0df6a14c3154e4017a603a0c493b48', 'D200', 'E02', 0),
	('A20160611', '渡辺 優衣', 'わたなべ ゆい', 'user19@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D620', 'E00', 0),
	('A20160621', '小林 一郎', 'こばやし いちろう', 'user55@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D730', 'E00', 0),
	('A20160709', '鈴木 健', 'すずき たける', 'user45@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D720', 'E00', 0),
	('A20160813', '加藤 美咲', 'さとう みさき', 'user108@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D530', 'E00', 0),
	('A20160828', '佐藤 優衣', 'さとう ゆい', 'user40@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D520', 'E00', 0),
	('A20161124', '小林 彩', 'こばやし あや', 'user76@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D540', 'E00', 0),
	('A20161215', '中村 真央', 'なかむら まお', 'user33@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D720', 'E00', 0),
	('A20161216', '山田 健', 'やまだ たける', 'user67@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D510', 'E00', 0),
	('A20161217', '高橋 真央', 'たかはし まお', 'user106@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D720', 'E00', 0),
	('A20170115', '加藤 花子', 'かとう はなこ', 'user44@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D530', 'E00', 0),
	('A20170330', '高橋 太郎', 'たかはし たろう', 'user7@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D520', 'E00', 0),
	('A20170407', '伊藤 大輔', 'いとう だいすけ', 'user57@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D630', 'E00', 0),
	('A20170626', '渡辺 美咲', 'わたなべ みさき', 'user68@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D630', 'E00', 0),
	('A20170802', '伊藤 彩', 'いとう あや', 'user82@example.com', 'a89980d83cc3ff0cf773f44c2c677410ea99aa2b6f29a71909965c0614942d9cbb6eeb96b323cab10f5a86caf44b929f3a0df6a14c3154e4017a603a0c493b48', 'D410', 'E01', 0),
	('A20170824', '中村 大輔', 'なかむら だいすけ', 'user25@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D620', 'E00', 0),
	('A20171125', '高橋 健', 'たかはし たける', 'user16@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D610', 'E00', 0),
	('A20171208', '山田 大輔', 'やまだ だいすけ', 'user14@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D510', 'E00', 0),
	('A20180119', '中村 直樹', 'なかむら なおき', 'user69@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D540', 'E00', 0),
	('A20180407', '渡辺 健', 'わたなべ たける', 'user99@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D730', 'E01', 0),
	('A20180504', '加藤 太郎', 'かとう たろう', 'user37@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D730', 'E00', 0),
	('A20180707', '加藤 真央', 'かとう まお', 'user95@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D540', 'E01', 0),
	('A20180828', '山田 健', 'やまだ たける', 'user105@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D740', 'E00', 0),
	('A20180906', '中村 優衣', 'なかむら ゆい', 'user47@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D720', 'E00', 0),
	('A20180926', '鈴木 健', 'さとう なおき', 'user117@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D700', 'E03', 0),
	('A20181217', '加藤 一郎', 'かとう いちろう', 'user94@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D740', 'E00', 0),
	('A20181223', '高橋 健', 'たかはし たける', 'user34@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D540', 'E00', 0),
	('A20190103', '渡辺 一郎', 'わたなべ いちろう', 'user2@example.com', 'a89980d83cc3ff0cf773f44c2c677410ea99aa2b6f29a71909965c0614942d9cbb6eeb96b323cab10f5a86caf44b929f3a0df6a14c3154e4017a603a0c493b48', 'D100', 'E02', 0),
	('A20190106', '小林 大輔', 'こばやし だいすけ', 'user15@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D540', 'E00', 0),
	('A20190113', '伊藤 健', 'いとう たける', 'user8@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D620', 'E00', 0),
	('A20190119', '小林 彩', 'こばやし あや', 'user103@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D510', 'E00', 0),
	('A20190211', '伊藤 直樹', 'いとう なおき', 'user66@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D620', 'E00', 0),
	('A20190227', '田中 花子', 'たなか はなこ', 'user104@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D540', 'E00', 0),
	('A20190305', '加藤 真央', 'かとう まお', 'user23@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D740', 'E00', 0),
	('A20190429', '加藤 彩', 'かとう あや', 'user83@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D710', 'E00', 0),
	('A20190524', '加藤 健', 'かとう たける', 'user119@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D630', 'E00', 0),
	('A20190725', '高橋 美咲', 'たかはし みさき', 'user42@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D420', 'E00', 0),
	('A20190814', '田中 直樹', 'たなか なおき', 'user26@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D630', 'E00', 0),
	('A20190907', '伊藤 優衣', 'いとう ゆい', 'user115@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D500', 'E02', 0),
	('A20190908', '山田 直樹', 'やまだ なおき', 'user110@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D510', 'E00', 0),
	('A20191126', '鈴木 美咲', 'すずき みさき', 'user73@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D720', 'E00', 0),
	('A20200207', '鈴木 健', 'すずき たける', 'user35@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D740', 'E00', 0),
	('A20200313', '田中 太郎', 'たなか たろう', 'user87@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D420', 'E01', 0),
	('A20200403', '加藤 一郎', 'かとう いちろう', 'user58@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D730', 'E00', 0),
	('A20200413', '渡辺 一郎', 'わたなべ いちろう', 'user46@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D730', 'E00', 0),
	('A20200418', '高橋 大輔', 'たかはし だいすけ', 'user97@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D740', 'E00', 0),
	('A20200519', '田中 優衣', 'たなか ゆい', 'user54@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D520', 'E00', 0),
	('A20200528', '中村 一郎', 'なかむら いちろう', 'user4@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D610', 'E00', 0),
	('A20200717', '佐藤 太郎', 'さとう たろう', 'user80@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D730', 'E00', 0),
	('A20200720', '田中 優衣', 'たなか ゆい', 'user98@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D540', 'E00', 0),
	('A20200913', '高橋 健', 'たかはし たける', 'user53@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D730', 'E00', 0),
	('A20200923', '伊藤 美咲', 'いとう みさき', 'user22@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D520', 'E00', 0),
	('A20210330', '高橋 直樹', 'たかはし なおき', 'user41@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D420', 'E00', 0),
	('A20210409', '加藤 一郎', 'かとう いちろう', 'user49@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D630', 'E00', 0),
	('A20210613', '高橋 真央', 'たかはし まお', 'user86@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D520', 'E00', 0),
	('A20210617', '伊藤 大輔', 'いとう だいすけ', 'user32@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D720', 'E00', 0),
	('A20210720', '佐藤 優衣', 'さとう ゆい', 'user11@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D630', 'E00', 0),
	('A20210904', '伊藤 彩', 'いとう あや', 'user65@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D530', 'E00', 0),
	('A20210930', '高橋 優衣', 'たかはし ゆい', 'user118@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D400', 'E00', 0),
	('A20211108', '田中 直樹', 'たなか なおき', 'user30@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D410', 'E00', 0),
	('A20211116', '田中 真央', 'たなか まお', 'user101@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D200', 'E00', 0),
	('A20220113', '佐藤 健', 'さとう たける', 'user31@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D410', 'E00', 0),
	('A20220317', '鈴木 彩', 'すずき あや', 'user27@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D710', 'E00', 0),
	('A20220320', '田中 優衣', 'たなか ゆい', 'user6@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D530', 'E00', 0),
	('A20220414', '伊藤 太郎', 'いとう たろう', 'user36@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D510', 'E00', 0),
	('A20220418', '鈴木 健', 'すずき たける', 'user5@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D620', 'E00', 0),
	('A20220613', '伊藤 太郎', 'いとう たろう', 'user92@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D400', 'E00', 0),
	('A20220707', '高橋 美咲', 'たかはし みさき', 'user70@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D530', 'E00', 0),
	('A20220822', '田中 太郎', 'たなか たろう', 'user51@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D610', 'E00', 0),
	('A20220908', '佐藤 美咲', 'さとう みさき', 'user75@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D000', 'E00', 0),
	('A20220910', '小林 太郎', 'こばやし たろう', 'user84@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D520', 'E01', 0),
	('A20221023', '渡辺 真央', 'わたなべ まお', 'user116@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D000', 'E00', 0),
	('A20221203', '佐藤 直樹', 'さとう なおき', 'user1@example.com', 'a89980d83cc3ff0cf773f44c2c677410ea99aa2b6f29a71909965c0614942d9cbb6eeb96b323cab10f5a86caf44b929f3a0df6a14c3154e4017a603a0c493b48', 'D410', 'E00', 0),
	('A20221207', '伊藤 彩', 'いとう あや', 'user62@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D100', 'E00', 0),
	('A20221226', '渡辺 大輔', 'わたなべ だいすけ', 'user114@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D734', 'E02', 0),
	('A20230111', '渡辺 彩', 'わたなべ あや', 'user13@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D100', 'E00', 0),
	('A2023011101', '伊藤 優衣', 'いとう ゆい', 'user28@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D740', 'E00', 0),
	('A20230225', '渡辺 彩', 'わたなべ あや', 'user17@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D710', 'E00', 0),
	('A20230226', '鈴木 美咲', 'すずき みさき', 'user56@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D420', 'E00', 0),
	('A20230324', '高橋 大輔', 'たかはし だいすけ', 'user59@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D740', 'E00', 0),
	('A20230419', '小林 健', 'こばやし たける', 'user111@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D300', 'E02', 0),
	('A20230425', '山田 美咲', 'やまだ みさき', 'user100@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D400', 'E00', 0),
	('A20230709', '高橋 優衣', 'たかはし ゆい', 'user52@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D740', 'E00', 0),
	('A20230812', '山田 花子', 'やまだ はなこ', 'user112@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D710', 'E00', 0),
	('A20230905', '加藤 直樹', 'さとう なおき', 'user89@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D740', 'E01', 0),
	('A20230916', '高橋 直樹', 'たかはし なおき', 'user18@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D630', 'E00', 0),
	('A20231205', '小林 花子', 'こばやし はなこ', 'user88@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D530', 'E01', 0),
	('A20231208', '鈴木 大輔', 'すずき だいすけ', 'user29@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D720', 'E00', 0),
	('A20240203', '小林 優衣', 'こばやし ゆい', 'user21@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D710', 'E00', 0),
	('A20240329', '加藤 優衣', 'かとう ゆい', 'user39@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D530', 'E00', 0),
	('A20240409', '佐藤 美咲', 'さとう みさき', 'user12@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D710', 'E00', 0),
	('A20240411', '佐藤 健', 'さとう たける', 'user109@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D712', 'E02', 0),
	('A20240702', '山田 優衣', 'やまだ ゆい', 'user107@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D740', 'E00', 0),
	('A20240704', '山田 花子', 'やまだ はなこ', 'user71@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D400', 'E00', 0),
	('A20240828', '渡辺 彩', 'わたなべ あや', 'user3@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D610', 'E00', 0),
	('A20241112', '渡辺 優衣', 'わたなべ ゆい', 'user60@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D730', 'E00', 0),
	('A20250113', '小林 美咲', 'こばやし みさき', 'user91@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D620', 'E01', 0),
	('A20250307', '佐藤 大輔', 'さとう だいすけ', 'user10@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D100', 'E00', 0),
	('A20250314', '渡辺 彩', 'わたなべ あや', 'user61@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D200', 'E00', 0),
	('A20250415', '佐藤 大輔', 'さとう だいすけ', 'user90@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D400', 'E00', 0),
	('A20250516', '田中 大輔', 'たなか だいすけ', 'user79@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D620', 'E00', 0),
	('A20250729', '中村 美咲', 'なかむら みさき', 'user93@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D630', 'E01', 0),
	('A20250816', '中村 太郎', 'なかむら たろう', 'user85@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D610', 'E01', 0),
	('A20251115', '渡辺 優衣', 'わたなべ ゆい', 'user9@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D530', 'E00', 0),
	('A20251224', '小林 健', 'こばやし たける', 'user20@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D510', 'E00', 0),
	('A20251229', '田中 彩', 'たなか あや', 'user50@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D720', 'E00', 0),
	('A2025122901', '渡辺 花子', 'わたなべ はなこ', 'user102@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D600', 'E02', 0),
	('A20260109', '田中 優衣', 'たなか ゆい', 'user43@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D610', 'E00', 0),
	('A20260112', '高橋 美咲', 'たかはし みさき', 'user81@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D510', 'E01', 0),
	('A20260307', '小林 直樹', 'こばやし なおき', 'user96@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D720', 'E01', 0),
	('A20260315', '中村 健', 'なかむら たける', 'user38@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D610', 'E00', 0),
	('A20260327', '伊藤 一郎', 'いとう いちろう', 'user74@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D610', 'E00', 0),
	('A99999999', 'テストテスト', 'てすとてすと', 'test@test.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D200', 'E03', 0);

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
  CONSTRAINT `FK_failed_logins_employees` FOREIGN KEY (`emp_id`) REFERENCES `employees` (`emp_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- テーブル simpleappapp2.failed_logins: ~3 rows (約) のデータをダンプしています
DELETE FROM `failed_logins`;
INSERT INTO `failed_logins` (`emp_id`, `quiz_attempts`, `password_attempts`, `first_failed_password_at`, `first_failed_quiz_at`, `last_failed_password_at`, `last_failed_quiz_at`, `lock_count`, `locked_until`, `created_at`, `updated_at`) VALUES
	('A00000001', 0, 0, NULL, NULL, NULL, NULL, 0, NULL, '2026-06-16 02:46:31', '2026-06-16 02:46:31'),
	('A20150212', 0, 0, NULL, NULL, NULL, NULL, 0, NULL, '2026-06-16 01:59:34', '2026-06-16 04:41:14'),
	('A20221203', 0, 0, NULL, NULL, NULL, NULL, 0, NULL, '2026-06-16 00:57:20', '2026-06-16 00:57:37');

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
) ENGINE=InnoDB AUTO_INCREMENT=146 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- テーブル simpleappapp2.security_quiz: ~18 rows (約) のデータをダンプしています
DELETE FROM `security_quiz`;
INSERT INTO `security_quiz` (`sq_id`, `emp_id`, `quiz`, `answer`) VALUES
	(128, 'A20221203', '初めて飼ったペットの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(129, 'A20221203', '子どものころに一番よく遊んだ路地や通りの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(130, 'A20221203', '初めて一人で泊まった旅館やホテルの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(131, 'A20170802', '初めて飼ったペットの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(132, 'A20170802', '子どものころに一番よく遊んだ路地や通りの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(133, 'A20170802', '初めて一人で泊まった旅館やホテルの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(134, 'A20190103', '初めて飼ったペットの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(135, 'A20190103', '子どものころに一番よく遊んだ路地や通りの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(136, 'A20190103', '初めて一人で泊まった旅館やホテルの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(137, 'A20160111', '初めて飼ったペットの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(138, 'A20160111', '子どものころに一番よく遊んだ路地や通りの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(139, 'A20160111', '初めて一人で泊まった旅館やホテルの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(140, 'A00000001', '初めて飼ったペットの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(141, 'A00000001', '子どものころに一番よく遊んだ路地や通りの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(142, 'A00000001', '初めて一人で泊まった旅館やホテルの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(143, 'A20150212', '初めて飼ったペットの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(144, 'A20150212', '子どものころに一番よく遊んだ路地や通りの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(145, 'A20150212', '初めて一人で泊まった旅館やホテルの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a');

--  テーブル simpleappapp2.status の構造をダンプしています
DROP TABLE IF EXISTS `status`;
CREATE TABLE IF NOT EXISTS `status` (
  `status_id` int(10) NOT NULL,
  `status_name` varchar(20) NOT NULL,
  PRIMARY KEY (`status_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- テーブル simpleappapp2.status: ~7 rows (約) のデータをダンプしています
DELETE FROM `status`;
INSERT INTO `status` (`status_id`, `status_name`) VALUES
	(1, '未承認'),
	(2, '上長承認'),
	(3, '管理部承認'),
	(4, '社長承認'),
	(5, '完了'),
	(6, '却下'),
	(7, '削除');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
