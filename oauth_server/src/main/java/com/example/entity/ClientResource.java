package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 客户端连接方式信息表
 * </p>
 *
 * @author dufu
 * @since 2023-01-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_client_resource")
public class ClientResource extends Model<ClientResource> {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 客户端ID
     */
    private String clientId;

    /**
     * 资源服务地址：http或https开头，如：http://127.0.0.1:8080
     */
    private String resourceServerUrl;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
