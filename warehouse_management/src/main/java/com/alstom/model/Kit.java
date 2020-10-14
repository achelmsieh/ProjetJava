package com.alstom.model;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "kits")
public class Kit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "[OF]")
	private String OF;

	@Enumerated(EnumType.ORDINAL)
	private EtatKit etat;

	private String projet;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date_entree")
	private Date dateEntree;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date_sortie")
	private Date dateSortie;

	@ManyToMany()
	@JoinTable(name = "kits_emplacements", joinColumns = @JoinColumn(name = "kit_id"), inverseJoinColumns = @JoinColumn(name = "emplacement_id"))
	private Set<Emplacement> emplacements;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_res_stock", nullable = true)
	private ResStock resStock;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_res_production", nullable = true)
	private ResProduction resProduction;

	public Kit() {
		super();
	}

	public Kit(String OF, EtatKit etat, String projet, Date dateEntree, Date dateSortie, Set<Emplacement> emplacements,
			ResStock resStock, ResProduction resProduction) {
		super();
		this.OF = OF;
		this.etat = etat;
		this.projet = projet;
		this.dateEntree = dateEntree;
		this.dateSortie = dateSortie;
		this.emplacements = emplacements;
		this.resStock = resStock;
		this.resProduction = resProduction;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getOF() {
		return OF;
	}

	public void setOF(String oF) {
		OF = oF;
	}

	public EtatKit getEtat() {
		return etat;
	}

	public void setEtat(EtatKit etat) {
		this.etat = etat;
	}

	public String getProjet() {
		return projet;
	}

	public void setProjet(String projet) {
		this.projet = projet;
	}

	public Date getDateEntree() {
		return dateEntree;
	}

	public void setDateEntree(Date dateEntree) {
		this.dateEntree = dateEntree;
	}

	public Date getDateSortie() {
		return dateSortie;
	}

	public void setDateSortie(Date dateSortie) {
		this.dateSortie = dateSortie;
	}

	public Set<Emplacement> getEmplacements() {
		return emplacements;
	}

	public void setEmplacements(Set<Emplacement> emplacements) {
		this.emplacements = emplacements;
	}

	public ResStock getResStock() {
		return resStock;
	}

	public void setResStock(ResStock resStock) {
		this.resStock = resStock;
	}

	public ResProduction getResProduction() {
		return resProduction;
	}

	public void setResProduction(ResProduction resProduction) {
		this.resProduction = resProduction;
	}

	@Override
	public String toString() {
		return "Kit [id=" + id + ", OF=" + OF + ", etat=" + etat + ", projet=" + projet + ", dateEntree=" + dateEntree
				+ ", dateSortie=" + dateSortie + ", emplacements=" + emplacements + ", resStock=" + resStock
				+ ", resProduction=" + resProduction + "]";
	}

}
