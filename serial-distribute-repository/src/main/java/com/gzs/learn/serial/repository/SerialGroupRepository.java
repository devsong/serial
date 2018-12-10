package com.gzs.learn.serial.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.gzs.learn.serial.domain.DataStatus;
import com.gzs.learn.serial.domain.SerialGroup;
import com.gzs.learn.serial.domain.pk.SerialGroupPK;
import com.gzs.learn.serial.exception.SerialCode;
import com.gzs.learn.serial.exception.SerialException;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class SerialGroupRepository {
    @Autowired
    private JdbcTemplate template;

    private static final RowMapper<SerialGroup> SerialGroupMap = new RowMapper<SerialGroup>() {
        public SerialGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
            SerialGroup group = new SerialGroup();
            group.setName(rs.getString(1));
            group.setVersion(rs.getInt(2));
            group.setStat(DataStatus.parser(rs.getShort(3)));
            group.setMin(rs.getLong(4));
            group.setMax(rs.getLong(5));
            group.setPartition(rs.getInt(6));
            group.setStep(rs.getInt(7));
            group.setUpid(rs.getLong(8));
            group.setTsup(rs.getLong(9));
            group.setTscr(rs.getTimestamp(10));
            return group;
        }
    };

    private static final String GET_SERIAL_GROUP = "SELECT `name`, ver, stat, min, max, part,"
            + " step, upid, uptime,tscr FROM serialgroup WHERE `name` = ?;";

    public List<SerialGroup> getSerialGroups(String name) {
        try {
            return this.template.query(GET_SERIAL_GROUP, SerialGroupMap, name);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<SerialGroup>();
        }
    }

    private static final String GET_SERIAL_GROUP_BY_VERSION =
            "SELECT `name`, ver, stat, min, max, part,"
                    + " step, upid, uptime,tscr FROM serialgroup WHERE `name` = ? AND `ver` = ?;";

    public SerialGroup getSerialGroup(String name, int version) {
        try {
            return this.template.queryForObject(GET_SERIAL_GROUP_BY_VERSION, SerialGroupMap, name,
                    version);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private static final String INSERT_SERIAL_GROUP = "INSERT INTO serialgroup (`name`, ver,"
            + " stat, min, max, part, step, upid, uptime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

    public boolean registSerialGroups(SerialGroup group) throws SerialException {
        try {
            return this.template.update(INSERT_SERIAL_GROUP, group.getName(), group.getVersion(),
                    group.getStat().getValue(), group.getMin(), group.getMax(),
                    group.getPartition(), group.getStep(), group.getUpid(), group.getTsup()) == 1;
        } catch (DuplicateKeyException e) {
            log.error("insertSerialGroups", e);
            throw new SerialException(SerialCode.SERIAL_ALREADY_EXISTS,
                    String.format("Duplicate Key, name=[%s], version=[%d].", group.getName(),
                            group.getVersion()));
        } catch (DataAccessException e) {
            log.error("insertSerialGroups", e);
            return false;
        }
    }

    private static final String GET_SERIAL_GROUPS_NAME =
            "SELECT `name`, ver  FROM serialgroup WHERE stat = ?;";
    private static final RowMapper<SerialGroupPK> SerialGroupNameMap =
            new RowMapper<SerialGroupPK>() {
                public SerialGroupPK mapRow(ResultSet rs, int rowNum) throws SQLException {
                    SerialGroupPK pk = new SerialGroupPK();
                    pk.setName(rs.getString(1));
                    pk.setVersion(rs.getInt(2));
                    return pk;
                }
            };

    public List<SerialGroupPK> getEnableSerialGroupsName() {
        try {
            return this.template.query(GET_SERIAL_GROUPS_NAME, SerialGroupNameMap,
                    DataStatus.ENABLE.calculate());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<SerialGroupPK>();
        }
    }
}
