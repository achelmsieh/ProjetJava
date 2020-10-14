package com.alstom.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "res_stock")
public class ResStock {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String nom;

	@Column(name = "mot_de_passe")
	private String motDePasse;

	public ResStock(long id, String nom, String motDePasse) {
		super();
		this.id = id;
		this.nom = nom;
		this.motDePasse = motDePasse;
	}

	public ResStock() {
		super();
	}

	public ResStock(String nom, String motDePasse) {
		super();
		this.nom = nom;
		this.motDePasse = motDePasse;
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

	public String getMotDePasse() {
		return motDePasse;
	}

	public void setMotDePasse(String motDePasse) {
		this.motDePasse = motDePasse;
	}

	@Override
	public String toString() {
		return "ResStock [id=" + id + ", nom=" + nom + ", motDePasse=" + motDePasse + "]";
	}

}
