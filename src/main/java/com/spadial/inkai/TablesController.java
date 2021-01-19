package com.spadial.inkai;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Controller
public class TablesController {

	private String dbHost;
	private String dbDatabase;
	private String dbUser;
	private String dbPassword;

	@GetMapping("/tables")
	public String tables(Model m) throws SQLException {
		Configurations configs = new Configurations();
		try {
			Configuration config = configs.properties(new File("config.properties"));
			// access configuration properties
			dbHost = config.getString("database.host");
			dbDatabase = config.getString("database.db");
			dbUser = config.getString("database.user");
			dbPassword = config.getString("database.password", "secret"); // provide a default

		} catch (ConfigurationException cex) {
			// Something went wrong
		}
		Connection conn = null;	
		List<String> r = new ArrayList<>();
		try {
			conn = DriverManager.getConnection("jdbc:mysql://" + dbHost + "/" + dbDatabase, dbUser, dbPassword);

			DatabaseMetaData md = conn.getMetaData();
			ResultSet rs = md.getTables("ictsdata", null, "%", null);
		
			while (rs.next()) {
				r.add(rs.getString(3));
			}
		} catch (SQLException ex) {
			return "Error accessing database: " + ex.getMessage();
		} finally {
			if (conn != null)
				conn.close();
		}

		m.addAttribute("tablas", r);
		return "index";
	}

	@GetMapping(value = "/download")
	public ResponseEntity<Object> downloadFile() throws IOException {
		Crosswalk t = new Crosswalk();
		File file = t.exportTemplate();
		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
		HttpHeaders headers = new HttpHeaders();
			
		headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
			
		return ResponseEntity.ok().headers(headers).contentLength(file.length()).contentType(
			MediaType.parseMediaType("application/octet-stream")).body(resource);
		}
			
    }
