
CREATE TABLE IF not EXISTS `oauth_refresh_token` (
       `token_id` varchar(255) DEFAULT NULL,
       `token` mediumblob,
       `authentication` mediumblob
) ENGINE=InnoDB DEFAULT CHARSET=utf8 comment '刷新令牌表';

CREATE TABLE IF not EXISTS `oauth_access_token` (
  `token_id` varchar(255) DEFAULT NULL,
  `token` mediumblob,
  `authentication_id` varchar(255) NOT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `client_id` varchar(255) DEFAULT NULL,
  `authentication` mediumblob,
  `refresh_token` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`authentication_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF not EXISTS `oauth_client_details` (
    `client_id` varchar(256) COLLATE utf8_unicode_ci NOT NULL,
    `resource_ids` varchar(256) COLLATE utf8_unicode_ci DEFAULT NULL,
    `client_secret` varchar(256) COLLATE utf8_unicode_ci DEFAULT NULL,
    `client_secret_mw` varchar(256) COLLATE utf8_unicode_ci DEFAULT NULL,
    `scope` varchar(256) COLLATE utf8_unicode_ci DEFAULT NULL,
    `authorized_grant_types` varchar(256) COLLATE utf8_unicode_ci DEFAULT NULL,
    `web_server_redirect_uri` varchar(256) COLLATE utf8_unicode_ci DEFAULT NULL,
    `authorities` varchar(256) COLLATE utf8_unicode_ci DEFAULT NULL,
    `access_token_validity` int(11) DEFAULT NULL,
    `refresh_token_validity` int(11) DEFAULT NULL,
    `additional_information` varchar(4096) COLLATE utf8_unicode_ci DEFAULT NULL,
    `autoapprove` varchar(256) COLLATE utf8_unicode_ci DEFAULT NULL,
    PRIMARY KEY (`client_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci comment '客户端记录表';

CREATE TABLE IF not EXISTS `oauth_approvals` (
    `userId` varchar(255) DEFAULT NULL,
    `clientId` varchar(255) DEFAULT NULL,
    `scope` varchar(255) DEFAULT NULL,
    `status` varchar(10) DEFAULT NULL,
    `expiresAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `lastModifiedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 comment '授权记录表';

CREATE TABLE IF not EXISTS `oauth_code` (
      `code` varchar(255) DEFAULT NULL,
      `authentication` mediumblob
) ENGINE=InnoDB DEFAULT CHARSET=utf8 comment '授权码记录表';

CREATE TABLE IF not EXISTS `oauth_client_token` (
  `token_id` varchar(255) DEFAULT NULL,
  `token` mediumblob,
  `authentication_id` varchar(255) NOT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `client_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`authentication_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF not EXISTS `t_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(32) NOT NULL,
  `password` varchar(128) NOT NULL,
  `phone` varchar(128) default NULL,
  `e_mail` varchar(128) default NULL,
  `status` int(11) not null DEFAULT 1 comment '1 启用，0禁用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


CREATE TABLE IF not EXISTS `t_client_resource` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `client_id` varchar(32) NOT NULL COMMENT '客户端ID',
    `resource_server_url` varchar(256) DEFAULT NULL COMMENT '资源服务地址：http或https开头，如：http://127.0.0.1:8080',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COMMENT='客户端连接方式信息表';

