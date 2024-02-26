package com.mindbyromanzanoni.utils

data class ReminderClass(var name: String = "Sunday", var id: Int = 1, var isSelected: Boolean = false,var shortName:String="")

fun getWeekName() = ArrayList<ReminderClass>().also {
    it.add(ReminderClass(name = "Sunday", id = 1,shortName="Sun"))
    it.add(ReminderClass(name = "Monday", id = 2,shortName="Mon"))
    it.add(ReminderClass(name = "Tuesday", id = 3,shortName="Tue"))
    it.add(ReminderClass(name = "Wednesday", id = 4,shortName="Wed"))
    it.add(ReminderClass(name = "Thursday", id = 5,shortName="Thu"))
    it.add(ReminderClass(name = "Friday", id = 6,shortName="Fri"))
    it.add(ReminderClass(name = "Saturday", id = 7,shortName="Sat"))
}
