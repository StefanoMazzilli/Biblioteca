package org.generation.italy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Scanner;

import org.generation.italy.model.Libri;

class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner sc= new Scanner(System.in);
		String scelta = new String();
		Libri libro;
		ArrayList<Libri> elencoLibri = new ArrayList<Libri>();
		
		//caricamento arraylist dei libri
		String url="jdbc:mysql://localhost:3306/biblioteca";
		System.out.println("Connessione al DataBase...");
		try (Connection conn=DriverManager.getConnection(url, "root", "")) {
			System.out.println("Connessione riuscita!");
			
			String sql="SELECT * FROM libri";
			try (PreparedStatement ps=conn.prepareStatement(sql)) {
				//provo a creare l'istruzione
				try (ResultSet rs=ps.executeQuery()) {
					while (rs.next()) {
						libro = new Libri();
						libro.id=rs.getInt("id_libri");
						libro.idAutore=rs.getInt("id_autore");
						libro.idGenere=rs.getInt("id_genere");
						libro.pubblicazione=rs.getDate("data_pubblicazione").toLocalDate();
						libro.numPagine=rs.getInt("num_pagine");
						libro.qnt=rs.getInt("qnt");
						libro.idEditore=rs.getInt("id_editore");
						elencoLibri.add(libro);
					}
				}
				System.out.println("Caricamento dati libri dal DataBase avvenuto con successo!");
			}
		} catch (Exception e) {
			System.out.println("Connessione non riuscita! Errore: "+e.getMessage());
		}
		
		System.out.println("Benvenuto");
		do {
			libro= new Libri();
			System.out.println("Selezionare il programma da usare immettendo il relativo codice");
			System.out.println("\n1) Inserimento nuovo libro\n2) Visualizza libri esistenti\n3) Cancellazione libro\n4) Esci");
			scelta= sc.nextLine();
			
		} while (!scelta.equals("4"));
		
		sc.close();
	}

}
