package com.alstom.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.alstom.model.enums.PersonnelRole;

@Entity
@Table(name = "Personnel")
public class Personnel {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "perso_id_sequence")
	@SequenceGenerator(name = "perso_id_sequence", sequenceName = "PR_ID_SEQ")
	private long id;

	private String nom;
	private String prenom;

	@Column(name = "matricule")
	private String matricule;
	@Column(name = "mot_de_passe")
	private String motDePasse;

	private PersonnelRole role;

	public Personnel() {
		super();
	}

	public Personnel(String nom, String prenom, String matricule, String motDePasse, PersonnelRole role) {
		super();
		this.nom = nom;
		this.prenom = prenom;
		this.matricule = matricule;
		this.motDePasse = motDePasse;
		this.role = role;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMatricule() {
		return matricule;
	}

	public void setMatricule(String matricule) {
		this.matricule = matricule;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public PersonnelRole getRole() {
		return role;
	}

	public void setRole(PersonnelRole role) {
		this.role = role;
	}

	public String getMotDePasse() {
		return motDePasse;
	}

	public void setMotDePasse(String motDePasse) {
		this.motDePasse = motDePasse;
	}

	@Override
	public String toString() {
		return nom + " " + prenom + " [" + matricule + "]";
	}

	public String getFullName() {
		String fullName = "";
		if (this.nom != null) {
			fullName += this.nom.toUpperCase();
		}

		if (this.prenom != null) {
			fullName += " " + capitalize(this.prenom);
		}

		return fullName;
	}

	private String capitalize(String str) {
		if (str == null)
			return "";
		if (str.length() < 2)
			return str;

		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
	}

//	@Override
//	public String toString() {
//		return "Personnel [id=" + id + ", nom=" + nom + ", prenom=" + prenom + ", matricule=" + matricule
//				+ ", motDePasse=" + motDePasse + ", role=" + role + "]";
//	}

}
