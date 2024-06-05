package org.generation.italy.model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class Autori {
	public int idAutore;
	public String nomeAutore;
	public String cognomeAutore;
	public LocalDate nascita;
	
	public static ArrayList<Autori> scaricaAutori (Connection conn) {
		ArrayList <Autori> elencoAutori= new ArrayList<Autori>();
		String sql="SELECT * FROM autori";
		do {
			try (PreparedStatement ps=conn.prepareStatement(sql)) {
				//provo a creare l'istruzione
				try (ResultSet rs=ps.executeQuery()) {
					while (rs.next()) {
						Autori autore = new Autori();
						autore.idAutore=rs.getInt("id_autori");
						autore.nomeAutore=rs.getString("nome");
						autore.cognomeAutore=rs.getString("cognome");
						autore.nascita=rs.getDate("data_nascita").toLocalDate();
						elencoAutori.add(autore);
					}
				}
			}catch (Exception e) {
				System.err.println("\nError: "+ e);
			}
		} while (elencoAutori.isEmpty());
		return elencoAutori;
	}

	public static int trovaId (String autore, ArrayList<Autori> elencoAutori, Scanner sc, Connection conn, DateTimeFormatter df) {
		int idAutore;
		do {
			idAutore=0;
			boolean trovato=false;
			for (int i=0; i<elencoAutori.size(); i++) {
				if ((elencoAutori.get(i).nomeAutore+" "+elencoAutori.get(i).cognomeAutore).equals(autore)) {
					idAutore=elencoAutori.get(i).idAutore;
					System.out.println("Id autore: "+idAutore);
					trovato=true;
				}
			}
			if (!trovato) {
				System.out.println("Autore non presente nel DataBase!");
				//procedere con l'inserimento del nuovo autore
				Autori.aggiungiAutore(sc, conn, df);
				elencoAutori=Autori.scaricaAutori(conn);
			}
		}while (idAutore==0);
		return idAutore;
	}

	public static String trovaNome (int idAutore, ArrayList<Autori> elencoAutori) {
		String nome=new String();
		for (int i=0; i<elencoAutori.size(); i++) {
			if (elencoAutori.get(i).idAutore==idAutore) {
				nome=elencoAutori.get(i).nomeAutore+" "+elencoAutori.get(i).cognomeAutore;
			}
		}
		return nome;
	}

	@Override
	public String toString() {
		return "Autori [id=" + idAutore + ", nome=" + nomeAutore + ", cognome=" + cognomeAutore
				+ ", nascita=" + nascita + "]\n";
	}

	public static void aggiungiAutore (Scanner sc, Connection conn, DateTimeFormatter df) {
		//inserisco il nuovo autore
		Autori nuovo=new Autori();
		System.out.println("\nProcedere con l'inserimento: ");
		System.out.print("Inserire il nome del nuovo autore: ");
		nuovo.nomeAutore=sc.nextLine();
		System.out.print("Inserire il cognome del nuovo autore: ");
		nuovo.cognomeAutore=sc.nextLine();
		System.out.print("Inserire la data di nascita del nuovo autore: ");
		nuovo.nascita= LocalDate.parse(sc.nextLine(), df);
		
		String sqlIns= "INSERT INTO autori (nome, cognome, data_nascita) "+"VALUES (?, ?, ?)";
		try (PreparedStatement inserimento=conn.prepareStatement(sqlIns)) {
			inserimento.setString(1, nuovo.nomeAutore);
			inserimento.setString(2, nuovo.cognomeAutore);
			inserimento.setDate(3, Date.valueOf(nuovo.nascita));
			int nInserimenti=inserimento.executeUpdate();
			if (nInserimenti>0) {
				System.out.println("Inserimento avvenuto con successo!");
			}
		} catch (Exception e) {
			System.err.println("Error: "+ e);
		}
	}
}
