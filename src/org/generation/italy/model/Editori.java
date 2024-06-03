package org.generation.italy.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class Editori {
	public int idEditore;
	public String nomeEditore;
	public String telEditore;
	
	public static ArrayList<Editori> scaricaEditori (Connection conn) {
		ArrayList <Editori> elencoEditori= new ArrayList<Editori>();
		String sql="SELECT * FROM editori";
		do {
			try (PreparedStatement ps=conn.prepareStatement(sql)) {
				//provo a creare l'istruzione
				try (ResultSet rs=ps.executeQuery()) {
					while (rs.next()) {
						Editori editore=new Editori();
						editore.idEditore=rs.getInt("id_editori");
						editore.nomeEditore=rs.getString("nome");
						editore.telEditore=rs.getString("num_telefono");
						elencoEditori.add(editore);
					}
				}
			}catch (Exception e) {
				System.err.println("\nError: "+ e);
			}
		} while (elencoEditori.isEmpty());
		return elencoEditori;
	}
	
	public static int trovaId (String editore, ArrayList<Editori> elencoEditori, Scanner sc, Connection conn, DateTimeFormatter df) {
		int idEditore;
		do {
			idEditore=0;
			boolean trovato = false;
			for (int i=0; i<elencoEditori.size(); i++) {
				if (elencoEditori.get(i).nomeEditore.equals(editore)) {
					idEditore=elencoEditori.get(i).idEditore;
					System.out.println("Id editore: "+idEditore);
					trovato = true;
				}
			}
			if (!trovato) {
				System.out.println("Editore non presente nel DataBase!");
				//procedere con inserimento del nuovo editore
				Editori.aggiungiEditore(sc, conn, df);
				elencoEditori=Editori.scaricaEditori(conn);
			}
		}while (idEditore==0);
		return idEditore;
	}
	
	public static String trovaNome (int idEditore, ArrayList<Editori> elencoEditori) {
		String nome=new String();
		for (int i=0; i<elencoEditori.size(); i++) {
			if (elencoEditori.get(i).idEditore==idEditore) {
				nome=elencoEditori.get(i).nomeEditore;
			}
		}
		return nome;
	}

	public static void aggiungiEditore (Scanner sc, Connection conn, DateTimeFormatter df) {
		//inserisco il nuovo editore
		System.out.println("\nProcedere con l'inserimento: ");
		System.out.print("Inserire il nome del nuovo editore: ");
		String nomeEditore=sc.nextLine();
		System.out.print("Inserire il numero di telefono del nuovo editore: ");
		String numeroEditore=sc.nextLine();
		
		String sqlIns= "INSERT INTO editori (nome, num_editore) "+"VALUES (?, ?)";
		try (PreparedStatement inserimento=conn.prepareStatement(sqlIns)) {
			inserimento.setString(1, nomeEditore);
			inserimento.setString(2, numeroEditore);
			int nInserimenti=inserimento.executeUpdate();
			if (nInserimenti>0) {
				System.out.println("Inserimento avvenuto con successo!");
			}
		} catch (Exception e) {
			System.err.println("Error: "+ e);
		}
	}
}
