package com.example.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDate
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.kotlin.datetime.time
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp


object Services : Table("services") {
    val id = integer("service_id").autoIncrement()
    val serviceName = varchar("service_name", 32)
    val servicePrice = decimal("service_price", 10, 2)

    override val primaryKey = PrimaryKey(id)
}

object SocialStatuses : Table("social_statuses") {
    val id = integer("social_status_id").autoIncrement()
    val discount = reference("discount_id", Discounts.id)
    val socialStatusName = varchar("social_status_name", 36)

    override val primaryKey = PrimaryKey(id)
}

object Patients : Table("patients") {
    val id = integer("patient_id").autoIncrement()
    val patientGender = char("patient_gender")
    val patientFirstName = varchar("patient_first_name", 32)
    val patientMiddleName = varchar("patient_middle_name", 36).nullable()
    val patientLastName = varchar("patient_last_name", 64)
    val patientBirthday = date("patient_birthday")
    val patientPhoneNumber = varchar("patient_phone_number", 18)
    val patientPassportSeries = varchar("patient_passport_series", 45)
    val patientPassportNumber = varchar("patient_passport_number", 45)
    val patientPassportIssueDate = date("patient_passport_issue_date")
    val patientPassportIssuedBy = varchar("patient_passport_issued_by", 45)
    val patientAddressCountry = varchar("patient_address_country", 45)
    val patientAddressRegion = varchar("patient_address_region", 45)
    val patientAddressLocality = varchar("patient_address_locality", 45)
    val patientAddressStreet = varchar("patient_address_street", 45)
    val patientAddressHouse = integer("patient_address_house")
    val patientAddressBody = integer("patient_address_body")
    val patientAddressApartment = integer("patient_address_apartment")

    override val primaryKey = PrimaryKey(id)
}

object Visits : Table("visits") {
    val id = integer("visit_id").autoIncrement()
    val discountId = reference("discount_id", Discounts.id)
    val patientId = reference("patient_id", Patients.id)
    val employeeId = reference("employee_id", Employees.id)
    val visitDateAndTime = timestamp("visit_date_and_time")

    override val primaryKey = PrimaryKey(id)
}

object Discounts : Table("discounts") {
    val id = integer("discount_id").autoIncrement()
    val discountPercent = short("discount_percent")

    override val primaryKey = PrimaryKey(id)
}

object Specialties : Table("specialties") {
    val id = integer("speciality_id").autoIncrement()
    val specialityName = varchar("speciality_name", 36)

    override val primaryKey = PrimaryKey(id)
}

object Employees : Table("employees") {
    val id = integer("employee_id").autoIncrement()
    val specialityId = reference("speciality_id", Specialties.id)
    val employeeFirstName = varchar("employee_first_name", 32)
    val employeeMiddleName = varchar("employee_middle_name", 36).nullable()
    val employeeLastName = varchar("employee_last_name", 64)
    val employeeBirthday = date("employee_birthday")
    val employeeGender = char("employee_gender")
    val employeeJobTitle = varchar("employee_job_title", 36)
    val employeePhoneNumber = varchar("employee_phone_number", 18)
    val employeeDurationOfVisit = time("employee_duration_of_visit").nullable()
    val employeeUsername = varchar("employee_username", 100)
    val employeeEmail = varchar("employee_email", 100)
    val employeePassword = varchar("employee_password", 100)

    override val primaryKey = PrimaryKey(id)
}

object Schedules : Table("schedules") {
    val id = integer("schedule_id").autoIncrement()
    val employeeId = reference("employee_id", Employees.id)
    val scheduleDate = date("schedule_date")
    val scheduleTimeFrom = time("schedule_time_from")
    val scheduleTimeTo = time("schedule_time_to")

    override val primaryKey = PrimaryKey(id)
}