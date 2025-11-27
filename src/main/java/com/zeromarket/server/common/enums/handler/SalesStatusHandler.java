package com.zeromarket.server.common.enums.handler;

import com.zeromarket.server.common.enums.SalesStatus;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(SalesStatus.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class SalesStatusHandler extends BaseTypeHandler<SalesStatus> {


    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, SalesStatus status,
        JdbcType jdbcType) throws SQLException {

    }

    @Override
    public SalesStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String name = rs.getString(columnName);
        return name == null ? null : SalesStatus.valueOf(name);
    }

    @Override
    public SalesStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String name = rs.getString(columnIndex);
        return name == null ? null : SalesStatus.valueOf(name);
    }

    @Override
    public SalesStatus getNullableResult(CallableStatement cs, int columnIndex)
        throws SQLException {
        String name = cs.getString(columnIndex);
        return name == null ? null : SalesStatus.valueOf(name);
    }
}
