package com.spadial.inkay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import java.io.File;

@SpringBootApplication
@RestController
public class InkayApplication {

	private String dbHost;
	private String dbDatabase;
	private String dbUser;
	private String dbPassword;

	public static void main(String[] args) {
		SpringApplication.run(InkayApplication.class, args);
	}

	@GetMapping("/tables")
	public String tables() throws SQLException {
		Configurations configs = new Configurations();
		try
		{
			Configuration config = configs.properties(new File("config.properties"));
			// access configuration properties
			dbHost = config.getString("database.host");
			dbDatabase = config.getString("database.db");
			dbUser = config.getString("database.user");
			dbPassword = config.getString("database.password", "secret");  // provide a default

		}
		catch (ConfigurationException cex)
		{
			// Something went wrong
		}
		StringBuilder s = new StringBuilder();
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(
			"jdbc:mysql://"+dbHost+"/"+dbDatabase, 
			dbUser, 
			dbPassword);

			DatabaseMetaData md = conn.getMetaData();
			ResultSet rs = md.getTables("ictsdata", null, "%", null);
			while (rs.next()) {
				s.append(rs.getString(3) + "<br/>");
			}		
		}
		catch (SQLException ex) {
			return "Error accessing database: " + ex.getMessage();
		}
		finally {
			if (conn != null)
				conn.close();
		}
		return s.toString();
	}

}
