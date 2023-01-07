
ALTER TABLE `auth_server`.`sys_user`
    MODIFY COLUMN `status` int(11) NULL DEFAULT 1 COMMENT '1启用，0停用' AFTER `password`;

ALTER TABLE `auth_server`.`sys_user`
    CHANGE COLUMN `user_name` `username` varchar(32)  NOT NULL COMMENT '1启用，0停用' AFTER `id` ;