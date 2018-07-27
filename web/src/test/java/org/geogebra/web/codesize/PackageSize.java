package org.geogebra.web.codesize;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.util.Charsets;
import org.geogebra.common.util.debug.Log;
import org.junit.Test;

import com.google.gwt.dev.util.collect.HashMap;

public class PackageSize {

	private static HashMap<String, JSONObject> packages = new HashMap<>();
	private static final boolean addUnattributed = true;

	@Test
	public void testPackages() throws JSONException {
		String html = loadFileIntoString(
				"build/gwt/extra/webSimple/soycReport/compile-report/initial-0-packageBreakdown.html");
		String[] table = html
				.substring(html.indexOf("<tr"), html.lastIndexOf("/tr>"))
				.split("<tr");
		int total = 0;
		for (String row : table) {
			String[] cells = row.split("<td");
			if (cells.length > 3) {
				try {
					addPackage(
							cells[1].substring(cells[1].indexOf(">", 7) + 1,
									cells[1].indexOf("</a")),
							cells[3].replaceAll("[^0-9]", ""));
					total += Integer
							.parseInt(cells[3].replaceAll("[^0-9]", ""));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		updateSelfSizes(packages.get(""));

		if (addUnattributed) {
			html = loadFileIntoString(
					"build/gwt/extra/webSimple/soycReport/compile-report/initial-0-codeTypeBreakdown.html");
			String bytes = html.split("<p class=\"soyc-breakdown-strings\">")[2]
					.replaceAll("[^0-9]", "");
			packages.get("").getJSONArray("children")
					.put(new JSONObject().put("name", "Dark matter").put("bytes", bytes));
		}
		System.out.println(total);
		System.out.println(packages.get("").toString());
	}

	private static void updateSelfSizes(JSONObject pkg) throws JSONException {
		if (pkg.has("children")) {
			JSONArray children = pkg.getJSONArray("children");
			for (int i = 0; i < children.length(); i++) {
				updateSelfSizes(children.getJSONObject(i));
			}
			if (pkg.has("bytes")) {
				JSONObject root = new JSONObject();
				root.put("bytes", pkg.get("bytes"));
				root.put("name", pkg.get("name") + "*");
				pkg.remove("bytes");
				pkg.getJSONArray("children").put(root);
			}
		}

	}

	private void addPackage(String name, String size) throws JSONException {
		if (!packages.containsKey(name)) {
			JSONObject self = new JSONObject();
			addParents(name, self);
			packages.put(name, self);
		}
		packages.get(name).put("bytes", size);
	}

	private void addParents(String name, JSONObject self)
			throws JSONException {
		self.put("name", name);
		String parent = name.contains(".")
				? name.substring(0, name.lastIndexOf(".")) : "";
		if (packages.get(parent) == null) {
			JSONObject parentPackage = new JSONObject();
			packages.put(parent, parentPackage);
			packages.get(parent).put("name", parent);
			if (parent.length() > 0) {
				addParents(parent, parentPackage);
			}			
		}
		if (!packages.get(parent).has("children")) {
			packages.get(parent).put("children", new JSONArray());
		}
		packages.get(parent).getJSONArray("children").put(self);
	}

	public static String loadFileIntoString(String filename) {

		InputStream ios = null;
		try {
			ios = new FileInputStream(new File(filename));
			return loadIntoString(ios);
		} catch (Exception e) {
			Log.error("problem loading " + filename);
		} finally {
			try {
				if (ios != null) {
					ios.close();
				}
			} catch (IOException e) {
				Log.error("problem loading " + filename);
			}
		}

		return null;
	}

	public static String loadIntoString(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(is, Charsets.UTF_8));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}
}
