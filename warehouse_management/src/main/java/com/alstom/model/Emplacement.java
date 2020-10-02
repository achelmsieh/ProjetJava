package com.alstom.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "emplacements")
public class Emplacement {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String coordonnee;

	@ManyToMany(mappedBy = "emplacements")
	private Set<Kit> kits;

	public Emplacement() {
		super();
	}

	public Emplacement(long id, String coordonnee, Set<Kit> kits) {
		super();
		this.id = id;
		this.coordonnee = coordonnee;
		this.kits = kits;
	}

	public Emplacement(String coordonnee) {
		this.coordonnee = coordonnee;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCoordonnee() {
		return coordonnee;
	}

	public void setCoordonnee(String coordonnee) {
		this.coordonnee = coordonnee;
	}

	public Set<Kit> getKits() {
		return kits;
	}

	public void setKits(Set<Kit> kits) {
		this.kits = kits;
	}

	@Override
	public String toString() {
		return "Emplacement [id=" + id + ", coordonnee=" + coordonnee + /* ", kits=" + kits + */ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coordonnee == null) ? 0 : coordonnee.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Emplacement other = (Emplacement) obj;
		if (coordonnee == null) {
			if (other.coordonnee != null)
				return false;
		} else if (!coordonnee.equals(other.coordonnee))
			return false;
		return true;
	}

}
