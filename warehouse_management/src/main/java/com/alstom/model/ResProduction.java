package com.alstom.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "res_production")
public class ResProduction {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "rp_id_sequence")
	@SequenceGenerator(name = "rp_id_sequence", sequenceName = "RP_ID_SEQ")
	private long id;

	private String nom;

	public ResProduction() {
		super();
	}

	public ResProduction(long id, String nom) {
		super();
		this.id = id;
		this.nom = nom;
	}

	public ResProduction(String nom) {
		super();
		this.nom = nom;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	@Override
	public String toString() {
//		return "ResProduction [id=" + id + ", nom=" + nom + "]";
		return nom + " (" + id + ")";
	}

}
