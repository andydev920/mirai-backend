package jp.mirai.module.infra.dal.mysql.db;

import jp.mirai.framework.mybatis.core.mapper.BaseMapperX;
import jp.mirai.module.infra.dal.dataobject.db.DataSourceConfigDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据源配置 Mapper
 *
 * @author mirai
 */
@Mapper
public interface DataSourceConfigMapper extends BaseMapperX<DataSourceConfigDO> {
}
