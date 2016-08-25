package com.emarsys

import org.scalatest.FlatSpec
import java.time.ZonedDateTime

class DueDateSpec extends FlatSpec {

	"9:00 Monday" should "be a valid working hour" in {
		assert(
			DueDate.isWorkingHour(
				ZonedDateTime.of(2016, 1, 4, 9, 0, 0, 0, DueDate.timezone)) == true)
	}

	"13:00 Monday" should "be a valid working hour" in {
		assert(
			DueDate.isWorkingHour(
				ZonedDateTime.of(2016, 1, 4, 13, 0, 0, 0, DueDate.timezone)) == true)
	}

	"17:00 Monday" should "be a valid working hour" in {
		assert(
			DueDate.isWorkingHour(
				ZonedDateTime.of(2016, 1, 4, 17, 0, 0, 0, DueDate.timezone)) == true)
	}

	"9:00 Wednesday" should "be a valid working hour" in {
		assert(
			DueDate.isWorkingHour(
				ZonedDateTime.of(2016, 1, 6, 9, 0, 0, 0, DueDate.timezone)) == true)
	}

	"13:00 Wednesday" should "be a valid working hour" in {
		assert(
			DueDate.isWorkingHour(
				ZonedDateTime.of(2016, 1, 6, 13, 0, 0, 0, DueDate.timezone)) == true)
	}

	"17:00 Wednesday" should "be a valid working hour" in {
		assert(
			DueDate.isWorkingHour(
				ZonedDateTime.of(2016, 1, 6, 17, 0, 0, 0, DueDate.timezone)) == true)
	}

	"9:00 Friday" should "be a valid working hour" in {
		assert(
			DueDate.isWorkingHour(
				ZonedDateTime.of(2016, 1, 8, 9, 0, 0, 0, DueDate.timezone)) == true)
	}

	"13:00 Friday" should "be a valid working hour" in {
		assert(
			DueDate.isWorkingHour(
				ZonedDateTime.of(2016, 1, 8, 9, 0, 0, 0, DueDate.timezone)) == true)
	}

	"17:00 Friday" should "be a valid working hour" in {
		assert(
			DueDate.isWorkingHour(
				ZonedDateTime.of(2016, 1, 8, 17, 0, 0, 0, DueDate.timezone)) == true)
	}

	"8:59 Monday" should "not be a valid working hour" in {
		assert(
			DueDate.isWorkingHour(
				ZonedDateTime.of(2016, 1, 4, 8, 59, 0, 0, DueDate.timezone)) == false)
	}

	"17:01 Monday" should "not be a valid working hour" in {
		assert(
			DueDate.isWorkingHour(
				ZonedDateTime.of(2016, 1, 4, 17, 1, 0, 0, DueDate.timezone)) == false)
	}

	"9:00 Saturday" should "not be a valid working hour" in {
		assert(
			DueDate.isWorkingHour(
				ZonedDateTime.of(2016, 1, 9, 9, 0, 0, 0, DueDate.timezone)) == false)
	}

	"13:00 Saturday" should "not be a valid working hour" in {
		assert(
			DueDate.isWorkingHour(
				ZonedDateTime.of(2016, 1, 9, 13, 0, 0, 0, DueDate.timezone)) == false)
	}

	"17:00 Saturday" should "not be a valid working hour" in {
		assert(
			DueDate.isWorkingHour(
				ZonedDateTime.of(2016, 1, 9, 17, 0, 0, 0, DueDate.timezone)) == false)
	}

	"9:00 Sunday" should "not be a valid working hour" in {
		assert(
			DueDate.isWorkingHour(
				ZonedDateTime.of(2016, 1, 10, 9, 0, 0, 0, DueDate.timezone)) == false)
	}

	"13:00 Sunday" should "not be a valid working hour" in {
		assert(
			DueDate.isWorkingHour(
				ZonedDateTime.of(2016, 1, 10, 13, 1, 0, 0, DueDate.timezone)) == false)
	}

	"17:00 Sunday" should "not be a valid working hour" in {
		assert(
			DueDate.isWorkingHour(
				ZonedDateTime.of(2016, 1, 10, 17, 1, 0, 0, DueDate.timezone)) == false)
	}

	"bug submitted on Tuesday at 14:12 with 16 hours - 2 days - turnaround time" should "be done by 14:12 on Thursday" in {
		val dueDate = DueDate.calculate(ZonedDateTime.of(2016, 1, 5, 14, 12, 0, 0, DueDate.timezone), 16)
		val expected = ZonedDateTime.of(2016, 1, 7, 14, 12, 0, 0, DueDate.timezone)
		assert(dueDate.equals(expected) == true)
	}

	"bug submitted on Monday at 9:00 with 1 hour turnaround time" should "be done by 10:00 on the same day" in {
		val dueDate = DueDate.calculate(ZonedDateTime.of(2016, 1, 4, 9, 0, 0, 0, DueDate.timezone), 1)
		val expected = ZonedDateTime.of(2016, 1, 4, 10, 0, 0, 0, DueDate.timezone)
		assert(dueDate.equals(expected) == true)
	}

	"bug submitted on Wednesday at 13:31 with 2 hour turnaround time" should "be done by 15:31 on the same day" in {
		val dueDate = DueDate.calculate(ZonedDateTime.of(2016, 1, 6, 13, 31, 0, 0, DueDate.timezone), 2)
		val expected = ZonedDateTime.of(2016, 1, 6, 15, 31, 0, 0, DueDate.timezone)
		assert(dueDate.equals(expected) == true)
	}

	"bug submitted on Friday at 17:00 with 1 hour turnaround time" should "be done by 10:00 on Monday next week" in {
		val dueDate = DueDate.calculate(ZonedDateTime.of(2016, 1, 8, 17, 0, 0, 0, DueDate.timezone), 1)
		val expected = ZonedDateTime.of(2016, 1, 11, 10, 0, 0, 0, DueDate.timezone)
		assert(dueDate.equals(expected) == true)
	}

	"bug submitted on Wednesday at 14:12 with 64 hours - 8 days - turnaround time" should "be done by 14:12 on Monday after next week" in {
		val dueDate = DueDate.calculate(ZonedDateTime.of(2016, 1, 6, 14, 12, 0, 0, DueDate.timezone), 64)
		val expected = ZonedDateTime.of(2016, 1, 18, 14, 12, 0, 0, DueDate.timezone)
		assert(dueDate.equals(expected) == true)
	}

}
