SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for serialgroup
-- ----------------------------
DROP TABLE IF EXISTS `serialgroup`;
CREATE TABLE `serialgroup` (
  `name` varchar(32) NOT NULL,
  `ver` int(11) NOT NULL DEFAULT '1',
  `stat` int(11) NOT NULL DEFAULT '1',
  `min` bigint(20) NOT NULL DEFAULT '0',
  `max` bigint(20) NOT NULL DEFAULT '1',
  `part` int(11) NOT NULL DEFAULT '32',
  `step` int(11) NOT NULL DEFAULT '10000',
  `upid` int(11) NOT NULL,
  `uptime` bigint(20) NOT NULL DEFAULT '1',
  `tscr` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`name`,`ver`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8

-- ----------------------------
-- Table structure for serialpart
-- ----------------------------
DROP TABLE IF EXISTS `serialpart`;
CREATE TABLE `serialpart` (
  `name` varchar(32) NOT NULL,
  `ver` int(11) NOT NULL DEFAULT '1',
  `part` int(11) NOT NULL DEFAULT '1',
  `stat` tinyint(4) NOT NULL DEFAULT '1',
  `min` bigint(20) NOT NULL DEFAULT '0',
  `max` bigint(20) NOT NULL DEFAULT '1',
  `used` bigint(20) NOT NULL,
  `tscr` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`name`,`ver`,`part`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8

-- ----------------------------
-- Table structure for serialused
-- ----------------------------
DROP TABLE IF EXISTS `serialused`;
CREATE TABLE `serialused` (
  `name` varchar(32) NOT NULL,
  `ver` int(11) NOT NULL DEFAULT '1',
  `part` int(11) NOT NULL DEFAULT '1',
  `upos` bigint(20) NOT NULL DEFAULT '1',
  `tsup` bigint(20) NOT NULL DEFAULT '0',
  `tscr` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`name`,`ver`,`part`,`upos`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8