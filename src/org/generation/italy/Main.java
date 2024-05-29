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
		DateTimeFormatter df=DateTimeFormatter.ofPattern("dd/MM/yyyy");
		Libri l;
		int posizione;
		
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
					}
				}
				System.out.println("Caricamento dati libri dal DataBase avvenuto con successo!");
			}
		} catch (Exception e) {
			System.out.println("Connessione non riuscita! Errore: "+e.getMessage());
		}
		
		System.out.println("Benvenuto");
		do {
			//inizio ciclo do-while per fare più operazioni
			libro= new Libri();
			System.out.println("Selezionare il programma da usare immettendo il relativo codice");
			System.out.println("\n1) Inserimento nuovo libro\n2) Visualizza libri esistenti\n3) Cancellazione libro\n4) Modifica libro esistente\n5) Esci");
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
					posizione=-1;
					for (int i=0; i<elencoLibri.size(); i++) {
						l=elencoLibri.get(i);
						if (l.titolo.equalsIgnoreCase(libro.titolo)) {
							posizione=i;
						}
					}
					if (posizione!=-1) {
						//il libro è già presente
						System.out.println("ATTENZIONE! Libro già presente nel DataBase!");
					}else {
						//il libro non è presente
						System.out.println("Libro non presente nel DataBase. Procedere con l'inserimento.");
						System.out.println("Connessione al DataBase...");
						try (Connection conn=DriverManager.getConnection(url, "root", "")) {
							//connessione al DB
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
								//preparazione dell'istruzione
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
								
							}
							//ora devo cercare l'id autoincrementante assegnato dal DB
							sql = "SELECT id_libri FROM libri WHERE titolo = ?";
							try (PreparedStatement cercaId=conn.prepareStatement(sql)) {
								cercaId.setString(1, libro.titolo);
								
								try(ResultSet idCercato=cercaId.executeQuery()) {
									if (idCercato.next()) {
										libro.id=idCercato.getInt("id_libri");
									}
								}
							}
							//adesso che ho tutti i dati del libro, lo aggiungo alla lista
							elencoLibri.add(libro);
							System.out.println("Libro aggiunto con successo!");
							
							
						}catch (Exception e) {
							System.err.println("Errore: "+e);
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
					for (Libri mostraLibri:elencoLibri) {
						contatore++;
						System.out.println("Libro n° "+contatore+"\n");
						System.out.println("Titolo: "+mostraLibri.titolo);
						System.out.println("Id del libro: "+mostraLibri.id);
						System.out.println("Data di pubblicazione: "+mostraLibri.pubblicazione);
						System.out.println("Numero di pagine: "+mostraLibri.numPagine);
						System.out.println("Quantità: "+mostraLibri.qnt);
					}
					// AGGIUNGERE LA VISUALIZZAZIONE DI AUTORE, EDITORE E GENERE!
					break;
				//		----- CASO 3 -----
				case "3":
					//passo direttamente al caso successivo
				case "cancellazione libro":
					System.out.println("Cancellazione libro");
					System.out.println("Inserire il titolo del libro da rimuovere:");
					String titoloRimozione=sc.nextLine();
					System.out.println("Connessione al DataBase in corso...");
					try (Connection conn=DriverManager.getConnection(url, "root", "")) {
						//connessione con il DB
						System.out.println("Connessione riuscita!");
						
						String sql="DELETE FROM libri WHERE titolo LIKE ?";
						try (PreparedStatement canc=conn.prepareStatement(sql)) {
							//cancello il libro dal DB
							canc.setString(1, "%"+titoloRimozione+"%");
							int righeInteressate=canc.executeUpdate();
							if (righeInteressate==0) {
								System.out.println("Non è stato trovato il libro cercato");
							}else {
								System.out.println("Numero di libri cancellati: "+righeInteressate);
							}
							//cancello il libro dalle liste locali
							posizione=-1;
							for (int i=0; i<elencoLibri.size(); i++) {
								l=elencoLibri.get(i);
								if (l.titolo.indexOf(titoloRimozione)>=0) {
									posizione=i;
								}
							}
							if(posizione>=0) {
								elencoLibri.remove(posizione);
							}
						}
						
					}catch (Exception e) {
						System.err.println("Error: "+e);
					}
				case "5":
					//passo al successivo
				case "esci":
					System.out.println("Arrivederci!");
					break;
			}
			System.out.println("\n\n\n");
		} while (!scelta.equals("5"));
		
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
			System.err.println("ERRORE: "+e);
		}
		return id;
	}

}
