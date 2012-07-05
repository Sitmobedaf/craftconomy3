package com.greatmancode.craftconomy3.currency;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.database.tables.CurrencyTable;

public class CurrencyManager {

	public static int DefaultCurrencyID;
			
	private HashMap<Integer, Currency> currencyList = new HashMap<Integer,Currency>();
	
	public CurrencyManager() {
		//Let's load all currency in the database
		List<CurrencyTable> result = Common.getInstance().getDatabaseManager().getDatabase().select(CurrencyTable.class).execute().find();
		Iterator<CurrencyTable> iterator = result.iterator();
		while (iterator.hasNext())
		{
			CurrencyTable entry = iterator.next();
			addCurrency(entry.name, entry.plural, entry.minor, entry.minorplural, false);
		}
		String defaultCurrency = Common.getInstance().getConfigurationManager().getConfig().getString("System.Default.Currency.Name");
		Currency defaultCur = getCurrency(defaultCurrency);
		if (defaultCur != null)
		{
			DefaultCurrencyID = defaultCur.getDatabaseID();
		}
		else
		{
			addCurrency(Common.getInstance().getConfigurationManager().getConfig().getString("System.Default.Currency.Name"),Common.getInstance().getConfigurationManager().getConfig().getString("System.Default.Currency.NamePlural"),Common.getInstance().getConfigurationManager().getConfig().getString("System.Default.Currency.Minor"),Common.getInstance().getConfigurationManager().getConfig().getString("System.Default.Currency.MinorPlural"),true);
		}
	}
	
	/**
	 * Get a currency
	 * @param id The Database ID
	 * @return A Currency instance if the currency is found else null
	 */
	public Currency getCurrency(int id) {
		Currency result = null;
		if (currencyList.containsKey(id))
		{
			result = currencyList.get(id);
		}
		return result;
	}
	/**
	 * 
	 * @param name
	 * @return
	 */
	public Currency getCurrency(String name) {
		Currency result = null;
		CurrencyTable DBresult = Common.getInstance().getDatabaseManager().getDatabase().select(CurrencyTable.class).where().equal("name", name).execute().findOne();
		if (DBresult != null)
		{
			result = getCurrency(DBresult.id);
		}
		return result;
	}
	
	/**
	 * Add a currency in the system
	 * @param name The main currency name
	 * @param plural The main currency name in plural
	 * @param minor The minor (cents) part of the currency
	 * @param minorPlural The minor (cents) part of the currency in plural
	 * @param save Do we add it in the database?
	 */
	public void addCurrency(String name, String plural, String minor, String minorPlural, boolean save) {
		addCurrency(-1, name, plural, minor, minorPlural, save);
	}
	/**
	 * Add a currency in the system
	 * @param name The main currency name
	 * @param plural The main currency name in plural
	 * @param minor The minor (cents) part of the currency
	 * @param minorPlural The minor (cents) part of the currency in plural
	 * @param save Do we add it in the database? If True, generates a databaseID (Whole new entry)
	 */
	public void addCurrency(int databaseID, String name, String plural, String minor, String minorPlural, boolean save) {
		if (save)
		{
			CurrencyTable entry = new CurrencyTable();
			entry.minor = minor;
			entry.minorplural = minorPlural;
			entry.name = name;
			entry.plural = plural;
			Common.getInstance().getDatabaseManager().getDatabase().save(entry);
			databaseID = entry.id;
		}
		currencyList.put(databaseID, new Currency(databaseID, name,plural,minor,minorPlural));
	}
}
