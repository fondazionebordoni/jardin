SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

DROP DATABASE IF EXISTS jardin;
CREATE DATABASE jardin;

USE jardin;

--
-- Database: `jardin`
--

-- --------------------------------------------------------

--
-- Table structure for table `__system_field`
--

DROP TABLE IF EXISTS `__system_field`;
CREATE TABLE IF NOT EXISTS `__system_field` (
  `id` int(11) NOT NULL,
  `default_header` tinyint(1) NOT NULL,
  `search_grouping` tinyint(1) NOT NULL default '1',
  `id_resultset` int(11) NOT NULL,
  `id_grouping` int(11) NOT NULL default '1',
  `type` varchar(50) NOT NULL default 'VARCHAR',
  `defaultvalue` varchar(255) default NULL,
  PRIMARY KEY  (`id`,`id_resultset`),
  KEY `id_grouping` (`id_grouping`),
  KEY `id_resultset` (`id_resultset`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `__system_fieldinpreference`
--

DROP TABLE IF EXISTS `__system_fieldinpreference`;
CREATE TABLE IF NOT EXISTS `__system_fieldinpreference` (
  `id_headerpreference` int(11) NOT NULL,
  `id_field` int(11) NOT NULL,
  PRIMARY KEY  (`id_headerpreference`,`id_field`),
  KEY `id_field` (`id_field`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `__system_group`
--

DROP TABLE IF EXISTS `__system_group`;
CREATE TABLE IF NOT EXISTS `__system_group` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(256) NOT NULL,
  `status` tinyint(1) NOT NULL default '1',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `__system_grouping`
--

DROP TABLE IF EXISTS `__system_grouping`;
CREATE TABLE IF NOT EXISTS `__system_grouping` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(256) NOT NULL,
  `alias` varchar(256) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `__system_groupwarnings`
--

DROP TABLE IF EXISTS `__system_groupwarnings`;
CREATE TABLE IF NOT EXISTS `__system_groupwarnings` (
  `id` int(11) NOT NULL auto_increment,
  `title` varchar(256) NOT NULL,
  `body` text,
  `date` datetime NOT NULL,
  `type` varchar(5) NOT NULL,
  `id_group` int(11) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `id_group` (`id_group`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `__system_headerpreference`
--

DROP TABLE IF EXISTS `__system_headerpreference`;
CREATE TABLE IF NOT EXISTS `__system_headerpreference` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(256) NOT NULL,
  `id_user` int(11) NOT NULL,
  `rescuedate` datetime default NULL,
  PRIMARY KEY  (`id`),
  KEY `id_user` (`id_user`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `__system_management`
--

DROP TABLE IF EXISTS `__system_management`;
CREATE TABLE IF NOT EXISTS `__system_management` (
  `id` int(11) NOT NULL auto_increment,
  `id_group` int(11) NOT NULL,
  `id_resource` int(11) NOT NULL,
  `readperm` tinyint(1) NOT NULL,
  `deleteperm` tinyint(1) NOT NULL,
  `modifyperm` tinyint(1) NOT NULL,
  `insertperm` tinyint(1) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `id_group` (`id_group`),
  KEY `id_resource` (`id_resource`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `__system_messages`
--

DROP TABLE IF EXISTS `__system_messages`;
CREATE TABLE IF NOT EXISTS `__system_messages` (
  `id` int(11) NOT NULL auto_increment,
  `title` varchar(256) NOT NULL,
  `body` text,
  `date` datetime NOT NULL,
  `type` varchar(5) NOT NULL,
  `sender` int(11) NOT NULL,
  `recipient` int(11) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `sender` (`sender`),
  KEY `recipient` (`recipient`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `__system_resource`
--

DROP TABLE IF EXISTS `__system_resource`;
CREATE TABLE IF NOT EXISTS `__system_resource` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(40) NOT NULL,
  `gestible` tinyint(1) NOT NULL default '1',
  `alias` varchar(40) default NULL,
  `note` text,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `__system_resultset`
--

DROP TABLE IF EXISTS `__system_resultset`;
CREATE TABLE IF NOT EXISTS `__system_resultset` (
  `id` int(11) NOT NULL,
  `statement` text NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `__system_user`
--

DROP TABLE IF EXISTS `__system_user`;
CREATE TABLE IF NOT EXISTS `__system_user` (
  `id` int(11) NOT NULL auto_increment,
  `username` varchar(256) NOT NULL,
  `password` varchar(256) NOT NULL,
  `name` varchar(256) default '',
  `surname` varchar(256) default '',
  `email` varchar(256) default '',
  `office` varchar(256) default '',
  `telephone` varchar(20) NOT NULL,
  `status` tinyint(1) NOT NULL default '1',
  `lastlogintime` datetime default NULL,
  `logincount` smallint(6) NOT NULL default '0',
  `id_group` int(11) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `id_group` (`id_group`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `__system_userwarnings`
--

DROP TABLE IF EXISTS `__system_userwarnings`;
CREATE TABLE IF NOT EXISTS `__system_userwarnings` (
  `id` int(11) NOT NULL auto_increment,
  `title` varchar(256) NOT NULL,
  `body` text,
  `date` datetime NOT NULL,
  `type` varchar(5) NOT NULL,
  `id_user` int(11) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `id_user` (`id_user`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Constraints for table `__system_field`
--
ALTER TABLE `__system_field`
  ADD CONSTRAINT `__system_field_ibfk_1` FOREIGN KEY (`id`) REFERENCES `__system_resource` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `__system_field_ibfk_2` FOREIGN KEY (`id_resultset`) REFERENCES `__system_resource` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `__system_field_ibfk_3` FOREIGN KEY (`id_grouping`) REFERENCES `__system_grouping` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `__system_fieldinpreference`
--
ALTER TABLE `__system_fieldinpreference`
  ADD CONSTRAINT `__system_fieldinpreference_ibfk_1` FOREIGN KEY (`id_headerpreference`) REFERENCES `__system_headerpreference` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `__system_fieldinpreference_ibfk_2` FOREIGN KEY (`id_field`) REFERENCES `__system_field` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `__system_groupwarnings`
--
ALTER TABLE `__system_groupwarnings`
  ADD CONSTRAINT `__system_groupwarnings_ibfk_1` FOREIGN KEY (`id_group`) REFERENCES `__system_group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
  
--
-- Constraints for table `__system_headerpreference`
--
ALTER TABLE `__system_headerpreference`
  ADD CONSTRAINT `__system_headerpreference_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `__system_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `__system_management`
--
ALTER TABLE `__system_management`
  ADD CONSTRAINT `__system_management_ibfk_1` FOREIGN KEY (`id_group`) REFERENCES `__system_group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `__system_management_ibfk_2` FOREIGN KEY (`id_resource`) REFERENCES `__system_resource` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `__system_groupwarnings`
--
ALTER TABLE `__system_messages`
  ADD CONSTRAINT `__system_messages_ibfk_1` FOREIGN KEY (`sender`) REFERENCES `__system_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `__system_messages_ibfk_2` FOREIGN KEY (`recipient`) REFERENCES `__system_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `__system_resultset`
--
ALTER TABLE `__system_resultset`
  ADD CONSTRAINT `__system_resultset_ibfk_1` FOREIGN KEY (`id`) REFERENCES `__system_resource` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `__system_user`
--
ALTER TABLE `__system_user`
  ADD CONSTRAINT `__system_user_ibfk_1` FOREIGN KEY (`id_group`) REFERENCES `__system_group` (`id`) ON DELETE SET NULL;
