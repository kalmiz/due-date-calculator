package com.emarsys

import java.time.{ DayOfWeek, ZoneId, ZonedDateTime }
import java.time.temporal.ChronoUnit

object DueDate {

	private def toWorkingHour(date: ZonedDateTime, hour: Int, resetMinutes: Boolean = false): ZonedDateTime = 
		ZonedDateTime.of(
			date.getYear,
			date.getMonthValue,
			date.getDayOfMonth,
			hour,
			(if (resetMinutes) 0 else date.getMinute),
			0,
			0,
			timezone)

	private val morning = toWorkingHour(
		_: ZonedDateTime, 
		Option(System getenv "EMARSYS_WORKDAY_BEGINS").getOrElse("9").toInt)

	private val afternoon = toWorkingHour(
		_: ZonedDateTime,
		Option(System getenv "EMARSYS_WORKDAY_ENDS").getOrElse("17").toInt,
		resetMinutes = true)

	private val workingDays = Set(
		DayOfWeek.MONDAY,
		DayOfWeek.TUESDAY,
		DayOfWeek.WEDNESDAY,
		DayOfWeek.THURSDAY,
		DayOfWeek.FRIDAY)

	// increase the date until it will be a valid wokring day (e.g. non-weeken), recursive function
	private def addDays(date: ZonedDateTime): ZonedDateTime = {
		val withPlusDay = date.plusDays(1)
		if (isWorkingHour(withPlusDay)) withPlusDay
		else addDays(withPlusDay)
	}

	// the main logic, recursive function
	private def doCalc(date: ZonedDateTime, hours: Int): ZonedDateTime = {
		if (hours <= 0) date
		else {
			val end = afternoon(date)
			val dateWithHours = date.plusHours(hours)
			if (dateWithHours.compareTo(end) <= 0) dateWithHours // we are done if the issue can be resolve on the same day
			else {
				val diff = Math.ceil(ChronoUnit.MINUTES.between(date, end).toDouble / 60).toInt
				doCalc(morning(addDays(date)), hours - diff)
			}
		}
	}

	// PUBLIC INTERFACE

	/**
	 * The current timezone.
	 *
	 * Reads the `user.timezone` system property. The default is "Europe/Budapest".
	 */
	val timezone = ZoneId of { 
		System.getProperty("user.timezone", "Europe/Budapest")
	}
	
	/**
	 * Returns true if the passed date params is a working hour (non-weekend and office time)
	 * 
	 * @param date The date to check
	 * @return True if the date is a working hour otherwise false 
	 */
	def isWorkingHour(date: ZonedDateTime): Boolean =
		(date.compareTo(morning(date)) >= 0 && date.compareTo(afternoon(date)) <= 0) &&
			(workingDays contains date.getDayOfWeek)

	/**
	 * Calculates the date and time when the issue is to be resolved.
	 *
	 * @param submitDate The date of the issue
	 * @param turnaroundTime The turnaround time
	 * @return The due date
	 */
	def calculate(submitDate: ZonedDateTime, turnaroundTime: Int): ZonedDateTime = {
		if (!isWorkingHour(submitDate)) {
			throw new IllegalArgumentException(submitDate + " is a nonworking day")
		}

		doCalc(submitDate, turnaroundTime)
	}
}
