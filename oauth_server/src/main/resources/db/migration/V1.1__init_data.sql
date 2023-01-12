
-- 初始化账号，admin 密码 123456
INSERT INTO t_user (username, password, phone, e_mail, status) VALUES('admin', '$2a$10$j3fuoe94Nyn6W3KFfw6en.paPTf1C4mUSosO4qh33efErALI1HRli', '12345678910', 'admin@admin.com', 1);

-- 初始化客户端，admin 密码 112233
INSERT INTO oauth_client_details (client_id, resource_ids, client_secret, `scope`, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove)
VALUES('admin', NULL, '$2a$10$3vYJGuouBujHv8SkHApjneWtLOiGAHv14hVpClsdqTveqiDV78xKu', 'all', 'password,refresh_token,authorization_code,sms_code', 'http://www.baidu.com', NULL, 3600, 864000, NULL, 'false');

INSERT INTO oauth_client_details (client_id, resource_ids, client_secret, `scope`, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove)
VALUES('robot1', NULL, '$2a$10$3vYJGuouBujHv8SkHApjneWtLOiGAHv14hVpClsdqTveqiDV78xKu', 'all', 'password,refresh_token,authorization_code,sms_code', 'http://www.baidu.com', NULL, 3600, 864000, NULL, 'false');
