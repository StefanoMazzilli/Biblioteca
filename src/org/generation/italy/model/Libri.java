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

	public static ArrayList<Libri> filtraLibriAutore (String filtro, ArrayList<Libri> elencoLibri, ArrayList<Autori> elencoAutori) {
		ArrayList<Libri> elencoFiltrati= new ArrayList<Libri>();
		int id=-1;
		for (int i = 0; i<elencoAutori.size(); i++) {
			if ((elencoAutori.get(i).nomeAutore+" "+elencoAutori.get(i).cognomeAutore).indexOf(filtro)>=0) {
				id=elencoAutori.get(i).idAutore;
			}
		}
		int idFiltroAutore=id;
		elencoFiltrati = (ArrayList<Libri>) elencoLibri.stream().filter(lib -> lib.idAutore==idFiltroAutore).collect(Collectors.toList());
		return elencoFiltrati;
	}
	
	public static ArrayList<Libri> filtraLibriGenere (String filtro, ArrayList<Libri> elencoLibri, ArrayList<Generi> elencoGeneri) {
		ArrayList<Libri> elencoFiltrati= new ArrayList<Libri>();
		int id=-1;
		for (int j = 0; j<elencoGeneri.size(); j++) {
			if (elencoGeneri.get(j).nomeGenere.indexOf(filtro)>=0) {
				id=elencoGeneri.get(j).idGenere;
			}
		}
		int idFiltroGenere=id;
		elencoFiltrati = (ArrayList<Libri>) elencoLibri.stream().filter(lib -> lib.idGenere==idFiltroGenere).collect(Collectors.toList());
		return elencoFiltrati;
	}

	@Override
	public String toString() {
		return "Libri [id=" + id + ", titolo=" + titolo + ", idAutore=" + idAutore + ", idGenere=" + idGenere
				+ ", pubblicazione=" + pubblicazione + ", numPagine=" + numPagine + ", qnt=" + qnt + ", idEditore="
				+ idEditore + "]\n";
	}
}
