package com.alstom.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.alstom.model.Emplacement;
import com.alstom.model.EtatKit;
import com.alstom.model.Kit;
import com.alstom.model.ResProduction;
import com.alstom.model.ResStock;

public class AppInitializer {

	private static ResStockService rss = new ResStockService();
	private static ResProductionService rps = new ResProductionService();
	private static KitService ks = new KitService();
	private static EmplacementService es = new EmplacementService();

	public static void init() {
		initResStock();
		initResProduction();
		initEmplacements();
		initKits();
	}

	public static void initEmplacements() {
		System.out.println("init emplacements");
		char[] alpha = "ABCDEFGHIJKLMN".toCharArray();

		List<Emplacement> emps = new ArrayList<>();

		for (int i = 0, len = alpha.length; i < len; i++) {
			for (int j = 1; j < 25; j++) {
				emps.add(new Emplacement(
						new StringBuilder().append(alpha[i]).append(j < 10 ? "0" : "").append(j).toString()));
			}
		}

		es.save(emps);
		System.out.println("init end");
	}

	public static void initResStock() {

		rss.save(new ResStock("1", "1"));
		rss.save(new ResStock("2", "2"));
	}

	public static void initResProduction() {

		rps.save(new ResProduction("rp1"));
		rps.save(new ResProduction("rp2"));
		rps.save(new ResProduction("rp3"));
	}

	public static void initKits() {
		Calendar enterDate = Calendar.getInstance();
		Set<Kit> kits = new HashSet<Kit>();

		Set<Emplacement> emps1 = new HashSet<>();
		emps1.add(es.getEmplacement("A01"));
		emps1.add(es.getEmplacement("A02"));
		emps1.add(es.getEmplacement("A03"));

		Set<Emplacement> emps2 = new HashSet<>();
		emps2.add(es.getEmplacement("A04"));
		emps2.add(es.getEmplacement("A05"));

//		Set<Emplacement> emps3 = new HashSet<>();
//		emps3.add(es.getEmplacement("B01"));

		Set<Emplacement> emps4 = new HashSet<>();
		emps4.add(es.getEmplacement("A06"));

		Set<Emplacement> emps5 = new HashSet<>();
		emps5.add(es.getEmplacement("B02"));

		kits.add(new Kit("11111111", EtatKit.ENSTOCK, "pjr1", getDate("04/01/2020 14:50"), null, emps1,
				rss.getResStock("1"), null));
		kits.add(new Kit("22222222", EtatKit.ENSTOCK, "pjr1", getDate("05/01/2020 10:15"), null, emps2,
				rss.getResStock("1"), null));
		kits.add(new Kit("33333333", EtatKit.SORTIE, "pjr2", getDate("17/03/2020 11:15"), getDate("18/03/2020 14:50"),
				null, rss.getResStock("1"), rps.getResProduction("rp1")));
		kits.add(new Kit("44444444", EtatKit.ENSTOCK, "pjr2", getDate("25/08/2020 14:50"), null, emps4,
				rss.getResStock("1"), null));
		kits.add(new Kit("55555555", EtatKit.ENSTOCK, "pjr3", getDate("09/09/2020 14:50"), null, emps5,
				rss.getResStock("1"), null));

		ks.save(kits);
	}

	public static void fetchAllData() {
		rss.getResStock().stream().forEach(System.out::println);
		rps.getResProduction().stream().forEach(System.out::println);
		es.getEmplacements().stream().forEach(System.out::println);
		ks.getKits().stream().forEach(System.out::println);
	}

	private static Date getDate(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		try {
			return sdf.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
}
