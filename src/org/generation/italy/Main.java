package org.generation.italy;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
		ArrayList<String> elencoTitoli = new ArrayList<String>();
		DateTimeFormatter df=DateTimeFormatter.ofPattern("dd/MM/yyyy");
		
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
						libro.titolo=rs.getString("titolo");
						libro.idAutore=rs.getInt("id_autore");
						libro.idGenere=rs.getInt("id_genere");
						libro.pubblicazione=rs.getDate("data_pubblicazione").toLocalDate();
						libro.numPagine=rs.getInt("num_pagine");
						libro.qnt=rs.getInt("qnt");
						libro.idEditore=rs.getInt("id_editore");
						elencoLibri.add(libro);
						elencoTitoli.add(libro.titolo);
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
			scelta= sc.nextLine().toLowerCase();
			switch (scelta) {
				//		----- CASO 1 -----
				case "1":
					//lo lascio andare al caso successivo
				case "inserimento nuovo libro":
					System.out.println("Inserimento nuovo libro nel DataBase.\n");
					
					//chiedo il titolo
					System.out.print("Inserire il titolo del libro: ");
					libro.titolo=sc.nextLine();
					//controllo che il libro non sia già presente nella lista
					if (elencoTitoli.contains(libro.titolo)) {
						//il libro è già presente
						System.out.println("ATTENZIONE! Libro già presente nel DataBase!");
					}else {
						//il libro non è presente
						System.out.println("Libro non presente nel DataBase. Procedere con l'inserimento.");
						System.out.println("Connessione al DataBase...");
						try (Connection conn=DriverManager.getConnection(url, "root", "")) {
							System.out.println("Connessione riuscita!");
							
							
							
							//chiedo l'autore
							System.out.print("Inserire l'autore del libro: ");
							String autore=sc.nextLine();
							libro.idAutore=trovaId(conn, "SELECT id_autori FROM autori WHERE CONCAT(nome,' ',cognome) = ?;", autore, "id_autori");
							System.out.println("Id autore: "+libro.idAutore);
							//chiedo il genere
							System.out.print("Inserire il genere del libro: ");
							String genere=sc.nextLine();
							libro.idGenere=trovaId(conn, "SELECT id_generi FROM generi WHERE nome = ?;", genere, "id_generi");
							System.out.println("Id genere: "+libro.idGenere);
							//chiedo la data di pubblicazione
							System.out.print("Inserire la data di pubblicazione: ");
							libro.pubblicazione = LocalDate.parse(sc.nextLine(), df);
							//chiedo il numero di pagine
							System.out.print("Inserire il numero di pagine: ");
							libro.numPagine=sc.nextInt();
							sc.nextLine();
							//chiedo la quantità
							System.out.print("Inserire la quantità aggiunta: ");
							libro.qnt=sc.nextInt();
							sc.nextLine();
							//chiedo l'editore
							System.out.print("Inserire il nome dell'editore: ");
							String editore=sc.nextLine();
							libro.idEditore=trovaId(conn, "SELECT id_editori FROM editori WHERE nome = ?;", editore, "id_editori");
							System.out.println("Id editore: "+libro.idEditore);
							
							
							String sql="INSERT INTO libri (id_libri, titolo, id_autore, id_genere, data_pubblicazione, num_pagine, qnt, id_editore) "
									+ "VALUE (null, ?, ?, ?, ?, ?, ?, ?)";
							try (PreparedStatement ps=conn.prepareStatement(sql)) {
								//imposto i valori dei parametri da inserire
								ps.setString(1, libro.titolo);
								ps.setInt(2, libro.idAutore);
								ps.setInt(3, libro.idGenere);
								ps.setDate(4, Date.valueOf(libro.pubblicazione));
								ps.setInt(5, libro.numPagine);
								ps.setInt(6, libro.qnt);
								ps.setInt(7, libro.idEditore);
								
								//eseguo l'istruzione
								ps.executeUpdate();
								libro.id=elencoLibri.size()+1;
								elencoLibri.add(libro);
								System.out.println("Libro aggiunto con successo!");
							}
							
						}catch (Exception e) {
							System.out.println("Errore: "+e);
						}
					}
					
					break;
				//		----- CASO 2 -----
				case "2":
					//passo al caso successivo
				case "visualizza libri esistenti":
					//scorro la lista dei libri per mostrarli
					int contatore=0;
					//uso il ciclo for-each per mostrare ogni elemento della lista elencoLibri
					for (Libri l:elencoLibri) {
						contatore++;
						System.out.println("Libro n° "+contatore+"\n");
						System.out.println("Titolo: "+l.titolo);
						System.out.println("Data di pubblicazione: "+l.pubblicazione);
						System.out.println("Numero di pagine: "+l.numPagine);
						System.out.println("Quantità: "+l.qnt);
					}
					break;
				//casi 3 e 4 da implementare!!!
			}
			System.out.println("\n\n\n");
		} while (!scelta.equals("4"));
		
		sc.close();
	}
	private static int trovaId (Connection conn, String sql, String cercare, String argomento) {
		int id=0;
		try (PreparedStatement ps=conn.prepareStatement(sql)) {
			ps.setString(1, cercare);
			System.out.println(ps.toString());
			try (ResultSet rs=ps.executeQuery()) {
				while (rs.next()) {
					System.out.println(rs.getInt(argomento));
					id=rs.getInt(argomento);
				}
			}
		}catch (Exception e) {
			System.out.println("ERRORE: "+e);
		}
		return id;
	}

}
