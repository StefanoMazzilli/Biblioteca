package org.generation.italy.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Libri {
	public int id;
	public String titolo;
	public int idAutore;
	public int idGenere;
	public LocalDate pubblicazione;
	public int numPagine;
	public int qnt;
	public int idEditore;
	
	public static ArrayList<Libri> scaricaLibri (Connection conn) {
		ArrayList <Libri> elencoLibri= new ArrayList<Libri>();
		String sql="SELECT * FROM libri";
		do {
			try (PreparedStatement ps=conn.prepareStatement(sql)) {
				//provo a creare l'istruzione
				try (ResultSet rs=ps.executeQuery()) {
					while (rs.next()) {
						Libri libro = new Libri();
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
			}catch (Exception e) {
				System.err.println("\nError: "+ e);
			}
		} while (elencoLibri.isEmpty());
		return elencoLibri;
	}

	
}
