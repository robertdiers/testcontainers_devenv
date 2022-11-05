package com.example.demo.util;

import java.sql.SQLException;
import java.util.Optional;

import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DbUrlPrinter {

    public static void printUrl(Optional<DataSource> ds) throws SQLException {
        if (ds.isPresent()) 
            log.info(ds.get().getConnection().getMetaData().getURL());
    }
    
}
