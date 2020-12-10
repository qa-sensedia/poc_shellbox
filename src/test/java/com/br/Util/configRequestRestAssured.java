package com.br.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class configRequestRestAssured {
	
	public static void main (String []args) {
		


			String conexaoJdbc = "jdbc:mysql://ms-fuelling-hml.c3xdqlguj1uc.sa-east-1.rds.amazonaws.com:3306/mps";
			String conexaoSSH = "ms-fuelling-hml.c3xdqlguj1uc.sa-east-1.rds.amazona?autoReconnect=true&useSSL=true";
			String USER = "root";
			String PASSWORD = "F7hGUvL1";
			
			try {
				Connection conexao = DriverManager.getConnection(conexaoJdbc, USER, PASSWORD);
				System.out.println("conectando...");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


}
