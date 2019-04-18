package kr.pe.codda.server.lib;

import java.sql.Connection;

import org.jooq.DSLContext;

public interface DBExecutorIF {
	public void execute(final Connection conn, final DSLContext create) throws Exception;
}
