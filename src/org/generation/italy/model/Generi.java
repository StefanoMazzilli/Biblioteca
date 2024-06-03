package org.generation.italy.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class Generi {
	public int idGenere;
	public String nomeGenere;
	
	public static ArrayList<Generi> scaricaGeneri (Connection conn) {
		ArrayList <Generi> elencoGeneri= new ArrayList<Generi>();
		String sql="SELECT * FROM generi";
		do {
			try (PreparedStatement ps=conn.prepareStatement(sql)) {
				//provo a creare l'istruzione
				try (ResultSet rs=ps.executeQuery()) {
					while (rs.next()) {
						Generi genere=new Generi();
						genere.idGenere=rs.getInt("id_generi");
						genere.nomeGenere=rs.getString("nome");
						elencoGeneri.add(genere);
					}
				}
			}catch (Exception e) {
				System.err.println("\nError: "+ e);
			}
		} while (elencoGeneri.isEmpty());
		return elencoGeneri;
	}
	
	public static int trovaId (String genere, ArrayList<Generi> elencoGeneri, Scanner sc, Connection conn, DateTimeFormatter df) {
		int idGenere;
		do {	
			idGenere=0;
			boolean trovato = false;
			for (int i=0; i<elencoGeneri.size(); i++) {
				if (elencoGeneri.get(i).nomeGenere.equals(genere)) {
					idGenere=elencoGeneri.get(i).idGenere;
					System.out.println("Id genere: "+idGenere);
					trovato=true;
				}
			}
			if (!trovato) {
				System.out.println("Genere non presente nel DataBase!");
				//procedere con l'inserimento del nuovo genere
				Generi.aggiungiGenere(sc, conn, df);
				elencoGeneri=scaricaGeneri(conn);
			}
		}while(idGenere==0);
		return idGenere;
	}

	public static String trovaNome (int idGenere, ArrayList<Generi> elencoGeneri) {
		String nome=new String();
		for (int i=0; i<elencoGeneri.size(); i++) {
			if (elencoGeneri.get(i).idGenere==idGenere) {
				nome=elencoGeneri.get(i).nomeGenere;
			}
		}
		return nome;
	}

	public static void aggiungiGenere (Scanner sc, Connection conn, DateTimeFormatter df) {
		//inserisco il nuovo genere
		System.out.println("\nProcedere con l'inserimento: ");
		System.out.print("Inserire il nome del nuovo genere: ");
		String nomeGenere=sc.nextLine();
		
		String sqlIns= "INSERT INTO generi (nome) "+"VALUES (?)";
		try (PreparedStatement inserimento=conn.prepareStatement(sqlIns)) {
			inserimento.setString(1, nomeGenere);
			int nInserimenti=inserimento.executeUpdate();
			if (nInserimenti>0) {
				System.out.println("Inserimento avvenuto con successo!");
			}
		}catch (Exception e) {
			System.err.println("Error: "+e);
		}
		
	}
}
