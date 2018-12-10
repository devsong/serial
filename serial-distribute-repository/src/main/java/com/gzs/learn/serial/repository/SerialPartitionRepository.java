package com.gzs.learn.serial.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gzs.learn.serial.domain.DataStatus;
import com.gzs.learn.serial.domain.SerialPartition;

@Repository
public class SerialPartitionRepository {

    @Autowired
    private JdbcTemplate template;

    private static final RowMapper<SerialPartition> SerialPartitionMap =
            new RowMapper<SerialPartition>() {
                public SerialPartition mapRow(ResultSet rs, int rowNum) throws SQLException {
                    SerialPartition partition = new SerialPartition();
                    partition.setName(rs.getString(1));
                    partition.setVersion(rs.getInt(2));
                    partition.setPartition(rs.getInt(3));
                    partition.setStat(DataStatus.parser(rs.getShort(4)));
                    partition.setMin(rs.getLong(5));
                    partition.setMax(rs.getLong(6));
                    partition.setUsed(rs.getLong(7));
                    partition.setTscr(rs.getTimestamp(8));
                    return partition;
                }
            };

    private static final String GET_SERIAL_PARTITION = "SELECT `name`, ver, part, stat, min, max,"
            + " used,tscr FROM serialpart AS P WHERE P.`name` = ? AND P.ver = ? AND P.stat = ?;";

    public List<SerialPartition> getSerialPartitions(String key, int version) {
        try {
            return this.template.query(GET_SERIAL_PARTITION, SerialPartitionMap, key, version,
                    DataStatus.ENABLE.calculate());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<SerialPartition>();
        }
    }

    private static final String GET_SERIAL_PARTITION_LOCK =
            "SELECT `name`, ver, part, stat, min, max,"
                    + " used,tscr FROM serialpart AS P WHERE P.`name` = ? AND P.ver = ? AND P.part = ? FOR UPDATE;";

    public SerialPartition getSerialPartition(String key, int version, int partition) {
        try {
            return this.template.queryForObject(GET_SERIAL_PARTITION_LOCK, SerialPartitionMap, key,
                    version, partition);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private static final String REGIST_SERIAL_PARTITION = "INSERT INTO serialpart (`name`, ver,"
            + " part, stat, min, max, used) VALUES (?, ? ,? ,? ,? ,? ,?);";

    @Transactional
    public boolean registSerialPartition(SerialPartition partition) {
        return this.template.update(REGIST_SERIAL_PARTITION, partition.getName(),
                partition.getVersion(), partition.getPartition(), partition.getStat().calculate(),
                partition.getMin(), partition.getMax(), partition.getUsed()) == 1;
    }

    private static final String UPDATE_SERIAL_PARTITION =
            "UPDATE serialpart AS P SET P.used = ? WHERE "
                    + "P.`name` = ? AND P.ver = ? AND P.part = ? AND P.used = ?;";

    @Transactional
    public boolean updateSerialPartition(String name, int version, int partition, long used,
            long last) {
        return this.template.update(UPDATE_SERIAL_PARTITION, used, name, version, partition,
                last) == 1;
    }
}
