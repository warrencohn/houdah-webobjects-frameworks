package com.houdah.movies.initialization;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import com.houdah.foundation.FormatterFactory;
import com.houdah.foundation.formatters.BooleanFormatter;
import com.houdah.foundation.formatters.KeyValueFormatter;
import com.houdah.foundation.formatters.TimestampFormatter;

public class Formatters
{

	public static void initialize()
	{
		initIntegerNumber();
		initDecimalNumber();
		initPercentNumber();

		initBoolean();

		initLongDate();
		initShortDate();
		initLongTime();
		initShortTime();
		initLongDateTime();
		initShortDateTime();

		initArrayCount();
	}

	public static void initIntegerNumber()
	{
		DecimalFormatSymbols decimal = new DecimalFormatSymbols();

		decimal.setDecimalSeparator(',');
		decimal.setGroupingSeparator('.');

		DecimalFormat formatter = new DecimalFormat("0");

		formatter.setGroupingSize(3);
		formatter.setGroupingUsed(true);
		formatter.setDecimalFormatSymbols(decimal);

		FormatterFactory factory = FormatterFactory.sharedInstance();
		String name = "integerNumber";

		factory.store(formatter, name);
	}

	public static void initDecimalNumber()
	{
		DecimalFormatSymbols decimal = new DecimalFormatSymbols();

		decimal.setDecimalSeparator(',');
		decimal.setGroupingSeparator('.');

		DecimalFormat formatter = new DecimalFormat();

		formatter.setGroupingSize(3);
		formatter.setGroupingUsed(true);
		formatter.setDecimalFormatSymbols(decimal);

		FormatterFactory factory = FormatterFactory.sharedInstance();
		String name = "decimalNumber";

		factory.store(formatter, name);
	}

	public static void initPercentNumber()
	{
		DecimalFormat formatter = new DecimalFormat("#0.00%");

		FormatterFactory factory = FormatterFactory.sharedInstance();
		String name = "percentNumber";

		factory.store(formatter, name);
	}

	public static void initBoolean()
	{
		BooleanFormatter formatter = new BooleanFormatter("non", "oui");

		FormatterFactory factory = FormatterFactory.sharedInstance();
		String name = "boolean";

		factory.store(formatter, name);
	}

	public static void initLongDate()
	{
		TimestampFormatter formatter = new TimestampFormatter("dd/MM/yyyy");

		FormatterFactory factory = FormatterFactory.sharedInstance();
		String name = "longDate";

		factory.store(formatter, name);
	}

	public static void initShortDate()
	{
		TimestampFormatter formatter = new TimestampFormatter("dd/MM/yy");

		FormatterFactory factory = FormatterFactory.sharedInstance();
		String name = "shortDate";

		factory.store(formatter, name);
	}

	public static void initLongTime()
	{
		TimestampFormatter formatter = new TimestampFormatter("HH:mm:ss");

		FormatterFactory factory = FormatterFactory.sharedInstance();
		String name = "longTime";

		factory.store(formatter, name);
	}

	public static void initShortTime()
	{
		TimestampFormatter formatter = new TimestampFormatter("HH:mm");

		FormatterFactory factory = FormatterFactory.sharedInstance();
		String name = "shortTime";

		factory.store(formatter, name);
	}

	public static void initLongDateTime()
	{
		TimestampFormatter formatter = new TimestampFormatter("MM/dd/yyyy HH:mm:ss");

		FormatterFactory factory = FormatterFactory.sharedInstance();
		String name = "longDateTime";

		factory.store(formatter, name);
	}

	public static void initShortDateTime()
	{
		TimestampFormatter formatter = new TimestampFormatter("MM/dd/yy HH:mm");

		FormatterFactory factory = FormatterFactory.sharedInstance();
		String name = "shortDateTime";

		factory.store(formatter, name);
	}

	public static void initArrayCount()
	{
		KeyValueFormatter formatter = new KeyValueFormatter("%count elements");

		FormatterFactory factory = FormatterFactory.sharedInstance();
		String name = "arrayCount";

		factory.store(formatter, name);
	}
}