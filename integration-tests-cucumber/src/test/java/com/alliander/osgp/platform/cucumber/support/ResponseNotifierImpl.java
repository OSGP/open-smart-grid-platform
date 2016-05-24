package com.alliander.osgp.platform.cucumber.support;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.stereotype.Component;

@Component
public class ResponseNotifierImpl implements ResponseNotifier {

    private Connection connection;

    @Override
    public boolean isResponseAvailable(String correlid) {
        boolean result = false;
        try {
            Statement st = conn().createStatement();
            ResultSet rs = st.executeQuery("SELECT count(*) FROM meter_response_data WHERE correlation_uid = '" + correlid + "'");
            while (rs.next()) {
                result = rs.getInt(1) > 0;
            }
            rs.close();
            st.close();
            return result;
        } catch (SQLException se) {
            System.err.println(se.getMessage());
            return false;
        }
    }

    private Connection conn() {
        if (this.connection == null) {
            this.connection = connectToDatabaseOrDie();
        }
        return this.connection;
    }

    private Connection connectToDatabaseOrDie() {
        try {
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://" + host() + "/" + database();
//            connection = DriverManager.getConnection(url, username(), password());
            connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/osgp_adapter_ws_smartmetering", "osp_admin", "osp_admin");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(2);
        }
        return connection;
    }

    private String host() {
        return "localhost";
    }
    
    private String database() {
        return "OSGP";
    }
    
    private String username() {
        return "osp_admin";
    }
    
    private String password() {
        return "osp_admin";
    }
}
