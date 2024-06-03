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
		/* TODO :
		 * Sistemare PROGRAMMA 2:
		 *  - Aggiungere filtri di ricerca;
		 *  - Aggiungere visualizzazione autori, editori e generi.
		 * Aggiungere altre possibilità di ricerca nel PROGRAMMA 4.
		 * Creare una funzione ANNULLA da usare in qualsiasi momento
		 * Creare una funzione di aggiornamento dati locali (lista elencoLibri)
		 */
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
					System.out.println("\n\n\nInserimento nuovo libro nel DataBase.\n");
					
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
							do {
								libro.idAutore=trovaId(conn, "SELECT id_autori FROM autori WHERE CONCAT(nome,' ',cognome) = ?;", autore, "id_autori");
								if (libro.idAutore==0) {
									System.out.println("Autore non presente nel DataBase!");
									
									//inserisco il nuovo autore
									System.out.println("\nProcedere con l'inserimento: ");
									System.out.print("Inserire il nome del nuovo autore: ");
									String nomeAutore=sc.nextLine();
									System.out.print("Inserire il cognome del nuovo autore: ");
									String cognomeAutore=sc.nextLine();
									System.out.print("Inserire la data di nascita del nuovo autore: ");
									LocalDate nascitaAutore= LocalDate.parse(sc.nextLine(), df);
									
									String sqlIns= "INSERT INTO autori (nome, cognome, data_nascita) "+"VALUES (?, ?, ?)";
									try (PreparedStatement inserimento=conn.prepareStatement(sqlIns)) {
										inserimento.setString(1, nomeAutore);
										inserimento.setString(2, cognomeAutore);
										inserimento.setDate(3, Date.valueOf(nascitaAutore));
										int nInserimenti=inserimento.executeUpdate();
										if (nInserimenti>0) {
											System.out.println("Inserimento avvenuto con successo!");
										}
									}
								}else {
									System.out.println("Id autore: "+libro.idAutore);
								}
							}while (libro.idAutore==0);
							
							//chiedo il genere
							System.out.print("Inserire il genere del libro: ");
							String genere=sc.nextLine();
							do {
								libro.idGenere=trovaId(conn, "SELECT id_generi FROM generi WHERE nome = ?;", genere, "id_generi");
								if (libro.idGenere==0) {
									System.out.println("Genere non presente nel DataBase!");
									
									//inserisco il nuovo genere
									System.out.println("\nProcedere con l'inserimento: ");
									System.out.print("Inserire il nome del nuovo genere: ");
									String nomeGenere=sc.nextLine();
									
									String sqlIns= "INSERT INTO autori (nome) "+"VALUES (?)";
									try (PreparedStatement inserimento=conn.prepareStatement(sqlIns)) {
										inserimento.setString(1, nomeGenere);
										int nInserimenti=inserimento.executeUpdate();
										if (nInserimenti>0) {
											System.out.println("Inserimento avvenuto con successo!");
										}
									}
								}else {
									System.out.println("Id genere: "+libro.idGenere);
								}
							}while (libro.idGenere==0);
							
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
							do {
								libro.idEditore=trovaId(conn, "SELECT id_editori FROM editori WHERE nome = ?;", editore, "id_editori");
								if (libro.idEditore==0) {
									System.out.println("Editore non presente nel DataBase!");
									
									//inserisco il nuovo editore
									System.out.println("\nProcedere con l'inserimento: ");
									System.out.print("Inserire il nome del nuovo editore: ");
									String nomeEditore=sc.nextLine();
									System.out.print("Inserire il numero di telefono del nuovo editore: ");
									String numeroEditore=sc.nextLine();
									
									String sqlIns= "INSERT INTO autori (nome, num_editore) "+"VALUES (?, ?)";
									try (PreparedStatement inserimento=conn.prepareStatement(sqlIns)) {
										inserimento.setString(1, nomeEditore);
										inserimento.setString(2, numeroEditore);
										int nInserimenti=inserimento.executeUpdate();
										if (nInserimenti>0) {
											System.out.println("Inserimento avvenuto con successo!");
										}
									}
								}else {
									System.out.println("Id editore: "+libro.idEditore);
								}
							}while (libro.idAutore==0);
							
							
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
					System.out.println("\n\n\nVisualizzazione libri");
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
					System.out.println("\n\n\nCancellazione libro");
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
					break;
				//		----- CASO 4 -----
				case "4":
					//passo al caso successivo
				case "modifica libro esistente":
					System.out.println("\n\n\nModifica di un libro presente nel DataBase");
					
					int idModifica=-1;
					int contaLibri;
					//creo la lista dei libri cercati da usare dopo
					ArrayList <Libri> libriCercati=new ArrayList<Libri>();
					System.out.println("Connessione al DataBase in corso...");
					try (Connection conn=DriverManager.getConnection(url, "root", "")) {
						System.out.println("Connessione riuscita!");
						System.out.print("\nInserire il titolo del libro di cui modificare i dati: ");
						String titoloModifica=sc.nextLine();
						System.out.println("\nRicerca del libro in corso...");
						String sql="SELECT * FROM libri WHERE titolo LIKE ?";
						try (PreparedStatement ricerca=conn.prepareStatement(sql)) {
							//faccio la ricerca dei libri che potrebbero corrispondere con quel titolo
							ricerca.setString(1, "%"+titoloModifica+"%");
							try (ResultSet cercati=ricerca.executeQuery()){
								contaLibri=0;
								l=new Libri();
								while (cercati.next()) {
									//conto quanti risultati ho trovato per i vari casi e li aggiungo alla lista
									l=new Libri();
									contaLibri++;
									l.id=cercati.getInt("id_libri");
									l.titolo=cercati.getString("titolo");
									libriCercati.add(l);
								}
								if (contaLibri==0) {
									//il titolo inserito non corrisponde a nessuno dei presenti nel DB
									System.out.println("Libro non trovato");
								}else {
									//caso in cui ho trovato almeno una corrispondenza
									if (contaLibri==1) {
										//ho trovato solo una corrispondenza tra titolo inserito e libri nel DB
										System.out.println("Il libro trovato è "+l.titolo+" e il suo ID è: "+l.id);
										idModifica=l.id;
									}else if (contaLibri>1) {
										//ho trovato più corrispondenze nel DB => chiedo quale sia quella giusta
										System.out.println("Sono stati trovati più libri:");
										for (int k=0; k<libriCercati.size(); k++) {
											//mostro i titoli trovati
											System.out.println((k+1)+") "+libriCercati.get(k).titolo);
										}
										do {
											//chiedo quale sia il titolo corretto e controllo il corretto inserimento
											idModifica=-1;
											System.out.println("\nScrivi il titolo completo del libro che vuoi cambiare");
											titoloModifica=sc.nextLine();
											for(int i=0; i<libriCercati.size(); i++) {
												if (libriCercati.get(i).titolo.equals(titoloModifica)) {
													System.out.println("\nLibro selezionato: "+libriCercati.get(i).titolo);
													idModifica=libriCercati.get(i).id;
												}
												
											}
											if (idModifica==-1)
												System.out.println("ATTENZIONE! Inserimento sbagliato, riprova!");
										} while (idModifica==-1);
									}
									//ID del titolo da modificare trovato => procedo con l'UPDATE
									
									//ora chiedo di inserire i nuovi dati del libro
									System.out.println("Inserire i nuovi dati del libro");
									Libri modifica=new Libri();
									
									modifica.id=idModifica;
									//cerco la posizione del libro nella lista locale
									int pos=0;
									for (int j=0; j<elencoLibri.size(); j++) {
										if (elencoLibri.get(j).id==idModifica) 
											pos=j;
									}
									
									//chiedo il titolo
									System.out.print("Inserire il titolo: ");
									modifica.titolo=sc.nextLine();
									//chiedo l'autore
									System.out.print("Inserire l'autore: ");
									String autore=sc.nextLine();
									modifica.idAutore=trovaId(conn, "SELECT id_autori FROM autori WHERE CONCAT(nome,' ',cognome) = ?;", autore, "id_autori");
									System.out.println("Id autore: "+modifica.idAutore);
									//chiedo il genere
									System.out.print("Inserire il genere del libro: ");
									String genere=sc.nextLine();
									modifica.idGenere=trovaId(conn, "SELECT id_generi FROM generi WHERE nome = ?;", genere, "id_generi");
									System.out.println("Id genere: "+modifica.idGenere);
									//chiedo la data di pubblicazione
									System.out.print("Inserire la data di pubblicazione: ");
									modifica.pubblicazione = LocalDate.parse(sc.nextLine(), df);
									//chiedo il numero di pagine
									System.out.print("Inserire il numero di pagine: ");
									modifica.numPagine=sc.nextInt();
									sc.nextLine();
									//chiedo la quantità
									System.out.print("Inserire la quantità aggiunta: ");
									modifica.qnt=sc.nextInt();
									sc.nextLine();
									//chiedo l'editore
									System.out.print("Inserire il nome dell'editore: ");
									String editore=sc.nextLine();
									modifica.idEditore=trovaId(conn, "SELECT id_editori FROM editori WHERE nome = ?;", editore, "id_editori");
									System.out.println("Id editore: "+modifica.idEditore);
									
									sql="UPDATE libri SET titolo = ?, id_autore = ?, id_genere = ?, data_pubblicazione = ?, num_pagine = ?, qnt = ?, id_editore = ? WHERE id_libri = ?";
									try (PreparedStatement update=conn.prepareStatement(sql)) {
										//preparo l'istruzione sql per aggiornare i dati
										update.setString(1, modifica.titolo);
										update.setInt(2, modifica.idAutore);
										update.setInt(3, modifica.idGenere);
										update.setDate(4, Date.valueOf(modifica.pubblicazione));
										update.setInt(5, modifica.numPagine);
										update.setInt(6, modifica.qnt);
										update.setInt(7, modifica.idEditore);
										update.setInt(8, modifica.id);
										
										//invio l'ordine di eseguire l'aggiornamento
										int righeInteressate=update.executeUpdate();
										elencoLibri.set(pos, modifica);
										if (righeInteressate>0) {
											System.out.println("Aggiornamento avvenuto con successo!");
										}
									}
								}
							}
						}
						
					}catch (Exception e) {
						System.err.println("Error: "+e);
					}
					
					break;
				//		----- CASO 5 -----
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
			try (ResultSet rs=ps.executeQuery()) {
				while (rs.next()) {
					id=rs.getInt(argomento);
				}
			}
		}catch (Exception e) {
			System.err.println("ERRORE: "+e);
		}
		return id;
		
	}

}
