DROP TABLE IF EXISTS `tweet`;

CREATE TABLE `tweet` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tweet_id` BIGINT(20) NOT NULL,
  `user_name` varchar(50) NOT NULL,
  `tweet` varchar(300) NOT NULL,
  `tweet_url` varchar(100) NOT NULL,
  `tweet_at` datetime NOT NULL,
  `created_at` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;