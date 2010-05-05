package it.fub.jardin.server;

import java.sql.Connection;

public class AdvancedSqlConnection {
	public enum ConnStatus {free, busy};
	public enum ConnType {normal, info};

	Connection conn;
	ConnStatus status;
	ConnType type;
	
	public ConnType getType() {
		return type;
	}

	public void setType(ConnType type) {
		this.type = type;
	}

	public ConnStatus getStatus() {
		return status;
	}

	public void setStatus(ConnStatus status) {
		this.status = status;
	}

	public Connection getConn() {
		return conn;
	}

	public AdvancedSqlConnection(Connection conn, ConnType type) {
		this.conn = conn;
		this.type = type;
		status = ConnStatus.free;
	}

}
