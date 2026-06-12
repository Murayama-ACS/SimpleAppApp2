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

-- テーブル simpleappapp2.applications: ~18 rows (約) のデータをダンプしています
DELETE FROM `applications`;
INSERT INTO `applications` (`apct_id`, `emp_id`, `content`, `type`, `method`, `amount`, `reason`, `remark`, `urgent`, `status_id`, `create_date`, `update_date`, `is_deleted`) VALUES
	('AP0611145608', 'A20240409', 'test1', 'その他', '立替払い（現金手渡し）', 10000, 'test1', 'test1', '緊急', 2, '2026-06-11 14:56:08', '2026-06-11 17:00:52', 0),
	('AP0611145708', 'A20230225', 'test2', 'その他', '立替払い (給与振込)', 3574756, 'test2', 'test2', '緊急', 2, '2026-06-11 14:57:08', '2026-06-11 17:00:51', 0),
	('AP0611145756', 'A20240203', 'test3', 'その他', '会社直接支払い', 400000, 'test3', 'test3', '通常', 2, '2026-06-11 14:57:56', '2026-06-11 17:00:49', 0),
	('AP0611145841', 'A20150909', 'test4', 'その他', '立替払い (給与振込)', 5000543, 'test4', 'test4', '通常', 2, '2026-06-11 14:58:41', '2026-06-11 17:00:48', 0),
	('AP0611145925', 'A20220317', 'test5', 'その他', '立替払い (給与振込)', 5677777, 'test5', 'test5', '緊急', 2, '2026-06-11 14:59:25', '2026-06-11 17:00:46', 0),
	('AP0611150013', 'A20160108', 'test6', 'その他', '会社直接支払い', 3000000, 'test6', 'test6', '緊急', 2, '2026-06-11 15:00:13', '2026-06-11 17:00:45', 0),
	('AP0611150059', 'A20190429', 'test7', 'その他', '会社直接支払い', 700000, 'test7', 'test7', '緊急', 2, '2026-06-11 15:00:59', '2026-06-11 17:00:44', 0),
	('AP0611150153', 'A20230812', 'test8', 'その他', '立替払い（現金手渡し）', 77777777, 'test8', 'test8', '緊急', 2, '2026-06-11 15:01:53', '2026-06-11 17:00:42', 0),
	('AP0612105413', 'A00000001', 'test', '研修参加申請', '会社直接支払い', 1000000, 'test', 'test', '緊急', 2, '2026-06-12 10:54:13', '2026-06-12 10:54:13', 0),
	('AP0612112518', 'A00000001', 'テストテストテストテストテストテストテストテストテストテストテストテスト', '研修参加申請', '立替払い (給与振込)', 11111111, 'テストテストテストテストテストテスト', 'テストテストテストテストテストテストテストテストテスト', '緊急', 2, '2026-06-12 11:25:18', '2026-06-12 11:25:18', 0),
	('AP0612112721', 'A00000001', 'tsdasdvsavsvdvdv', '備品購入申請', '立替払い (給与振込)', 11111111, 'vdffgsgsav', 'fsdfefs', '緊急', 2, '2026-06-12 11:27:21', '2026-06-12 11:27:21', 0),
	('AP0612112825', 'A00000001', 'AAAAAABBBBBBBBBCCCCCCCCCC', '出張申請', '立替払い（現金手渡し）', 11111111, 'aaaaaabbbbbbbccccccccdddddddddeeeeee', 'ffffffffffddddddddsssssssssssaaaa', '緊急', 2, '2026-06-12 11:28:25', '2026-06-12 11:28:25', 0),
	('AP0612113138', 'A00000001', 'aaaaaaaaaaaaaaaaaaaaa', '備品購入申請', '立替払い（現金手渡し）', 11111111, 'aaaaaaaaaaaaa', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', '緊急', 2, '2026-06-12 11:31:38', '2026-06-12 11:31:38', 0),
	('AP0612113146', 'A00000001', 'aaaaaaaaaaaaaaaaaaaaa', '備品購入申請', '立替払い（現金手渡し）', 11111111, 'aaaaaaaaaaaaa', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', '緊急', 2, '2026-06-12 11:31:46', '2026-06-12 11:31:46', 0),
	('AP0612113536', 'A00000001', 'aaaaaaaaaaaaaaa', '備品購入申請', '立替払い（現金手渡し）', 1111111111, 'aaaaaaaaaaaaaaaaaaaaaa', 'aaaaaaaaaaaaaaaaaa', '緊急', 2, '2026-06-12 11:35:36', '2026-06-12 11:35:36', 0),
	('AP0612113713', 'A00000001', '111111111', '研修参加申請', '立替払い (給与振込)', 1111111111, '111111111', '1111111111', '緊急', 2, '2026-06-12 11:37:13', '2026-06-12 11:37:13', 0),
	('AP0612113907', 'A00000001', '111111111111', 'その他', '立替払い（現金手渡し）', 1111111111, '111111111111', '11111111111', '緊急', 2, '2026-06-12 11:39:07', '2026-06-12 11:39:07', 0),
	('AP0612114110', 'A00000001', '111111', '出張申請', '立替払い（現金手渡し）', 11111, '111111111', '', '通常', 2, '2026-06-12 11:41:10', '2026-06-12 11:41:10', 0);

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

-- テーブル simpleappapp2.approvals: ~43 rows (約) のデータをダンプしています
DELETE FROM `approvals`;
INSERT INTO `approvals` (`approval_id`, `apct_id`, `emp_id`, `comment`, `time`, `status_id`) VALUES
	('APV260611161047882', 'AP0611150153', 'A20160108', '', '2026-06-11 16:10:47', 2),
	('APV260611161049598', 'AP0611150059', 'A20160108', '', '2026-06-11 16:10:49', 2),
	('APV260611161050929', 'AP0611145925', 'A20160108', '', '2026-06-11 16:10:50', 2),
	('APV260611161052562', 'AP0611145841', 'A20160108', '', '2026-06-11 16:10:52', 2),
	('APV260611161054014', 'AP0611145756', 'A20160108', '', '2026-06-11 16:10:54', 2),
	('APV260611161055986', 'AP0611145708', 'A20160108', '', '2026-06-11 16:10:55', 2),
	('APV260611161057643', 'AP0611145608', 'A20160108', '', '2026-06-11 16:10:57', 2),
	('APV260611163205700', 'AP0611150153', 'A20190103', '', '2026-06-11 16:32:05', 2),
	('APV260611163207297', 'AP0611150153', 'A20190103', '', '2026-06-11 16:32:07', 2),
	('APV260611163233007', 'AP0611150153', 'A20190103', '', '2026-06-11 16:32:33', 2),
	('APV260611163233019', 'AP0611150153', 'A20190103', '', '2026-06-11 16:32:33', 2),
	('APV260611164249750', 'AP0611150153', 'A20190103', '', '2026-06-11 16:42:49', 2),
	('APV260611164253950', 'AP0611150059', 'A20190103', '', '2026-06-11 16:42:53', 2),
	('APV260611164321153', 'AP0611150059', 'A20190103', '', '2026-06-11 16:43:21', 2),
	('APV260611164746912', 'AP0611150153', 'A20190103', '', '2026-06-11 16:47:46', 3),
	('APV260611164749716', 'AP0611150059', 'A20190103', '', '2026-06-11 16:47:49', 3),
	('APV260611164751155', 'AP0611145925', 'A20190103', '', '2026-06-11 16:47:51', 3),
	('APV260611164752744', 'AP0611145841', 'A20190103', '', '2026-06-11 16:47:52', 3),
	('APV260611164754474', 'AP0611145756', 'A20190103', '', '2026-06-11 16:47:54', 3),
	('APV260611164756028', 'AP0611145708', 'A20190103', '', '2026-06-11 16:47:56', 3),
	('APV260611164757548', 'AP0611145608', 'A20190103', '', '2026-06-11 16:47:57', 3),
	('APV260611165456773', 'AP0611150153', 'A20160108', '', '2026-06-11 16:54:56', 2),
	('APV260611165458413', 'AP0611150059', 'A20160108', '', '2026-06-11 16:54:58', 2),
	('APV260611165500161', 'AP0611145925', 'A20160108', '', '2026-06-11 16:55:00', 2),
	('APV260611165502225', 'AP0611145841', 'A20160108', '', '2026-06-11 16:55:02', 2),
	('APV260611165503883', 'AP0611145756', 'A20160108', '', '2026-06-11 16:55:03', 2),
	('APV260611165505398', 'AP0611145708', 'A20160108', '', '2026-06-11 16:55:05', 2),
	('APV260611165506817', 'AP0611145608', 'A20160108', '', '2026-06-11 16:55:06', 2),
	('APV260611165847240', 'AP0611150153', 'A20190103', '', '2026-06-11 16:58:47', 3),
	('APV260611165848957', 'AP0611150059', 'A20190103', '', '2026-06-11 16:58:48', 3),
	('APV260611165851200', 'AP0611145925', 'A20190103', '', '2026-06-11 16:58:51', 3),
	('APV260611165852672', 'AP0611145841', 'A20190103', '', '2026-06-11 16:58:52', 3),
	('APV260611165854088', 'AP0611145756', 'A20190103', '', '2026-06-11 16:58:54', 3),
	('APV260611165855524', 'AP0611145708', 'A20190103', '', '2026-06-11 16:58:55', 3),
	('APV260611165857190', 'AP0611145608', 'A20190103', '', '2026-06-11 16:58:57', 5),
	('APV260611170042440', 'AP0611150153', 'A20190103', '', '2026-06-11 17:00:42', 3),
	('APV260611170044024', 'AP0611150059', 'A20190103', '', '2026-06-11 17:00:44', 3),
	('APV260611170045355', 'AP0611150013', 'A20190103', '', '2026-06-11 17:00:45', 3),
	('APV260611170046763', 'AP0611145925', 'A20190103', '', '2026-06-11 17:00:46', 3),
	('APV260611170048159', 'AP0611145841', 'A20190103', '', '2026-06-11 17:00:48', 3),
	('APV260611170049727', 'AP0611145756', 'A20190103', '', '2026-06-11 17:00:49', 3),
	('APV260611170051502', 'AP0611145708', 'A20190103', '', '2026-06-11 17:00:51', 5),
	('APV260611170052849', 'AP0611145608', 'A20190103', '', '2026-06-11 17:00:52', 5);

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
	('A00000001', '山田 太一', 'ceo@example.com', 'a89980d83cc3ff0cf773f44c2c677410ea99aa2b6f29a71909965c0614942d9cbb6eeb96b323cab10f5a86caf44b929f3a0df6a14c3154e4017a603a0c493b48', 'D000', 'E04', 0),
	('A20150207', '高橋 優衣', 'user77@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D100', 'E00', 0),
	('A20150212', '小林 直樹', 'user113@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D400', 'E02', 0),
	('A20150624', '山田 彩', 'user48@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D730', 'E00', 0),
	('A20150629', '高橋 優衣', 'user72@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D000', 'E02', 0),
	('A20150807', '山田 優衣', 'user63@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D200', 'E00', 0),
	('A20150909', '田中 健', 'user24@example.com', 'a89980d83cc3ff0cf773f44c2c677410ea99aa2b6f29a71909965c0614942d9cbb6eeb96b323cab10f5a86caf44b929f3a0df6a14c3154e4017a603a0c493b48', 'D710', 'E00', 0),
	('A20160108', '山田 真央', 'user64@example.com', 'a89980d83cc3ff0cf773f44c2c677410ea99aa2b6f29a71909965c0614942d9cbb6eeb96b323cab10f5a86caf44b929f3a0df6a14c3154e4017a603a0c493b48', 'D710', 'E01', 0),
	('A20160111', '田中 太郎', 'user78@example.com', 'a89980d83cc3ff0cf773f44c2c677410ea99aa2b6f29a71909965c0614942d9cbb6eeb96b323cab10f5a86caf44b929f3a0df6a14c3154e4017a603a0c493b48', 'D200', 'E02', 0),
	('A20160611', '渡辺 優衣', 'user19@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D620', 'E00', 0),
	('A20160621', '小林 一郎', 'user55@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D730', 'E00', 0),
	('A20160709', '鈴木 健', 'user45@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D720', 'E00', 0),
	('A20160813', '加藤 美咲', 'user108@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D530', 'E00', 0),
	('A20160828', '佐藤 優衣', 'user40@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D520', 'E00', 0),
	('A20161124', '小林 彩', 'user76@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D540', 'E00', 0),
	('A20161215', '中村 真央', 'user33@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D720', 'E00', 0),
	('A20161216', '山田 健', 'user67@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D510', 'E00', 0),
	('A20161217', '高橋 真央', 'user106@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D720', 'E00', 0),
	('A20170115', '加藤 花子', 'user44@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D530', 'E00', 0),
	('A20170330', '高橋 太郎', 'user7@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D520', 'E00', 0),
	('A20170407', '伊藤 大輔', 'user57@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D630', 'E00', 0),
	('A20170626', '渡辺 美咲', 'user68@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D630', 'E00', 0),
	('A20170802', '伊藤 彩', 'user82@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D410', 'E01', 0),
	('A20170824', '中村 大輔', 'user25@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D620', 'E00', 0),
	('A20171125', '高橋 健', 'user16@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D610', 'E00', 0),
	('A20171208', '山田 大輔', 'user14@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D510', 'E00', 0),
	('A20180119', '中村 直樹', 'user69@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D540', 'E00', 0),
	('A20180407', '渡辺 健', 'user99@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D730', 'E01', 0),
	('A20180504', '加藤 太郎', 'user37@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D730', 'E00', 0),
	('A20180707', '加藤 真央', 'user95@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D540', 'E01', 0),
	('A20180828', '山田 健', 'user105@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D740', 'E00', 0),
	('A20180906', '中村 優衣', 'user47@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D720', 'E00', 0),
	('A20180926', '鈴木 健', 'user117@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D700', 'E03', 0),
	('A20181217', '加藤 一郎', 'user94@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D740', 'E00', 0),
	('A20181223', '高橋 健', 'user34@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D540', 'E00', 0),
	('A20190103', '渡辺 一郎', 'user2@example.com', 'a89980d83cc3ff0cf773f44c2c677410ea99aa2b6f29a71909965c0614942d9cbb6eeb96b323cab10f5a86caf44b929f3a0df6a14c3154e4017a603a0c493b48', 'D100', 'E02', 0),
	('A20190106', '小林 大輔', 'user15@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D540', 'E00', 0),
	('A20190113', '伊藤 健', 'user8@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D620', 'E00', 0),
	('A20190119', '小林 彩', 'user103@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D510', 'E00', 0),
	('A20190211', '伊藤 直樹', 'user66@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D620', 'E00', 0),
	('A20190227', '田中 花子', 'user104@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D540', 'E00', 0),
	('A20190305', '加藤 真央', 'user23@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D740', 'E00', 0),
	('A20190429', '加藤 彩', 'user83@example.com', 'a89980d83cc3ff0cf773f44c2c677410ea99aa2b6f29a71909965c0614942d9cbb6eeb96b323cab10f5a86caf44b929f3a0df6a14c3154e4017a603a0c493b48', 'D710', 'E00', 0),
	('A20190524', '加藤 健', 'user119@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D630', 'E00', 0),
	('A20190725', '高橋 美咲', 'user42@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D420', 'E00', 0),
	('A20190814', '田中 直樹', 'user26@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D630', 'E00', 0),
	('A20190907', '伊藤 優衣', 'user115@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D500', 'E02', 0),
	('A20190908', '山田 直樹', 'user110@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D510', 'E00', 0),
	('A20191126', '鈴木 美咲', 'user73@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D720', 'E00', 0),
	('A20200207', '鈴木 健', 'user35@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D740', 'E00', 0),
	('A20200313', '田中 太郎', 'user87@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D420', 'E01', 0),
	('A20200403', '加藤 一郎', 'user58@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D730', 'E00', 0),
	('A20200413', '渡辺 一郎', 'user46@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D730', 'E00', 0),
	('A20200418', '高橋 大輔', 'user97@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D740', 'E00', 0),
	('A20200519', '田中 優衣', 'user54@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D520', 'E00', 0),
	('A20200528', '中村 一郎', 'user4@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D610', 'E00', 0),
	('A20200717', '佐藤 太郎', 'user80@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D730', 'E00', 0),
	('A20200720', '田中 優衣', 'user98@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D540', 'E00', 0),
	('A20200913', '高橋 健', 'user53@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D730', 'E00', 0),
	('A20200923', '伊藤 美咲', 'user22@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D520', 'E00', 0),
	('A20210330', '高橋 直樹', 'user41@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D420', 'E00', 0),
	('A20210409', '加藤 一郎', 'user49@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D630', 'E00', 0),
	('A20210613', '高橋 真央', 'user86@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D520', 'E00', 0),
	('A20210617', '伊藤 大輔', 'user32@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D720', 'E00', 0),
	('A20210720', '佐藤 優衣', 'user11@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D630', 'E00', 0),
	('A20210904', '伊藤 彩', 'user65@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D530', 'E00', 0),
	('A20210930', '高橋 優衣', 'user118@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D400', 'E00', 0),
	('A20211108', '田中 直樹', 'user30@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D410', 'E00', 0),
	('A20211116', '田中 真央', 'user101@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D200', 'E00', 0),
	('A20220113', '佐藤 健', 'user31@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D410', 'E00', 0),
	('A20220317', '鈴木 彩', 'user27@example.com', 'a89980d83cc3ff0cf773f44c2c677410ea99aa2b6f29a71909965c0614942d9cbb6eeb96b323cab10f5a86caf44b929f3a0df6a14c3154e4017a603a0c493b48', 'D710', 'E00', 0),
	('A20220320', '田中 優衣', 'user6@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D530', 'E00', 0),
	('A20220414', '伊藤 太郎', 'user36@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D510', 'E00', 0),
	('A20220418', '鈴木 健', 'user5@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D620', 'E00', 0),
	('A20220613', '伊藤 太郎', 'user92@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D400', 'E00', 0),
	('A20220707', '高橋 美咲', 'user70@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D530', 'E00', 0),
	('A20220822', '田中 太郎', 'user51@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D610', 'E00', 0),
	('A20220908', '佐藤 美咲', 'user75@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D000', 'E00', 0),
	('A20220910', '小林 太郎', 'user84@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D520', 'E01', 0),
	('A20221023', '渡辺 真央', 'user116@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D000', 'E00', 0),
	('A20221203', '佐藤 直樹', 'user1@example.com', 'a89980d83cc3ff0cf773f44c2c677410ea99aa2b6f29a71909965c0614942d9cbb6eeb96b323cab10f5a86caf44b929f3a0df6a14c3154e4017a603a0c493b48', 'D410', 'E00', 0),
	('A20221207', '伊藤 彩', 'user62@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D100', 'E00', 0),
	('A20221226', '渡辺 大輔', 'user114@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D734', 'E02', 0),
	('A20230111', '渡辺 彩', 'user13@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D100', 'E00', 0),
	('A2023011101', '伊藤 優衣', 'user28@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D740', 'E00', 0),
	('A20230225', '渡辺 彩', 'user17@example.com', 'a89980d83cc3ff0cf773f44c2c677410ea99aa2b6f29a71909965c0614942d9cbb6eeb96b323cab10f5a86caf44b929f3a0df6a14c3154e4017a603a0c493b48', 'D710', 'E00', 0),
	('A20230226', '鈴木 美咲', 'user56@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D420', 'E00', 0),
	('A20230324', '高橋 大輔', 'user59@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D740', 'E00', 0),
	('A20230419', '小林 健', 'user111@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D300', 'E02', 0),
	('A20230425', '山田 美咲', 'user100@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D400', 'E00', 0),
	('A20230709', '高橋 優衣', 'user52@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D740', 'E00', 0),
	('A20230812', '山田 花子', 'user112@example.com', 'a89980d83cc3ff0cf773f44c2c677410ea99aa2b6f29a71909965c0614942d9cbb6eeb96b323cab10f5a86caf44b929f3a0df6a14c3154e4017a603a0c493b48', 'D710', 'E00', 0),
	('A20230905', '加藤 直樹', 'user89@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D740', 'E01', 0),
	('A20230916', '高橋 直樹', 'user18@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D630', 'E00', 0),
	('A20231205', '小林 花子', 'user88@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D530', 'E01', 0),
	('A20231208', '鈴木 大輔', 'user29@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D720', 'E00', 0),
	('A20240203', '小林 優衣', 'user21@example.com', 'a89980d83cc3ff0cf773f44c2c677410ea99aa2b6f29a71909965c0614942d9cbb6eeb96b323cab10f5a86caf44b929f3a0df6a14c3154e4017a603a0c493b48', 'D710', 'E00', 0),
	('A20240329', '加藤 優衣', 'user39@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D530', 'E00', 0),
	('A20240409', '佐藤 美咲', 'user12@example.com', 'a89980d83cc3ff0cf773f44c2c677410ea99aa2b6f29a71909965c0614942d9cbb6eeb96b323cab10f5a86caf44b929f3a0df6a14c3154e4017a603a0c493b48', 'D710', 'E00', 0),
	('A20240411', '佐藤 健', 'user109@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D712', 'E02', 0),
	('A20240702', '山田 優衣', 'user107@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D740', 'E00', 0),
	('A20240704', '山田 花子', 'user71@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D400', 'E00', 0),
	('A20240828', '渡辺 彩', 'user3@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D610', 'E00', 0),
	('A20241112', '渡辺 優衣', 'user60@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D730', 'E00', 0),
	('A20250113', '小林 美咲', 'user91@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D620', 'E01', 0),
	('A20250307', '佐藤 大輔', 'user10@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D100', 'E00', 0),
	('A20250314', '渡辺 彩', 'user61@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D200', 'E00', 0),
	('A20250415', '佐藤 大輔', 'user90@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D400', 'E00', 0),
	('A20250516', '田中 大輔', 'user79@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D620', 'E00', 0),
	('A20250729', '中村 美咲', 'user93@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D630', 'E01', 0),
	('A20250816', '中村 太郎', 'user85@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D610', 'E01', 0),
	('A20251115', '渡辺 優衣', 'user9@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D530', 'E00', 0),
	('A20251224', '小林 健', 'user20@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D510', 'E00', 0),
	('A20251229', '田中 彩', 'user50@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D720', 'E00', 0),
	('A2025122901', '渡辺 花子', 'user102@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D600', 'E02', 0),
	('A20260109', '田中 優衣', 'user43@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D610', 'E00', 0),
	('A20260112', '高橋 美咲', 'user81@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D510', 'E01', 0),
	('A20260307', '小林 直樹', 'user96@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D720', 'E01', 0),
	('A20260315', '中村 健', 'user38@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D610', 'E00', 0),
	('A20260327', '伊藤 一郎', 'user74@example.com', 'e396816ae3b6367fe0384bdeaea3d8f3bb692e0869724f1fb2aabb4b93af3c8ab86c57d044aaa238b3bdd2b18e3ee9f26b55180997b93cf7ee11d50c225eb0ac', 'D610', 'E00', 0);

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

-- テーブル simpleappapp2.failed_logins: ~2 rows (約) のデータをダンプしています
DELETE FROM `failed_logins`;
INSERT INTO `failed_logins` (`emp_id`, `quiz_attempts`, `password_attempts`, `first_failed_password_at`, `first_failed_quiz_at`, `last_failed_password_at`, `last_failed_quiz_at`, `lock_count`, `locked_until`, `created_at`, `updated_at`) VALUES
	('A00000001', 0, 0, NULL, NULL, NULL, NULL, 0, NULL, '2026-06-11 02:15:31', '2026-06-11 04:28:00'),
	('A20190103', 0, 0, NULL, NULL, NULL, NULL, 0, NULL, '2026-06-11 07:55:39', '2026-06-11 07:55:39');

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
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- テーブル simpleappapp2.security_quiz: ~37 rows (約) のデータをダンプしています
DELETE FROM `security_quiz`;
INSERT INTO `security_quiz` (`sq_id`, `emp_id`, `quiz`, `answer`) VALUES
	(25, 'A00000001', '初めて飼ったペットの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(26, 'A00000001', '子どものころに一番よく遊んだ路地や通りの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(27, 'A00000001', '初めて一人で泊まった旅館やホテルの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(28, 'A00000001', '学生時代に仲間から呼ばれていたあだ名は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(29, 'A20221203', '初めて飼ったペットの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(30, 'A20221203', '子どものころに一番よく遊んだ路地や通りの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(31, 'A20221203', '初めて一人で泊まった旅館やホテルの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(32, 'A20240409', '初めて飼ったペットの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(33, 'A20240409', '子どものころに一番よく遊んだ路地や通りの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(34, 'A20240409', '初めて一人で泊まった旅館やホテルの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(35, 'A20230225', '初めて飼ったペットの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(36, 'A20230225', '子どものころに一番よく遊んだ路地や通りの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(37, 'A20230225', '初めて一人で泊まった旅館やホテルの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(38, 'A20240203', '初めて飼ったペットの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(39, 'A20240203', '子どものころに一番よく遊んだ路地や通りの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(40, 'A20240203', '初めて一人で泊まった旅館やホテルの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(41, 'A20150909', '初めて飼ったペットの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(42, 'A20150909', '子どものころに一番よく遊んだ路地や通りの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(43, 'A20150909', '初めて一人で泊まった旅館やホテルの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(44, 'A20220317', '初めて飼ったペットの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(45, 'A20220317', '子どものころに一番よく遊んだ路地や通りの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(46, 'A20220317', '初めて一人で泊まった旅館やホテルの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(47, 'A20160108', '初めて飼ったペットの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(48, 'A20160108', '子どものころに一番よく遊んだ路地や通りの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(49, 'A20160108', '初めて一人で泊まった旅館やホテルの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(50, 'A20190429', '初めて飼ったペットの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(51, 'A20190429', '子どものころに一番よく遊んだ路地や通りの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(52, 'A20190429', '初めて一人で泊まった旅館やホテルの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(53, 'A20230812', '初めて飼ったペットの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(54, 'A20230812', '子どものころに一番よく遊んだ路地や通りの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(55, 'A20230812', '初めて一人で泊まった旅館やホテルの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(56, 'A20190103', '初めて飼ったペットの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(57, 'A20190103', '子どものころに一番よく遊んだ路地や通りの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(58, 'A20190103', '初めて一人で泊まった旅館やホテルの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(59, 'A20160111', '初めて飼ったペットの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(60, 'A20160111', '子どものころに一番よく遊んだ路地や通りの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a'),
	(61, 'A20160111', '初めて一人で泊まった旅館やホテルの名前は？', '4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a');

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
