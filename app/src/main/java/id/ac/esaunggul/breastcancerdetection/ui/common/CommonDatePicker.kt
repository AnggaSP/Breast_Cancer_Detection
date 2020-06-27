/*
 * Copyright 2020 Angga Satya Putra
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package id.ac.esaunggul.breastcancerdetection.ui.common

import androidx.annotation.StringRes
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Calendar
import java.util.TimeZone

/**
 * Builder class for common [MaterialDatePicker] used throughout this project.
 *
 * @see [MaterialDatePicker] for the full explanation of what this class does.
 */
class CommonDatePicker {

    /**
     * Create an instance of [MaterialDatePicker] with default range of one month from now().
     *
     * @param monthLimit Int the amount of month(s) to limit from now() to limit.
     * @param title Int String resource for the title String.
     */
    fun build(monthLimit: Int = 1, @StringRes title: Int): MaterialDatePicker<Long> {
        val builder = MaterialDatePicker.Builder.datePicker()
        val constraint = CalendarConstraints.Builder()

        val date = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        date.add(Calendar.MONTH, monthLimit)
        val maxDate = date.timeInMillis

        val dateValidatorMin = DateValidatorPointForward.now()
        val dateValidatorMax = DateValidatorPointBackward.before(maxDate)

        val listValidators = mutableListOf<CalendarConstraints.DateValidator>()
        listValidators.add(dateValidatorMin)
        listValidators.add(dateValidatorMax)

        val validators = CompositeDateValidator.allOf(listValidators)
        constraint.setValidator(validators)

        builder.setTitleText(title)
        builder.setCalendarConstraints(constraint.build())

        return builder.build()
    }
}