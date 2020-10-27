package com.alstom.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class OrderDataExtrator {

	// return map for each OF with his DSP (date de sortie prévue)
	public static Map<String, Date> getKitsDSP() {
		Map<String, Date> rsltKits = new HashMap<String, Date>();

		Map<Date, List<String>> dd = datesOFs();

		TreeMap<Date, List<String>> datesOFs = new TreeMap<Date, List<String>>(dd);

		if (datesOFs != null && !datesOFs.isEmpty()) {
			datesOFs.forEach((date, ofs) -> {
				if (ofs != null)
					ofs.forEach(of -> rsltKits.put(of, date));
			});
		}

		return rsltKits;
	}

	private static Map<Date, List<String>> datesOFs() {
		Map<Date, List<String>> rslt = new HashMap<Date, List<String>>();

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.WEEK_OF_MONTH, -2);
		for (int j = 0; j < 5; j++) {
			cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
			cal.clear(Calendar.MINUTE);
			cal.clear(Calendar.SECOND);
			cal.clear(Calendar.MILLISECOND);

			for (int i = 0; i < 6; i++) {
				Date date = getFormatedDate(cal.getTime());
				rslt.put(date, AgendaExtractor.getOFs(date));
				cal.add(Calendar.DAY_OF_WEEK, 1);
			}
			cal.add(Calendar.WEEK_OF_MONTH, 1);
		}

		return rslt;
	}

	private static Date getFormatedDate(Date date) {
		DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			return sdf.parse(sdf.format(date));
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
}
