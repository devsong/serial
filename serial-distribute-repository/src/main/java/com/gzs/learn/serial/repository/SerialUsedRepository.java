package com.gzs.learn.serial.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class SerialUsedRepository {
	private final static int PARTITION = 0;
	@Autowired
	private JdbcTemplate template;

	private static final String GET_SERIAL_USED_MAX = "SELECT MAX(upos) FROM serialused WHERE `name` = ? AND ver = ? AND part = ?;";
	private static final RowMapper<Long> SerialUserMaxMap = new RowMapper<Long>() {
		public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs.getLong(1);
		}
	};

	public Long getSerialMax(String name, int version) {
		try {
			return this.template.queryForObject(GET_SERIAL_USED_MAX,
					SerialUserMaxMap, name, version, PARTITION);
		} catch (EmptyResultDataAccessException e) {
			return 0L;
		}
	}

	private static final String REGIST_SERIAL = "INSERT INTO serialused (`name`, ver, part, upos, tsup) VALUES (?, ? ,? ,? ,?);";

	@Transactional
	public boolean registSerial(String name, int version, long max, long tsup) {
		return this.template.update(REGIST_SERIAL, name, version, PARTITION,
				max, tsup) == 1;
	}
}
