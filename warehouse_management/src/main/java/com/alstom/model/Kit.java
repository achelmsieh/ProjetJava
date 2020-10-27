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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import com.alstom.model.enums.EtatKit;

@Entity
@Table(name = "kits")
public class Kit {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "kit_id_sequence")
	@SequenceGenerator(name = "kit_id_sequence", sequenceName = "KIT_ID_SEQ")
	private long id;

	@Column(name = "[OF]")
	private String OF;

	@Enumerated(EnumType.ORDINAL)
	private EtatKit etat;

	private String projet;

	private String DTR;
	private double runTime;
	private String description;
	private int nRAME;
	private String indiceCPC;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date_entree")
	private Date dateEntree;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date_sortie")
	private Date dateSortie;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date_sortie_prevue")
	private Date dateSortiePrevue;

	@ManyToMany()
	@JoinTable(name = "kits_emplacements", joinColumns = @JoinColumn(name = "kit_id"), inverseJoinColumns = @JoinColumn(name = "emplacement_id"))
	private Set<Emplacement> emplacements;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_res_stock", nullable = true)
	private Personnel resStock;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_res_production", nullable = true)
	private Personnel resProduction;

	public Kit() {
		super();
	}

	public Kit(String OF, EtatKit etat, String projet, Date dateEntree, Date dateSortie, Set<Emplacement> emplacements,
			Personnel resStock, Personnel resProduction) {
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

	public Kit(long id, String oF, EtatKit etat, String projet, String dTR, double runTime, String description,
			int nRAME, String indiceCPC, Date dateEntree, Date dateSortie, Set<Emplacement> emplacements,
			Personnel resStock, Personnel resProduction) {
		super();
		this.id = id;
		OF = oF;
		this.etat = etat;
		this.projet = projet;
		DTR = dTR;
		this.runTime = runTime;
		this.description = description;
		this.nRAME = nRAME;
		this.indiceCPC = indiceCPC;
		this.dateEntree = dateEntree;
		this.dateSortie = dateSortie;
		this.emplacements = emplacements;
		this.resStock = resStock;
		this.resProduction = resProduction;
	}

	public Kit(String of) {
		super();
		this.OF = of;
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

	public String getDTR() {
		return DTR;
	}

	public void setDTR(String dTR) {
		DTR = dTR;
	}

	public double getRunTime() {
		return runTime;
	}

	public void setRunTime(double runTime) {
		this.runTime = runTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getnRAME() {
		return nRAME;
	}

	public void setnRAME(int nRAME) {
		this.nRAME = nRAME;
	}

	public String getIndiceCPC() {
		return indiceCPC;
	}

	public void setIndiceCPC(String indiceCPC) {
		this.indiceCPC = indiceCPC;
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

	public Date getDateSortiePrevue() {
		return dateSortiePrevue;
	}

	public void setDateSortiePrevue(Date dateSortiePrevue) {
		this.dateSortiePrevue = dateSortiePrevue;
	}

	public Set<Emplacement> getEmplacements() {
		return emplacements;
	}

	public void setEmplacements(Set<Emplacement> emplacements) {
		this.emplacements = emplacements;
	}

	public Personnel getResStock() {
		return resStock;
	}

	public void setResStock(Personnel resStock) {
		this.resStock = resStock;
	}

	public Personnel getResProduction() {
		return resProduction;
	}

	public void setResProduction(Personnel resProduction) {
		this.resProduction = resProduction;
	}

	@Override
	public String toString() {
		return "Kit [id=" + id + ", OF=" + OF + ", etat=" + etat + ", projet=" + projet + ", DTR=" + DTR + ", runTime="
				+ runTime + ", description=" + description + ", nRAME=" + nRAME + ", indiceCPC=" + indiceCPC
				+ ", dateEntree=" + dateEntree + ", dateSortie=" + dateSortie + ", dateSortiePrevue=" + dateSortiePrevue
				+ ", emplacements=" + emplacements + ", resStock=" + resStock + ", resProduction=" + resProduction
				+ "]";
	}

	public boolean isPlanning() {
		return this.etat == EtatKit.PLANNING;
	}

	public boolean isSortie() {
		return this.etat == EtatKit.SORTIE;
	}

	public boolean isEnStock() {
		return this.etat == EtatKit.ENSTOCK;
	}
}
